package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider
import kotlin.math.max
import kotlin.math.min

class WeightPredictionProvider(private val minProb: Double, private val m1: Double, private val m2: Double, private val commitSize: Double) : PredictionProvider {

    private class VoteProvider(private val m: Double) {
        var result = 0.0
        private var votesCounter = 0
        private var votesSum = 0.0

        private fun R() = votesSum / votesCounter.toDouble()

        override fun toString(): String {
            return result.toString()
        }

        fun vote(rate: Double) {
            votesCounter++
            votesSum += rate

            val v = votesCounter.toDouble()
            result = (v / (m + v)) * R() + (m / (m + v)) * 0.6
        }
    }

    private fun vote(firstFile: CommittedFile, commit: Commit): HashMap<CommittedFile, Double> {
        val candidates = HashMap<CommittedFile, VoteProvider>()
        val commits = firstFile.getCommits().filter { it.time < commit.time }
        for (fileCommit in commits) {
            for (secondFile in fileCommit.getFiles()) {
                if (secondFile in commit.getFiles()) {
                    continue
                }
                val currentRate = max(1.0, commitSize / fileCommit.getFiles().size.toDouble()) * min(1.0, firstFile.getCommits().size.toDouble() / secondFile.getCommits().size)
                candidates.putIfAbsent(secondFile, VoteProvider(m2))
                candidates[secondFile]!!.vote(currentRate)
            }
        }

        val answer = HashMap<CommittedFile, Double>()
        for ((key, value) in candidates) {
            answer[key] = value.result
        }

        return answer
    }

    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {
        val candidates = HashMap<CommittedFile, VoteProvider>()

        for (file in commit.getFiles()) {
            val currentVotes = vote(file, commit)
            for ((currentFile, currentVote) in currentVotes) {
                candidates.putIfAbsent(currentFile, VoteProvider(m1))
                candidates[currentFile]!!.vote(currentVote)
            }
        }

        val filteredCandidates = candidates
                .filter { it.value.result > minProb }

        val sortedCandidates = filteredCandidates
                .toList()
                .sortedBy { (_, value) -> value.result }
                .reversed()
        if (commit.getFiles().size > 5) {
            return arrayListOf()
        }

        return sortedCandidates
                .map { it.first }
                .subList(0, Integer.min(filteredCandidates.size, maxPredictedFileCount))
    }
}