import com.intellij.openapi.application.PathManager
import com.intellij.testFramework.PlatformTestCase
import org.junit.Test
import com.jetbrains.gitalso.storage.index.IndexFileManager

class LogFileTest: PlatformTestCase() {

    @Test
    fun testLogFolder() {
        project.getComponent(IndexFileManager::class.java)
        println(project.locationHash)
        println(PathManager.getLogPath())
    }
}