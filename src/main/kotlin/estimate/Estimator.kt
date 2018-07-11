package estimate

import commitInfo.Commit
import commitInfo.PipeLineCommit
import predict.PredictionProvider
import repository.GitAlsoService
import storage.csv.PredictionResult

class Estimator(private val service: GitAlsoService) {

    private fun createCommit(pipeLineCommit: PipeLineCommit): Commit {
        val mapIDToFile = service.mapIDToFile
        val commit = Commit(pipeLineCommit.time)
        for (id in pipeLineCommit.files) {
            commit.addFile(mapIDToFile[id]!!)
        }
        return commit
    }

    fun predictForDatasetWithForgottenFiles(dataset: List<PipeLineCommit>, predictionProvider: PredictionProvider): PredictionResult {

        var rightPrediction = 0
        var wrongPrediction = 0
        var rightSilentPrediction = 0
        var wrongSilentPrediction = 0

        for (pipeLineCommit in dataset) {
            val commit = createCommit(pipeLineCommit)
            println("Current commit: $commit")

            val prediction = predictionProvider.commitPredict(commit)

            println("Prediction: $prediction")
            println("Expected: ${pipeLineCommit.forgottenFiles}")

            var right = false

            for (predictedFile in prediction) {
                if (predictedFile.id in pipeLineCommit.forgottenFiles) {
                    right = true
                    rightPrediction += 1
                    break
                }
            }
            if (!right) {
                if (prediction.isEmpty()) {
                    if (pipeLineCommit.forgottenFiles.isEmpty()) {
                        rightSilentPrediction += 1
                    } else {
                        wrongSilentPrediction += 1
                    }
                } else {
                    wrongPrediction += 1
                }
            }
        }

        println()
        println("Right: $rightPrediction")
        println("Wrong: $wrongPrediction")
        println("Right silent: $rightSilentPrediction")
        println("Wrong silent: $wrongSilentPrediction")

        return PredictionResult(rightPrediction, wrongPrediction, rightSilentPrediction, wrongSilentPrediction)
    }
}