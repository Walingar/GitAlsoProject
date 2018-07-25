package com.jetbrains.gitalso.repository

import com.intellij.openapi.vcs.FilePath
import com.jetbrains.gitalso.commitInfo.Commit

interface RepositoryInfo {
    fun getCommit(root: FilePath, files: Collection<FilePath>): Commit?
}