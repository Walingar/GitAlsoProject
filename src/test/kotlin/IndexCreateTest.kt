import gitLog.createGitLogWithTimestampsAndFiles
import gitLog.getCommitsFromGitLogWithTimestampsAndFiles
import index.createIndexFromCommits
import junit.framework.Assert
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.File

class IndexCreateTest {
    @Test
    fun testIndexCreateForThisRepo() {
//        val commits = getCommitsFromGitLogWithTimestampsAndFiles(log!!)
//        val index = createIndexFromCommits(commits, "current")
//        for (file in index.getIndex()) {
//            assertTrue(file.value.size < commits.size)
//        }
    }
}