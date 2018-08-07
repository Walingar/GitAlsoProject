package com.jetbrains.gitalso.log

enum class LogField(val internalType: Class<*>) {
    FACTORS(Map::class.java),
    REPOSITORY(String::class.java),
    FILES(List::class.java),
    PREDICTION_MODIFIED(List::class.java),
    PREDICTION_UNMODIFIED(List::class.java),
    STATE_BEFORE(String::class.java),
    STATE_AFTER(String::class.java),
    TIME(Number::class.java)
}