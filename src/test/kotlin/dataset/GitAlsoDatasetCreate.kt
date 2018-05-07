package dataset

import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.File
import GitAlsoService
import index.parseIndex

class GitAlsoDatasetCreate {

    private val startTime = 1483228800L
    private val endTime = 1488326400L

    private fun parseImpl(repositoryName: String) {
        val directoryIndex = File("data/index/$repositoryName")
        val service = GitAlsoService()
        parseIndex(service, directoryIndex)
        assertTrue(service.getCommits().isNotEmpty())

        val directoryDataset = File("data/dataset/$repositoryName")
        val dataset = createRandomDataset(service, startTime, endTime)

        assertTrue(dataset.isNotEmpty())

        printDataset(directoryDataset, "fullDataset.ga", dataset)
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