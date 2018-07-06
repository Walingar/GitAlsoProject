package dataset


import getDatasetFromFile
import getDatasetFromService
import getGitAlsoServiceFromIndex
import junit.framework.TestCase.assertEquals
import org.junit.Test
import storage.dataset.DatasetType

class GitAlsoDatasetParse {

    private val startTime = 1483228800L
    private val endTime = 1488326400L

    private fun testDataset(repositoryName: String, datasetType: DatasetType) {
        val datasetFromFile = getDatasetFromFile(repositoryName, datasetType)

        val service = getGitAlsoServiceFromIndex(repositoryName)
        val datasetFromService = getDatasetFromService(repositoryName, datasetType, service, startTime, endTime)
        
        assertEquals(datasetFromFile, datasetFromService)
    }

    private fun testFullDataset(repositoryName: String) {
        testDataset(repositoryName, DatasetType.FULL)
    }


    @Test
    fun testPandas() {
        testFullDataset("pandas")
    }

    @Test
    fun testIJCommunity() {
        testFullDataset("intellij-community")
    }
}