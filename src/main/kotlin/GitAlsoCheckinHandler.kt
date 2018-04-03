import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.util.PairConsumer
import predict.predictForCommit
import java.io.File

class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val project: Project = panel.project

    private val title = "GitAlso plugin"

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        if (DumbService.getInstance(project).isDumb) {
            Messages.showErrorDialog("Cannot commit right now because IDE updates the indices. Please try again later", title)
            return ReturnResult.CANCEL
        }
        val files = getFiles()
        val commit = ServiceManager.getService(project, GitAlsoService::class.java).committed(files, System.currentTimeMillis() / 1000)
        val predict = predictForCommit(commit)
        if (predict.isNotEmpty()) {
            Messages.showMessageDialog(project,
                    String.format("May be you forgot these files: %n%s",
                            predict.joinToString(System.lineSeparator(), transform = { file -> file.toString(commit) })),
                    "Files to be committed",
                    Messages.getInformationIcon())
        }
        return ReturnResult.COMMIT
    }

    private fun getFiles(): List<List<String>> {
        return panel.files.map({ file -> listOf("M", file.relativeTo(File(project.basePath)).toString()) })
    }
}