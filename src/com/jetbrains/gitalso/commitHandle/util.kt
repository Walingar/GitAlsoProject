package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.VcsRef
import com.intellij.vcs.log.data.RefsModel
import java.util.*

fun ClosedRange<Int>.random() = Random().nextInt((endInclusive + 1) - start) + start

fun RefsModel.findBranch(root: VirtualFile, branchName: String): VcsRef? {
    val branches = this.allRefsByRoot[root]?.streamBranches() ?: return null
    return branches
            .filter { vcsRef ->
                vcsRef.name == branchName
            }
            .findFirst()
            .orElse(null)
}