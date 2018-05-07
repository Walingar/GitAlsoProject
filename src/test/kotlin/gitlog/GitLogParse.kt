package gitlog

import gitLog.createGitLogWithTimestampsAuthorsAndFiles
import gitLog.getCommitsFromGitLogWithTimestampsAuthorsAndFiles
import GitAlsoService
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import java.io.File

class GitLogParse {
    private fun parseImpl(repositoryName: String) {
        val log = createGitLogWithTimestampsAuthorsAndFiles(File("data/repository/$repositoryName"))
        val service = GitAlsoService()
        getCommitsFromGitLogWithTimestampsAuthorsAndFiles(log!!, service)
        assertNotNull(service)
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