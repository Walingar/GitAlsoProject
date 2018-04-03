package gitLog

import com.intellij.openapi.project.Project
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


private fun executeCommand(from: File, command: String): String? {
    try {
        val file = File("log")
        val proc = ProcessBuilder(*command.split(" ").toTypedArray())
                .directory(from)
                .redirectErrorStream(true)
                .redirectOutput(file)
                .start()

        proc.waitFor(5, TimeUnit.MINUTES)
        val log = file.readText()
        return log
    } catch (e: IOException) {
        System.err.print("ERROR: something went wrong when trying to get $command from: $from")
        e.printStackTrace()
        return null
    }
}

fun createSimpleGitLog(repository: File): String? {
    return executeCommand(repository, "git log")
}

fun createGitLogWithTimestampsAndFiles(repository: File, project: Project): String? {
    return executeCommand(repository, "git log --name-status -C --pretty=format:%at")
}