package com.jetbrains.gitalso.storage.log.hash

import com.intellij.openapi.application.PathManager
import java.io.File

class SaltFilePathProvider {

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

    private fun getDataDirectory(): File {
        if (!saltDirectory.exists()) {
            saltDirectory.mkdirs()
        }
        return saltDirectory
    }
}