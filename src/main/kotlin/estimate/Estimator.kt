package estimate

import commitInfo.Commit
import commitInfo.PipeLineCommit
import predict.PredictionProvider
import repository.GitAlsoService
import storage.csv.PredictionResult

class Estimator(private val service: GitAlsoService) {

    private fun createCommit(pipeLineCommit: PipeLineCommit): Commit {
        val commit = Commit(pipeLineCommit.time)
        for (name in pipeLineCommit.files) {
            commit.addFile(service.files[name]!!)
        }
        return commit
    }

    fun predictForDatasetWithForgottenFiles(dataset: List<PipeLineCommit>, predictionProvider: PredictionProvider): PredictionResult {
        var rightPrediction = 0
        var wrongPrediction = 0
        var silentPrediction = 0
        var sizeCounter = 0
        var counterNonEmpty = 0
        var rightAtFirst = 0

        for (pipeLineCommit in dataset) {
            val commit = createCommit(pipeLineCommit)

            val prediction = predictionProvider.commitPredict(commit)


            var right = false
            sizeCounter += prediction.size
            if (prediction.isNotEmpty()) {
                counterNonEmpty++
            }

            for (predictedFile in prediction) {
                if (predictedFile.id in pipeLineCommit.forgottenFiles) {
                    right = true
                    if (prediction[0] == predictedFile) {
                        rightAtFirst++
                    }
                    rightPrediction++
                    break
                }
            }

            if (!right) {
                if (prediction.isEmpty()) {
                    silentPrediction++
                } else {
                    wrongPrediction++
                }
            }
        }
        println()
        //println(sizeCounter.toDouble() / counterNonEmpty)

        return PredictionResult(rightAtFirst, rightPrediction, wrongPrediction, silentPrediction)
    }


    fun compareTwoPredictionProviders(dataset: List<PipeLineCommit>, predictionProvider1: PredictionProvider, predictionProvider2: PredictionProvider) {

        fun printLog(pipeLineCommit: PipeLineCommit) {
            println("Current commit: ${pipeLineCommit.time}")
            println("Files: ${pipeLineCommit.files}")
            println("Forgotten files: ${pipeLineCommit.forgottenFiles}")
        }

        for (pipeLineCommit in dataset) {
            val commit = createCommit(pipeLineCommit)
            val prediction1 = predictionProvider1.commitPredict(commit)
            val prediction2 = predictionProvider2.commitPredict(commit)
            var right1 = false
            var right2 = false
            var wrong1 = false
            var wrong2 = false

            for (predictedFile in prediction1) {
                if (predictedFile.id in pipeLineCommit.forgottenFiles) {
                    right1 = true
                    break
                }
            }

            for (predictedFile in prediction2) {
                if (predictedFile.id in pipeLineCommit.forgottenFiles) {
                    right2 = true
                    break
                }
            }

            if (!right1 && prediction1.isNotEmpty()) {
                wrong1 = true
            }
            if (!right2 && prediction2.isNotEmpty()) {
                wrong2 = true
            }
            if (right1 && !right2) {
                printLog(pipeLineCommit)
                println("First prediction is right: $prediction1")
                println("Second prediction is not right: $prediction2")
                println()
            } else if (!wrong1 && wrong2) {
                printLog(pipeLineCommit)
                println("First prediction is not wrong: $prediction1")
                println("Second prediction is wrong: $prediction2")
                println()
            }
        }
    }

}