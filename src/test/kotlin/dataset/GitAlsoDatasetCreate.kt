package dataset

import createDataset
import getGitAlsoServiceFromIndex
import org.junit.Test
import storage.dataset.DatasetType

class GitAlsoDatasetCreate {

    private val startTime = 1483228800L
    private val endTime = 1488326400L

    private fun fullDataset(repositoryName: String) {
        val service = getGitAlsoServiceFromIndex(repositoryName)
        createDataset(repositoryName, DatasetType.FULL, service, startTime, endTime)
    }

    private fun simpleDataset(repositoryName: String) {
        val service = getGitAlsoServiceFromIndex(repositoryName)
        createDataset(repositoryName, DatasetType.SIMPLE, service, startTime, endTime)
    }

    private fun randomDataset(repositoryName: String) {
        val service = getGitAlsoServiceFromIndex(repositoryName)
        createDataset(repositoryName, DatasetType.RANDOM, service, startTime, endTime)
    }

    private fun createAllDatasetTypes(repositoryName: String) {
        fullDataset(repositoryName)
        simpleDataset(repositoryName)
        randomDataset(repositoryName)
    }

    @Test
    fun testPandas() {
        createAllDatasetTypes("pandas")
    }

    @Test
    fun testGoogleTest() {
        createAllDatasetTypes("googletest")
    }

    @Test
    fun testIJRust() {
        createAllDatasetTypes("intellij-rust")
    }

    @Test
    fun testKotlin() {
        createAllDatasetTypes("kotlin")
    }

    @Test
    fun testIJCommunity() {
        createAllDatasetTypes("intellij-community")
    }
}