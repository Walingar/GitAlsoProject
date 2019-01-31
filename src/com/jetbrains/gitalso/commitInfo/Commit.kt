package com.jetbrains.gitalso.commitInfo

class Commit(val id: Int, val time: Long = System.currentTimeMillis()) {
    private val _files = HashSet<CommittedFile>()

    val files: Set<CommittedFile>
        get() = _files

    fun addFile(file: CommittedFile) {
        _files.add(file)
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