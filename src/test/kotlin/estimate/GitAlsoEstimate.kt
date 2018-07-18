package estimate

import commitInfo.PipeLineCommit
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
import predict.current.*


@RunWith(Parameterized::class)
class GitAlsoEstimate(val repositoryName: String, val datasetType: DatasetType, val predictionType: PredictionType) {
    private val csvFile = File("data/results/resultMonday.csv")

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            val parameters = ArrayList<Array<Any>>()
            val allRepositories = arrayListOf("pandas", "intellij-community", "kotlin", "intellij-rust")
            val currentRepositories = arrayListOf("kotlin")
            for (repository in allRepositories) {
                for (datasetType in DatasetType.values()) {
                    if (datasetType == DatasetType.SIMPLE) {
                        continue // TODO: add support of this dataset
                    }
                    val allPredictionTypes = arrayOf(
                            PredictionType.SIMPLE_FORMULA,
                            PredictionType.TIME,
                            PredictionType.WEIGHT_NEW,
                            PredictionType.WEIGHT_WITH_FILTER)
                    val currentPredictionType = arrayOf(PredictionType.WEIGHT_WITH_FILTER)
                    for (predictionType in allPredictionTypes) {
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
            PredictionType.TIME -> TimePredictionProvider()
            PredictionType.COMMIT_TIME -> CommitTimePredictionProvider()
            PredictionType.SIMPLE_FORMULA -> SimplePredictionProvider()
            PredictionType.WEIGHT_NEW -> NewWeightPredictionProvider()
            PredictionType.WEIGHT -> SimpleWeightPredictionProvider()
            PredictionType.WEIGHT_WITH_FILTER -> WeightWithFilterPredictionProvider()
        }
    }

    private fun addToCSV(repositoryName: String, datasetType: DatasetType, predictionType: PredictionType, result: PredictionResult) {
        result.addToCSV(csvFile, repositoryName, datasetType, predictionType)
    }


    private fun averageRandomPrediction(
            estimator: Estimator,
            predictionProvider: PredictionProvider,
            dataset: List<PipeLineCommit>,
            randomPredictCount: Int
    ): PredictionResult {
        var prediction = estimator.predictForDatasetWithForgottenFiles(dataset, predictionProvider)
        for (i in 2..randomPredictCount) {
            val newPrediction = estimator.predictForDatasetWithForgottenFiles(dataset, predictionProvider)
            prediction = PredictionResult(
                    prediction.rightAtFirst + newPrediction.rightAtFirst,
                    prediction.right + newPrediction.right,
                    prediction.wrong + newPrediction.wrong,
                    prediction.silent + newPrediction.silent
            )
        }
        return PredictionResult(
                prediction.rightAtFirst / randomPredictCount,
                prediction.right / randomPredictCount,
                prediction.wrong / randomPredictCount,
                prediction.silent / randomPredictCount
        )
    }

    private fun predict(repositoryName: String, datasetType: DatasetType, predictionType: PredictionType) {
        println(predictionType)
        val service = getGitAlsoServiceFromIndex(repositoryName)
        val dataset = getDatasetFromFile(repositoryName, datasetType)
        val predictionProvider = getPredictionProvider(predictionType)
        val estimator = Estimator(service)
        val prediction = if (predictionType == PredictionType.RANDOM) {
            averageRandomPrediction(estimator, predictionProvider, dataset, 200)
        } else {
            estimator.predictForDatasetWithForgottenFiles(dataset, predictionProvider)
        }
        addToCSV(repositoryName, datasetType, predictionType, prediction)
    }

    private val compareWith = PredictionType.WEIGHT_WITH_FILTER

    private fun compare(repositoryName: String, datasetType: DatasetType, predictionType: PredictionType) {
        val service = getGitAlsoServiceFromIndex(repositoryName)
        val dataset = getDatasetFromFile(repositoryName, datasetType)
        val predictionProvider1 = getPredictionProvider(predictionType)
        val predictionProvider2 = getPredictionProvider(compareWith)
        val estimator = Estimator(service)
        estimator.compareTwoPredictionProviders(dataset, predictionProvider1, predictionProvider2)
    }

    @Test
    fun test() {
        try {
            println("TEST: $repositoryName, $datasetType, $predictionType")
            predict(repositoryName, datasetType, predictionType)
        } catch (e: NotImplementedError) {
            println(e)
        }
    }
}