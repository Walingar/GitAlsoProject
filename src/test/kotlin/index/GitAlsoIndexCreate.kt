package index

import createGitAlsoIndex
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.File
import storage.index.IndexFilePathProvider

class GitAlsoIndexCreate {
    private fun createImpl(repositoryName: String) {
        createGitAlsoIndex(repositoryName)
        val indexFilePathProvider = IndexFilePathProvider(repositoryName)

        assertTrue(File("data/index/$repositoryName").exists())
        assertTrue(indexFilePathProvider.getDataFiles().size == 3)
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
    fun testTestRepository() {
        createImpl("testRepo")
    }
}