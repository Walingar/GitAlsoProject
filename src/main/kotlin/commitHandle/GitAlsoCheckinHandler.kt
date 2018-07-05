package commitHandle

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
import commitInfo.Commit
import git4idea.GitUtil
import predict.getMaxByCommit
import predict.getSimpleRateForFile
import repository.GitAlsoService
import storage.log.*

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
        val uidProvider = ServiceManager.getService(project, UserIDProvider::class.java)

        val x = GitUtil.getRepositoryManager(project)
        print(x.repositories)

        val logWriter = LogFileWriter(project)

        Messages.showInfoMessage(PathManager.getSystemPath(), "test")
        // TODO: author name from git4idea

        val time = System.currentTimeMillis() / 1000
        val author = "Unknown"
        val commit = service.createCommit(time, author, files)
        val predict = predictForCommit(commit).filter { getSimpleRateForFile(it, commit) >= 25 }
        val message = if (predict.size == 1) {
            "You might have forgotten this file"
        } else {
            "You might have forgotten these files"
        }

        // TODO: load indices there, not in the start from git4idea

        // TODO: this code is so ugly
        val factors = HashMap<String, Map<String, Number>>()
        for (firstFile in commit.getFiles()) {
            for (secondFile in predict) {
                factors += getFactors(firstFile, secondFile, time, getMaxByCommit(commit))
            }
        }

        val event = LogEvent(
                uidProvider.installationID(),
                "1",
                "1",
                System.currentTimeMillis() / 1000,
                Action.NOT_WATCHED,
                factors
        )

        if (predict.isNotEmpty() || true) {
            return if (Messages.showDialog(project,
                            String.format("$message: %n%s",
                                    predict.joinToString(System.lineSeparator(), transform = { file -> "rate: ${getSimpleRateForFile(file, commit)}% file: ${file.toString(commit)}" })),
                            "Files to be committed",
                            arrayOf("Commit", "Cancel"),
                            1,
                            Messages.getInformationIcon()) == 0) {
                service.committed(files, Commit(time, author))
                event.action = Action.COMMIT
                logWriter.log(event)
                ReturnResult.COMMIT
            } else {
                event.action = Action.CANCEL
                logWriter.log(event)
                ReturnResult.CANCEL
            }
        }
        service.committed(files, Commit(time, author))
        logWriter.log(event)
        return ReturnResult.COMMIT
    }

    private fun getFiles(): List<String> {
        return panel.files.map({ file -> file.relativeTo(File(project.basePath)).toString() })
    }
}