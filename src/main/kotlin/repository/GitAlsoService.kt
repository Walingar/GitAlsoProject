package repository

import commitInfo.Commit
import commitInfo.CommittedFile
import kotlin.math.max

class GitAlsoService {
    val files = HashMap<String, CommittedFile>()
    val commits = HashMap<Long, Commit>()


    fun committedGitLog(commitByString: List<List<String>>, commitLog: Commit) {
        fun commitFile(name: String, commit: Commit) {
            files.putIfAbsent(name, CommittedFile(name))
            val file = files[name]!!
            file.committed(commit)
        }

        fun rename(firstFile: String, secondFile: String) {
            val fileA = files[firstFile]!!
            val fileB = files[secondFile]!!

            fileA.names.add(fileB)
            fileB.names.add(fileA)
        }

        commits.putIfAbsent(commitLog.time, commitLog)
        val commit = commits[commitLog.time]!!

        for (change in commitByString) {
            if (change.size == 3) {
                val (type, firstFile, secondFile) = change
                if (type[0] == 'R') {
                    commitFile(firstFile, commit)
                    commitFile(secondFile, commit)
                    rename(firstFile, secondFile)
                }
            } else if (change.size == 2) {
                val (_, file) = change
                commitFile(file, commit)
            }
        }
    }
}