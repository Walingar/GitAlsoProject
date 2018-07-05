package log.validate

import log.LogEvent

class LogValidator {
    companion object {
        fun validate(event: LogEvent, eventString: String): Boolean {
            val eventFromString = LogEvent.Companion.fromString(eventString)
            if (eventFromString == event && event.factors.size == eventFromString.factors.size) {
                for ((key, _) in event.factors) {
                    if (key !in eventFromString.factors) {
                        return false
                    }
                }
                return true
            }
            return false
        }
    }
}