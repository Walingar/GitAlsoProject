package storage.log

import storage.FilePathProvider
import java.io.File

class LogFilePathProvider : FilePathProvider {
    override fun cleanupOldFiles() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDataDirectory(): File {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDataFiles(): List<File> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}