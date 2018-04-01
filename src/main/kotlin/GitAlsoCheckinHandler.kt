import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.util.PairConsumer

class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val project: Project = panel.project

    private val title = "GitAlso plugin"

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        if (DumbService.getInstance(project).isDumb) {
            Messages.showErrorDialog("Cannot commit right now because IDE updates the indices. Please try again later", title)
            return ReturnResult.CANCEL
        }

        Messages.showMessageDialog(project, getFiles(), "Files to commit", Messages.getInformationIcon())
        return ReturnResult.CLOSE_WINDOW
    }

    private fun getFiles(): String {
        val list = panel.files
        return list.joinToString("\n")
    }

}