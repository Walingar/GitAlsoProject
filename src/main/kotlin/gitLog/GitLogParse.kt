package gitLog

import com.intellij.openapi.components.ServiceManager
import commit.Commit
import GitAlsoService
import com.intellij.openapi.project.Project

fun getCommitsFromGitLogWithTimestampsAndFiles(log: String, project: Project, maxCountFiles: Int = 10000): Collection<Commit> {
    val commits = HashSet<Commit>()
    var time = System.currentTimeMillis()
    var currentCommit: Commit
    val changes = ArrayList<List<String>>()

    for (i in log.lines()) {
        val line = i.trim()
        if (line.isEmpty()) {
            continue
        }
        if (line[0].isDigit()) {
            if (changes.size != 20) {
                currentCommit = ServiceManager.getService(project, GitAlsoService::class.java).committed(changes, time)
                if (currentCommit.getFiles().isNotEmpty()) {
                    commits.add(currentCommit)
                }
            }
            changes.clear()
            time = line.toLong()
            continue
        }

        if (changes.size < 20) {
            changes.add(i.split("\\s".toRegex()))
        }
    }
    currentCommit = ServiceManager.getService(project, GitAlsoService::class.java).committed(changes, time)
    if (currentCommit.getFiles().isNotEmpty()) {
        commits.add(currentCommit)
    }

    return commits
}