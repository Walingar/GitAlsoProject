import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import gitLog.createGitLogWithTimestampsAndFiles
import gitLog.getCommitsFromGitLogWithTimestampsAndFiles
import org.jetbrains.annotations.NotNull
import java.io.File

class GitAlsoComponent(private val project: Project) : ProjectComponent {
    @NotNull
    override fun getComponentName(): String {
        return "GitAlsoComponent"
    }

    override fun projectOpened() {
        val log = createGitLogWithTimestampsAndFiles(File(project.basePath), project)
        getCommitsFromGitLogWithTimestampsAndFiles(log!!, project)
    }
}