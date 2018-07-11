package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider

class FormulaPredictionProvider: PredictionProvider {
    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {
        TODO("predict by formula without time") //To change body of created functions use File | Settings | File Templates.
    }
}