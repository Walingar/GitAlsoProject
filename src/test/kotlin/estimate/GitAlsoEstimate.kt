package estimate

import getDatasetFromFile
import getGitAlsoServiceFromIndex
import org.junit.Test
import org.junit.runner.RunWith
import predict.PredictionProvider
import predict.PredictionType
import storage.csv.PredictionResult
import storage.dataset.DatasetType
import java.io.File
import org.junit.runners.Parameterized
import predict.baseline.RandomPredictionProvider
import predict.baseline.SimplePredictionProvider
import predict.current.TimePredictionProvider


@RunWith(Parameterized::class)
class GitAlsoEstimate(val repositoryName: String, val datasetType: DatasetType, val predictionType: PredictionType) {

    private val csvFile = File("data/results/resultWithSimple.csv")


    companion object {

        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
//            val randomPredictCount = 50
            val parameters = ArrayList<Array<Any>>()
            val repositories = arrayListOf("pandas", "intellij-community")
            for (repository in repositories) {
                for (datasetType in DatasetType.values()) {
                    for (predictionType in PredictionType.values()) {
//                        if (predictionType == PredictionType.RANDOM) {
//                            for (i in 1..randomPredictCount) {
//                                parameters += arrayOf(repository, datasetType, predictionType)
//                            }
//                        }
                        parameters += arrayOf(repository, datasetType, predictionType)
                    }
                }
            }
            return parameters
        }
    }

    private fun getPredictionProvider(predictionType: PredictionType): PredictionProvider {
        return when (predictionType) {

            PredictionType.RANDOM -> RandomPredictionProvider()
            PredictionType.TIME -> TimePredictionProvider(14, 0.4)
            PredictionType.COMMIT_TIME -> TODO("COMMIT_TIME not implemented")
            PredictionType.SIMPLE_FORMULA -> SimplePredictionProvider()
            PredictionType.FORMULA -> TODO("FORMULA not implemented")
            PredictionType.WEIGHT -> TODO("WEIGHT not implemented")
        }
    }

    private fun addToCSV(repositoryName: String, datasetType: DatasetType, predictionType: PredictionType, result: PredictionResult) {
        result.addToCSV(csvFile, repositoryName, datasetType, predictionType)
    }


    private fun predict(repositoryName: String, datasetType: DatasetType, predictionType: PredictionType) {
        println(predictionType)
        val service = getGitAlsoServiceFromIndex(repositoryName)
        val dataset = getDatasetFromFile(repositoryName, datasetType)
        val predictionProvider = getPredictionProvider(predictionType)
        val estimator = Estimator(service)
        if (datasetType == DatasetType.SIMPLE) {
            return
        }
        var prediction = estimator.predictForDatasetWithForgottenFiles(dataset, predictionProvider)
        if (predictionType == PredictionType.RANDOM) {
            val randomPredictCount = 200
            for (i in 2..randomPredictCount) {
                val newPrediction = estimator.predictForDatasetWithForgottenFiles(dataset, predictionProvider)
                prediction = PredictionResult(
                        prediction.right + newPrediction.right,
                        prediction.wrong + newPrediction.wrong,
                        prediction.rightSilent + newPrediction.rightSilent,
                        prediction.wrongSilent + newPrediction.wrongSilent)
            }
            prediction = PredictionResult(
                    prediction.right / randomPredictCount,
                    prediction.wrong / randomPredictCount,
                    prediction.rightSilent / randomPredictCount,
                    prediction.wrongSilent / randomPredictCount
            )
        }
        addToCSV(repositoryName, datasetType, predictionType, prediction)
    }


    @Test
    fun test() {
        try {
            predict(repositoryName, datasetType, predictionType)
        } catch (e: NotImplementedError) {
            println(e)
        }
    }
}