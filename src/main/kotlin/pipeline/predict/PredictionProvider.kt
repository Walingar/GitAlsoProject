package predict

import commitInfo.Commit
import commitInfo.CommittedFile

interface PredictionProvider {

    fun commitPredict(commit: Commit, maxPredictedFileCount: Int = 5): List<CommittedFile>

}