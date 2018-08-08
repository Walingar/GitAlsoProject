package storage.index

import commitInfo.Commit
import gitLog.createGitLog
import gitLog.createGitLogSinceCommit
import gitLog.getCommitsFromGitLog
import repository.GitAlsoService
import java.io.File

class IndexFileManager(repositoryName: String) {

    private val filePathProvider = IndexFilePathProvider(repositoryName)
    private val basePath = File("data/repository/$repositoryName")

    private fun fullGitLog(service: GitAlsoService) {
        val log = createGitLog(basePath)
        if (log != null) {
            getCommitsFromGitLog(log, service)
        }
    }

    private fun createCommitsDataIndex(service: GitAlsoService): String {
        val index = StringBuffer()
        for ((_, commit) in service.commits) {
            index.append("${commit.time} ${commit.author}${System.lineSeparator()}")
        }

        return index.toString()
    }

    private fun createFilesIndex(service: GitAlsoService): String {
        val index = StringBuilder()
        for ((_, file) in service.files) {
            index.append("$file ")
            for (name in file.names) {
                index.append("$name ")
            }
            index.append(System.lineSeparator())
        }

        return index.toString()
    }

    private fun createCommitsIndex(service: GitAlsoService): String {
        val index = StringBuffer()

        for ((_, commit) in service.commits) {
            index.append("${commit.time} ")
            for (file in commit.files) {
                index.append("$file ")
            }
            index.append(System.lineSeparator())
        }

        return index.toString()
    }

    private fun parseCommitsDataIndex(index: String): Map<Long, Commit> {
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

    private fun parseFilesIndex(index: String): Map<Int, String> {
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

    private fun parseCommitsIndex(service: GitAlsoService, index: String, commits: Map<Long, Commit>, idToFileName: Map<Int, String>) {
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

    fun read(service: GitAlsoService) {
        val commits = parseCommitsDataIndex(filePathProvider.commitsDataIndexFile.readText())

        if (commits.isEmpty()) {
            fullGitLog(service)
        } else {
            val idToFileName = parseFilesIndex(filePathProvider.filesIndexFile.readText())
            parseCommitsIndex(service, filePathProvider.commitsIndexFile.readText(), commits, idToFileName)
        }


        val lastCommit = service.lastCommit
        if (lastCommit != null) {
            val log = createGitLogSinceCommit(basePath, lastCommit)
            getCommitsFromGitLog(log!!, service)
        } else {
            fullGitLog(service)
        }

        write(service)
    }

    fun write(service: GitAlsoService) {
        filePathProvider.commitsIndexFile.writeText(createCommitsIndex(service))

        filePathProvider.filesIndexFile.writeText(createFilesIndex(service))

        filePathProvider.commitsDataIndexFile.writeText(createCommitsDataIndex(service))
    }

}