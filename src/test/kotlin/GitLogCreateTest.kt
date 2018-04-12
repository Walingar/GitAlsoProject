import com.intellij.testFramework.LightPlatformTestCase
import gitLog.createGitLogWithTimestampsAndFiles
import gitLog.createSimpleGitLog
import org.junit.Test

class GitLogCreateTest : LightPlatformTestCase() {

    @Test
    fun testSimpleRepoInTempProject() {
        val log = createSimpleGitLog(getProject())
        assertTrue(log != null && log.isEmpty())
        println(log)
    }

    @Test
    fun testRepoInTempProjectWithTimeStampAndFiles() {
        val log = createGitLogWithTimestampsAndFiles(getProject())
        assertTrue(log != null && log.isEmpty())
        println(log)
    }
}
