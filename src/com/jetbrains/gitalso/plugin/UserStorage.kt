package com.jetbrains.gitalso.plugin

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "GitAlsoUserStorage")
class UserStorage : PersistentStateComponent<UserStorage> {
    override fun loadState(state: UserStorage) {
        XmlSerializerUtil.copyBean(state, this)
    }

    var lastAction = "Commit"
    var step = 0
    var isTurnedOff = false

    override fun getState() = this
}