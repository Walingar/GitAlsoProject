package com.jetbrains.gitalso.validation

import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factors
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.validation.result.ArrayListValidationResult
import org.junit.Test

import org.junit.Assert.*

class ServerLogValidatorTest {

    @Test
    fun testValidateValid() {
        val events = ArrayList<String>()
        for (time in 1..10) {
            for (action in Action.values()) {
                val eventJson = HashMap<String, Any>()
                for (file1 in 1..10) {
                    for (file2 in 1..10) {
                        eventJson["${file1}_$file2"] = hashMapOf(Factors.SCORES to arrayOf(1.1, 1.2, 1.3))
                    }
                }
                val event = LogEvent(
                        time.toLong(),
                        "gitalso",
                        "1.0",
                        "testID",
                        "1",
                        "1",
                        action,
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