package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.impl.FileStatusManagerImpl
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.PairConsumer
import com.intellij.vcs.log.VcsUser
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.gitalso.commitHandle.ui.GitAlsoDialog
import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.State
import com.jetbrains.gitalso.predict.WeightWithFilterTunedPredictionProvider
import com.jetbrains.gitalso.repository.IDEARepositoryInfo
import com.jetbrains.gitalso.storage.log.Logger
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet


class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val project: Project = panel.project
    private val rootPath = project.basePath
    private val title = "GitAlso plugin"

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
    ): Map<Pair<CommittedFile, CommittedFile>, Set<Commit>> {
        val commits = HashMap<Pair<CommittedFile, CommittedFile>, HashSet<Commit>>()
        val predictionSet = topPrediction.toSet()

        for (file in commit.files) {
            for (fileCommit in file.commits) {
                fileCommit.files
                        .filter { it in predictionSet }
                        .forEach {
                            commits.putIfAbsent(Pair(file, it), HashSet())
                            commits[Pair(file, it)]!!.add(fileCommit)
                        }
            }
        }

        return commits
    }

    private fun getCommitsAuthorMask(commits: Map<Pair<CommittedFile, CommittedFile>, Set<Commit>>, author: VcsUser?): Map<Pair<CommittedFile, CommittedFile>, Set<Commit>> {
        if (author == null) {
            return mapOf()
        }
        val authorMask = HashMap<Pair<CommittedFile, CommittedFile>, HashSet<Commit>>()
        for ((pair, set) in commits) {
            authorMask.putIfAbsent(pair, hashSetOf())
            for (commit in set) {
                if (commit.author == author) {
                    authorMask[pair]!!.add(commit)
                }
            }
        }

        return authorMask
    }

    private fun prepare(map: Map<Pair<CommittedFile, CommittedFile>, Set<Commit>>) =
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

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        try {
            if (DumbService.getInstance(project).isDumb) {
                Messages.showErrorDialog(project, "Cannot commit right now because IDE updates the indices " +
                        "of the project in the background. Please try again later.",
                        title)
                return ReturnResult.CANCEL
            }

            if (rootPath == null) {
                sendLogs()
                return ReturnResult.COMMIT
            }

            val root = VcsUtil.getFilePath(rootPath).virtualFile
            if (root == null) {
                sendLogs()
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
                sendLogs()
                return ReturnResult.COMMIT
            }

            // a lot of files are not interesting for prediction and so slow
            if (commit.files.size > 25) {
                Logger.simpleActionLog(Action.COMMIT_CLICKED, State.BEFORE_COMMIT, State.A_LOT_OF_FILES)
                sendLogs()
                return ReturnResult.COMMIT
            }

            val result = WeightWithFilterTunedPredictionProvider()
                    .commitPredict(commit)

            result.sessionID = sessionId
            result.repository = repository.toString()

            val commits = getCommitTimesFromPrediction(commit, result.topPrediction)
            val commitsAuthor = getCommitsAuthorMask(commits, repository.author)
            val files = result.prediction.mapNotNull { it.path.virtualFile }
            val time = System.currentTimeMillis() - startTime

            // prediction is empty
            if (files.isEmpty()) {
                val event = result.getLogEvent(
                        State.BEFORE_COMMIT,
                        State.NOT_SHOWED,
                        Action.COMMIT_CLICKED,
                        time,
                        prepare(commits),
                        prepare(commitsAuthor)
                )
                Logger.log(event)
                sendLogs()
                return ReturnResult.COMMIT
            }

            val modifiedFiles = files.filter { isModified(it) }.toSet()
            val unmodifiedFiles = files.filter { !isModified(it) }.toSet()

            val dialog = GitAlsoDialog(project, modifiedFiles, unmodifiedFiles)

            val event = result.getLogEvent(
                    State.BEFORE_COMMIT,
                    State.SHOW_MAIN_DIALOG,
                    Action.COMMIT_CLICKED,
                    time,
                    prepare(commits),
                    prepare(commitsAuthor),
                    modifiedFiles.toList(),
                    unmodifiedFiles.toList()
            )
            Logger.log(event)

            dialog.show()

            return if (dialog.exitCode == 1) {
                Logger.simpleActionLog(Action.CANCEL, State.SHOW_MAIN_DIALOG, State.AFTER_COMMIT)
                sendLogs()
                ReturnResult.CANCEL
            } else {
                Logger.simpleActionLog(Action.COMMIT, State.SHOW_MAIN_DIALOG, State.AFTER_COMMIT)
                sendLogs()
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
        }
    }

}