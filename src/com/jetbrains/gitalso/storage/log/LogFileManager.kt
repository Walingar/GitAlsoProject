package com.jetbrains.gitalso.storage.log

import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.validation.ClientLogValidator

class LogFileManager {
    private val logFileProvider = LogFilePathProvider()
    fun log(event: LogEvent) {
        logFileProvider.cleanupOldFiles()
        val file = logFileProvider.newLogFile()
        val logString = event.toString()
        if (!ClientLogValidator.validate(event, logString)) {
            file.writeText("INVALID$logString")
            return
        }
        file.writeText(event.toString())
    }
}