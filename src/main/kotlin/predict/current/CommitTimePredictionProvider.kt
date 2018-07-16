package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider
import predict.getMaxByCommit
import kotlin.math.max
import kotlin.math.sqrt

class CommitTimePredictionProvider(private val n: Int = 14, private val minProb: Double = 0.4, private val commitTick: Int = 20) : PredictionProvider {

    private fun getRateForCommits(commits: Collection<Commit>): Double {
        var ans = 0.0
        val sizeWithCurrentCommit = commits.size + 1
        for ((index, _) in commits.sortedBy { it.time }.withIndex()) {
            val delay = sizeWithCurrentCommit - index
            val delayTicks = delay / commitTick
            ans += 1 / (delayTicks + 1)
        }
        return ans
    }

    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {
        val maxByCommit = getMaxByCommit(commit)

        val candidates = HashMap<CommittedFile, Double>()
        val ratesHashMap = HashMap<CommittedFile, Double>()

        for (firstFile in commit.getFiles()) {
            val commits = firstFile.getCommits().filter { it.time < commit.time }
            ratesHashMap[firstFile] = getRateForCommits(firstFile.getCommits().filter { it.time < commit.time })

            for (fileCommits in commits) {
                for (secondFile in fileCommits.getFiles()) {
                    if (secondFile == firstFile) {
                        continue
                    }
                    if (secondFile !in ratesHashMap) {
                        ratesHashMap[secondFile] = getRateForCommits(secondFile.getCommits().filter { it.time < commit.time })
                    }
                    val rateForFirstFile = ratesHashMap[firstFile]!!
                    val rateForSecondFile = ratesHashMap[secondFile]!!
                    val intersection = getRateForCommits(commits.filter { secondFile in it.getFiles() })
                    val union = rateForFirstFile + rateForSecondFile - intersection

                    val currentRate = (intersection * sqrt(rateForFirstFile * rateForSecondFile)) /
                            (union * java.lang.Double.max(n.toDouble(), maxByCommit.toDouble()))

                    candidates.putIfAbsent(secondFile, 0.0)
                    candidates[secondFile] = candidates[secondFile]!! + currentRate
                }
            }
        }

        val filteredCandidates = candidates
                .filter { it.value > minProb }

        return filteredCandidates
                .toList()
                .sortedBy { (_, value) -> value }
                .reversed()
                .map { it.first }
                .subList(0, Integer.min(filteredCandidates.size, maxPredictedFileCount))
    }
}