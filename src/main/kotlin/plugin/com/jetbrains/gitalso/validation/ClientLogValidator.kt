package com.jetbrains.gitalso.validation

import com.jetbrains.gitalso.log.LogEvent

object ClientLogValidator : LogValidator() {
    fun validate(event: LogEvent, eventString: String): Boolean {
        val eventFromString = validate(eventString) ?: return false
        return eventFromString == event
    }
}