package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.impl.FileStatusManagerImpl
import com.intellij.openapi.vcs.impl.VcsFileStatusProvider
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PairConsumer
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.gitalso.commitHandle.ui.GitAlsoDialog
import com.jetbrains.gitalso.predict.WeightWithFilterTunedPredictionProvider
import com.jetbrains.gitalso.repository.IDEARepositoryInfo


class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val project: Project = panel.project
    private val root = project.baseDir

    private fun files(): List<FilePath> {
        return panel.files.map { file -> getFilePath(file.absolutePath) }
    }

    private val fileStatusManager = FileStatusManagerImpl.getInstance(project)

    private fun isModified(file: VirtualFile) = fileStatusManager.getStatus(file) == FileStatus.MODIFIED

    private fun getFilePath(file: String) = VcsUtil.getFilePath(file)

    private fun getFilePath(file: VirtualFile) = VcsUtil.getFilePath(file)

    private fun getRoot(file: FilePath) = VcsUtil.getVcsRootFor(project, file)


    // TODO: add log create and send from PredictionResult
    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        if (root == null) {
            return ReturnResult.COMMIT
        }

        val repository = IDEARepositoryInfo(project)
        val filesFromRoot = files().filter { getRoot(it) == root }
        val commit = repository.getCommit(getFilePath(root), filesFromRoot) ?: return ReturnResult.COMMIT

        val files = WeightWithFilterTunedPredictionProvider()
                .commitPredict(commit).mapNotNull { it.path.virtualFile }

        if (files.isEmpty()) {
            return ReturnResult.COMMIT
        }

        val modifiedFiles = files.filter { isModified(it) }.toSet()
        val unmodifiedFiles = files.filter { !isModified(it) }.toSet()

        val dialog = GitAlsoDialog(project, modifiedFiles, unmodifiedFiles)
        dialog.show()

        return if (dialog.exitCode == 0) {
            ReturnResult.CANCEL
        } else {
            return ReturnResult.COMMIT
        }

    }
}