package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider

class WeightPredictionProvider : PredictionProvider {
    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {
        TODO("predict by weight") //To change body of created functions use File | Settings | File Templates.
    }
}