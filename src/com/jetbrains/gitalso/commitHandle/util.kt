package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.changes.ui.CommitChangeListDialog
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcsUtil.VcsUtil
import git4idea.checkin.GitCheckinEnvironment

fun CheckinProjectPanel.filePaths(): List<FilePath> {
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

fun CheckinProjectPanel.getRootFiles(project: Project): Map<VirtualFile, Collection<FilePath>> {
    val rootFiles = HashMap<VirtualFile, HashSet<FilePath>>()

    this.filePaths().forEach { file ->
        val fileRoot = VcsUtil.getVcsRootFor(project, file)
        if (fileRoot != null) {
            rootFiles.getOrPut(fileRoot) { HashSet() }.add(file)
        }
    }

    return rootFiles
}