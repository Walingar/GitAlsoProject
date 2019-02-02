package com.jetbrains.gitalso.plugin

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import kotlin.math.max
import kotlin.math.min

@State(name = "GitAlsoUserStorage", storages = [Storage(file = "gitalso.user.storage.xml")])
class UserSettings : PersistentStateComponent<UserSettings.Companion.State> {
    companion object {
        const val THRESHOLD_PRECISION = 100

        data class State(var lastAction: UserAction = UserAction.COMMIT,
                         var step: Double = 0.0,
                         var threshold: Double = 0.35,
                         var isTurnedOn: Boolean = true)

        enum class UserAction {
            COMMIT,
            CANCEL
        }
    }

    private var currentState: UserSettings.Companion.State = State()

    var threshold: Double
        get() = Math.round(currentState.threshold * THRESHOLD_PRECISION).toDouble() / THRESHOLD_PRECISION
        set(value) {
            safeUpdateThreshold(value)
        }

    var isTurnedOn: Boolean
        get() = currentState.isTurnedOn
        set(value) {
            currentState.isTurnedOn = value
        }

    override fun loadState(state: State) {
        currentState = state
    }

    override fun getState() = currentState

    private fun safeUpdateThreshold(newValue: Double) {
        val minThreshold = 0.0
        val maxThreshold = 1.0
        currentState.threshold = min(maxThreshold, max(minThreshold, newValue))
    }

    fun updateState(type: UserAction) {
        val gamma = 0.9
        val minimalStep = 0.05
        val chunksToBorderCount = 7


        if (type == UserAction.CANCEL && currentState.step <= 0) {
            safeUpdateThreshold(currentState.threshold - 0.005)
            currentState.lastAction = type
            return
        }
        currentState.lastAction = type

        currentState.step *= gamma
        currentState.step = when (type) {
            UserAction.COMMIT -> {
                min((1 - currentState.threshold) / chunksToBorderCount,
                        currentState.step + minimalStep)
            }
            UserAction.CANCEL -> {
                max(-currentState.threshold / chunksToBorderCount,
                        currentState.step - minimalStep)
            }
        }
        safeUpdateThreshold(currentState.threshold + currentState.step)
    }
}