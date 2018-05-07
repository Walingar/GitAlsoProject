package dataset

data class PipeLineCommit(val time: Long, val files: List<Int>, val forgottenFiles: List<Int>) {
    override fun toString(): String {
        return "$time;${files.joinToString(", ")};${forgottenFiles.joinToString(", ")}"
    }
}