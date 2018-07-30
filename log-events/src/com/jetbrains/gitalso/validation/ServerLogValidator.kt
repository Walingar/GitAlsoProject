package com.jetbrains.gitalso.validation

import com.jetbrains.gitalso.validation.result.ValidationResult

class ServerLogValidator(val result: ValidationResult) : LogValidator() {
    fun validate(lines: Collection<String>) {
        lines.forEach {
            if (validate(it) != null) {
                result.validLines.add(it)
            } else {
                result.errorLines.add(it)
            }
        }
    }
}