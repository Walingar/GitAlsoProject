package com.jetbrains.gitalso.repository

import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.data.VcsLogStructureFilterImpl
import com.intellij.vcs.log.data.index.IndexDataGetter
import com.jetbrains.gitalso.commit.info.Commit
import java.util.*

class FilesHistoryProvider(private val root: VirtualFile, private val dataGetter: IndexDataGetter) {
    private val commits = HashMap<Int, Commit>()

    private fun getCommitHashesWithFile(file: FilePath): Collection<Int> {
        val structureFilter = VcsLogStructureFilterImpl(setOf(file))
        return dataGetter.filter(listOf(structureFilter))
    }

    private fun getFileHistory(file: FilePath): Set<Commit> {
        val commitsSet = HashSet<Commit>()
        val commitHashes = getCommitHashesWithFile(file)

        for (commitId in commitHashes) {
            val commit = commits.getOrPut(commitId) {
                Commit(commitId, dataGetter.getCommitTime(commitId) ?: 0, dataGetter.getChangedPaths(commitId))
            }
            commitsSet.add(commit)
        }

        return commitsSet
    }

    fun getFilesHistory(files: Collection<FilePath>): Map<FilePath, Set<Commit>> {
        val currentCommit = HashMap<FilePath, Set<Commit>>()
        for (file in files) {
            currentCommit[file] = getFileHistory(file)
        }
        return currentCommit
    }

    override fun toString() = root.path
}