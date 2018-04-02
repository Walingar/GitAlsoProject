package gitLog

import commit.Commit
import commit.CommittedFile

fun getCommitsFromGitLogWithTimestampsAndFiles(log: String): Collection<Commit> {
    val commits = HashSet<Commit>()
    val lines = log.lines()
    var time = System.currentTimeMillis()
    var currentCommit = Commit(time)
    var fileCounter = 0
    val mapNameToID = HashMap<String, Int>()

    for (i in lines) {
        val line = i.trim()
        if (line.isEmpty()) {
            continue
        }
        if (line.toLongOrNull() != null) {
            if (currentCommit.getFiles().isNotEmpty()) {
                commits.add(currentCommit)
            }
            time = line.toLong()
            currentCommit = Commit(time)
            continue
        }

        val change = i.split("\\s".toRegex())

        if (change.size == 3) {
            val (type, firstFile, secondFile) = change
            if (type[0] == 'R') {
                if (firstFile !in mapNameToID) {
                    mapNameToID[firstFile] = fileCounter++
                }
                val id = mapNameToID[firstFile]
                mapNameToID.remove(firstFile)

                val committedFile = CommittedFile(id!!)
                currentCommit.addFile(committedFile)
                committedFile.committed(currentCommit, secondFile)
            }
        } else {
            val (_, file) = change
            if (file !in mapNameToID) {
                mapNameToID[file] = fileCounter++
            }
            val id = mapNameToID[file]
            val committedFile = CommittedFile(id!!)
            currentCommit.addFile(committedFile)
            committedFile.committed(currentCommit, file)
        }

    }

    if (currentCommit.getFiles().isNotEmpty()) {
        commits.add(currentCommit)
    }

    return commits
}