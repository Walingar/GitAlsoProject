package log

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import log.validate.LogValidator
import java.io.File

class LogFileWriter(project: Project) {
    private val projectHash = project.locationHash
    private val logDirectory = File(PathManager.getLogPath()).resolve(File("git-also/$projectHash"))
    private val logFileName = "git-also-log.json"

    fun log(event: LogEvent) {
        // TODO: add cleaning (if log is large)

        logDirectory.mkdirs()
        val file = logDirectory.resolve(logFileName)
        if (!file.exists()) {
            file.createNewFile()
        }

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