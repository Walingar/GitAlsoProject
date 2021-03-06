package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider
import kotlin.math.min

class WeightWithFilterTunedPredictionProvider(private val minProb: Double = 0.3, private val m: Double = 3.2, private val commitSize: Double = 8.0) : PredictionProvider {

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
        val filteredCommits = firstFile.commits.filter { it.time < commit.time }
        val commits = filteredCommits.sortedBy { it.time }.reversed().subList(0, min(filteredCommits.size, 20))
        for (fileCommit in commits) {
            for (secondFile in fileCommit.files) {
                if (secondFile in commit.files) {
                    continue
                }
                val currentRate = min(1.0, commitSize / fileCommit.files.size.toDouble())
                val currentWeight = 1.0
                candidates.putIfAbsent(secondFile, VoteProvider(m))

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
        if (commit.files.size > 25) {
            return arrayListOf()
        }
        val candidates = HashMap<CommittedFile, Double>()
        val votes = ArrayList<Pair<CommittedFile, Double>>()

        val scores = HashMap<Pair<CommittedFile, CommittedFile>, Number>()


        for (file in commit.files) {
            val currentVotes = vote(file, commit)
            for ((currentFile, currentVote) in currentVotes) {
                votes.add(Pair(currentFile, currentVote))
                candidates.putIfAbsent(currentFile, 0.0)
                scores[Pair(file, currentFile)] = currentVote
                candidates[currentFile] = candidates[currentFile]!! + currentVote
            }
        }

        val sortedPrediction = candidates
                .toList()
                .map { (key, value) -> key to value / commit.files.size }
                .sortedBy { (_, value) -> value }
                .reversed()

        val filteredCandidates = sortedPrediction.filter { it.second > minProb}

        var sliceBy = maxPredictedFileCount

        while (sliceBy + 1 < filteredCandidates.size && filteredCandidates[sliceBy].second == filteredCandidates[sliceBy + 1].second) {
            sliceBy++
        }

        return filteredCandidates
                .map { it.first }
                .subList(0, min(filteredCandidates.size, sliceBy))
    }
}