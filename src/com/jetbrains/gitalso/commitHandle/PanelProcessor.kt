package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.changes.ui.CommitChangeListDialog
import com.intellij.vcsUtil.VcsUtil
import git4idea.checkin.GitCheckinEnvironment

object PanelProcessor {
    fun files(panel: CheckinProjectPanel): List<FilePath> {
        return panel.files.map { file -> VcsUtil.getFilePath(file.absolutePath) }
    }

    fun isAmend(panel: CheckinProjectPanel): Boolean {
        if (panel !is CommitChangeListDialog) return false
        val gitCheckinOptions = panel.additionalComponents.filterIsInstance(GitCheckinEnvironment.GitCheckinOptions::class.java).firstOrNull()
                ?: return false
        return gitCheckinOptions.isAmend
    }
}