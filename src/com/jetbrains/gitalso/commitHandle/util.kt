package com.jetbrains.gitalso.commitHandle

import java.util.*

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start