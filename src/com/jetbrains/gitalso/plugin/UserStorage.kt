package com.jetbrains.gitalso.plugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import kotlin.math.max
import kotlin.math.min

@State(name = "GitAlsoUserStorage", storages = [Storage(file = "gitalso.user.storage.xml")])
object UserStorage : PersistentStateComponent<UserStorage.State> {
    class State {
        var lastAction = UserAction.COMMIT
        var step = 0.0
        var threshold = 0.35
        var isTurnedOn = true
    }

    private var currentState = State()

    override fun loadState(state: State) {
        currentState = state
    }

    override fun getState() = currentState

    fun resetState(storage: State) {
        storage.step = 0.0
        storage.threshold = 0.35
    }

    enum class UserAction {
        COMMIT,
        CANCEL
    }

    fun updateState(storage: State, type: UserAction) {
        val gamma = 0.9

        if (type == UserAction.CANCEL && storage.step <= 0) {
            storage.threshold -= 0.005
            storage.lastAction = type
            return
        }
        storage.lastAction = type

        storage.step *= gamma
        storage.step = when (type) {
            UserAction.COMMIT -> {
                min(((1 - storage.threshold) / 7), storage.step + 0.05)
            }
            UserAction.CANCEL -> {
                max(-(storage.threshold / 7), storage.step - 0.05)
            }
        }
        storage.threshold = min(max(storage.threshold + storage.step, 0.15), 1.0)
    }
}