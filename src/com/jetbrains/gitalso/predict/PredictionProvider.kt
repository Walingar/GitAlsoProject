package com.jetbrains.gitalso.predict

import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile

interface PredictionProvider {
    fun commitPredict(commit: Commit, maxPredictedFileCount: Int = 5): List<CommittedFile>
}