import gitLog.createGitLog
import gitLog.getCommitsFromGitLog
import repository.GitAlsoService
import java.io.File

fun getGitLog(repositoryName: String) = createGitLog(File("data/repository/$repositoryName"))

fun getGitLogAndParseIt(repositoryName: String): GitAlsoService {
    val log = getGitLog(repositoryName)
    val service = GitAlsoService()
    getCommitsFromGitLog(log!!, service)
    return service
}

