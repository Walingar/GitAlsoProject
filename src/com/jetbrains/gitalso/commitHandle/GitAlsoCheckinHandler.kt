package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.util.PairConsumer
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.gitalso.predict.WeightWithFilterTunedPredictionProvider
import com.jetbrains.gitalso.repository.IDEARepositoryInfo

class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val project: Project = panel.project

    private val title = "GitAlso plugin"

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        val commit = IDEARepositoryInfo(project)
                .getCommit(getFilePath(project.baseDir.path), getFiles()) ?: return ReturnResult.COMMIT

        val prediction = WeightWithFilterTunedPredictionProvider().commitPredict(commit)
        return if (Messages.showDialog(project,
                        String.format("test: %n%s",
                                prediction.joinToString(System.lineSeparator(), transform = { file -> "file: $file" })),
                        "Files to be committed",
                        arrayOf("Commit", "Cancel"),
                        1,
                        Messages.getInformationIcon()) == 0) {
            ReturnResult.COMMIT
        } else {
            ReturnResult.CANCEL
        }
    }

    private fun getFilePath(file: String) = VcsUtil.getFilePath(file)

    private fun getFiles(): List<FilePath> {
        return panel.files.map { file -> getFilePath(file.absolutePath) }
    }
}