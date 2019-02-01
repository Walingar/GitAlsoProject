package com.jetbrains.gitalso.repository

import com.intellij.vcs.log.VcsLogIndexService

class LogIndexService: VcsLogIndexService {
    override fun requiresPathsForwardIndex() = true
}