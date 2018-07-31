package com.jetbrains.gitalso.predict

import com.intellij.openapi.application.PermanentInstallationID
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factor
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.log.LogField
import com.jetbrains.gitalso.log.State

data class PredictionResult(val scores: Map<Pair<CommittedFile, CommittedFile>, Number>, val prediction: Collection<CommittedFile>) {
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

    fun getLogEvent(
            stateBefore: State,
            stateAfter: State,
            action: Action,
            commits: Map<Pair<CommittedFile, CommittedFile>, Set<Long>>
    ): LogEvent {
        val factors = HashMap<String, Map<Factor, Any>>()
        for ((key, value) in scores) {
            if (key !in commits) {
                continue
            }
            factors[key.toString()] = mapOf(
                    Factor.SCORES to value,
                    Factor.COMMITS to commits[key]!!)
        }

        val fields = mapOf(
                LogField.STATE_BEFORE to stateBefore,
                LogField.STATE_AFTER to stateAfter,
                LogField.REPOSITORY to repository,
                LogField.FACTORS to factors
        )

        return LogEvent(timestamp, recorderID, recorderVersion, userID, sessionID.toString(), action, bucket, fields)
    }
}