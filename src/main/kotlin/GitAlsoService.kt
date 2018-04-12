import commit.Commit
import commit.CommittedFile

class GitAlsoService {
    private var fileCounter = 0
    private val mapNameToID = HashMap<String, CommittedFile>()


    fun committed(commitByString: List<List<String>>, time: Long): Commit {
        val commit = Commit(time)

        for (change in commitByString) {
            if (change.size == 3) {
                val (type, firstFile, secondFile) = change
                if (type[0] == 'R') {
                    mapNameToID.putIfAbsent(firstFile, CommittedFile(fileCounter++))
                    mapNameToID[secondFile] = mapNameToID[firstFile]!!
                    mapNameToID.remove(firstFile)
                    mapNameToID[secondFile]!!.committed(commit, secondFile)
                }
            } else if (change.size == 2) {
                val (_, file) = change
                mapNameToID.putIfAbsent(file, CommittedFile(fileCounter++))
                mapNameToID[file]!!.committed(commit, file)
            }
        }

        return commit
    }
}