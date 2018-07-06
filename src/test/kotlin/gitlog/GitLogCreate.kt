package gitlog

import gitLog.createGitLog
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import java.io.File

class GitLogCreate {
    private fun createImpl(repositoryName: String) {
        val log = createGitLog(File("data/repository/$repositoryName"))
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
}