package index

import GitAlsoService
import commit.Commit
import java.io.File

fun parseCommitsDataIndex(index: String): Map<Long, Commit> {
    val commits = HashMap<Long, Commit>()
    for (line in index.lines()) {
        if (line.isBlank()) {
            continue
        }
        val splitHeader = line.split("\\s".toRegex())
        val time = splitHeader[0].toLong()
        val author = splitHeader.subList(1, splitHeader.size).joinToString(" ")
        commits[time] = Commit(time, author)
    }
    return commits
}

fun parseFilesIndex(index: String): Map<Int, String> {
    val idToFileName = HashMap<Int, String>()

    for (line in index.lines()) {
        if (line.isBlank()) {
            continue
        }
        val (id, name) = line.split("\\s".toRegex())
        idToFileName[id.toInt()] = name
    }

    return idToFileName
}

fun parseCommitsIndex(service: GitAlsoService, index: String, commits: Map<Long, Commit>, idToFileName: Map<Int, String>) {
    var commit = Commit(0, "Unknown")
    val files = ArrayList<Int>()
    for (line in index.lines()) {
        if (line.isBlank()) {
            continue
        }
        if (line[0] == 'C') {
            if (files.isNotEmpty()) {
                service.committedFromIndex(files, commit, idToFileName)
            }
            files.clear()
            val (_, idString) = line.split("\\s".toRegex())
            val id = idString.toLong()
            commit = commits[id]!!
        } else {
            files.add(line.toInt())
        }
    }

    if (files.isNotEmpty()) {
        service.committedFromIndex(files, commit, idToFileName)
    }

}

fun parseIndex(service: GitAlsoService, directory: File) {
    val commits = parseCommitsDataIndex(directory.resolve("commitsDataIndex.ga").readText())
    val idToFileName = parseFilesIndex(directory.resolve("filesIndex.ga").readText())
    parseCommitsIndex(service, directory.resolve("commitsIndex.ga").readText(), commits, idToFileName)
}