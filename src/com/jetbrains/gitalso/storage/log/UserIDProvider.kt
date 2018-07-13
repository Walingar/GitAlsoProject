package com.jetbrains.gitalso.storage.log

import com.intellij.openapi.application.PermanentInstallationID

class UserIDProvider {
    fun installationID() = PermanentInstallationID.get()
}