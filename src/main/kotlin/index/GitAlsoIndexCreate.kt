package index

import GitAlsoService
import java.io.File

fun createCommitsDataIndex(service: GitAlsoService): String {
    val index = StringBuffer()
    for (commit in service.getCommits()) {
        index.append("${commit.time} ${commit.author}${System.lineSeparator()}")
    }

    return index.toString()
}

fun createFilesIndex(service: GitAlsoService): String {
    val index = StringBuffer()
    val mapIDtoFile = service.getIDTofile()
    val time = System.currentTimeMillis()
    for (id in 0 until service.getFileCount()) {
        index.append("$id ${mapIDtoFile[id]!!.getName(time)}${System.lineSeparator()}")
    }

    return index.toString()
}

fun createCommitsIndex(service: GitAlsoService): String {
    val index = StringBuffer()

    for (commit in service.getCommits()) {
        index.append("C ${commit.time}${System.lineSeparator()}")
        for (file in commit.getFiles()) {
            index.append("${file.id}${System.lineSeparator()}")
        }
    }

    return index.toString()
}

fun printIndex(directory: File, indexName: String, index: String) {
    directory.mkdirs()
    val file = directory.resolve(indexName)
    file.createNewFile()
    file.writeText(index)
}