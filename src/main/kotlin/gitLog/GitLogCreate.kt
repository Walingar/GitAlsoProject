package gitLog

import com.intellij.openapi.project.Project
import commitInfo.Commit
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit


private fun executeCommand(dir: File, command: List<String>): String? {
    try {
        val file = createTempFile()
        val fileError = createTempFile()
        val proc = ProcessBuilder(command)
                .directory(dir)
                .redirectOutput(file)
                .redirectError(fileError)
                .start()

        proc.waitFor(5, TimeUnit.MINUTES)

        val log = file.readText()
        fileError.readText()
        file.delete()
        fileError.delete()

        return log
    } catch (e: IOException) {
        System.err.print("ERROR: something went wrong when trying to get $command from: ${dir}")
        e.printStackTrace()
        return null
    }
}

fun createGitLogSinceCommit(dir: File, commit: Commit): String? {
    val command = arrayListOf(
            "git",
            "-c",
            "diff.renameLimit=99999",
            "log",
            "--since=${commit.time}",
            "--reverse",
            "-50000",
            "--name-status",
            "-C",
            "--pretty=format:%at %an")
    return executeCommand(dir, command)
}

fun createGitLog(dir: File): String? {
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
    return executeCommand(dir, command)
}