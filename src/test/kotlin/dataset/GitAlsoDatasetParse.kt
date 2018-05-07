package dataset

import index.parseIndex
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.File
import GitAlsoService
import estimate.Estimator

class GitAlsoDatasetParse {

    private val datasetName = "fullDataset.ga"

    private fun parseImpl(repositoryName: String) {
        val directoryDataset = File("data/dataset/$repositoryName")
        val fileDataset = directoryDataset.resolve(datasetName)

        val dataset = parseDataset(fileDataset.readText())

        assertTrue(dataset.isNotEmpty())
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