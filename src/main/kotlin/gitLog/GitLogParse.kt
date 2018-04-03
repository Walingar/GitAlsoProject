package gitLog

import com.intellij.openapi.components.ServiceManager
import commit.Commit
import GitAlsoService
import com.intellij.openapi.project.Project

fun getCommitsFromGitLogWithTimestampsAndFiles(log: String, project: Project): Collection<Commit> {
    val commits = HashSet<Commit>()
    val lines = log.lines()
    var time = System.currentTimeMillis()
    var currentCommit: Commit
    val changes = ArrayList<List<String>>()

    for (i in lines) {
        val line = i.trim()
        if (line.isEmpty()) {
            continue
        }
        if (line.toLongOrNull() != null) {
            currentCommit = ServiceManager.getService(project, GitAlsoService::class.java).committed(changes, time)
            if (currentCommit.getFiles().isNotEmpty()) {
                commits.add(currentCommit)
            }
            changes.clear()
            time = line.toLong()
            continue
        }
        changes.add(i.split("\\s".toRegex()))
    }
    currentCommit = ServiceManager.getService(project, GitAlsoService::class.java).committed(changes, time)
    if (currentCommit.getFiles().isNotEmpty()) {
        commits.add(currentCommit)
    }

    return commits
}