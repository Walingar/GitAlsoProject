package com.jetbrains.gitalso.log

import com.jetbrains.gitalso.json.GitAlsoJsonSerializer

class LogEvent(
        val timestamp: Long,
        val recorderId: String,
        val recorderVersion: String,
        val userID: String,
        val sessionID: String,
        val repositoryID: String,
        var action: Action,
        val factors: Map<String, Any>) {

    override fun toString() = "$timestamp\t" +
            "$recorderId\t" +
            "$recorderVersion\t" +
            "$userID\t" +
            "$sessionID\t" +
            "$repositoryID\t" +
            "$action\t" +
            GitAlsoJsonSerializer.toJson(factors)

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + recorderId.hashCode()
        result = 31 * result + recorderVersion.hashCode()
        result = 31 * result + userID.hashCode()
        result = 31 * result + sessionID.hashCode()
        result = 31 * result + repositoryID.hashCode()
        result = 31 * result + action.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LogEvent

        if (timestamp != other.timestamp) return false
        if (recorderId != other.recorderId) return false
        if (recorderVersion != other.recorderVersion) return false
        if (userID != other.userID) return false
        if (sessionID != other.sessionID) return false
        if (repositoryID != other.repositoryID) return false
        if (action != other.action) return false

        return true
    }
}