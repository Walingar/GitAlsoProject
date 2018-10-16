package com.jetbrains.gitalso.commitInfo

import com.intellij.openapi.project.Project
import com.intellij.vcs.log.impl.VcsProjectLog

class Commit(project: Project, val id: Int) {

    private val indexData = VcsProjectLog.getInstance(project).dataManager!!.index.dataGetter!!

    val files = HashSet<CommittedFile>()

    fun addFile(file: CommittedFile) {
        files.add(file)
    }

    val time
        get() =
            if (id != -1) {
                indexData.getAuthorTime(id)
            } else {
                System.currentTimeMillis()
            }


    val author
        get() =
            if (id != -1) {
                indexData.getAuthor(id)?.toString() ?: ""
            } else {
                ""
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