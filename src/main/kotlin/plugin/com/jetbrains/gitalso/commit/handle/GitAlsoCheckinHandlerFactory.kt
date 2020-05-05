package com.jetbrains.gitalso.commit.handle

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitContext
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory
import com.intellij.vcs.log.impl.VcsProjectLog
import com.jetbrains.gitalso.plugin.UserSettings

class GitAlsoCheckinHandlerFactory : CheckinHandlerFactory() {
    override fun createHandler(panel: CheckinProjectPanel, commitContext: CommitContext): CheckinHandler {
        if (!ServiceManager.getService(UserSettings::class.java).isPluginEnabled) {
            return CheckinHandler.DUMMY
        }
        val dataManager = VcsProjectLog.getInstance(panel.project).dataManager ?: return CheckinHandler.DUMMY
        val dataGetter = dataManager.index.dataGetter ?: return CheckinHandler.DUMMY
        return if (dataManager.dataPack.isFull) {
            GitAlsoCheckinHandler(panel, dataManager, dataGetter)
        } else {
            CheckinHandler.DUMMY
        }
    }
}