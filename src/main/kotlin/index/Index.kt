package index

import commit.Commit
import commit.CommittedFile

// TODO: useless??
class Index(private val repoName: String) {
    private val filesWithCommits = HashMap<CommittedFile, MutableSet<Commit>>()

    fun addCommitToFile(file: CommittedFile, commit: Commit) {
        if (file !in filesWithCommits) {
            filesWithCommits[file] = HashSet()
        }
        filesWithCommits[file]?.add(commit)
    }

    fun getIndex(): HashMap<CommittedFile, MutableSet<Commit>> {
        return filesWithCommits
    }

    fun getRepoName(): String {
        return repoName
    }
}