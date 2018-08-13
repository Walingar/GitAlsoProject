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
import java.util.concurrent.Executors


@RunWith(Parameterized::class)
class GitAlsoEstimate(val repositoryName: String, val datasetType: DatasetType, val predictionType: PredictionType) {
    private val csvFile = File("data/results/resultTest.csv")

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
                            PredictionType.WEIGHT_WITH_FILTER,
                            PredictionType.WEIGHT_WITH_FILTER_TUNED)
                    val currentPredictionType = arrayOf(PredictionType.WEIGHT_WITH_FILTER_TUNED)
                    for (predictionType in currentPredictionType) {
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
            PredictionType.TOTAL_WEIGHT -> TotalWeightPredictionProvider()
            PredictionType.WEIGHT_WITH_FILTER_TUNED -> WeightWithFilterTunedPredictionProvider()
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

    private fun score(result: PredictionResult) = result.wrong * (-5) + result.right * (2) + result.silent

    private fun estimate(repositoryName: String, datasetType: DatasetType, predictionType: PredictionType) {
        val dir = File("temp/$repositoryName/$datasetType")
        dir.mkdirs()

        val outputFile = dir.resolve("temp.log")
        outputFile.createNewFile()
        outputFile.writeText("minProb, m, fileSize, score\n")

        val service = getGitAlsoServiceFromIndex(repositoryName)
        val dataset = getDatasetFromFile(repositoryName, datasetType)
        val estimator = Estimator(service)
        var s = 0
        for (minProb in listOf(0.0)) {
            for (m in 1..100) {
                for (fileSize in 1..10) {
                    val predictionProvider = WeightWithFilterTunedPredictionProvider(minProb, m.toDouble() / 10, fileSize.toDouble())
                    val result = estimator.predictForDatasetWithForgottenFiles(dataset, predictionProvider)
                    val score = score(result)
                    outputFile.appendText("$minProb, $m, $fileSize, $score\n")
                    s++
                    print(s)
                }
            }
        }
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