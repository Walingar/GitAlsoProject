package commit

class CommittedFile(val id: Int) {
    private val commits = HashSet<Commit>()
    private val names = HashMap<Commit, String>()

    fun committed(commit: Commit, name: String) {
        if (commit !in commits) {
            commits.add(commit)
            names[commit] = name
            commit.addFile(this)
        }
    }

    fun getCommits(): Collection<Commit> {
        return commits
    }

    fun getName(time: Long): String {
        var delta = time
        var fileName = ""
        for ((commit, name) in names) {
            val curDelta = time - commit.time
            if (curDelta in 1..(delta - 1)) {
                delta = curDelta
                fileName = name
            }
        }
        return fileName
    }

    override fun toString(): String {
        return id.toString()
    }

    fun toString(currentCommit: Commit): CharSequence {
        return this.getName(currentCommit.time)
    }

    override fun equals(other: Any?): Boolean {
        if (other is CommittedFile) {
            return other.id == this.id
        }
        return false
    }

    override fun hashCode(): Int {
        return this.id
    }
}