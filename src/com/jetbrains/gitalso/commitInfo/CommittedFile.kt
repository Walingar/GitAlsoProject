package com.jetbrains.gitalso.commitInfo

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.vcs.log.impl.VcsProjectLog
import com.jetbrains.gitalso.storage.log.hash.HashProvider
import com.sun.deploy.util.Base64Wrapper.encodeToString
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*


class CommittedFile(project: Project, val path: FilePath) {
    private val projectLog = VcsProjectLog.getInstance(project)
    private val indexData = projectLog.dataManager!!.index.dataGetter!!

    val commits = HashSet<Commit>()

    fun committed(commit: Commit) {
        commits.add(commit)
        commit.addFile(this)
    }


    private val id by lazy {
        HashProvider.hash(path.path)
    }

    val names get() = indexData.getKnownNames(path)

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