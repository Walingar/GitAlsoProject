package com.jetbrains.gitalso.log

enum class Factor(val internalType: Class<*>) {
    SCORES(Number::class.java) // TODO: come up with factors
}