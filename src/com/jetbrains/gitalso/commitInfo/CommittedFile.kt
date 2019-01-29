package com.jetbrains.gitalso.commitInfo

import com.intellij.openapi.vcs.FilePath
import com.jetbrains.gitalso.storage.log.hash.HashProvider
import java.util.*


class CommittedFile(val path: FilePath) {
    val commits = HashSet<Commit>()

    fun committed(commit: Commit) {
        commits.add(commit)
        commit.addFile(this)
    }

    private val id by lazy {
        HashProvider.hash(path.path)
    }

    override fun toString() = id.toString()

    override fun hashCode() = path.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CommittedFile

        if (path != other.path) return false

        return true
    }
}