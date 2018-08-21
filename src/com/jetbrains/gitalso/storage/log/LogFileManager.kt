package com.jetbrains.gitalso.storage.log

import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.storage.log.send.LogSender
import com.jetbrains.gitalso.validation.ClientLogValidator
import java.io.File

class LogFileManager {
    private val logFileProvider = LogFilePathProvider()

    private fun sendFile(file: File) {
        if (LogSender.send(file.readText(), true)) {
            file.delete()
        }
    }

    fun sendFiles() {
        val files = logFileProvider.getDataFiles()
        for (file in files) {
            sendFile(file)
        }
    }

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