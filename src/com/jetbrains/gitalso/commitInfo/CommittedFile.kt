package com.jetbrains.gitalso.commitInfo

import com.intellij.openapi.project.Project
import com.intellij.openapi.vcs.FilePath
import com.intellij.vcs.log.impl.VcsProjectLog
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


    val id by lazy {
        val p = 31
        var hash = 0
        var pPow = 1
        for (ch in path.path) {
            hash += (ch - 'a' + 1) * pPow
            pPow *= p
        }
        hash
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