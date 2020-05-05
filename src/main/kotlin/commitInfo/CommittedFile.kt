package commitInfo

import java.util.*


class CommittedFile(val path: String) {
    val commits = HashSet<Commit>()
    val names = HashSet<CommittedFile>()
    val id = path

    fun committed(commit: Commit) {
        commits.add(commit)
        commit.addFile(this)
    }

    override fun toString() = path

    override fun hashCode() = path.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommittedFile

        if (path != other.path) return false

        return true
    }
}