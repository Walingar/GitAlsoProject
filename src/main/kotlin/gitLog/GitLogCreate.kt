package gitLog

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


private fun executeCommand(from: File, command: String): String? {
    return try {
        val proc = ProcessBuilder(*command.split(" ").toTypedArray())
                .directory(from)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
        proc.waitFor(2, TimeUnit.MINUTES)
        proc.inputStream.bufferedReader().readText()
    } catch (e: IOException) {
        System.err.print("ERROR: something went wrong when trying to get $command from: $from")
        e.printStackTrace()
        null
    }
}

fun createSimpleGitLog(repository: File): String? {
    return executeCommand(repository, "git log")
}

fun createGitLogWithTimestampsAndFiles(repository: File): String? {
    return executeCommand(repository, "git log --name-status -C --pretty=format:%at")
}