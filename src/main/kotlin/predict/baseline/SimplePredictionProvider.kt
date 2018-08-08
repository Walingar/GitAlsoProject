package predict.baseline

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider
import kotlin.math.max

class SimplePredictionProvider : PredictionProvider {

    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {
        val candidates = HashMap<CommittedFile, Double>()
        val sizeHashMap = HashMap<CommittedFile, Int>()
        for (firstFile in commit.files) {
            val commits = firstFile.commits.filter { it.time < commit.time }
            for (fileCommits in commits) {
                for (secondFile in fileCommits.files) {

                    if (secondFile !in sizeHashMap) {
                        sizeHashMap[secondFile] = secondFile.commits.filter { it.time < commit.time }.size
                    }
                    val firstFileCommitCount = commits.size
                    val secondFileCommitCount = sizeHashMap[secondFile]!!
                    val intersection = commits.count { secondFile in it.files }
                    val union = firstFileCommitCount + secondFileCommitCount - intersection


                    val currentRate = intersection.toDouble() / union.toDouble()
                    candidates.putIfAbsent(secondFile, currentRate)
                    candidates[secondFile] = max(candidates[secondFile]!!, currentRate)
                }
            }
        }

        val filteredListOfCandidates = candidates
                .toList()
                .sortedBy { (_, value) -> value }
                .filter { it.second > 0.25 }
                .filter { it.first !in commit.files }
                .map { it.first }
                .reversed()

        return filteredListOfCandidates
                .subList(0, Integer.min(filteredListOfCandidates.size, maxPredictedFileCount))
    }
}