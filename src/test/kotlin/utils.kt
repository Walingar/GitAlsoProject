import gitLog.createGitLog
import gitLog.getCommitsFromGitLog
import repository.GitAlsoService
import storage.index.IndexFileManager
import storage.index.IndexFilePathProvider
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