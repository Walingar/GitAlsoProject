package gitLog

import com.intellij.openapi.project.Project
import com.intellij.testFramework.TempFiles
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit


private fun executeCommand(project: Project, command: String): String? {
    try {
        val file = createTempFile()
        val proc = ProcessBuilder(*command.split(" ").toTypedArray())
                .directory(File(project.basePath))
                .redirectErrorStream(true)
                .redirectOutput(file)
                .start()

        proc.waitFor(5, TimeUnit.MINUTES)
        val log = file.readText()
        file.delete()
        return log
    } catch (e: IOException) {
        System.err.print("ERROR: something went wrong when trying to get $command from: ${File(project.basePath)}")
        e.printStackTrace()
        return null
    }
}

fun createSimpleGitLog(project: Project): String? {
    return executeCommand(project, "git log")
}

fun createGitLogWithTimestampsAndFiles(project: Project): String? {
    return executeCommand(project, "git log --name-status -C --pretty=format:%at")
}