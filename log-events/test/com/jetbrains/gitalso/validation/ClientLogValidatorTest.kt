package com.jetbrains.gitalso.validation

import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.Factors
import com.jetbrains.gitalso.log.LogEvent
import junit.framework.Assert.assertTrue
import org.junit.Test

class ClientLogValidatorTest {

    @Test
    fun validate() {
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

        assertTrue(ClientLogValidator.validate(event, event.toString()))
    }
}