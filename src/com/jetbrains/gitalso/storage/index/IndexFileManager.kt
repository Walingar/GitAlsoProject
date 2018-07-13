package com.jetbrains.gitalso.storage.index

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.DumbModeTask
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.gitLog.createGitLog
import com.jetbrains.gitalso.gitLog.createGitLogSinceCommit
import com.jetbrains.gitalso.gitLog.getCommitsFromGitLog
import com.jetbrains.gitalso.repository.GitAlsoService
import java.io.File

class IndexFileManager(private val project: Project) {

    private val filePathProvider = IndexFilePathProvider(project)
    private val basePath = File(project.basePath)

    private fun fullGitLog(service: GitAlsoService) {
        val log = createGitLog(basePath)
        if (log != null) {
            getCommitsFromGitLog(log, service)
        }
    }

    private fun createCommitsDataIndex(service: GitAlsoService): String {
        val index = StringBuffer()
        for (commit in service.commits) {
            index.append("${commit.time} ${commit.author}${System.lineSeparator()}")
        }

        return index.toString()
    }

    private fun createFilesIndex(service: GitAlsoService): String {
        val index = StringBuffer()
        val mapIDtoFile = service.mapIDToFile
        val time = System.currentTimeMillis()
        for (id in 0 until service.getFileCount()) {
            index.append("$id ${mapIDtoFile[id]!!.getName(time)}${System.lineSeparator()}")
        }

        return index.toString()
    }

    private fun createCommitsIndex(service: GitAlsoService): String {
        val index = StringBuffer()

        for (commit in service.commits) {
            index.append("C ${commit.time}${System.lineSeparator()}")
            for (file in commit.getFiles()) {
                index.append("${file.id}${System.lineSeparator()}")
            }
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

        DumbService.getInstance(project).queueTask(object : DumbModeTask() {
            override fun performInDumbMode(progress: ProgressIndicator) {
                write(service)
            }
        })
    }

    fun write(service: GitAlsoService) {
        filePathProvider.commitsIndexFile.writeText(createCommitsIndex(service))

        filePathProvider.filesIndexFile.writeText(createFilesIndex(service))

        filePathProvider.commitsDataIndexFile.writeText(createCommitsDataIndex(service))
    }
}