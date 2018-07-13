package com.jetbrains.gitalso.storage.log

import com.jetbrains.gitalso.storage.log.validate.LogValidator

class LogFileManager {
    private val logFileProvider = LogFilePathProvider()
    fun log(event: LogEvent) {
        logFileProvider.cleanupOldFiles()

        val file = logFileProvider.newLogFile()
        val logString = event.toString()
        if (!LogValidator.validate(event, logString)) {
            file.appendText("ERROR. Event is not valid: $logString")
            file.appendText(System.lineSeparator())
            return
        }
        file.appendText(event.toString())
        file.appendText(System.lineSeparator())
    }
}