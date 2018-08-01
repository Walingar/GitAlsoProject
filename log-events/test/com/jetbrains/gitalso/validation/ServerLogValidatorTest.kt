package com.jetbrains.gitalso.validation

import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factor
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.log.LogField
import com.jetbrains.gitalso.validation.result.ArrayListValidationResult
import org.junit.Test
import com.jetbrains.gitalso.log.State

import org.junit.Assert.*

class ServerLogValidatorTest {

    @Test
    fun testValidateValid() {
        val events = ArrayList<String>()
        for (time in 1..10) {
            for (action in Action.values()) {
                val eventJson = HashMap<LogField, Any>()
                val logEvents = HashMap<String, Any>()
                for (file1 in 1..10) {
                    for (file2 in 1..10) {
                        logEvents["($file1, $file2)"] = mapOf(
                                Factor.SCORES to arrayOf(1.0, 1.1, 1.2),
                                Factor.COMMITS to (1..10).toList())
                    }
                }
                eventJson[LogField.FACTORS] = logEvents
                eventJson[LogField.REPOSITORY] = "1"
                val states = State.values().toList().shuffled()
                eventJson[LogField.STATE_BEFORE] = states[0]
                eventJson[LogField.STATE_AFTER] = states[1]
                eventJson[LogField.FILES] = (1..10).toList()
                eventJson[LogField.PREDICTION_MODIFIED] = (1..5).toList()
                eventJson[LogField.PREDICTION_UNMODIFIED] = (6..10).toList()
                val event = LogEvent(
                        time.toLong(),
                        "gitalso",
                        "1.0",
                        "testID",
                        "1",
                        action,
                        "1",
                        eventJson
                )
                events += event.toString()
            }
        }
        val result = ArrayListValidationResult()
        val validator = ServerLogValidator(result)
        validator.validate(events)
        events.forEach { event -> println(event) }

        assertEquals(result.validLines.size, 10 * Action.values().size)
        assertTrue(result.errorLines.isEmpty())
    }
}