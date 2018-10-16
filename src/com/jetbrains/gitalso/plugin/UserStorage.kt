package com.jetbrains.gitalso.plugin

import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil

@State(name = "GitAlsoUserStorage", storages = [Storage(file = "GitAlsoUserStorage.xml")])
object UserStorage : PersistentStateComponent<UserStorage.State> {
    override fun loadState(state: State) {
        currentState = state
    }

    var currentState = State()

    class State {
        var lastAction = "Commit"
        var step = 0
        var isTurnedOff = false
    }

    override fun getState() = currentState
}