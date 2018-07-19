package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider
import kotlin.math.min

class WeightWithFilterTunedPredictionProvider(private val minProb: Double = 0.8, private val m2: Double = 4.7, private val commitSize: Double = 5.0) : PredictionProvider {

    private class VoteProvider(private val m: Double) {
        var result = 0.0
        private var votesCounter = 0.0
        private var votesSum = 0.0

        private fun R() = if (votesCounter != 0.0) votesSum / votesCounter else 0.0

        override fun toString(): String {
            return result.toString()
        }

        fun vote(rate: Double, weight: Double) {

            votesCounter += weight
            votesSum += rate

            val R = R()
            val v = votesCounter
            result = (v / (m + v)) * R + (m / (m + v)) * 0.25
        }
    }

    private fun vote(firstFile: CommittedFile, commit: Commit): HashMap<CommittedFile, Double> {
        val candidates = HashMap<CommittedFile, VoteProvider>()
        val filteredCommits = firstFile.getCommits().filter { it.time < commit.time }
        val commits = filteredCommits.sortedBy { it.time }.reversed().subList(0, min(filteredCommits.size, 10))
        for (fileCommit in commits) {
            for (secondFile in fileCommit.getFiles()) {
                if (secondFile in commit.getFiles()) {
                    continue
                }
                val currentRate = min(1.0, commitSize / fileCommit.getFiles().size.toDouble())

                val filesFileCommitFromCommit = fileCommit.getFiles().count { it in commit.getFiles() }
                val currentWeight = filesFileCommitFromCommit.toDouble() / commitSize
                candidates.putIfAbsent(secondFile, VoteProvider(m2))

                candidates[secondFile]!!.vote(currentRate, currentWeight)
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

        for (file in commit.getFiles()) {
            val currentVotes = vote(file, commit)
            for ((currentFile, currentVote) in currentVotes) {
                votes.add(Pair(currentFile, currentVote))
                candidates.putIfAbsent(currentFile, 0.0)
                candidates[currentFile] = candidates[currentFile]!! + currentVote
            }
        }

        val filteredCandidates = HashMap<CommittedFile, Double>()

        for ((file, score) in candidates) {
            if (score > minProb) {
                for (commitFile in commit.getFiles()) {
                    val commitFileSize = commitFile.getCommits().filter { it.time < commit.time }.size.toDouble()
                    if (file.getCommits().count{it.time < commit.time} / commitFileSize > 0.1) {
                        filteredCandidates[file] = score
                    }
                }
            }
        }

        val sortedCandidates = filteredCandidates
                .toList()
                .sortedBy { (_, value) -> value }
                .reversed()

        val sliceBy = min(sortedCandidates.size, maxPredictedFileCount)

        return sortedCandidates
                .map { it.first }
                .subList(0, sliceBy)
    }
}