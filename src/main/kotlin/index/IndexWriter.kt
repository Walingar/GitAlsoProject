package index

import com.intellij.openapi.application.PathManager
import GitAlsoService
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.DumbModeTask
import com.intellij.openapi.project.Project
import java.io.File


class IndexWriter(project: Project) : DumbModeTask() {
    private val service = ServiceManager.getService(project, GitAlsoService::class.java)
    private val projectHash = project.locationHash
    private val indexDirectory = PathManager.getIndexRoot().resolve(File("git-also/$projectHash"))
    private val commitsIndex = "commitsIndex.ga"
    private val filesIndex = "filesIndex.ga"
    private val commitsDataIndex = "commitsDataIndex.ga"

    override fun performInDumbMode(indicator: ProgressIndicator) {
        indicator.text = "Git-also indexing"
        indicator.fraction = 0.0

        printIndex(indexDirectory, commitsIndex, createCommitsIndex(service))
        indicator.fraction = 0.35

        printIndex(indexDirectory, filesIndex, createFilesIndex(service))
        indicator.fraction = 0.70

        printIndex(indexDirectory, commitsDataIndex, createCommitsDataIndex(service))
        indicator.fraction = 1.0
    }
}