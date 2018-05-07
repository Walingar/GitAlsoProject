package gitLog

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


private fun executeCommand(directory: File, command: List<String>): String? {
    try {
        val file = createTempFile()
        val fileError = createTempFile()
        val proc = ProcessBuilder(command)
                .directory(directory)
                .redirectOutput(file)
                .redirectError(fileError)
                .start()

        proc.waitFor(5, TimeUnit.MINUTES)

        val log = file.readText()
        val error = fileError.readText()
        file.delete()
        fileError.delete()

        return log
    } catch (e: IOException) {
        System.err.print("ERROR: something went wrong when trying to get $command from: $directory")
        e.printStackTrace()
        return null
    }
}

fun createSimpleGitLog(directory: File): String? {
    val command = arrayListOf(
            "git",
            "log")
    return executeCommand(directory, command)
}

fun createGitLogWithTimestampsAuthorsAndFiles(directory: File): String? {
    val command = arrayListOf(
            "git",
            "-c",
            "diff.renameLimit=99999",
            "log",
            "--reverse",
            "-50000",
            "--name-status",
            "-C",
            "--pretty=format:%at %an")
    return executeCommand(directory, command)
}