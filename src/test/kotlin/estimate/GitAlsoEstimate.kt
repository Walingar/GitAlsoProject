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
import predict.current.CommitTimePredictionProvider
import predict.current.TimePredictionProvider
import predict.current.WeightPredictionProvider


@RunWith(Parameterized::class)
class GitAlsoEstimate(val repositoryName: String, val datasetType: DatasetType, val predictionType: PredictionType) {
    private val csvFile = File("data/results/resultTest.csv")

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            val parameters = ArrayList<Array<Any>>()
            val repositories = arrayListOf("pandas", "intellij-community")
            for (repository in repositories) {
                for (datasetType in DatasetType.values()) {
                    if (datasetType == DatasetType.SIMPLE) {
                        continue // TODO: add support of this dataset
                    }
                    for (predictionType in arrayOf(PredictionType.WEIGHT)) {
                        parameters += arrayOf(repository, datasetType, predictionType)
                    }
                }
            }
            return parameters
        }
    }

    private var m1 = 2.0
    private var m2 = 20.0
    private var commitSize = 4.0
    private var minProb = 0.6

    private fun getPredictionProvider(predictionType: PredictionType): PredictionProvider {
        return when (predictionType) {

            PredictionType.RANDOM -> RandomPredictionProvider()
            PredictionType.TIME -> TimePredictionProvider(14, 0.4)
            PredictionType.COMMIT_TIME -> CommitTimePredictionProvider(14, 0.35, 20)
            PredictionType.SIMPLE_FORMULA -> SimplePredictionProvider()
            PredictionType.WEIGHT -> WeightPredictionProvider(minProb, m1, m2, commitSize)
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
                    prediction.right + newPrediction.right,
                    prediction.wrong + newPrediction.wrong,
                    prediction.rightSilent + newPrediction.rightSilent,
                    prediction.wrongSilent + newPrediction.wrongSilent
            )
        }
        return PredictionResult(
                prediction.right / randomPredictCount,
                prediction.wrong / randomPredictCount,
                prediction.rightSilent / randomPredictCount,
                prediction.wrongSilent / randomPredictCount
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


    @Test
    fun test() {
        try {
            predict(repositoryName, datasetType, predictionType)
        } catch (e: NotImplementedError) {
            println(e)
        }
    }
}