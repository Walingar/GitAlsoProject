package com.jetbrains.gitalso.repository

import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.data.VcsLogStructureFilterImpl
import com.intellij.vcs.log.data.index.IndexDataGetter
import com.jetbrains.gitalso.commit.info.Commit
import java.util.*

class IDEARepositoryInfo(private val root: VirtualFile, private val dataGetter: IndexDataGetter) {
    private fun getCommitHashesWithFile(file: FilePath): Collection<Int> {
        val structureFilter = VcsLogStructureFilterImpl(setOf(file))
        return dataGetter.filter(listOf(structureFilter))
    }

    private fun createCommittedFile(file: FilePath): Set<Commit> {
        val commitsSet = HashSet<Commit>()

        for (commitId in getCommitHashesWithFile(file)) {
            commitsSet.add(Commit(
                    commitId,
                    dataGetter.getCommitTime(commitId) ?: 0,
                    dataGetter.getChangedPaths(commitId)
            ))
        }

        return commitsSet
    }

    fun getCommit(files: Collection<FilePath>): Map<FilePath, Set<Commit>> {
        val currentCommit = HashMap<FilePath, Set<Commit>>()
        for (file in files) {
            currentCommit[file] = createCommittedFile(file)
        }
        return currentCommit
    }

    override fun toString() = root.path
}