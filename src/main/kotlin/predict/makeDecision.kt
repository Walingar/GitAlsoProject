package predict

import commit.Commit
import commit.CommittedFile
import java.lang.Double.max
import kotlin.math.sqrt

import GitAlsoService

var curService = GitAlsoService()

private fun getIntersection(firstFile: CommittedFile, secondFile: CommittedFile): List<Commit> {
    val intersection = ArrayList<Commit>()
    for (firstCommit in firstFile.getCommits()) {
        for (secondCommit in secondFile.getCommits()) {
            if (firstCommit == secondCommit) {
                intersection.add(firstCommit)
            }
        }
    }
    return intersection.filter { it.time < currentTime }
}

private fun getTimeRate(time: Long, startTime: Long): Double {
    val week = 604800
    val parameter = sqrt(((startTime - time) / week).toDouble())
    if (parameter == 0.0) {
        return sqrt(week.toDouble())
    }
    return 1 / parameter
}

private fun getCommitRate(time: Long, startTime: Long): Double {
    val chunk = 200
    val parameter = sqrt(((curService.getCommits().filter { it.time in (time + 1)..(startTime - 1) }.size.toDouble()) / chunk))
    if (parameter == 0.0) {
        return sqrt(chunk.toDouble())
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

fun getProbabilityWithTime(firstFile: CommittedFile, secondFile: CommittedFile, time: Long, n: Int, maxByCommit: Int): Double {
    val intersection = getRateForCommits(getIntersection(firstFile, secondFile), time)
    val rateForFirstFile = getRateForCommits(firstFile.getCommits().filter { it.time < currentTime }, time)
    val rateForSecondFile = getRateForCommits(secondFile.getCommits().filter { it.time < currentTime }, time)
    val union = rateForFirstFile + rateForSecondFile - intersection
    if (union == 0.0) {
        return 0.0
    }
    return (intersection * sqrt(rateForFirstFile * rateForSecondFile)) /
            (union * max(n.toDouble(), maxByCommit.toDouble()))
}