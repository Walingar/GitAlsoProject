import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import gitLog.createGitLogWithTimestampsAndFiles
import gitLog.getCommitsFromGitLogWithTimestampsAndFiles
import org.jetbrains.annotations.NotNull

class GitAlsoComponent(private val project: Project) : ProjectComponent {
    @NotNull
    override fun getComponentName(): String {
        return "GitAlsoComponent"
    }

    // TODO: move it do background of commit window
    override fun projectOpened() {
        val log = createGitLogWithTimestampsAndFiles(project)
        getCommitsFromGitLogWithTimestampsAndFiles(log!!, project)
    }
}