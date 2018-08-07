package com.jetbrains.gitalso.log

enum class Factor(val internalType: Class<*>) {
    COMMITS(Number::class.java),
    COMMITS_SAME_AUTHOR(Number::class.java),
    SCORES(Number::class.java)
}