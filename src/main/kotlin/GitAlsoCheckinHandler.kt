import com.intellij.openapi.application.PathManager
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
import commit.Commit
import index.IndexGetter
import index.IndexWriter
import predict.getSimpleRateForFile
import kotlin.math.max

class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val project: Project = panel.project

    private val title = "GitAlso plugin"

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        if (DumbService.getInstance(project).isDumb) {
            Messages.showErrorDialog("Cannot commit right now because IDE updates the indices. Please try again later", title)
            return ReturnResult.CANCEL
        }
        val files = getFiles()
        val service = ServiceManager.getService(project, GitAlsoService::class.java)
        // TODO: author name
        val time = System.currentTimeMillis() / 1000
        val author = "Unknown"
        val commit = service.createCommit(time, author, files)
        val predict = predictForCommit(commit)
        if (predict.isNotEmpty()) {
            if (Messages.showDialog(project,
                            String.format("May be you forgot these files(with rates): %n%s",
                                    predict.joinToString(System.lineSeparator(), transform = { file -> "rate: ${max(getSimpleRateForFile(file, commit), 50)}% file: ${file.toString(commit)}" })),
                            "Files to be committed",
                            arrayOf("Commit", "Cancel"),
                            1,
                            Messages.getInformationIcon()) == 0) {
                service.committed(files, Commit(time, author))
                DumbService.getInstance(project).queueTask(IndexWriter(project))
                return ReturnResult.COMMIT
            } else {
                return ReturnResult.CANCEL
            }
        }
        service.committed(files, Commit(time, author))
        DumbService.getInstance(project).queueTask(IndexWriter(project))
        return ReturnResult.COMMIT
    }

    private fun getFiles(): List<String> {
        return panel.files.map({ file -> file.relativeTo(File(project.basePath)).toString() })
    }
}