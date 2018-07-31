package com.jetbrains.gitalso.log

enum class LogField(val internalType: Class<*>) {
    FACTORS(Map::class.java),
    REPOSITORY(String::class.java),
    STATE_BEFORE(String::class.java),
    STATE_AFTER(String::class.java)
}