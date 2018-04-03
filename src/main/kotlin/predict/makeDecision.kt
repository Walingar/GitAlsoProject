package predict

import commit.Commit
import commit.CommittedFile
import java.lang.Double.max
import kotlin.math.sqrt


private fun getIntersection(firstFile: CommittedFile, secondFile: CommittedFile): Collection<Commit> {
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
    var ans = 1.0
    for (commit in commits) {
        if (commit.getTime() < startTime) {
            ans += getTimeRate(commit.getTime(), startTime)
        }
    }
    return ans
}

fun getProbabilityWithTime(firstFile: CommittedFile, secondFile: CommittedFile, time: Long, n: Int, maxByCommit: Int): Double {
    val intersection = getRateForCommits(getIntersection(firstFile, secondFile), time)
    val rateForFirstFile = getRateForCommits(firstFile.getCommits(), time)
    val rateForSecondFile = getRateForCommits(secondFile.getCommits(), time)
    val union = rateForFirstFile + rateForSecondFile - intersection
    return (intersection * sqrt(rateForFirstFile * rateForSecondFile)) /
            (union * max(n.toDouble(), maxByCommit.toDouble()))
}