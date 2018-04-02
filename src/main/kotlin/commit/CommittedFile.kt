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

    fun getName(commit: Commit): String? {
        return names[commit]
    }

    override fun toString(): String {
        return id.toString()
    }

    fun toString(commit: Commit): CharSequence {
        return names[commit]!!
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