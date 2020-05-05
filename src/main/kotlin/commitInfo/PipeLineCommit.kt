package commitInfo

data class PipeLineCommit(val time: Long, val files: List<String>, val forgottenFiles: List<String>) {
    override fun toString(): String {
        return "$time;${files.joinToString(", ")};${forgottenFiles.joinToString(", ")}"
    }

    override fun hashCode(): Int {
        return time.toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PipeLineCommit

        if (time != other.time) return false
        if (files != other.files) return false
        if (forgottenFiles != other.forgottenFiles) return false

        return true
    }

}