package com.jetbrains.gitalso.validation.result

class ArrayListValidationResult : ValidationResult {
    override val validLines = ArrayList<String>()
    override val errorLines = ArrayList<String>()
}