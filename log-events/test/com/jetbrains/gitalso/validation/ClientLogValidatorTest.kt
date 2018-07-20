package com.jetbrains.gitalso.validation

import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factors
import com.jetbrains.gitalso.log.LogEvent
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ClientLogValidatorTest {

    @Test
    fun testValidateValid() {
        val event = LogEvent(
                System.currentTimeMillis() / 1000,
                "gitalso",
                "1.0",
                "testID",
                "1",
                "1",
                Action.COMMIT,
                mapOf(
                        "101_123" to mapOf(
                                Factors.SCORES to listOf(1.0, 1.2, 1.2)
                        )
                )
        )
        print(event)

        assertTrue(ClientLogValidator.validate(event, event.toString()))
    }

    @Test
    fun testValidateInvalidTimestamp() {
        val eventJson = "1532074221.1\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "1\t" +
                "COMMIT\t" +
                "{\"101_123\":{\"SCORES\":[1.00,1.20,1.20]}}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidAction() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "1\t" +
                "INVALID_ACTION_TEST\t" +
                "{\"101_123\":{\"SCORES\":[1.00,1.20,1.20]}}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidJSONFactor() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "1\t" +
                "COMMIT\t" +
                "{\"101_123\":{\"NOT_FACTOR\":[1.00,1.20,1.20]}}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidJSONFactorTypeList() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "1\t" +
                "COMMIT\t" +
                "{\"101_123\":{\"SCORES\":[\"A\",1.20,1.20]}}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidJSONFactorTypeSimple() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "1\t" +
                "COMMIT\t" +
                "{\"101_123\":{\"SCORES\":\"A\"}}"

        assertNull(ClientLogValidator.validate(eventJson))
    }


}