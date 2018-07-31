package com.jetbrains.gitalso.storage.log

import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.log.State
import com.jetbrains.gitalso.predict.PredictionResult

object Logger {
    private val logManager by lazy {
        LogFileManager()
    }

    var sessionId = 1
    var repository = "0"

    fun log(event: LogEvent) {
        logManager.log(event)
    }

    fun simpleActionLog(action: Action, stateBefore: State, stateAfter: State) {
        val result = PredictionResult()
        result.sessionID = sessionId
        result.repository = repository
        log(result.getLogEvent(stateBefore, stateAfter, action, HashMap()))
    }
}