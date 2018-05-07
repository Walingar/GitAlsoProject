package estimate

import GitAlsoService
import dataset.PipeLineCommit
import predict.predictForCommit
import commit.Commit
import predict.curService

class Estimator(val service: GitAlsoService, val dataset: List<PipeLineCommit>) {
    private val extremeProbability = 0.1

    private fun createCommit(pipeLineCommit: PipeLineCommit): Commit {
        val mapIDToFile = service.getIDTofile()
        val commit = Commit(pipeLineCommit.time)
        for (id in pipeLineCommit.files) {
            commit.addFile(mapIDToFile[id]!!)
        }
        return commit
    }

    private fun getCommitIndex(commit: Commit): Int {
        return service.getCommits().indexOf(commit)
    }

    fun predictForSimpleDataset(n: Int = 14, minProbability: Double = 0.4, maxPredict: Int = 5) {
        var countSilent = 0
        var countRight = 0
        curService = service
        for (pipeLineCommit in dataset) {
            val commit = createCommit(pipeLineCommit)
            val commits = service.getCommits()
            val index = getCommitIndex(commit)
            val predict = predictForCommit(commit, n, minProbability, maxPredict)

            if (predict.isEmpty()) {
                countSilent++
                continue
            }

            loop@ for (i in index + 1..index + 6) {
                val curCommit = commits[i]
                for (file in predict) {
                    if (file in curCommit.getFiles()) {
                        countRight++
                        println("commit: $curCommit predict: $predict")
                        break@loop
                    }
                }
            }
        }

        println(countSilent)
        println(countRight)
    }

    fun predictForRandomDataset(n: Int = 14, minProbability: Double = 0.4, maxPredict: Int = 5) {
        var countSilent = 0
        var countRight = 0
        curService = service
        for (pipeLineCommit in dataset) {
            val commit = createCommit(pipeLineCommit)

            val predict = predictForCommit(commit, n, minProbability, maxPredict)
            if (predict.isEmpty()) {
                countSilent++
                continue
            }

            for (id in pipeLineCommit.forgottenFiles) {
                val mapIDToFile = service.getIDTofile()
                if (mapIDToFile[id]!! in predict) {
                    println("commit: ${pipeLineCommit.time} file: $id ")
                    countRight++
                    break
                }
            }
            println("commit: ${pipeLineCommit.time} predict: $predict required: ${pipeLineCommit.forgottenFiles}")
        }

        println(countSilent)
        println(countRight)
    }
}