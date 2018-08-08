package gitlog

import getGitLog
import junit.framework.TestCase.assertNotNull
import org.junit.Test

class GitLogCreate {
    private fun createImpl(repositoryName: String) {
        val log = getGitLog(repositoryName)
        assertNotNull(log)
        print(log)
    }

    @Test
    fun testITMO_Java() {
        createImpl("ITMO_Java")
    }

    @Test
    fun testPandas() {
        createImpl("pandas")
    }

    @Test
    fun testIJCommunity() {
        createImpl("intellij-community")
    }

    @Test
    fun testIJRust() {
        createImpl("intellij-rust")
    }

    @Test
    fun testKotlin() {
        createImpl("kotlin")
    }
}