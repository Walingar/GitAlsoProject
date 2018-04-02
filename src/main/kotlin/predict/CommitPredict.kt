package predict

import commit.Commit
import commit.CommittedFile
import java.lang.Integer.min

private fun predictForFileInCommit(firstFile: CommittedFile, time: Long, n: Int, maxByCommit: Int, minProb: Double):
        Collection<Pair<Double, CommittedFile>> {

    val was = HashSet<Pair<CommittedFile, CommittedFile>>()
    val scores = ArrayList<Pair<Double, CommittedFile>>()
    for (commit in firstFile.getCommits()) {
        for (secondFile in commit.getFiles()) {
            if (firstFile != secondFile && Pair(firstFile, secondFile) !in was) {
                was.add(Pair(firstFile, secondFile))
                val prob = getProbabilityWithTime(firstFile, secondFile, time, n, maxByCommit)
                if (prob > minProb) {
                    scores.add(Pair(prob, secondFile))
                }
            }
        }
    }

    return scores
}

fun predictForCommit(commit: Commit): Collection<CommittedFile> {
    var maxByCommit = 0
    for (file in commit.getFiles()) {
        val temp = file.getCommits().size
        if (temp >= maxByCommit) {
            maxByCommit = temp
        }
    }
    val scores = HashSet<Pair<Double, CommittedFile>>()
    for (file in commit.getFiles()) {
        predictForFileInCommit(file, commit.getTime(), 14, maxByCommit, 0.4).forEach {
            scores.add(it)
        }
    }
    var maxInPrediction = Double.MAX_VALUE
    val prediction = HashSet<CommittedFile>()

    while (prediction.size != min(scores.size, 5)) {

        // counting max
        var curMax = 0.0
        for (predict in scores) {
            if (predict.first > curMax && predict.first < maxInPrediction) {
                curMax = predict.first
            }
        }
        maxInPrediction = curMax

        // adding max to predict
        for (predict in scores) {
            if (prediction.size == 5) {
                break
            }
            if (maxInPrediction == predict.first) {
                prediction.add(predict.second)
            }
        }
    }

    return prediction
}