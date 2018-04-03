package commit

class CommittedFile(private val id: Int) {
    private val commits = HashSet<Commit>()
    private val names = HashMap<Commit, String>()

    fun committed(commit: Commit, name: String) {
        commits.add(commit)
        names[commit] = name
        commit.addFile(this)
    }

    fun getId(): Int {
        return id
    }

    fun getCommits(): Collection<Commit> {
        return commits
    }

    fun getName(currentCommit: Commit): String {
        var delta = currentCommit.getTime()
        var fileName = ""
        for ((commit, name) in names) {
            val curDelta = currentCommit.getTime() - commit.getTime()
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
        return this.getName(currentCommit)
    }

    override fun equals(other: Any?): Boolean {
        if (other is CommittedFile) {
            return other.getId() == this.getId()
        }
        return false
    }

    override fun hashCode(): Int {
        return this.getId()
    }
}