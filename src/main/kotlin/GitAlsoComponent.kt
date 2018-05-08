import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import index.IndexGetter
import index.IndexWriter
import org.jetbrains.annotations.NotNull

class GitAlsoComponent(private val project: Project) : ProjectComponent {
    @NotNull
    override fun getComponentName(): String {
        return "GitAlsoComponent"
    }

    override fun projectOpened() {
        DumbService.getInstance(project).queueTask(IndexGetter(project))
    }
}