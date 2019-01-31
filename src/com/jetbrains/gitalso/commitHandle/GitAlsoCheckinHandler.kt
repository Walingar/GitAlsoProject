package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.changes.ui.BooleanCommitOption
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.ui.RefreshableOnComponent
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PairConsumer
import com.intellij.vcs.log.data.VcsLogData
import com.intellij.vcs.log.data.index.IndexDataGetter
import com.intellij.vcs.log.util.VcsLogUtil.findBranch
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.gitalso.commitHandle.ui.GitAlsoDialog
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.plugin.UserStorage
import com.jetbrains.gitalso.predict.PredictedChange
import com.jetbrains.gitalso.predict.PredictedFile
import com.jetbrains.gitalso.predict.PredictedFilePath
import com.jetbrains.gitalso.predict.WeightWithFilterTunedPredictionProvider
import com.jetbrains.gitalso.repository.IDEARepositoryInfo
import java.util.function.Consumer

class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel, private val dataManager: VcsLogData, private val dataGetter: IndexDataGetter) : CheckinHandler() {
    companion object {
        private val LOG = Logger.getInstance(GitAlsoCheckinHandler::class.java)
    }

    private val project: Project = panel.project
    private val LOG = com.intellij.openapi.diagnostic.Logger.getInstance(GitAlsoCheckinHandler::class.java)
    private val changeListManager = ChangeListManager.getInstance(project)

    override fun getBeforeCheckinConfigurationPanel(): RefreshableOnComponent {
        return BooleanCommitOption(
                panel,
                "Predict forgotten files",
                true,
                { UserStorage.state.isTurnedOn },
                Consumer { UserStorage.state.isTurnedOn = it }
        )
    }

    private fun getPredictedCommittedFiles(root: VirtualFile, isAmend: Boolean, threshold: Double): List<CommittedFile> {
        val repository = IDEARepositoryInfo(root, dataGetter)

        val filesFromRoot = panel.files()
                .filter { VcsUtil.getVcsRootFor(project, it) == root }
                .toMutableSet()
        if (isAmend) {
            val ref = findBranch(dataManager.dataPack.refsModel, root, "HEAD")
            if (ref != null) {
                filesFromRoot.addAll(
                        dataGetter.getChangedPaths(
                                dataManager.storage.getCommitIndex(ref.commitHash, root)
                        )
                )
            }
        }
        if (filesFromRoot.size > 25) {
            return emptyList()
        }

        return WeightWithFilterTunedPredictionProvider(minProb = threshold)
                .commitPredict(repository.getCommit(filesFromRoot))
    }

    private fun getPredictedFiles(isAmend: Boolean, threshold: Double): List<PredictedFile> {
        val files = mutableListOf<CommittedFile>()
        for (root in panel.roots) {
            if (!dataManager.index.isIndexed(root)) {
                continue
            }
            files.addAll(getPredictedCommittedFiles(root, isAmend, threshold))
        }
        return files.map {
            val currentChange = changeListManager.getChange(it.path)
            if (currentChange != null) {
                PredictedChange(currentChange)
            } else {
                PredictedFilePath(it.path)
            }
        }
    }

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        try {
            val userStorage = UserStorage.state
            if (!userStorage.isTurnedOn || panel.files.size > 25) {
                return ReturnResult.COMMIT
            }

            val isAmend = panel.isAmend()
            val threshold = userStorage.threshold
            val predictedFiles = ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(
                            ThrowableComputable<List<PredictedFile>, Exception> {
                                getPredictedFiles(isAmend, threshold)
                            },
                            "GitAlso Plugin",
                            true,
                            project
                    )

            if (predictedFiles.isEmpty()) {
                return ReturnResult.COMMIT
            }

            val dialog = GitAlsoDialog(project, predictedFiles)
            dialog.show()

            return if (dialog.exitCode == 1) {
                UserStorage.updateState(userStorage, UserStorage.UserAction.CANCEL)
                ReturnResult.CANCEL
            } else {
                UserStorage.updateState(userStorage, UserStorage.UserAction.COMMIT)
                ReturnResult.COMMIT
            }
        } catch (e: Exception) {
            LOG.error("Unexpected problem with GitAlso prediction: $e")
            return ReturnResult.COMMIT
        }
    }

}