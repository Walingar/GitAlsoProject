package com.jetbrains.gitalso.plugin

import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class NotificationManager : StartupActivity {

    companion object {
        private const val PLUGIN_NAME = "GitAlso plugin"
        private const val MESSAGE_TEXT =
                "Data about your commits will be anonymously reported. " +
                        "No personal data, code or file names will be sent."

        private const val MESSAGE_SHOWN_KEY = "gitalso.stats.allow.message.shown"
    }

    private fun isMessageShown() = PropertiesComponent.getInstance().getBoolean(MESSAGE_SHOWN_KEY, false)

    private fun setMessageShown(value: Boolean) = PropertiesComponent.getInstance().setValue(MESSAGE_SHOWN_KEY, value)

    override fun runActivity(project: Project) {
        if (!isMessageShown()) {
            notify(project)
            setMessageShown(true)
        }
    }

    private fun notify(project: Project) {
        val notification = Notification(PLUGIN_NAME, PLUGIN_NAME, MESSAGE_TEXT, NotificationType.INFORMATION)
        notification.notify(project)
    }

}