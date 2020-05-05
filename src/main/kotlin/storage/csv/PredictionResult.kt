package storage.csv

import predict.PredictionType
import storage.dataset.DatasetType
import java.io.File

data class PredictionResult(val rightAtFirst: Int, val right: Int, val wrong: Int, val silent: Int) {
    override fun toString() = "$rightAtFirst, $right, $wrong, $silent${System.lineSeparator()}"

    private fun getHeader() = "Repository, Dataset, Decision Function, Right at top1, Right, Wrong, Silent${System.lineSeparator()}"

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