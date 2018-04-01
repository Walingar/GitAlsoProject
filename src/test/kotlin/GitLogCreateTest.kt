import gitLog.createGitLogWithTimestampsAndFiles
import gitLog.createSimpleGitLog
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.File

class GitLogCreateTest {
    @Test
    fun testSimpleRepoInCurDir() {
        val log = createSimpleGitLog(File("."))
        assertTrue(log != null && log.isNotEmpty())
        println(log)
    }

    @Test
    fun testRepoInCurDirWithTimeStampAndFiles() {
        val log = createGitLogWithTimestampsAndFiles(File("."))
        assertTrue(log != null && log.isNotEmpty())
        println(log)
    }
}
