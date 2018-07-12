package predict

import commitInfo.Commit
import commitInfo.CommittedFile

fun getIntersection(firstFile: CommittedFile, secondFile: CommittedFile): List<Commit> {
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

fun getMaxByCommit(commit: Commit): Int {
    val currentTime = commit.time
    var maxByCommit = 0
    for (file in commit.getFiles()) {
        val temp = file.getCommits().filter { it.time < currentTime }.size
        if (temp >= maxByCommit) {
            maxByCommit = temp
        }
    }
    return maxByCommit
}

fun isPredictable(commit: Commit, fileIDToPredict: Int): Boolean {
    for (file in commit.getFiles()) {
        for (fileCommit in file.getCommits().filter { it.time < commit.time }) {
            for (secondFile in fileCommit.getFiles()) {
                if (secondFile.id == fileIDToPredict) {
                    return true
                }
            }
        }
    }

    return false
}