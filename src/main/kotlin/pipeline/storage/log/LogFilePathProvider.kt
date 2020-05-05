package storage.log

import predict.PredictionType
import storage.FilePathProvider
import java.io.File
import java.io.FileFilter

class LogFilePathProvider(repositoryName: String) : FilePathProvider {

    private val logDirectory = File("data/log/$repositoryName")

    private val logPrefix = "log"

    fun newLogFile(type: PredictionType): File {
        val file = getDataDirectory().resolve("${logPrefix}_$type")
        file.createNewFile()

        return file
    }

    override fun getDataFiles(): List<File> {
        val directory = getDataDirectory()

        return directory.listFiles(FileFilter { it.isFile }).sortedBy { it.name }
    }

    override fun getDataDirectory(): File {
        if (!logDirectory.exists()) {
            logDirectory.mkdirs()
        }

        return logDirectory
    }
}