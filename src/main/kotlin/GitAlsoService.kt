import commit.Commit
import commit.CommittedFile
import kotlin.math.max

class GitAlsoService {
    private var fileCounter = 0
    private val mapNameToFile = HashMap<String, CommittedFile>()
    private val commits = ArrayList<Commit>()
    private val mapIDToFile = HashMap<Int, CommittedFile>()

    fun getCommits() = commits

    fun getFileCount() = fileCounter

    fun getIDTofile() = mapIDToFile

    fun committedFromIndex(files: List<Int>, commit: Commit, idToFileName: Map<Int, String>) {
        commits.add(commit)
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

        if (commit.getFiles().isNotEmpty()) {
            commits.add(commit)
        }
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

        if (commit.getFiles().isNotEmpty()) {
            commits.add(commit)
        }
    }
}