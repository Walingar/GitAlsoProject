package com.jetbrains.gitalso.predict

import com.intellij.openapi.application.PermanentInstallationID
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factor
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.log.LogField

data class PredictionResult(val scores: Map<Pair<CommittedFile, CommittedFile>, Number>, val prediction: Collection<CommittedFile>) {
    fun getLogEvent(repository: String, action: Action): LogEvent {
        val timestamp = System.currentTimeMillis()
        val recorderID = "git-also"
        val recorderVersion = "1"
        val userID = PermanentInstallationID.get()
        val sessionID = "1"
        val bucket = "-1"
        val factors = HashMap<String, Map<Factor, Any>>()
        for ((key, value) in scores) {
            factors[key.toString()] = mapOf(Factor.SCORES to value)
        }
        val fields = mapOf(
                LogField.REPOSITORY to repository,
                LogField.FACTORS to factors
        )

        return LogEvent(timestamp, recorderID, recorderVersion, userID, sessionID, action, bucket, fields)
    }
}