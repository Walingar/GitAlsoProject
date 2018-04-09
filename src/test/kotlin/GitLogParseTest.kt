import com.intellij.testFramework.LightPlatformTestCase
import gitLog.getCommitsFromGitLogWithTimestampsAndFiles
import org.junit.Test
import java.io.File

class GitLogParseTest : LightPlatformTestCase() {

    @Test
    fun testGetCommitsFromThisRepoWithNonEmptyCommits() {
        val log = File("test/resources/log").readText()
        assertNotNull(log)
        val commits = getCommitsFromGitLogWithTimestampsAndFiles(log, getProject())
        assertTrue(commits.size == "\\d*\\d\n".toRegex().findAll(log).toList().size)
        print(commits.joinToString("\n"))
    }
}