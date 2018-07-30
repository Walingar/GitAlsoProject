package com.jetbrains.gitalso.log

enum class LogField(val internalType: Class<*>) {
    FACTORS(Map::class.java),
    REPOSITORY(String::class.java)
}