package predict

import commitInfo.Commit
import commitInfo.CommittedFile

fun getIntersection(firstFile: CommittedFile, secondFile: CommittedFile): List<Commit> {
    val intersection = ArrayList<Commit>()
    for (firstCommit in firstFile.commits) {
        for (secondCommit in secondFile.commits) {
            if (firstCommit == secondCommit) {
                intersection.add(firstCommit)
            }
        }
    }
    return intersection
}

fun getMaxByCommit(commit: Commit): Int {
    val currentTime = commit.time
    var maxByCommit = 0
    for (file in commit.files) {
        val temp = file.commits.filter { it.time < currentTime }.size
        if (temp >= maxByCommit) {
            maxByCommit = temp
        }
    }
    return maxByCommit
}