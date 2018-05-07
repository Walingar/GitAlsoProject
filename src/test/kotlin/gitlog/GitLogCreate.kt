package gitlog

import gitLog.createGitLogWithTimestampsAuthorsAndFiles
import junit.framework.TestCase.assertNotNull
import org.junit.Test
import java.io.File

class GitLogCreate {
    private fun createImpl(repositoryName: String) {
        val log = createGitLogWithTimestampsAuthorsAndFiles(File("data/repository/$repositoryName"))
        print(log)
        assertNotNull(log)
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