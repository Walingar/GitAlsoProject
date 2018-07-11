package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider

class CommitTimePredictionProvider: PredictionProvider {
    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {
        TODO("predict by formula with time, but with commit ticks instead of ts") //To change body of created functions use File | Settings | File Templates.
    }
}