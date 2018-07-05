import com.intellij.openapi.application.PathManager
import com.intellij.testFramework.LightPlatformTestCase
import com.intellij.testFramework.PlatformTestCase
import com.intellij.testFramework.TestDataPath
import junit.framework.Assert
import org.junit.Test

class LogFileTest: PlatformTestCase() {

    @Test
    fun testLogFolder() {
        println(project.locationHash)
        println(PathManager.getLogPath())
    }
}