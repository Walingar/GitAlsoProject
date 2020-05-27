package com.jetbrains.gitalso.storage.log.hash

import com.intellij.openapi.application.PathManager
import com.jetbrains.gitalso.storage.FilePathProvider
import java.io.File
import java.io.FileFilter

class SaltFilePathProvider : FilePathProvider {

    private val saltDirectory = File(PathManager.getSystemPath()).resolve(File("git-also/salt"))
    private val saltFileName = "salt"

    fun getSaltFile(): File {
        val directory = getDataDirectory()
        val saltFile = directory.resolve(saltFileName)

        if (!saltFile.exists()) {
            saltFile.createNewFile()
        }
        return saltFile
    }

    override fun getDataFiles(): List<File> {
        val directory = getDataDirectory()

        return directory.listFiles(FileFilter { it.isFile }).sortedBy { it.name }
    }

    override fun getDataDirectory(): File {
        if (!saltDirectory.exists()) {
            saltDirectory.mkdirs()
        }
        return saltDirectory
    }


    // it is useless there
    override fun cleanupOldFiles() {
    }

}