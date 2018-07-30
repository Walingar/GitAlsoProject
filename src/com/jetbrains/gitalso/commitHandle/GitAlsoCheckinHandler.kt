package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.impl.FileStatusManagerImpl
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PairConsumer
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.gitalso.commitHandle.ui.GitAlsoDialog
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.predict.PredictionResult
import com.jetbrains.gitalso.predict.WeightWithFilterTunedPredictionProvider
import com.jetbrains.gitalso.repository.IDEARepositoryInfo
import com.jetbrains.gitalso.storage.log.LogFileManager


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


    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        if (root == null) {
            return ReturnResult.COMMIT
        }

        val logManager = LogFileManager()
        val repository = IDEARepositoryInfo(project)
        val filesFromRoot = files().filter { getRoot(it) == root }
        val commit = repository.getCommit(getFilePath(root), filesFromRoot)

        if (commit == null) {
            logManager.log(PredictionResult(mapOf(), ArrayList()).getLogEvent(repository.toString(), Action.NOT_INDEXED))
            return ReturnResult.COMMIT
        }

        val result = WeightWithFilterTunedPredictionProvider()
                .commitPredict(commit)

        val files = result.prediction.mapNotNull { it.path.virtualFile }

        if (files.isEmpty()) {
            logManager.log(result.getLogEvent(repository.toString(), Action.NOT_WATCHED))
            return ReturnResult.COMMIT
        }

        val modifiedFiles = files.filter { isModified(it) }.toSet()
        val unmodifiedFiles = files.filter { !isModified(it) }.toSet()

        val dialog = GitAlsoDialog(project, modifiedFiles, unmodifiedFiles)
        dialog.show()

        return if (dialog.exitCode == 1) {
            logManager.log(result.getLogEvent(repository.toString(), Action.CANCEL))
            ReturnResult.CANCEL
        } else {
            logManager.log(result.getLogEvent(repository.toString(), Action.COMMIT))
            return ReturnResult.COMMIT
        }

    }
}