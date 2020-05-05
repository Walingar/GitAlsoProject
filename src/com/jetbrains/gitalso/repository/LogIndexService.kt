package com.jetbrains.gitalso.repository

import com.intellij.openapi.components.ServiceManager
import com.intellij.vcs.log.VcsLogIndexService
import com.jetbrains.gitalso.plugin.UserSettings

class LogIndexService : VcsLogIndexService {
    override fun requiresPathsForwardIndex() = ServiceManager.getService(UserSettings::class.java).isPluginEnabled
}