package storage.index

import com.intellij.openapi.application.PathManager
import storage.FilePathProvider
import java.io.File
import com.intellij.openapi.project.Project
import java.io.FileFilter

class IndexFilePathProvider(project: Project) : FilePathProvider {
    private val projectHash = project.locationHash
    private val indexDirectory = File(PathManager.getSystemPath()).resolve(File("git-also/index/$projectHash"))

    private val commitsIndexFileName = "commitsIndex.ga"
    private val filesIndexFileName = "filesIndex.ga"
    private val commitsDataIndexFileName = "commitsDataIndex.ga"

    private fun getFile(dir: File, fileName: String): File {
        val file = dir.resolve(File(fileName))
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    val commitsIndexFile
        get() = getFile(getDataDirectory(), commitsIndexFileName)

    val filesIndexFile
        get() = getFile(getDataDirectory(), filesIndexFileName)

    val commitsDataIndexFile
        get() = getFile(getDataDirectory(), commitsDataIndexFileName)

    override fun getDataDirectory(): File {
        if (!indexDirectory.exists()) {
            indexDirectory.mkdirs()
        }
        return indexDirectory
    }

    override fun cleanupOldFiles() {
        // do nothing because it's indices
    }

    override fun getDataFiles(): List<File> {
        val directory = getDataDirectory()

        return directory.listFiles(FileFilter { it.isFile }).sortedBy { it.name }
    }
}