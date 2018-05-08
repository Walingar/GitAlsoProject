package predict

import commit.Commit
import commit.CommittedFile
import kotlin.math.max
import kotlin.math.sqrt

private fun getIntersection(firstFile: CommittedFile, secondFile: CommittedFile): List<Commit> {
    val intersection = ArrayList<Commit>()
    for (firstCommit in firstFile.getCommits()) {
        for (secondCommit in secondFile.getCommits()) {
            if (firstCommit == secondCommit) {
                intersection.add(firstCommit)
            }
        }
    }
    return intersection
}

private fun getTimeRate(time: Long, startTime: Long): Double {
    val week = 604800
    val parameter = sqrt(((startTime - time) / week).toDouble())
    if (parameter == 0.0) {
        return sqrt(week.toDouble())
    }
    return 1 / parameter
}

fun getRateForCommits(commits: Collection<Commit>, startTime: Long): Double {
    var ans = 0.0
    for (commit in commits) {
        if (commit.time < startTime) {
            ans += getTimeRate(commit.time, startTime)
        }
    }
    return ans
}

fun getSimpleRateForFile(file: CommittedFile, commit: Commit): Int {
    var rate = 0
    for (secondFile in commit.getFiles()) {
        if (file != secondFile) {
            val intersection = getIntersection(file, secondFile).size
            val rateForFirstFile = file.getCommits().size
            val rateForSecondFile = secondFile.getCommits().size
            val union = rateForFirstFile + rateForSecondFile - intersection
            rate = max(rate, intersection * 100 / union)
        }
    }
    return rate
}

fun getProbabilityWithTime(firstFile: CommittedFile, secondFile: CommittedFile, time: Long, n: Int, maxByCommit: Int): Double {
    val intersection = getRateForCommits(getIntersection(firstFile, secondFile), time)
    val rateForFirstFile = getRateForCommits(firstFile.getCommits(), time)
    val rateForSecondFile = getRateForCommits(secondFile.getCommits(), time)
    val union = rateForFirstFile + rateForSecondFile - intersection
    if (union == 0.0) {
        return 0.0
    }
    return (intersection * sqrt(rateForFirstFile * rateForSecondFile)) /
            (union * max(n.toDouble(), maxByCommit.toDouble()))
}