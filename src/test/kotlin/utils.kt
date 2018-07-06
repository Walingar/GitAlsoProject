import commitInfo.PipeLineCommit
import gitLog.createGitLog
import gitLog.getCommitsFromGitLog
import repository.GitAlsoService
import storage.dataset.*
import storage.index.IndexFileManager
import java.io.File

fun getGitLog(repositoryName: String) = createGitLog(File("data/repository/$repositoryName"))

fun getGitLogAndParseIt(repositoryName: String): GitAlsoService {
    val log = getGitLog(repositoryName)
    val service = GitAlsoService()
    getCommitsFromGitLog(log!!, service)
    return service
}

fun createGitAlsoIndex(repositoryName: String) {
    val service = getGitLogAndParseIt(repositoryName)
    val indexFileManager = IndexFileManager(repositoryName)
    indexFileManager.write(service)
}

fun getGitAlsoServiceFromIndex(repositoryName: String): GitAlsoService {
    val service = GitAlsoService()
    val indexFileManager = IndexFileManager(repositoryName)
    indexFileManager.read(service)
    return service
}

fun getDatasetFileManager(repositoryName: String, datasetType: DatasetType): DatasetFileManager {
    return when (datasetType) {
        DatasetType.RANDOM -> RandomDatasetFileManager(repositoryName)
        DatasetType.SIMPLE -> SimpleDatasetFileManager(repositoryName)
        DatasetType.FULL -> FullDatasetFileManager(repositoryName)
    }
}

fun getDatasetFromService(repositoryName: String, datasetType: DatasetType, service: GitAlsoService, startTime: Long, endTime: Long): List<PipeLineCommit> {
    val datasetFileManager = getDatasetFileManager(repositoryName, datasetType)

    return datasetFileManager.createDataset(service, startTime, endTime)
}

fun createDataset(repositoryName: String, datasetType: DatasetType, service: GitAlsoService, startTime: Long, endTime: Long) {
    val datasetFileManager = getDatasetFileManager(repositoryName, datasetType)

    datasetFileManager.write(service, startTime, endTime)
}

fun getDatasetFromFile(repositoryName: String, datasetType: DatasetType): List<PipeLineCommit> {
    val datasetFileManager = getDatasetFileManager(repositoryName, datasetType)

    return datasetFileManager.read()
}