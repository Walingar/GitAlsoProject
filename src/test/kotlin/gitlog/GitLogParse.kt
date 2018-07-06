package gitlog

import gitLog.createGitLog
import gitLog.getCommitsFromGitLog
import repository.GitAlsoService
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.File

class GitLogParse {
    private fun parseImpl(repositoryName: String) {
        val log = createGitLog(File("data/repository/$repositoryName"))
        val service = GitAlsoService()
        assertNotNull(log)
        getCommitsFromGitLog(log!!, service)
        assertTrue(service.commits.isNotEmpty())
        print(service.commits.joinToString("\n"))
    }

    @Test
    fun testITMO_Java() {
        parseImpl("ITMO_Java")
    }

    @Test
    fun testPandas() {
        parseImpl("pandas")
    }

    @Test
    fun testIJCommunity() {
        parseImpl("intellij-community")
    }
}