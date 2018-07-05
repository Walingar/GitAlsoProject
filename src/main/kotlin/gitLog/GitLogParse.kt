package gitLog

import repository.GitAlsoService
import commitInfo.Commit

fun getCommitsFromGitLog(log: String, service: GitAlsoService, maxCountFiles: Int = 20) {
    var time = System.currentTimeMillis()
    var author = "Unknown"
    val commit = ArrayList<List<String>>()
    val commits = HashMap<Long, Commit>()

    for (i in log.lines()) {
        val line = i.trim()
        if (line.isBlank()) {
            continue
        }

        if (line[0].isDigit()) {
            if (commit.isNotEmpty() && commit.size <= maxCountFiles) {
                if (time !in commits) {
                    val currentCommit = Commit(time, author)
                    service.committedGitLog(commit, currentCommit)
                    commits[time] = currentCommit
                } else {
                    service.committedGitLog(commit, commits[time]!!)
                }
            }
            val splitHeader = line.split("\\s".toRegex())
            time = splitHeader[0].toLong()
            author = splitHeader.subList(1, splitHeader.size).joinToString(" ")
            if (author.isBlank()) {
                author = "Unknown"
            }
            commit.clear()
            continue
        }

        commit.add(line.split("\\s".toRegex()))
    }

    if (commit.isNotEmpty() && commit.size <= maxCountFiles) {
        service.committedGitLog(commit, Commit(time, author))
    }
}