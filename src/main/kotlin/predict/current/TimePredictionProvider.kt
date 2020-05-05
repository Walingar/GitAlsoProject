package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider
import predict.getIntersection
import predict.getMaxByCommit
import kotlin.math.sqrt

class TimePredictionProvider(private val n: Int = 14, private val minProb: Double = 0.4) : PredictionProvider {

    private fun getTimeRate(time: Long, startTime: Long): Double {
        val week = 604800
        val parameter = sqrt(((startTime - time) / week).toDouble())
        if (parameter == 0.0) {
            return sqrt(week.toDouble())
        }
        return 1 / parameter
    }

    private fun getRateForCommits(commits: Collection<Commit>, startTime: Long): Double {
        var ans = 0.0
        for (commit in commits) {
            if (commit.time < startTime) {
                ans += getTimeRate(commit.time, startTime)
            }
        }
        return ans
    }

    private fun getProbabilityWithTime(firstFile: CommittedFile, secondFile: CommittedFile, time: Long, n: Int, maxByCommit: Int): Double {
        val intersection = getRateForCommits(getIntersection(firstFile, secondFile).filter { it.time < time }, time)
        val rateForFirstFile = getRateForCommits(firstFile.commits.filter { it.time < time }, time)
        val rateForSecondFile = getRateForCommits(secondFile.commits.filter { it.time < time }, time)
        val union = rateForFirstFile + rateForSecondFile - intersection
        if (union == 0.0) {
            return 0.0
        }
        return (intersection * sqrt(rateForFirstFile * rateForSecondFile)) /
                (union * java.lang.Double.max(n.toDouble(), maxByCommit.toDouble()))
    }

    private fun predictForFileInCommit(firstFile: CommittedFile, currentTime: Long, n: Int, maxByCommit: Int, minProb: Double):
            List<Pair<Double, CommittedFile>> {

        val was = HashSet<CommittedFile>()
        val scores = ArrayList<Pair<Double, CommittedFile>>()

        for (commit in firstFile.commits.filter { it.time < currentTime }) {
            for (secondFile in commit.files) {
                if (firstFile != secondFile && secondFile !in was) {
                    was.add(secondFile)

                    val prob = getProbabilityWithTime(firstFile, secondFile, currentTime, n, maxByCommit)
                    if (prob > minProb) {
                        scores.add(Pair(prob, secondFile))
                    }
                }
            }
        }

        return scores
    }


    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {
        val currentTime = commit.time

        val maxByCommit = getMaxByCommit(commit)
        val scores = HashMap<CommittedFile, Double>()

        for (file in commit.files) {
            val prediction = predictForFileInCommit(file, currentTime, n, maxByCommit, minProb)


            prediction.forEach {
                if (it.second !in commit.files) {
                    if (it.second in scores) {
                        scores[it.second] = scores[it.second]!! + it.first
                    } else {
                        scores[it.second] = it.first
                    }
                }
            }

        }

        return scores
                .toList()
                .sortedBy { (_, value) -> value }
                .reversed()
                .map { it.first }
                .subList(0, Integer.min(scores.size, maxPredictedFileCount))
    }

}