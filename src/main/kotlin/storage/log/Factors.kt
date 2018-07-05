package storage.log

import commitInfo.CommittedFile
import predict.getIntersection
import predict.getRateForCommits

fun getFactors(
        firstFile: CommittedFile,
        secondFile: CommittedFile,
        time: Long,
        maxByCommit: Int
): Map<String, Map<String, Number>> {
    val factors = HashMap<String, Number>()
    factors["A"] = firstFile.getCommits().size
    factors["B"] = secondFile.getCommits().size
    factors["intersection"] = getIntersection(firstFile, secondFile).size
    factors["A rate"] = getRateForCommits(firstFile.getCommits(), time)
    factors["B rate"] = getRateForCommits(secondFile.getCommits(), time)
    factors["intersection rate"] = getRateForCommits(getIntersection(firstFile, secondFile), time)
    factors["max by commit"] = maxByCommit
    return hashMapOf("${firstFile.id}_${secondFile.id}" to factors)
}