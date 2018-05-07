package estimate

import dataset.parseDataset
import index.parseIndex
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.io.File
import GitAlsoService


class GitAlsoEstimate {

    private val datasetName = "fullDataset.ga"

    private fun parseImpl(repositoryName: String) {
        val directoryDataset = File("data/dataset/$repositoryName")
        val fileDataset = directoryDataset.resolve(datasetName)

        val dataset = parseDataset(fileDataset.readText())

        assertTrue(dataset.isNotEmpty())

        val directory = File("data/index/$repositoryName")
        val service = GitAlsoService()
        parseIndex(service, directory)

        val estimator = Estimator(service, dataset)
        estimator.predictForRandomDataset()
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