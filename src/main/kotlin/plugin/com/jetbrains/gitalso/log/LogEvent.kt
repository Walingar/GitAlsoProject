package com.jetbrains.gitalso.log

import com.jetbrains.gitalso.json.GitAlsoJsonSerializer

class LogEvent(
        val timestamp: Long,
        val recorderID: String,
        val recorderVersion: String,
        val userID: String,
        val sessionID: String,
        val bucket: String,
        var action: Action,
        val fields: Map<LogField, Any>) {


    override fun toString() = "$timestamp\t" +
            "$recorderID\t" +
            "$recorderVersion\t" +
            "$userID\t" +
            "$sessionID\t" +
            "$bucket\t" +
            "$action\t" +
            GitAlsoJsonSerializer.toJson(fields)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LogEvent

        if (timestamp != other.timestamp) return false
        if (recorderID != other.recorderID) return false
        if (recorderVersion != other.recorderVersion) return false
        if (userID != other.userID) return false
        if (sessionID != other.sessionID) return false
        if (action != other.action) return false
        if (bucket != other.bucket) return false
        if (fields.size != other.fields.size) return false

        for ((key, _) in fields) {
            if (key !in other.fields) {
                return false
            }
        }

        return true
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + recorderID.hashCode()
        result = 31 * result + recorderVersion.hashCode()
        result = 31 * result + userID.hashCode()
        result = 31 * result + sessionID.hashCode()
        result = 31 * result + action.hashCode()
        result = 31 * result + bucket.hashCode()
        result = 31 * result + fields.size
        return result
    }
}