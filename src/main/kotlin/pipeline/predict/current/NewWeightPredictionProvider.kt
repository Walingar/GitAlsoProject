package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider

import kotlin.math.min

class NewWeightPredictionProvider(private val minProb: Double = 0.57, private val m2: Double = 20.0, private val commitSize: Double = 5.0) : PredictionProvider {

    private class VoteProvider(private val m: Double) {
        var result = 0.0
        private var votesCounter = 0
        private var votesSum = 0.0

        private fun R() = if (votesCounter != 0) votesSum / votesCounter.toDouble() else 0.0

        override fun toString(): String {
            return result.toString()
        }

        fun vote(rate: Double) {
            val R = R()
            votesCounter++
            votesSum += rate

            val v = votesCounter.toDouble()
            result = (v / (m + v)) * R + (m / (m + v)) * 0.6
        }
    }

    private fun vote(firstFile: CommittedFile, commit: Commit): HashMap<CommittedFile, Double> {
        val candidates = HashMap<CommittedFile, VoteProvider>()
        val commits = firstFile.commits.filter { it.time < commit.time }
        for (fileCommit in commits) {
            for (secondFile in fileCommit.files) {
                if (secondFile in commit.files) {
                    continue
                }
                val currentRate = min(1.0, commitSize / fileCommit.files.size.toDouble())
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
        val candidates = HashMap<CommittedFile, Double>()
        val votes = ArrayList<Pair<CommittedFile, Double>>()

        for (file in commit.files) {
            val currentVotes = vote(file, commit)
            for ((currentFile, currentVote) in currentVotes) {
                votes.add(Pair(currentFile, currentVote))
                candidates.putIfAbsent(currentFile, 0.0)
                candidates[currentFile] = candidates[currentFile]!! + currentVote
            }
        }

        val filteredCandidates = candidates
                .filter { it.value > minProb }

        val sortedCandidates = filteredCandidates
                .toList()
                .sortedBy { (_, value) -> value }
                .reversed()
        if (commit.files.size > 5) {
            return arrayListOf()
        }

        return sortedCandidates
                .map { it.first }
                .subList(0, Integer.min(filteredCandidates.size, maxPredictedFileCount))
    }
}