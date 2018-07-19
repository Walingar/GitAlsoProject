package com.jetbrains.gitalso.log

enum class Factors(val type: Class<Any>) {
    SCORES(ArrayList<Int>().javaClass) // TODO: come up with factors
}