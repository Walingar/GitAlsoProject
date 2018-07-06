package index

import getGitAlsoServiceFromIndex
import getGitLogAndParseIt
import junit.framework.TestCase.assertEquals
import org.junit.Test

class GitAlsoIndexParse {
    private fun parseImpl(repositoryName: String) {
        val serviceFromIndex = getGitAlsoServiceFromIndex(repositoryName)
        val serviceFromLog = getGitLogAndParseIt(repositoryName)

        assertEquals(serviceFromLog.commits.size, serviceFromIndex.commits.size)
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

    @Test
    fun testTestRepository() {
        parseImpl("testRepo")
    }
}