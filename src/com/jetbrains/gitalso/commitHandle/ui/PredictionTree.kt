package com.jetbrains.gitalso.commitHandle.ui

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.PlatformIcons
import java.awt.Color
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class PredictionTree(private val root: VirtualFile, private val drawable: Set<VirtualFile>, private val color: Color) {

    private fun dfs(parent: VirtualFile): DefaultMutableTreeNode {
        val parentNode = DefaultMutableTreeNode(parent)
        for (child in parent.children) {
            if (child in drawable) {
                val childNode = dfs(child)
                parentNode.add(childNode)
            }
        }
        return parentNode
    }

    private class IconAndColorRenderer(private val color: Color) : ColoredTreeCellRenderer() {
        override fun customizeCellRenderer(tree: JTree, value: Any?, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean) {
            value as DefaultMutableTreeNode
            val file = value.userObject as VirtualFile

            icon = if (file.isDirectory) {
                PlatformIcons.FOLDER_ICON
            } else {
                file.fileType.icon
            }

            if (leaf) {
                append(file.name, SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, color))
            } else {
                append(file.name, SimpleTextAttributes.REGULAR_ATTRIBUTES)
            }
        }

    }

    private fun expandAll(tree: Tree) {
        var i = 0
        while (i < tree.rowCount) {
            tree.expandRow(i)
            i++
        }
    }

    fun getTree(): Tree {
        val root = dfs(root)
        val tree = Tree(DefaultTreeModel(root))

        expandAll(tree)
        tree.autoscrolls = true
        tree.isRootVisible = false
        tree.isHorizontalAutoScrollingEnabled = true
        tree.cellRenderer = IconAndColorRenderer(color)

        return tree
    }
}