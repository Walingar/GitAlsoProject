package commit

class CommittedFile(private val id: Int) {
    private val commits = HashSet<Commit>()
    private val names = HashMap<Commit, String>()

    fun commited(commit: Commit, name: String) {
        commits.add(commit)
        names[commit] = name
    }

    fun getId(): Int {
        return id
    }

    fun getCommits(): Set<Commit> {
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

}