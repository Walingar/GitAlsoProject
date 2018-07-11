package estimate

import getDatasetFromFile
import getGitAlsoServiceFromIndex
import org.junit.Test
import org.junit.runner.RunWith
import predict.PredictionProvider
import predict.PredictionType
import predict.baseline.RandomPredictionProvider
import predict.current.TimePredictionProvider
import storage.csv.PredictionResult
import storage.dataset.DatasetType
import java.io.File
import org.junit.runners.Parameterized


@RunWith(Parameterized::class)
class GitAlsoEstimate(val repositoryName: String, val datasetType: DatasetType, val predictionType: PredictionType) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            val parameters = ArrayList<Array<Any>>()
            val repositories = arrayListOf("pandas", "intellij-community")
            for (repository in repositories) {
                for (datasetType in DatasetType.values()) {
                    for (predictionType in PredictionType.values()) {
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
            PredictionType.SIMPLE_FORMULA -> TODO("SIMPLE_FORMULA not implemented")
            PredictionType.FORMULA -> TODO("FORMULA not implemented")
            PredictionType.WEIGHT -> TODO("WEIGHT not implemented")
        }
    }

    private fun addToCSV(repositoryName: String, datasetType: DatasetType, predictionType: PredictionType, result: PredictionResult) {
        val csvFile = File("data/results/result1.csv")
        result.addToCSV(csvFile, repositoryName, datasetType, predictionType)
    }


    private fun predict(repositoryName: String, datasetType: DatasetType, predictionType: PredictionType) {
        val service = getGitAlsoServiceFromIndex(repositoryName)
        val dataset = getDatasetFromFile(repositoryName, datasetType)
        val predictionProvider = getPredictionProvider(predictionType)
        val estimator = Estimator(service)

        val prediction = estimator.predictForDatasetWithForgottenFiles(dataset, predictionProvider)
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