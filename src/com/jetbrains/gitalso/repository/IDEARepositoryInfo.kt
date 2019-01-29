package com.jetbrains.gitalso.repository

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.data.VcsLogStructureFilterImpl
import com.intellij.vcs.log.impl.VcsProjectLog
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.storage.log.hash.HashProvider
import java.util.*

class IDEARepositoryInfo(private val project: Project) {
    private val dataManager = VcsProjectLog.getInstance(project).dataManager
    private val commits = HashSet<Int>()
    private val files = HashMap<FilePath, CommittedFile>()
    private val root = VcsUtil.getFilePath(project.basePath).virtualFile

    private val containedInBranchConditionMaster by lazy {
        val refs = dataManager!!.dataPack.refsModel
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

    val author by lazy {
        if (root != null) {
            dataManager!!.currentUser[root]
        } else {
            null
        }
    }

    private val dataGetter by lazy {
        VcsProjectLog.getInstance(project).dataManager!!.index.dataGetter!!
    }

    private fun getCommitHashesWithFile(file: FilePath): Collection<Int> {
        val structureFilter = VcsLogStructureFilterImpl(setOf(file))
        val fileCommits = dataGetter.filter(listOf(structureFilter))
        return fileCommits.filter { containedInBranchConditionMaster?.value(it) ?: false }
    }

    private fun getCommittedFile(file: FilePath) = if (file in files) {
        files[file]!!
    } else {
        val committedFile = CommittedFile(project, file)
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

            val commit = Commit(project, commitID)
            for (commitFile in dataGetter.getChangedPaths(commitID)) {
                getCommittedFile(commitFile).committed(commit)
            }
        }

        return getCommittedFile(file)
    }

    fun getCommit(root: VirtualFile, files: Collection<FilePath>): Commit {
        val commit = Commit(project, -1)

        for (file in files) {
            commit.addFile(createCommittedFile(file))
        }

        return commit
    }

    override fun toString() = if (project.basePath == null) "null" else HashProvider.hash(project.basePath!!).toString()

}