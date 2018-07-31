package com.jetbrains.gitalso.log

enum class Factor(val internalType: Class<*>) {
    COMMITS(Number::class.java),
    SCORES(Number::class.java)
}