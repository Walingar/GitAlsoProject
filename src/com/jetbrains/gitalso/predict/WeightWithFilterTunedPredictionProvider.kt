package com.jetbrains.gitalso.predict

import com.intellij.openapi.vcs.FilePath
import com.jetbrains.gitalso.commitInfo.Commit
import kotlin.math.min

class WeightWithFilterTunedPredictionProvider(private val minProb: Double = 0.3) {
    private val m: Double = 3.2
    private val commitSize: Double = 8.0

    private class VoteProvider(private val m: Double) {
        private var votesCounter = 0.0
        private var votesSum = 0.0

        private fun R() = if (votesCounter != 0.0) votesSum / votesCounter else 0.0

        fun result(): Double {
            val v = votesCounter
            return (v / (m + v)) * R() + (m / (m + v)) * 0.25
        }

        fun vote(rate: Double) {
            votesCounter += 1.0
            votesSum += rate
        }
    }

    private fun vote(fileCommits: Set<Commit>, commitFiles: Set<FilePath>): Map<FilePath, Double> {
        val candidates = HashMap<FilePath, VoteProvider>()
        val commits = fileCommits
                .sortedBy { it.time }
                .reversed()
                .take(20)
        for (fileCommit in commits) {
            for (secondFile in fileCommit.files) {
                if (secondFile in commitFiles) {
                    continue
                }
                val currentRate = min(1.0, commitSize / fileCommit.files.size.toDouble())
                candidates.getOrPut(secondFile) { VoteProvider(m) }.vote(currentRate)
            }
        }

        return candidates.mapValues { it.value.result() }
    }


    fun commitPredict(commit: Commit, maxPredictedFileCount: Int = 5): List<CommittedFile> {
        val candidates = HashMap<CommittedFile, Double>()

        for ((file, commits) in commit) {
            val currentVotes = vote(commits, commit.keys)
            for ((currentFile, currentVote) in currentVotes) {
                candidates.merge(currentFile, currentVote, Double::plus)
            }
        }

        return candidates
                .mapValues { it.value / commit.size }
                .filterValues { it > minProb }
                .toList()
                .sortedByDescending { it.second }
                .take(maxPredictedFileCount)
                .map { it.first }
    }
}