package index

import getGitAlsoServiceFromIndex
import getGitLogAndParseIt
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class GitAlsoIndexParse {
    private fun parseImpl(repositoryName: String) {
        val serviceFromIndex = getGitAlsoServiceFromIndex(repositoryName)
        val serviceFromLog = getGitLogAndParseIt(repositoryName)

        assertEquals(serviceFromLog.commits.size, serviceFromIndex.commits.size)
        assertTrue(serviceFromIndex.commits.all { (time, commit) -> commit.files == serviceFromLog.commits[time]!!.files })
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
    fun testIJRust() {
        parseImpl("intellij-rust")
    }

    @Test
    fun testKotlin() {
        parseImpl("kotlin")
    }
}