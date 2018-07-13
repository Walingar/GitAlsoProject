package gitlog

import getGitLogAndParseIt
import junit.framework.TestCase.assertTrue
import org.junit.Test

class GitLogParse {
    private fun parseImpl(repositoryName: String) {
        val service = getGitLogAndParseIt(repositoryName)

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

    @Test
    fun testGoogleTest() {
        parseImpl("googletest")
    }

    @Test
    fun testIJRust() {
        parseImpl("intellij-rust")
    }

    @Test
    fun testKotlin() {
        parseImpl("kotlin")
    }
}