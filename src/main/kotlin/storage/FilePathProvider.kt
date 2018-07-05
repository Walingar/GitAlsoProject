package storage

import java.io.File

interface FilePathProvider {
    fun getDataFiles(): List<File>


    fun getDataDirectory(): File


    fun cleanupOldFiles()
}