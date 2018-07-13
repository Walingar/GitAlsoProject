package com.jetbrains.gitalso.storage.log

import com.google.gson.reflect.TypeToken
import com.jetbrains.gitalso.json.GitAlsoJsonSerializer

class LogEvent(
        val userID: String,
        val sessionID: String,
        val repositoryID: String,
        val timestamp: Long,
        var action: Action,
        val factors: Map<String, Map<String, Number>>
) {
    override fun toString(): String {
        return "$userID\t" +
                "$sessionID\t" +
                "$repositoryID\t" +
                "$timestamp\t" +
                "$action\t" +
                GitAlsoJsonSerializer.toJson(factors)
    }

    companion object {
        private val factorsType = object : TypeToken<Map<String, Map<String, Number>>>() {}.type

        private fun getFactorsFromJson(json: String) = GitAlsoJsonSerializer.fromJsonToType<Map<String, Map<String, Number>>>(json, factorsType)

        fun fromString(eventString: String): LogEvent {
            val elements = eventString.split("\t")
            val userID = elements[0]
            val sessionID = elements[1]
            val repositoryID = elements[2]
            val timestamp = elements[3].toLong()
            val action = Action.valueOf(elements[4])
            val factorsString = elements.subList(5, elements.size).joinToString("\t")

            val factorsSet = getFactorsFromJson(factorsString)

            return LogEvent(userID, sessionID, repositoryID, timestamp, action, factorsSet)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is LogEvent) {
            if (
                    this.userID != other.userID ||
                    this.sessionID != other.sessionID ||
                    this.repositoryID != other.repositoryID ||
                    this.timestamp != other.timestamp ||
                    this.action != other.action
            )
                return false
            return true
        }
        return false
    }

    // generated hashCode
    override fun hashCode(): Int {
        var result = userID.hashCode()
        result = 31 * result + sessionID.hashCode()
        result = 31 * result + repositoryID.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + action.hashCode()
        return result
    }
}