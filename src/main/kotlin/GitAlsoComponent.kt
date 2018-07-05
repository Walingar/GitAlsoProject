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
        // TODO: change it to ApplicationManager.getApplication().executeOnPooledThread
        // after removing GitAlsoService because it is very expensive
        DumbService.getInstance(project).queueTask(IndexGetter(project))
    }
}