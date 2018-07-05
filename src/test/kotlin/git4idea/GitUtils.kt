package git4idea

import java.io.File
import java.util.concurrent.TimeUnit

fun git(command: List<String>) {
    executeCommand(arrayListOf("git") + command)
}

fun createRepo() {
    git(arrayListOf("init"))
}

//fun addFileToRepo() {
//
//}

fun cd(dir: File) {
    if (dir.isDirectory) {
        executeCommand(arrayListOf(
                "cd",
                dir.absolutePath
        ))
    } else {
        throw IllegalArgumentException("Excepted directory")
    }
}

fun executeCommand(command: List<String>) {
    val file = createTempFile()
    val fileError = createTempFile()
    val proc = ProcessBuilder(command)
            .redirectOutput(file)
            .redirectError(fileError)
            .start()

    proc.waitFor(5, TimeUnit.MINUTES)
    val error = fileError.readText()
    if (!error.isBlank()) {
        println("ERROR. While executing task [$command]. $error")
        return
    }

    println("SUCCESS. While executing task [$command]. ${file.readText()}")
}