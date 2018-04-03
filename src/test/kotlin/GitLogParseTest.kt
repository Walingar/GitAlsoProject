import com.intellij.testFramework.LightPlatformTestCase
import gitLog.createGitLogWithTimestampsAndFiles
import gitLog.getCommitsFromGitLogWithTimestampsAndFiles
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import org.junit.Test
import java.io.File

class GitLogParseTest : LightPlatformTestCase() {

    @Test
    fun testGetCommitsFromThisRepoWithNonEmptyCommits() {
        val log = createGitLogWithTimestampsAndFiles(getProject())
        assertNotNull(log)
        val commits = getCommitsFromGitLogWithTimestampsAndFiles(log!!, getProject())
        assertTrue(commits.size == "\\d\\d*\n".toRegex().findAll(log).toList().size)
        print(commits.joinToString("\n"))
    }
}