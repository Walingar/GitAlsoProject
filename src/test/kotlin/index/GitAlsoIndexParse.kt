package index

import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.File
import GitAlsoService

class GitAlsoIndexParse {
    private fun parseImpl(repositoryName: String) {
        val directory = File("data/index/$repositoryName")
        val service = GitAlsoService()
        parseIndex(service, directory)
        assertTrue(service.getCommits().isNotEmpty())
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