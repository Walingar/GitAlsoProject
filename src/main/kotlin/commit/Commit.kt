package commit

class Commit(private val time: Long) {
    private val files = HashSet<CommittedFile>()

    fun getTime(): Long {
        return time
    }

    fun getFiles(): Set<CommittedFile> {
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
        return files.joinToString{t -> t.toString(this)}
    }

}