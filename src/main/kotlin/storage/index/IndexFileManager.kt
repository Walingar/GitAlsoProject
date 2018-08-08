package storage.index

import commitInfo.Commit
import commitInfo.CommittedFile
import gitLog.createFullGitLog
import gitLog.createGitLogSinceCommit
import gitLog.getCommitsFromGitLog
import repository.GitAlsoService
import java.io.File

class IndexFileManager(repositoryName: String) {

    private val filePathProvider = IndexFilePathProvider(repositoryName)
    private val basePath = File("data/repository/$repositoryName")

    private fun fullGitLog(service: GitAlsoService) {
        val log = createFullGitLog(basePath)
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

    fun write(service: GitAlsoService) {
        filePathProvider.commitsIndexFile.writeText(createCommitsIndex(service))

        filePathProvider.filesIndexFile.writeText(createFilesIndex(service))

        filePathProvider.commitsDataIndexFile.writeText(createCommitsDataIndex(service))
    }

    private fun parseCommitsDataIndex(index: String): Map<Long, Commit> {
        val commits = HashMap<Long, Commit>()
        for (line in index.lines()) {
            if (line.isBlank()) {
                continue
            }
            val splitHeader = line.split("\\s".toRegex())
            val time = splitHeader[0].toLong()
            commits[time] = Commit(time)
        }
        return commits
    }

    private fun parseFilesIndex(index: String): Map<String, CommittedFile> {
        val files = HashMap<String, CommittedFile>()

        for (line in index.lines()) {
            if (line.isBlank()) {
                continue
            }
            val splitLine = line.split("\\s".toRegex())
            val currentFile = splitLine[0]
            val names = splitLine.subList(1, splitLine.size)
            files.putIfAbsent(currentFile, CommittedFile(currentFile))
            val currentCommittedFile = files[currentFile]!!

            for (name in names) {
                files.putIfAbsent(name, CommittedFile(name))
                val nameFile = files[name]!!
                currentCommittedFile.names.add(nameFile)
            }
        }

        return files
    }

    private fun parseCommitsIndex(service: GitAlsoService, index: String, commits: Map<Long, Commit>, files: Map<String, CommittedFile>) {
        for ((time, commit) in commits) {
            service.commits[time] = commit
        }

        for ((name, file) in files) {
            service.files[name] = file
        }

        for (line in index.lines()) {
            if (line.isBlank()) {
                continue
            }

            val splitLine = line.split("\\s".toRegex())
            val currentCommit = splitLine[0].toLong()
            val commitFiles = splitLine.subList(1, splitLine.size)
            val commit = commits[currentCommit]!!

            for (name in commitFiles) {
                val file = files[name]!!
                file.committed(commit)
            }
        }
    }

    fun read(service: GitAlsoService) {
        val commits = parseCommitsDataIndex(filePathProvider.commitsDataIndexFile.readText())
        val files = parseFilesIndex(filePathProvider.filesIndexFile.readText())
        parseCommitsIndex(service, filePathProvider.commitsIndexFile.readText(), commits, files)
    }

}