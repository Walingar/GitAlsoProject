package com.jetbrains.gitalso.validation

import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factor
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.log.LogField
import junit.framework.Assert
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

    @Test
    fun testGeneratedLogCANCEL() {
        val eventJson = "1532944761029\tgit-also\t1\tf83ce224-0b90-4d92-9c6b-4015ed97e81d\t1\tCANCEL\t-1\t{\"REPOSITORY\":\"G2iYaqFXdwle9OSYmdGlNppH23rRo2uN/pSiSqRwRhk\\u003d\",\"FACTORS\":{\"(960044793, 845646880)\":{\"SCORES\":0.51},\"(1591886002, -174624003)\":{\"SCORES\":0.42},\"(960044793, -174624003)\":{\"SCORES\":0.51},\"(1591886002, 845646880)\":{\"SCORES\":0.43}}}"

        Assert.assertNotNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testGeneratedLogNotWatched() {
        val eventJson = "1532944722254\tgit-also\t1\tf83ce224-0b90-4d92-9c6b-4015ed97e81d\t1\tNOT_WATCHED\t-1\t{\"REPOSITORY\":\"G2iYaqFXdwle9OSYmdGlNppH23rRo2uN/pSiSqRwRhk\\u003d\",\"FACTORS\":{}}"

        Assert.assertNotNull(ClientLogValidator.validate(eventJson))
    }

    @Test
    fun testGeneratedLogNotIndexed() {
        val eventJson = "1532944643569\tgit-also\t1\tf83ce224-0b90-4d92-9c6b-4015ed97e81d\t1\tNOT_INDEXED\t-1\t{\"REPOSITORY\":\"G2iYaqFXdwle9OSYmdGlNppH23rRo2uN/pSiSqRwRhk\\u003d\",\"FACTORS\":{}}"

        Assert.assertNotNull(ClientLogValidator.validate(eventJson))
    }
}