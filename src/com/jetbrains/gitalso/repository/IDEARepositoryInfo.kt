package com.jetbrains.gitalso.repository

import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.data.VcsLogStructureFilterImpl
import com.intellij.vcs.log.data.index.IndexDataGetter
import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import java.util.*

class IDEARepositoryInfo(private val root: VirtualFile, private val dataGetter: IndexDataGetter) {
    private val commits = HashSet<Int>()
    private val files = HashMap<FilePath, CommittedFile>()

    private fun getCommitHashesWithFile(file: FilePath): Collection<Int> {
        val structureFilter = VcsLogStructureFilterImpl(setOf(file))
        return dataGetter.filter(listOf(structureFilter))
    }

    private fun getCommittedFile(file: FilePath) = files.getOrPut(file) { CommittedFile(file) }

    private fun createCommittedFile(file: FilePath): CommittedFile {
        val commitHashes = getCommitHashesWithFile(file)

        for (commitId in commitHashes) {
            if (commitId in commits) {
                continue
            }
            commits.add(commitId)

            val commit = Commit(commitId, dataGetter.getCommitTime(commitId) ?: 0)
            for (commitFile in dataGetter.getChangedPaths(commitId)) {
                getCommittedFile(commitFile).committed(commit)
            }
        }

        return getCommittedFile(file)
    }

    fun getCommit(files: Collection<FilePath>): Commit {
        val commit = Commit(-1)

        for (file in files) {
            commit.addFile(createCommittedFile(file))
        }

        return commit
    }

    override fun toString() = root.path
}