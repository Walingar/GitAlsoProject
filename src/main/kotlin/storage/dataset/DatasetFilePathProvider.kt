package storage.dataset

import storage.FilePathProvider
import java.io.File
import java.io.FileFilter

class DatasetFilePathProvider(repositoryName: String) : FilePathProvider {
    private val datasetDirectory = File("data/dataset/$repositoryName")
    private val datasetPrefix = "dataset"

    fun getDatasetFile(type: DatasetType): File {
        val datasetFile = getDataDirectory().resolve("${datasetPrefix}_$type")
        if (!datasetFile.exists()) {
            datasetFile.createNewFile()
        }
        return datasetFile
    }

    override fun getDataFiles(): List<File> {
        val directory = getDataDirectory()

        return directory.listFiles(FileFilter { it.isFile }).sortedBy { it.name }
    }

    override fun getDataDirectory(): File {
        if (!datasetDirectory.exists()) {
            datasetDirectory.mkdirs()
        }
        return datasetDirectory
    }

}