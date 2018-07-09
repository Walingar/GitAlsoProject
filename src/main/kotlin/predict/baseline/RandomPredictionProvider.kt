package predict.baseline

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider

class RandomPredictionProvider : PredictionProvider {

    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {
        val currentTime = commit.time
        val allFiles = HashSet<CommittedFile>()

        for (file in commit.getFiles()) {
            for (fileCommit in file.getCommits()) {
                if (fileCommit.time > currentTime) {
                    continue
                }

                for (otherFile in fileCommit.getFiles()) {
                    if (otherFile !in allFiles) {
                        allFiles += otherFile
                    }
                }
            }
        }
        val prediction = allFiles.filter { it !in commit.getFiles() }
        return prediction.shuffled().subList(0, Integer.min(prediction.size, maxPredictedFileCount))
    }

}