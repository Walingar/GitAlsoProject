import gitLog.createGitLogWithTimestampsAndFiles
import gitLog.getCommitsFromGitLogWithTimestampsAndFiles
import index.createIndexFromCommits
import index.printIndexToFile
import junit.framework.Assert
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.File

class IndexPrintTest {
    @Test
    fun testIndexPrintForThisRepo(){
        val log = createGitLogWithTimestampsAndFiles(File("."))
        Assert.assertNotNull(log)
        val commits = getCommitsFromGitLogWithTimestampsAndFiles(log!!)
        val index = createIndexFromCommits(commits, "current")
        printIndexToFile(index)
        assertTrue(File("./index/").isDirectory)
    }
}