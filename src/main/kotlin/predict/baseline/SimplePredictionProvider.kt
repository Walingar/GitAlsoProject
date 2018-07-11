package predict.baseline

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider

class SimplePredictionProvider : PredictionProvider {

    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {
        TODO("Add prediction by (A int B/ A un B)")
    }
}