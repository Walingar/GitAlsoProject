package predict.current

import commitInfo.Commit
import commitInfo.CommittedFile
import predict.PredictionProvider
import kotlin.math.min

class TotalWeightPredictionProvider(private val minProb: Double = 0.5, private val m: Double = 2.0, private val commitSize: Double = 2.0) : PredictionProvider {

    private class VoteProvider(private val m: Double) {
        private var votesCounter = 0
        private var votesSum = 0.0

        private fun R() = if (votesCounter != 0) votesSum / votesCounter.toDouble() else 0.0

        override fun toString(): String {
            return count().toString()
        }

        fun vote(rate: Double) {
            votesCounter++
            votesSum += rate
        }

        fun count(): Double {
            val R = R()
            val v = votesCounter.toDouble()

            return (v / (m + v)) * R + (m / (m + v)) * 0.6
        }
    }

    private fun updateScore(file: CommittedFile, score: Double, filesScore: HashMap<CommittedFile, VoteProvider>) {
        filesScore.putIfAbsent(file, VoteProvider(m))
        filesScore[file]!!.vote(score)
    }

    private fun fileVote(firstFile: CommittedFile, commit: Commit, filesScore: HashMap<CommittedFile, VoteProvider>) {
        val fileCommits = firstFile.getCommits().filter { it.time < commit.time }

        for (currentCommit in fileCommits) {
            val currentCommitSize = currentCommit.getFiles().size.toDouble()

            for (secondFile in currentCommit.getFiles()) {
                if (secondFile in commit.getFiles()) {
                    continue
                }

                val currentScore = commitSize / currentCommitSize
                updateScore(secondFile, currentScore, filesScore)
            }
        }
    }

    override fun commitPredict(commit: Commit, maxPredictedFileCount: Int): List<CommittedFile> {

        val filesScore = HashMap<CommittedFile, VoteProvider>()

        val voter = VoteProvider(m)

        for (file in commit.getFiles()) {
            fileVote(file, commit, filesScore)
        }

        var sliceBy = 0

        val sortedCandidates = filesScore
                .toList()
                .map { (key, value) -> Pair(key, value.count()) }
                .sortedBy { (_, value) -> value}
                .reversed()

        while (sliceBy + 1 < filesScore.size && sortedCandidates[sliceBy].second == sortedCandidates[sliceBy + 1].second) {
            sliceBy++
        }

        val slicedCandidates = sortedCandidates.subList(0, min(sliceBy + 1, sortedCandidates.size))

        val filteredCandidates = HashMap<CommittedFile, Double>()

        for ((file, score) in slicedCandidates) {
            val predictedFileCommits = file.getCommits().filter { it.time < commit.time }

            if (score > minProb) {
                for (commitFile in commit.getFiles()) {
                    val predictedFileWithCommitFileSize = predictedFileCommits.count { commitFile in it.getFiles() }.toDouble()
                    val predictedFileSize = predictedFileCommits.size.toDouble()
                    if (
                            predictedFileWithCommitFileSize / predictedFileSize > 0.3
                    ) {
                        filteredCandidates[file] = score
                    }
                }
            }
        }

        return filteredCandidates
                .map { it.key }
    }

}