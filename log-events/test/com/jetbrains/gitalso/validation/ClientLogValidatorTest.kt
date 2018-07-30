package com.jetbrains.gitalso.validation

import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factor
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.log.LogField
import junit.framework.TestCase.*
import org.junit.Test

class ClientLogValidatorTest {

    @Test
    fun testValidateFromEvent() {
        val event = LogEvent(
                System.currentTimeMillis() / 1000,
                "gitalso",
                "1.0",
                "testID",
                "1",
                Action.COMMIT,
                "1",
                mapOf(
                        LogField.FACTORS to mapOf("123_111" to mapOf(Factor.SCORES to arrayOf(1.0, 1.1, 1.2))),
                        LogField.REPOSITORY to "1"
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
                "INVALID_ACTION_TEST\t" +
                "1\t" +
                "{\"FACTORS\":{\"123_111\":{\"SCORES\":[1.00,1.10,1.20]}},\"REPOSITORY\":\"1\"}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidJSONField() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "COMMIT\t" +
                "1\t" +
                "{\"FACTORS\":{\"123_111\":{\"SCORES\":[1.00,1.10,1.20]}},\"REPOSITORY_INVALID\":\"1\"}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidJSONFactors() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "COMMIT\t" +
                "1\t" +
                "{\"FACTORS\":{\"123_111\":{\"SCORES_INVALID\":[1.00,1.10,1.20]}},\"REPOSITORY\":\"1\"}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidJSONFactorTypeList() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "COMMIT\t" +
                "1\t" +
                "{\"FACTORS\":{\"123_111\":{\"SCORES\":[\"A\",1.10,1.20]}},\"REPOSITORY\":\"1\"}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidJSONFactorTypeSimple() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "COMMIT\t" +
                "1\t" +
                "{\"FACTORS\":{\"123_111\":{\"SCORES\":\"A\"}},\"REPOSITORY\":\"1\"}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateValidString() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "COMMIT\t" +
                "1\t" +
                "{\"FACTORS\":{\"123_111\":{\"SCORES\":[1.11,1.10,1.20]}},\"REPOSITORY\":\"1\"}"

        assertNotNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidEmptyKey() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "COMMIT\t" +
                "1\t" +
                "{\"FACTORS\":}"

        assertNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateValidMapScoresKey() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "COMMIT\t" +
                "1\t" +
                "{\"FACTORS\":{\"123_111\":{\"SCORES\":{\"A\":5.5}}},\"REPOSITORY\":\"1\"}"
        assertNotNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testValidateInvalidMapScoresKey() {
        val eventJson = "1532074221\t" +
                "gitalso\t" +
                "1.0\t" +
                "testID\t" +
                "1\t" +
                "COMMIT\t" +
                "1\t" +
                "{\"FACTORS\":{\"123_111\":{\"SCORES\":{\"A\":\"5.5\"}}},\"REPOSITORY\":\"1\"}"
        assertNull(ClientLogValidator.validate(eventJson))
    }
}