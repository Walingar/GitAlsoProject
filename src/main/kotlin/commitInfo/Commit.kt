package commitInfo

class Commit(val time: Long) {
    val files = HashSet<CommittedFile>()

    fun addFile(file: CommittedFile) {
        files.add(file)
    }


    val author = "null"

    override fun toString() = time.toString()

    override fun hashCode() = time.toInt()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Commit

        if (time != other.time) return false

        return true
    }
}