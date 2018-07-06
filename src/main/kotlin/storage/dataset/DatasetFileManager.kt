package storage.dataset


import commitInfo.PipeLineCommit
import repository.GitAlsoService

abstract class DatasetFileManager(repositoryName: String) {
    private val datasetFilePathProvider = DatasetFilePathProvider(repositoryName)

    protected abstract val type: DatasetType

    abstract fun createDataset(service: GitAlsoService, startTime: Long, endTime: Long): List<PipeLineCommit>

    fun read(): List<PipeLineCommit> {
        val datasetFile = datasetFilePathProvider.getDatasetFile(type)
        val datasetString = datasetFile.readText()

        val dataset = ArrayList<PipeLineCommit>()
        for (commit in datasetString.lines()) {
            if (commit.isBlank()) {
                continue
            }
            val (timeString, filesString, forgottenFilesString) = commit.split(';')
            val time = timeString.toLong()
            val files = filesString.split(", ").map { file -> file.toInt() }.toList()
            val forgottenFiles = if (forgottenFilesString.isNotBlank()) {
                forgottenFilesString.split(", ").map { file -> file.toInt() }.toList()
            } else {
                arrayListOf()
            }

            dataset.add(PipeLineCommit(time, files, forgottenFiles))
        }

        return dataset
    }

    fun write(service: GitAlsoService, startTime: Long, endTime: Long) {
        val dataset = createDataset(service, startTime, endTime)
        val datasetFile = datasetFilePathProvider.getDatasetFile(type)

        datasetFile.writeText("")
        for (datasetCommit in dataset) {
            datasetFile.appendText(datasetCommit.toString() + System.lineSeparator())
        }
    }
}