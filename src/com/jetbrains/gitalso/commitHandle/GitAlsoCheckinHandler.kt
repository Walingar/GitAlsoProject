package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.changes.ui.BooleanCommitOption
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.ui.RefreshableOnComponent
import com.intellij.util.PairConsumer
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.gitalso.commitHandle.ui.GitAlsoDialog
import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.State
import com.jetbrains.gitalso.plugin.UserStorage
import com.jetbrains.gitalso.predict.PredictionResultProcessor
import com.jetbrains.gitalso.predict.WeightWithFilterTunedPredictionProvider
import com.jetbrains.gitalso.repository.IDEARepositoryInfo
import com.jetbrains.gitalso.storage.log.Logger
import java.util.function.Consumer

class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val project: Project = panel.project
    private val rootPath = project.basePath
    private val filesProcessor = CommitFilesProcessor(panel.project)

    private fun preparePredictionData(map: Map<Pair<CommittedFile, CommittedFile>, Set<Commit>>): Map<Pair<CommittedFile, CommittedFile>, Set<Long>> =
            map.map { (key, value) ->
                key to value
                        .map {
                            it.id.toLong()
                        }
                        .toSet()
            }.toMap()

    private fun sendLogs() {
        ApplicationManager.getApplication().executeOnPooledThread {
            Logger.sendLogs()
        }
    }

    override fun getBeforeCheckinConfigurationPanel(): RefreshableOnComponent? {
        return BooleanCommitOption(
                panel,
                "Predict forgotten files (Exp.)",
                true,
                { UserStorage.state.isTurnedOn },
                Consumer { UserStorage.state.isTurnedOn = it }
        )
    }

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        try {
            if (DumbService.getInstance(project).isDumb) {
                return ReturnResult.COMMIT
            }

            val userStorage = UserStorage.state
            if (!userStorage.isTurnedOn) {
                return ReturnResult.COMMIT
            }

            if (rootPath == null) {
                return ReturnResult.COMMIT
            }

            val root = VcsUtil.getFilePath(rootPath).virtualFile ?: return ReturnResult.COMMIT

            val startTime = System.currentTimeMillis()

            val sessionId = (0 until Int.MAX_VALUE).random()
            val repository = IDEARepositoryInfo(project)
            val filesFromRoot = PanelProcessor.files(panel).filter { filesProcessor.getRoot(it) == root }
            val commit = repository.getCommit(filesProcessor.getFilePath(root), filesFromRoot)

            Logger.repository = repository.toString()
            Logger.sessionId = sessionId

            // idea is not indexed
            if (commit == null) {
                Logger.simpleActionLog(Action.COMMIT_CLICKED, State.BEFORE_COMMIT, State.NOT_INDEXED)
                return ReturnResult.COMMIT
            }

            // a lot of files are not interesting for prediction and so slow
            if (commit.files.size > 25) {
                Logger.simpleActionLog(Action.COMMIT_CLICKED, State.BEFORE_COMMIT, State.A_LOT_OF_FILES)
                return ReturnResult.COMMIT
            }

            val result = WeightWithFilterTunedPredictionProvider(minProb = userStorage.threshold)
                    .commitPredict(commit)

            result.sessionID = sessionId
            result.repository = repository.toString()

            val author = PanelProcessor.getAuthor(panel) ?: (repository.author?.toString() ?: "")
            val commits = PredictionResultProcessor.getCommitTimesFromPrediction(commit, result.topPrediction)
            val commitsAuthor = PredictionResultProcessor.getCommitsAuthorMask(commits, author)
            val files = result.prediction.mapNotNull { it.path.virtualFile }
            val time = System.currentTimeMillis() - startTime

            // prediction is empty
            if (files.isEmpty()) {
                val event = result.getLogEvent(
                        State.BEFORE_COMMIT,
                        State.NOT_SHOWED,
                        Action.COMMIT_CLICKED,
                        time,
                        preparePredictionData(commits),
                        preparePredictionData(commitsAuthor)
                )
                Logger.log(event)
                return ReturnResult.COMMIT
            }

            val modifiedFiles = files.filter { filesProcessor.isModified(it) }.toSet()
            val unmodifiedFiles = files.filter { filesProcessor.isUnmodified(it) }.toSet()

            val dialog = GitAlsoDialog(project, modifiedFiles, unmodifiedFiles)

            val event = result.getLogEvent(
                    State.BEFORE_COMMIT,
                    State.SHOW_MAIN_DIALOG,
                    Action.COMMIT_CLICKED,
                    time,
                    preparePredictionData(commits),
                    preparePredictionData(commitsAuthor),
                    modifiedFiles.toList(),
                    unmodifiedFiles.toList()
            )
            Logger.log(event)

            dialog.show()

            return if (dialog.exitCode == 1) {
                Logger.simpleActionLog(Action.CANCEL, State.SHOW_MAIN_DIALOG, State.AFTER_COMMIT)
                UserStorage.updateState(userStorage, UserStorage.UserAction.CANCEL)
                ReturnResult.CANCEL
            } else {
                Logger.simpleActionLog(Action.COMMIT, State.SHOW_MAIN_DIALOG, State.AFTER_COMMIT)
                UserStorage.updateState(userStorage, UserStorage.UserAction.COMMIT)
                ReturnResult.COMMIT
            }
        } catch (e1: Exception) {
            try {
                // log: exception was
                Logger.simpleActionLog(Action.COMMIT_CLICKED, State.NOT_INDEXED, State.NOT_INDEXED)
            } catch (e2: Exception) {
                // filesystem exception
            } finally {
                return ReturnResult.COMMIT
            }
        } finally {
            sendLogs()
        }
    }

}