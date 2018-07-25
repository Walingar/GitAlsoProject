package com.jetbrains.gitalso.repository

import com.intellij.vcs.log.VcsLogIndexService
import com.intellij.vcs.log.impl.VcsLogManager
import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import kotlin.math.max

class LogIndexService: VcsLogIndexService {
    override fun requiresPathsForwardIndex() = true
}