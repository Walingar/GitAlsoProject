package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.changes.ui.BooleanCommitOption
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.ui.RefreshableOnComponent
import com.intellij.util.PairConsumer
import com.intellij.vcs.log.data.VcsLogData
import com.intellij.vcs.log.data.index.IndexDataGetter
import com.jetbrains.gitalso.commitHandle.ui.GitAlsoDialog
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.plugin.UserStorage
import com.jetbrains.gitalso.predict.PredictedChange
import com.jetbrains.gitalso.predict.PredictedFilePath
import com.jetbrains.gitalso.predict.WeightWithFilterTunedPredictionProvider
import com.jetbrains.gitalso.repository.IDEARepositoryInfo
import java.util.function.Consumer

class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel, private val dataManager: VcsLogData, private val dataGetter: IndexDataGetter) : CheckinHandler() {
    private val project: Project = panel.project
    private val LOG = com.intellij.openapi.diagnostic.Logger.getInstance(GitAlsoCheckinHandler::class.java)

    override fun getBeforeCheckinConfigurationPanel(): RefreshableOnComponent? {
        return BooleanCommitOption(
                panel,
                "Predict forgotten files",
                true,
                { UserStorage.state.isTurnedOn },
                Consumer { UserStorage.state.isTurnedOn = it }
        )
    }

    private fun getPredictedFiles(isAmend: Boolean, threshold: Double) = mutableListOf<CommittedFile>()
            .apply {
                for (root in panel.roots) {
                    if (!dataManager.index.isIndexed(root)) {
                        continue
                    }
                    val repository = IDEARepositoryInfo(root, dataGetter)
                    val filesFromRoot = PanelProcessor.files(panel).toMutableList()
                    if (isAmend) {
                        val ref = dataManager.dataPack.refsModel.findBranch(root, "HEAD")
                        if (ref != null) {
                            filesFromRoot.addAll(
                                    dataGetter.getChangedPaths(
                                            dataManager.storage.getCommitIndex(ref.commitHash, root)
                                    )
                            )
                        }
                    }
                    if (filesFromRoot.size > 25) {
                        continue
                    }
                    val commit = repository.getCommit(filesFromRoot)

                    addAll(WeightWithFilterTunedPredictionProvider(minProb = threshold)
                            .commitPredict(commit)
                    )
                }
            }
            .map {
                val changeListManager = ChangeListManager.getInstance(project)
                val currentChange = changeListManager.getChange(it.path)
                if (currentChange != null) {
                    PredictedChange(currentChange)
                } else {
                    PredictedFilePath(it.path)
                }
            }

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        try {
            val userStorage = UserStorage.state
            if (!userStorage.isTurnedOn || panel.files.size > 25) {
                return ReturnResult.COMMIT
            }

            val predictedFiles = getPredictedFiles(PanelProcessor.isAmend(panel), userStorage.threshold)

            // prediction is empty
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
            LOG.info("Unexpected problem with GitAlso prediction: $e")
            return ReturnResult.COMMIT
        }
    }

}