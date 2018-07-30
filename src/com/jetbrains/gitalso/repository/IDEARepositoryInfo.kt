package com.jetbrains.gitalso.repository

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.vcs.log.data.VcsLogStructureFilterImpl
import com.intellij.vcs.log.impl.VcsProjectLog
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*
import kotlin.math.min

class IDEARepositoryInfo(private val project: Project) : RepositoryInfo {
    private val dataManager = VcsProjectLog.getInstance(project).dataManager
    private val predictable: Boolean
    private val commits = HashSet<Int>()
    private val files = HashMap<FilePath, CommittedFile>()


    init {
        predictable = dataManager != null && dataManager.dataPack.isFull
    }

    private val dataGetter by lazy {
        VcsProjectLog.getInstance(project).dataManager!!.index.dataGetter!!
    }

    private fun getCommitHashesWithFile(file: FilePath): Collection<Int> {
        val structureFilter = VcsLogStructureFilterImpl(setOf(file))
        val containedInBranchCondition = dataManager!!.containingBranchesGetter.getContainedInBranchCondition("master", VcsUtil.getVcsRootFor(project, file)!!)
        // solution with distinct is not good, because commits can have common author time
        // TODO: find how to filter commits by structure without repetitions
        return dataGetter.filter(listOf(structureFilter)).filter { containedInBranchCondition.value(dataManager.getCommitId(it)) }.distinctBy { dataGetter.getAuthorTime(it) }
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

    override fun getCommit(root: FilePath, files: Collection<FilePath>): Commit? {
        if (
                files.isEmpty() ||
                !predictable ||
                root.virtualFile == null ||
                !dataManager!!.index.isIndexed(root.virtualFile!!)) {
            return null
        }

        val commit = Commit(project, -1)

        for (file in files) {
            commit.addFile(createCommittedFile(file))
        }

        return commit
    }

    override fun toString(): String {
        val digest = MessageDigest.getInstance("SHA-256")
        if (project.basePath == null) {
            return "null"
        }
        val hash = digest.digest(project.basePath!!.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(hash)
    }

}