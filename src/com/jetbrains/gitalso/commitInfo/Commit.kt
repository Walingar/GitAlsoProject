package com.jetbrains.gitalso.commitInfo

class Commit(val id: Int, val time: Long = System.currentTimeMillis()) {
    val files = HashSet<CommittedFile>()

    fun addFile(file: CommittedFile) {
        files.add(file)
    }


    override fun toString() = id.toString()

    override fun hashCode() = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Commit

        if (id != other.id) return false

        return true
    }
}