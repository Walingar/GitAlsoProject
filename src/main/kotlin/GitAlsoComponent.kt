import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.DumbModeTask
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.NotNull
import repository.GitAlsoService
import storage.index.IndexFileManager

class GitAlsoComponent(private val project: Project) : ProjectComponent {
    @NotNull
    override fun getComponentName(): String {
        return "GitAlsoComponent"
    }

    override fun projectOpened() {
        DumbService.getInstance(project).queueTask(object : DumbModeTask() {
            override fun performInDumbMode(progress: ProgressIndicator) {
                IndexFileManager(project).read(ServiceManager.getService(project, GitAlsoService::
                class.java))
            }
        })
    }
}