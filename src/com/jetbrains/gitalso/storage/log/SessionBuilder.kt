package com.jetbrains.gitalso.storage.log

import com.jetbrains.gitalso.commitInfo.CommittedFile
import com.jetbrains.gitalso.log.Action
import com.jetbrains.gitalso.log.LogEvent
import com.jetbrains.gitalso.log.State
import com.jetbrains.gitalso.predict.PredictionResult

class SessionBuilder(private val sessionId: Int = 1) {
    var repositoryId = "1"
    var stateBefore = State.BEFORE_COMMIT
    var stateAfter = State.AFTER_COMMIT
    var action = Action.COMMIT_CLICKED
    var time = 0L
    var commitFiles = listOf<CommittedFile>()
    var result: PredictionResult? = null

    fun build(): LogEvent = if (result != null) {
        TODO()
    } else {
        TODO()
    }

}