package commitInfo

class Commit(val time: Long, val author: String = "Unknown") {
    private val files = HashSet<CommittedFile>()

    fun getFiles(): HashSet<CommittedFile> {
        return files
    }

    fun addFile(file: CommittedFile) {
        files.add(file)
    }

    fun isFileInCommit(file: CommittedFile): Boolean {
        if (file in files) {
            return true
        }
        return false
    }

    override fun toString(): String {
        return time.toString()
    }

    fun toFullString(): String {
        return files.joinToString { t -> t.toString(this) }
    }

    override fun equals(other: Any?): Boolean {
        if (other is Commit) {
            return other.time == this.time
        }
        return false
    }

    override fun hashCode(): Int {
        return this.time.toInt()
    }

}