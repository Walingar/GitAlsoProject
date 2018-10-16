package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vcs.FileStatus
import com.intellij.openapi.vcs.impl.FileStatusManagerImpl
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcsUtil.VcsUtil

class CommitFilesProcessor(private val project: Project) {
    private val fileStatusManager = FileStatusManagerImpl.getInstance(project)

    fun getFilePath(file: VirtualFile): FilePath = VcsUtil.getFilePath(file)

    fun isModified(file: VirtualFile) = fileStatusManager.getStatus(file) == FileStatus.MODIFIED

    fun isUnmodified(file: VirtualFile) = fileStatusManager.getStatus(file) == FileStatus.NOT_CHANGED

    fun getRoot(file: FilePath) = VcsUtil.getVcsRootFor(project, file)
}