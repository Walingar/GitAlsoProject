package git4idea

import com.intellij.testFramework.PlatformTestCase
import org.junit.Test
import java.io.File

class Git4IdeaTest : PlatformTestCase() {


    // TODO: ask about the easiest way to use git4idea tests

    @Test
    fun testGitUtil() {

        println(project.baseDir)
        // cd(File(homePath))
        executeCommand(arrayListOf("pwd"))
    }
}