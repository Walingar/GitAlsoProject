package com.jetbrains.gitalso.repository

import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.data.VcsLogData
import com.intellij.vcs.log.data.VcsLogStructureFilterImpl
import com.intellij.vcs.log.data.index.IndexDataGetter
import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.storage.log.hash.HashProvider
import java.util.*

class IDEARepositoryInfo(private val root: VirtualFile, private val dataManager: VcsLogData, private val dataGetter: IndexDataGetter) {
    private val commits = HashSet<Int>()
    private val files = HashMap<FilePath, CommittedFile>()

    private val containedInBranchConditionMaster by lazy {
        val refs = dataManager.dataPack.refsModel
        val branchRef = refs.branches.find { vcsRef ->
            vcsRef.root == root && vcsRef.name == "master"
        }
        if (branchRef == null) {
            null
        } else {
            dataManager.dataPack.permanentGraph
                    .getContainedInBranchCondition(listOf(
                            dataManager.getCommitIndex(branchRef.commitHash, branchRef.root)
                    ))
        }
    }

    val author = dataManager.currentUser[root]

    private fun getCommitHashesWithFile(file: FilePath): Collection<Int> {
        val structureFilter = VcsLogStructureFilterImpl(setOf(file))
        val fileCommits = dataGetter.filter(listOf(structureFilter))
        return fileCommits.filter { containedInBranchConditionMaster?.value(it) ?: false }
    }

    private fun getCommittedFile(file: FilePath) = if (file in files) {
        files[file]!!
    } else {
        val committedFile = CommittedFile(file)
        files[file] = committedFile
        committedFile
    }

    private fun createCommittedFile(file: FilePath): CommittedFile {
        val commitHashes = getCommitHashesWithFile(file)

        for (commitID in commitHashes) {
            if (commitID in commits) {
                continue
            }
            commits.add(commitID)

            val commit = Commit(
                    commitID,
                    dataGetter.getAuthorTime(commitID) ?: 0,
                    dataGetter.getAuthor(commitID)?.name ?: ""
            )
            for (commitFile in dataGetter.getChangedPaths(commitID)) {
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

    override fun toString() = HashProvider.hash(root.path).toString()
}