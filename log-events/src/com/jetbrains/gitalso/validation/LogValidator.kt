package com.jetbrains.gitalso.validation

import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.jetbrains.gitalso.json.GitAlsoJsonSerializer
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factors
import com.jetbrains.gitalso.log.LogEvent

abstract class LogValidator {
    private val factorsType = object : TypeToken<Map<String, Any>>() {}.type

    private fun getFactorsFromJson(json: String) = GitAlsoJsonSerializer.fromJsonToType<Map<String, Any>>(json, factorsType)

    private fun checkFactorValueType(value: Any?, type: Class<Any>): Boolean {
        return when (value) {
            null -> false
            else -> type == value.javaClass
        }
    }

    private fun isFactorsValid(map: Map<*, *>): Boolean {
        return map.all { (key, value) ->
            when (key) {
                is Factors -> checkFactorValueType(value, key.type)
                else -> false
            }
        }
    }

    private fun isJsonValid(json: Map<String, Any>): Boolean {
        return json.all { (name, value) ->
            when (value) {
                is Map<*, *> -> isFactorsValid(value)
                is String -> name == "MESSAGE"
                else -> false
            }
        }
    }

    fun validate(line: String): LogEvent? {
        val elements = line.split("\t")
        if (elements.size < 8) {
            return null
        }

        val timestamp = try {
            elements[0].toLong()
        } catch (e: NumberFormatException) {
            null
        } ?: return null

        val recorderID = elements[1]
        val recorderVersion = elements[2]

        val userID = elements[3]
        val sessionID = elements[4]
        val repositoryID = elements[5]

        val action = try {
            Action.valueOf(elements[4])
        } catch (e: IllegalArgumentException) {
            null
        } ?: return null

        val factorsString = elements.subList(5, elements.size).joinToString("\t")

        val factors = try {
            getFactorsFromJson(factorsString)
        } catch (e: JsonSyntaxException) {
            null
        } ?: return null

        if (!isJsonValid(factors)) {
            return null
        }

        return LogEvent(timestamp, recorderID, recorderVersion, userID, sessionID, repositoryID, action, factors)
    }
}