package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.changes.ui.CommitChangeListDialog
import com.intellij.vcsUtil.VcsUtil
import git4idea.checkin.GitCheckinEnvironment

fun CheckinProjectPanel.files(): List<FilePath> {
    return this.files.map { file -> VcsUtil.getFilePath(file.absolutePath) }
}

fun CheckinProjectPanel.isAmend(): Boolean {
    if (this !is CommitChangeListDialog) return false
    val gitCheckinOptions = this
            .additionalComponents
            .filterIsInstance(GitCheckinEnvironment.GitCheckinOptions::class.java)
            .firstOrNull()
            ?: return false
    return gitCheckinOptions.isAmend
}