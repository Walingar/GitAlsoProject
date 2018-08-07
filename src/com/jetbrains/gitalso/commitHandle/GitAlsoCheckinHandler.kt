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
import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.State
import com.jetbrains.gitalso.predict.PredictionResult
import com.jetbrains.gitalso.predict.WeightWithFilterTunedPredictionProvider
import com.jetbrains.gitalso.repository.IDEARepositoryInfo
import com.jetbrains.gitalso.storage.log.Logger
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.min


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

    private fun ClosedRange<Int>.random() =
            Random().nextInt((endInclusive + 1) - start) + start

    private fun getCommitTimesFromPrediction(
            commit: Commit,
            topPrediction: Collection<CommittedFile>
    ): Map<Pair<CommittedFile, CommittedFile>, Set<Long>> {
        val commits = HashMap<Pair<CommittedFile, CommittedFile>, HashSet<Long>>()
        val predictionSet = topPrediction.toSet()

        for (file in commit.files) {
            for (fileCommit in file.commits) {
                fileCommit.files
                        .filter { it in predictionSet }
                        .forEach {
                            commits.putIfAbsent(Pair(file, it), HashSet())
                            commits[Pair(file, it)]!!.add(fileCommit.time ?: -1)
                        }
            }
        }

        return commits
    }

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        if (root == null) {
            return ReturnResult.COMMIT
        }
        val startTime = System.currentTimeMillis()

        val sessionId = (0 until Int.MAX_VALUE).random()
        val repository = IDEARepositoryInfo(project)
        val filesFromRoot = files().filter { getRoot(it) == root }
        val commit = repository.getCommit(getFilePath(root), filesFromRoot)

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

        val result = WeightWithFilterTunedPredictionProvider()
                .commitPredict(commit)

        result.sessionID = sessionId
        result.repository = repository.toString()

        val commits = getCommitTimesFromPrediction(commit, result.topPrediction)

        val files = result.prediction.mapNotNull { it.path.virtualFile }
        val time = System.currentTimeMillis() - startTime

        // prediction is empty
        if (files.isEmpty()) {
            val event = result.getLogEvent(State.BEFORE_COMMIT, State.NOT_SHOWED, Action.COMMIT_CLICKED, time, commits)
            Logger.log(event)
            return ReturnResult.COMMIT
        }

        val modifiedFiles = files.filter { isModified(it) }.toSet()
        val unmodifiedFiles = files.filter { !isModified(it) }.toSet()

        val dialog = GitAlsoDialog(project, modifiedFiles, unmodifiedFiles)

        val event = result.getLogEvent(State.BEFORE_COMMIT, State.SHOW_MAIN_DIALOG, Action.COMMIT_CLICKED, time, commits, modifiedFiles.toList(), unmodifiedFiles.toList())
        Logger.log(event)

        dialog.show()

        return if (dialog.exitCode == 1) {
            Logger.simpleActionLog(Action.CANCEL, State.SHOW_MAIN_DIALOG, State.AFTER_COMMIT)
            ReturnResult.CANCEL
        } else {
            Logger.simpleActionLog(Action.COMMIT, State.SHOW_MAIN_DIALOG, State.AFTER_COMMIT)
            ReturnResult.COMMIT
        }

    }
}