package com.jetbrains.gitalso.predict

import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import kotlin.math.min

class WeightWithFilterTunedPredictionProvider(private val minProb: Double = 0.3, private val m: Double = 3.2, private val commitSize: Double = 8.0) {

    private class VoteProvider(private val m: Double) {
        var result = 0.0
        private var votesCounter = 0.0
        private var votesSum = 0.0

        private fun R() = if (votesCounter != 0.0) votesSum / votesCounter else 0.0

        fun vote(rate: Double, weight: Double) {

            votesCounter += weight
            votesSum += rate

            val R = R()
            val v = votesCounter
            result = (v / (m + v)) * R + (m / (m + v)) * 0.25
        }
    }

    private fun vote(firstFile: CommittedFile, commit: Commit): Map<CommittedFile, Double> {
        val candidates = HashMap<CommittedFile, VoteProvider>()
        val filteredCommits = firstFile.commits
        val commits = filteredCommits
                .sortedBy { it.time }
                .reversed()
                .take(20)
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


    fun commitPredict(commit: Commit, maxPredictedFileCount: Int = 5): List<CommittedFile> {
        val candidates = HashMap<CommittedFile, Double>()

        for (file in commit.files) {
            val currentVotes = vote(file, commit)
            for ((currentFile, currentVote) in currentVotes) {
                candidates.merge(currentFile, currentVote, Double::plus)
            }
        }

        return candidates
                .filterValues { it / commit.files.size > minProb }
                .toList()
                .sortedByDescending { it.second }
                .take(maxPredictedFileCount)
                .map { it.first }
    }
}