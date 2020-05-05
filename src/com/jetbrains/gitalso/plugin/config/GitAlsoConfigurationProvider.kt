package com.jetbrains.gitalso.plugin.config

import com.intellij.openapi.options.ConfigurableProvider

class GitAlsoConfigurationProvider : ConfigurableProvider() {
    override fun createConfigurable() = GitAlsoConfigurationPanel()

    override fun canCreateConfigurable() = true // TODO: add registry key check there
}