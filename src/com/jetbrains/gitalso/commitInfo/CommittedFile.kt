package com.jetbrains.gitalso.commitInfo

import com.intellij.openapi.vcs.FilePath
import java.util.*


class CommittedFile(val path: FilePath) {
    private val _commits = HashSet<Commit>()

    val commits: Set<Commit>
        get() = _commits

    fun committed(commit: Commit) {
        _commits.add(commit)
        commit.addFile(this)
    }

    override fun toString() = path.path

    override fun hashCode() = path.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommittedFile

        if (path != other.path) return false

        return true
    }
}