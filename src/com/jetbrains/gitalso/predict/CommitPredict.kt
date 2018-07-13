package com.jetbrains.gitalso.predict

import com.jetbrains.gitalso.commitInfo.Commit
import com.jetbrains.gitalso.commitInfo.CommittedFile
import java.lang.Integer.min

var currentTime = 0L

private fun predictForFileInCommit(firstFile: CommittedFile, time: Long, n: Int, maxByCommit: Int, minProb: Double):
        List<Pair<Double, CommittedFile>> {

    val was = HashSet<CommittedFile>()
    val scores = ArrayList<Pair<Double, CommittedFile>>()

    for (commit in firstFile.getCommits().filter { it.time < currentTime }) {
        for (secondFile in commit.getFiles()) {
            if (firstFile != secondFile && secondFile !in was) {
                was.add(secondFile)
                val prob = getProbabilityWithTime(firstFile, secondFile, time, n, maxByCommit)
                if (prob > minProb) {
                    scores.add(Pair(prob, secondFile))
                }
            }
        }
    }

    return scores
}

fun getMaxByCommit(commit: Commit): Int {
    var maxByCommit = 0
    for (file in commit.getFiles()) {
        val temp = file.getCommits().filter { it.time < currentTime }.size
        if (temp >= maxByCommit) {
            maxByCommit = temp
        }
    }
    return maxByCommit
}

val counted = HashMap<Pair<Long, CommittedFile>, List<Pair<Double, CommittedFile>>>()

fun predictForCommit(commit: Commit, n: Int = 14, minProb: Double = 0.4, maxPredict: Int = 5): List<CommittedFile> {
    currentTime = commit.time
    val maxByCommit = getMaxByCommit(commit)

    val scores = HashMap<CommittedFile, Double>()
    for (file in commit.getFiles()) {

        counted[Pair(commit.time, file)] = predictForFileInCommit(file, commit.time, n, maxByCommit, minProb)


        counted[Pair(commit.time, file)]!!.forEach {
            if (it.second !in commit.getFiles()) {
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
            .subList(0, min(scores.size, maxPredict))
}