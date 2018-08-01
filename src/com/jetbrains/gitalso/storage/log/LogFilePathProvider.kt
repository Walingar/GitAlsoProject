package com.jetbrains.gitalso.storage.log

import com.intellij.openapi.application.PathManager
import com.jetbrains.gitalso.storage.FilePathProvider
import java.io.File
import java.io.FileFilter

class LogFilePathProvider : FilePathProvider {

    companion object {
        const val MAX_ALLOWED_SEND_SIZE = 2 * 1024 * 1024
    }

    private val logDirectory = File(PathManager.getSystemPath()).resolve(File("git-also/log"))

    private val logPrefix = "log"

    private fun getSuffix(name: String) = name.substringAfter('_').toLong()

    fun newLogFile(): File {
        val files = getDataFiles()
        val lastIndex =
                if (files.isEmpty())
                    0
                else
                    getSuffix(files.last().name)
        val newFile = logDirectory.resolve("${logPrefix}_${lastIndex + 1}")
        newFile.createNewFile()

        return newFile
    }

    override fun cleanupOldFiles() {
        var size = 0L
        val files = getDataFiles()
        files.forEach { size += it.length() }
        if (size >= MAX_ALLOWED_SEND_SIZE) {
            var current = 0
            while (size >= MAX_ALLOWED_SEND_SIZE) {
                size -= files[current].length()
                files[current].delete()
                current++
            }
        }
    }

    override fun getDataDirectory(): File {
        if (!logDirectory.exists()) {
            logDirectory.mkdirs()
        }
        return logDirectory
    }

    override fun getDataFiles(): List<File> {
        val directory = getDataDirectory()

        return directory.listFiles(FileFilter { it.isFile }).sortedBy { getSuffix(it.name) }
    }
}