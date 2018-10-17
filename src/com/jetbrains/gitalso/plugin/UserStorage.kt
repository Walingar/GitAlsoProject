package com.jetbrains.gitalso.plugin

import com.intellij.openapi.components.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

@State(name = "GitAlsoUserStorage", storages = [Storage(file = "GitAlsoUserStorage.xml")])
object UserStorage : PersistentStateComponent<UserStorage.State> {
    class State {
        var lastAction = "Commit"
        var step = 0
        var threshold = 0.3
        var isTurnedOff = false
    }

    var currentState = State()

    override fun loadState(state: State) {
        currentState = state
    }

    override fun getState() = currentState

    fun userStorageUpdate(storage: UserStorage.State, type: String) {
        if (storage.lastAction == type) {
            storage.step++
        } else {
            storage.step = 0
        }
        storage.lastAction = type
        when (type) {
            "Commit" -> {
                storage.threshold += 0.05 / (1 + 2.71.pow(-0.2 * storage.step))
                storage.threshold = min(storage.threshold, 0.7)
            }
            "Cancel" -> {
                storage.threshold -= 0.01 / (1 + 2.71.pow(-0.05 * storage.step))
                storage.threshold = max(storage.threshold, 0.15)
            }
            else -> throw IllegalStateException()
        }
    }
}