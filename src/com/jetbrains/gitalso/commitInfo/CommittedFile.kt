package com.jetbrains.gitalso.commitInfo

import com.intellij.openapi.vcs.FilePath
import java.util.*


class CommittedFile(val path: FilePath) {
    val commits = HashSet<Commit>()

    fun committed(commit: Commit) {
        commits.add(commit)
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