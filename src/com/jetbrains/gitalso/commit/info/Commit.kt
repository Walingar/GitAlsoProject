package com.jetbrains.gitalso.commit.info

import com.intellij.openapi.vcs.FilePath

class Commit(val id: Int, val time: Long) {
    private val _files = HashSet<FilePath>()

    val files: Set<FilePath>
        get() = _files

    fun addFile(file: FilePath) {
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