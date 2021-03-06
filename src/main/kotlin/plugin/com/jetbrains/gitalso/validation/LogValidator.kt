package com.jetbrains.gitalso.validation

import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.jetbrains.gitalso.json.GitAlsoJsonSerializer
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factor
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.log.LogField

abstract class LogValidator {
    private val factorsType = object : TypeToken<Map<String, Any>>() {}.type

    private fun getFactorsFromJson(json: String) = GitAlsoJsonSerializer.fromJsonToType<Map<String, Any>>(json, factorsType)


    private fun checkFactorValueType(value: Any?, type: Class<*>) =
            when (value) {
                null -> false
                is List<*> -> value.all {
                    it != null && type.isAssignableFrom(it::class.java)
                }
                is Map<*, *> -> value.all { (_, value) ->
                    value != null && type.isAssignableFrom(value::class.java)
                }
                else -> type.isAssignableFrom(value::class.java)
            }

    private fun isFactorsValuesValid(map: Map<*, *>) =
            map.all { (key, value) ->
                when (key) {
                    is String -> key in Factor.values().map { it.toString() } &&
                            checkFactorValueType(value, Factor.valueOf(key).internalType)
                    else -> false
                }
            }

    private fun isFactorsKeyValid(key: String): Boolean {
        if (!key.startsWith('(') || !key.endsWith(')')) {
            return false
        }

        val splitKey = key.substring(1 until key.length - 1).split(',')
        if (splitKey.size != 2) {
            return false
        }
        return try {
            splitKey[0].trim().toLong()
            splitKey[1].trim().toLong()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isFactorsValid(map: Map<*, *>) =
            map.all { (key, value) ->
                when (key) {
                    is String -> isFactorsKeyValid(key) && value is Map<*, *> && isFactorsValuesValid(value)
                    else -> false
                }
            }

    private fun isJsonField(key: String): Boolean {
        return key in LogField.values().map { it.toString() }
    }

    private fun isJsonFieldValid(key: String, value: Any): Boolean {
        if (!isJsonField(key)) {
            return false
        }
        val field = LogField.valueOf(key)
        return field.internalType.isAssignableFrom(value::class.java) &&
                if (field == LogField.FACTORS) {
                    value as Map<*, *>
                    isFactorsValid(value)
                } else {
                    true
                }
    }

    private fun isJsonValid(json: Map<String, Any>) = json.all { (key, value) -> isJsonFieldValid(key, value) }

    fun validate(line: String): LogEvent? {
        val elements = line.split("\t")
        if (elements.size != 8) {
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

        val bucket = elements[5]

        val action = try {
            Action.valueOf(elements[6])
        } catch (e: IllegalArgumentException) {
            null
        } ?: return null

        val factorsString = elements.subList(7, elements.size).joinToString("\t")

        val factors = try {
            getFactorsFromJson(factorsString)
        } catch (e: JsonSyntaxException) {
            null
        } ?: return null

        if (!isJsonValid(factors)) {
            return null
        }
        val validFactors = factors
                .map { (key, value) -> Pair(LogField.valueOf(key), value) }
                .toMap()

        return LogEvent(timestamp, recorderID, recorderVersion, userID, sessionID, bucket, action, validFactors)
    }
}