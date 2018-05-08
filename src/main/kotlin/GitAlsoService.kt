import commit.Commit
import commit.CommittedFile
import kotlin.math.max

class GitAlsoService {
    var lastCommit: Commit? = null
    private var fileCounter = 0
    private val mapNameToFile = HashMap<String, CommittedFile>()
    private val commits = ArrayList<Commit>()
    private val mapIDToFile = HashMap<Int, CommittedFile>()

    fun getCommits() = commits

    fun getFileCount() = fileCounter

    fun getIDToFile() = mapIDToFile

    fun createCommit(time: Long, author: String, files: List<String>): Commit {
        val commit = Commit(time, author)
        for (fileName in files) {
            if (fileName in mapNameToFile) {
                commit.addFile(mapNameToFile[fileName]!!)
            }
        }
        return commit
    }

    private fun commit(commit: Commit) {
        if (commit.getFiles().isNotEmpty()) {
            commits.add(commit)
        }
    }

    fun committedFromIndex(files: List<Int>, commit: Commit, idToFileName: Map<Int, String>) {
        for (id in files) {
            if (id !in mapIDToFile) {
                val file = CommittedFile(id)
                val fileName = idToFileName[id]!!
                mapNameToFile[fileName] = file
                mapIDToFile[id] = file
            }
            mapIDToFile[id]!!.committed(commit, idToFileName[id]!!)
            fileCounter = max(id + 1, fileCounter)
        }
        commit(commit)
        if (lastCommit == null || lastCommit!!.time < commit.time) {
            lastCommit = commit
        }
    }

    private fun commitFile(name: String) {
        if (name !in mapNameToFile) {
            val file = CommittedFile(fileCounter)
            mapNameToFile[name] = file
            mapIDToFile[fileCounter] = file
            fileCounter++
        }
    }

    fun committed(files: List<String>, commit: Commit) {
        for (file in files) {
            commitFile(file)
            mapNameToFile[file]!!.committed(commit, file)
        }

        commit(commit)
    }

    fun committedGitLog(commitByString: List<List<String>>, commit: Commit) {
        for (change in commitByString) {
            if (change.size == 3) {
                val (type, firstFile, secondFile) = change
                if (type[0] == 'R') {
                    commitFile(firstFile)
                    mapNameToFile[secondFile] = mapNameToFile[firstFile]!!
                    mapNameToFile.remove(firstFile)
                    mapNameToFile[secondFile]!!.committed(commit, secondFile)
                }
            } else if (change.size == 2) {
                val (_, file) = change
                commitFile(file)
                mapNameToFile[file]!!.committed(commit, file)
            }
        }

        commit(commit)
    }
}