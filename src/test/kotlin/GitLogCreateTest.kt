import com.intellij.testFramework.LightPlatformTestCase
import com.intellij.testFramework.LightPlatformTestCase.getProject
import gitLog.createGitLogWithTimestampsAndFiles
import gitLog.createSimpleGitLog
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.File

class GitLogCreateTest : LightPlatformTestCase() {

    @Test
    fun testSimpleRepoInCurDir() {
        val log = createSimpleGitLog(getProject())
        assertTrue(log != null && log.isNotEmpty())
        println(log)
    }

    @Test
    fun testRepoInCurDirWithTimeStampAndFiles() {
        val log = createGitLogWithTimestampsAndFiles(getProject())
        assertTrue(log != null && log.isNotEmpty())
        println(log)
    }
}
