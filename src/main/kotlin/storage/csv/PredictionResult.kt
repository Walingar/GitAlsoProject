package storage.csv

import predict.PredictionType
import storage.dataset.DatasetType
import java.io.File

data class PredictionResult(private val right: Int, private val wrong: Int, private val rightSilent: Int, private val wrongSilent: Int) {
    override fun toString() = "$right, $wrong, $rightSilent, $wrongSilent${System.lineSeparator()}"

    private fun getHeader() = "Repository, Dataset, Decision Function, Right, Wrong, Right silent, Wrong silent${System.lineSeparator()}"

    fun addToCSV(csvFile: File, repositoryName: String, datasetType: DatasetType, predictionType: PredictionType) {
        val newCSVData = StringBuilder()

        if (!csvFile.exists()) {
            csvFile.parentFile.mkdirs()
            csvFile.createNewFile()
        }

        val currentCSVData = csvFile.readText()
        if (currentCSVData.isBlank()) {
            newCSVData.append(getHeader())
        } else {
            newCSVData.append(currentCSVData)
        }

        newCSVData.append("$repositoryName, $datasetType, $predictionType, ${this}")

        csvFile.writeText(newCSVData.toString())
    }
}