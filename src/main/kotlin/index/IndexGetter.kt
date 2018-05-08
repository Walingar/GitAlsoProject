package index

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.DumbModeTask
import com.intellij.openapi.project.Project
import java.io.File
import GitAlsoService
import com.intellij.openapi.project.DumbService
import gitLog.createGitLog
import gitLog.createGitLogSinceCommit
import gitLog.getCommitsFromGitLog

class IndexGetter(private val project: Project) : DumbModeTask() {
    private val service = ServiceManager.getService(project, GitAlsoService::class.java)
    private val projectHash = project.locationHash
    private val indexDirectory = PathManager.getIndexRoot().resolve(File("git-also/$projectHash"))
    private val commitsIndex = "commitsIndex.ga"
    private val filesIndex = "filesIndex.ga"
    private val commitsDataIndex = "commitsDataIndex.ga"

    private fun fullGitLog() {
        val log = createGitLog(project)
        if (log != null) {
            getCommitsFromGitLog(log, project)
        }
    }

    override fun performInDumbMode(indicator: ProgressIndicator) {
        if (
                indexDirectory.resolve(File(commitsIndex)).exists() &&
                indexDirectory.resolve(File(filesIndex)).exists() &&
                indexDirectory.resolve(File(commitsDataIndex)).exists()) {
            parseIndex(service, indexDirectory)
            val lastCommit = service.lastCommit
            if (lastCommit != null) {
                val log = createGitLogSinceCommit(project, lastCommit)
                getCommitsFromGitLog(log!!, project)
            } else {
                fullGitLog()
            }
        } else {
            fullGitLog()
        }
        DumbService.getInstance(project).queueTask(IndexWriter(project))
    }
}