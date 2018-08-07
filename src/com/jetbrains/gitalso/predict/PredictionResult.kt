package com.jetbrains.gitalso.predict

import com.intellij.openapi.application.PermanentInstallationID
import com.intellij.openapi.vfs.VirtualFile
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factor
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.log.LogField
import com.jetbrains.gitalso.log.State
import com.jetbrains.gitalso.storage.log.hash.HashProvider

data class PredictionResult(
        val commit: List<CommittedFile> = ArrayList(),
        val scores: Map<Pair<CommittedFile, CommittedFile>, Number> = HashMap(),
        val prediction: Collection<CommittedFile> = ArrayList(),
        val topScores: Map<Pair<CommittedFile, CommittedFile>, Number> = HashMap(),
        val topPrediction: Collection<CommittedFile> = ArrayList()
) {
    private val timestamp by lazy {
        System.currentTimeMillis()
    }
    private val recorderID = "git-also"
    private val recorderVersion = "1"
    private val userID by lazy {
        PermanentInstallationID.get()
    }
    private val bucket = "-1"
    var repository = "null"
    var sessionID = 1

    private fun putIfNotEmptyValue(map: MutableMap<LogField, Any>, field: LogField, value: Collection<*>) {
        if (value.isNotEmpty()) {
            map[field] = value
        }
    }

    private fun putIfNotEmptyValue(map: MutableMap<LogField, Any>, field: LogField, value: Map<*, *>) {
        if (value.isNotEmpty()) {
            map[field] = value
        }
    }

    fun getLogEvent(
            stateBefore: State,
            stateAfter: State,
            action: Action,
            time: Long,
            commits: Map<Pair<CommittedFile, CommittedFile>, Set<Long>>,
            commitsAuthorMask: Map<Pair<CommittedFile, CommittedFile>, List<Number>> = mapOf(),
            predictionModified: List<VirtualFile> = ArrayList(),
            predictionUnmodified: List<VirtualFile> = ArrayList()
    ): LogEvent {
        val factors = HashMap<String, HashMap<Factor, Any>>()
        for ((key, value) in topScores) {
            if (key !in commits) {
                continue
            }
            factors[key.toString()] = hashMapOf(
                    Factor.SCORES to value,
                    Factor.COMMITS to commits[key]!!)
            if (key in commitsAuthorMask) {
                factors[key.toString()]!![Factor.COMMITS_AUTHOR_MASK] = commitsAuthorMask[key]!!
            }
        }

        val dependentFields = HashMap<LogField, Any>()

        putIfNotEmptyValue(dependentFields,
                LogField.PREDICTION_MODIFIED,
                predictionModified.map { HashProvider.hash(it.path) })

        putIfNotEmptyValue(dependentFields,
                LogField.PREDICTION_UNMODIFIED,
                predictionUnmodified.map { HashProvider.hash(it.path) })

        putIfNotEmptyValue(dependentFields,
                LogField.FACTORS,
                factors)

        putIfNotEmptyValue(dependentFields,
                LogField.FILES,
                commit.map { it.toString() })

        if (time != 0L) {
            dependentFields[LogField.TIME] = time
        }

        val fields = dependentFields + mapOf(
                LogField.STATE_BEFORE to stateBefore,
                LogField.STATE_AFTER to stateAfter,
                LogField.REPOSITORY to repository
        )

        return LogEvent(timestamp, recorderID, recorderVersion, userID, sessionID.toString(), action, bucket, fields)
    }
}