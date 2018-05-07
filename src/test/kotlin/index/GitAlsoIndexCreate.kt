package index

import gitLog.createGitLogWithTimestampsAuthorsAndFiles
import gitLog.getCommitsFromGitLogWithTimestampsAuthorsAndFiles
import org.junit.Test
import java.io.File
import GitAlsoService

class GitAlsoIndexCreate {
    private fun createImpl(repositoryName: String) {
        val log = createGitLogWithTimestampsAuthorsAndFiles(File("data/repository/$repositoryName"))
        val service = GitAlsoService()
        getCommitsFromGitLogWithTimestampsAuthorsAndFiles(log!!, service)

        val directory = File("data/index/$repositoryName")

        printIndex(directory, "commitsIndex.ga", createCommitsIndex(service))
        printIndex(directory, "filesIndex.ga", createFilesIndex(service))
        printIndex(directory, "commitsDataIndex.ga", createCommitsDataIndex(service))
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