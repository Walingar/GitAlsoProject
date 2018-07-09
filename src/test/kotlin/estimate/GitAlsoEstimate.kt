package estimate

import getDatasetFromFile
import getGitAlsoServiceFromIndex
import org.junit.Test
import predict.baseline.RandomPredictionProvider
import predict.current.TimePredictionProvider
import storage.dataset.DatasetType


class GitAlsoEstimate {
    private fun timePrediction(repositoryName: String, datasetType: DatasetType) {
        val service = getGitAlsoServiceFromIndex(repositoryName)
        val dataset = getDatasetFromFile(repositoryName, datasetType)
        val predictionProvider = TimePredictionProvider(14, 0.4)
        val estimator = Estimator(service)

        estimator.predictForDatasetWithForgottenFiles(dataset, predictionProvider)
    }

    private fun randomPrediction(repositoryName: String, datasetType: DatasetType) {
        val service = getGitAlsoServiceFromIndex(repositoryName)
        val dataset = getDatasetFromFile(repositoryName, datasetType)
        val predictionProvider = RandomPredictionProvider()
        val estimator = Estimator(service)

        estimator.predictForDatasetWithForgottenFiles(dataset, predictionProvider)
    }

    @Test
    fun `test pandas time prediction for full dataset`() {
        timePrediction("pandas", DatasetType.FULL)
    }

    @Test
    fun `test pandas time prediction for random dataset`() {
        timePrediction("pandas", DatasetType.RANDOM)
    }

    @Test
    fun `test pandas random prediction for full dataset`() {
        randomPrediction("pandas", DatasetType.FULL)
    }


    @Test
    fun `test pandas random prediction for random dataset`() {
        randomPrediction("pandas", DatasetType.RANDOM)
    }


    @Test
    fun `test intellij-community time prediction for full dataset`() {
        timePrediction("intellij-community", DatasetType.FULL)
    }

    @Test
    fun `test intellij-community time prediction for random dataset`() {
        timePrediction("intellij-community", DatasetType.RANDOM)
    }


    @Test
    fun `test intellij-community random prediction for full dataset`() {
        randomPrediction("intellij-community", DatasetType.FULL)
    }


    @Test
    fun `test intellij-community random prediction for random dataset`() {
        randomPrediction("intellij-community", DatasetType.RANDOM)
    }
}