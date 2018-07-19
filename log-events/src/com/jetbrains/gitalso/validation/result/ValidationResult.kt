package com.jetbrains.gitalso.validation.result

interface ValidationResult {
    val validLines: MutableCollection<String>
    val errorLines: MutableCollection<String>
}