package com.jetbrains.gitalso.commitHandle

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vcs.CheckinProjectPanel
import com.intellij.openapi.vcs.changes.CommitExecutor
import com.intellij.openapi.vcs.checkin.CheckinHandler
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.PairConsumer
import com.intellij.util.PlatformIcons
import com.intellij.vcsUtil.VcsUtil
import java.awt.BorderLayout
import javax.swing.*
import javax.swing.tree.*
import java.awt.Color

class GitAlsoCheckinHandler(private val panel: CheckinProjectPanel) : CheckinHandler() {
    private val project: Project = panel.project
    private val files by lazy {
        panel.files.map { file -> getFilePath(file.absolutePath) }
    }

    private val title = "GitAlso plugin"

    // TODO: make it pretty and fast
    private class TestDialog(private val project: Project, private val files: Set<VirtualFile>) : DialogWrapper(project) {
        private val drawable = HashSet<VirtualFile>()

        init {
            for (file in files) {
                drawable.add(file)
                var parent = file.parent
                while (parent != null) {
                    drawable.add(parent)
                    parent = parent.parent
                }
            }
            init()
            title = "GitAlso"
        }

        private fun isPrefix(node: VirtualFile) = node in drawable

        private fun dfs(parent: VirtualFile): DefaultMutableTreeNode {
            val parentNode = DefaultMutableTreeNode(parent)
            for (child in parent.children) {
                if (isPrefix(child)) {
                    val childNode = dfs(child)
                    parentNode.add(childNode)
                }
            }
            return parentNode
        }

        private class Renderer(private val color: Color) : ColoredTreeCellRenderer() {
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

        private fun getPredictionTree(color: Color): Tree {
            val root = dfs(project.baseDir!!)
            val comp = Tree(DefaultTreeModel(root))
            var i = 0
            while (i < comp.rowCount) {
                comp.expandRow(i)
                i++
            }
            comp.autoscrolls = true
            comp.isRootVisible = false
            comp.isHorizontalAutoScrollingEnabled = true
            comp.cellRenderer = Renderer(color)
            return comp
        }

        override fun createCenterPanel(): JComponent? {
            val mainContent = JPanel(BorderLayout(20, 20))
            val topPanel = JPanel(BorderLayout())
            val predictionCommitPanel = JPanel(BorderLayout())
            val middlePanel = JPanel(BorderLayout())
            val predictionChangePanel = JPanel(BorderLayout())
            val panel1 = JPanel(BorderLayout())
            val panel2 = JPanel(BorderLayout())


            topPanel.add(JLabel("You might have forgotten to commit these files: "))
            predictionCommitPanel.add(ScrollPaneFactory.createScrollPane(getPredictionTree(Color(56, 117, 214))))

            panel1.add(topPanel, BorderLayout.PAGE_START)
            panel1.add(predictionCommitPanel, BorderLayout.LINE_START)

            middlePanel.add(JLabel("You might have forgotten to change these files: "))
            predictionChangePanel.add(ScrollPaneFactory.createScrollPane(getPredictionTree(Color.BLACK)))



            panel2.add(middlePanel, BorderLayout.PAGE_START)
            panel2.add(predictionChangePanel, BorderLayout.LINE_START)

            mainContent.add(panel1, BorderLayout.PAGE_START)
            mainContent.add(panel2, BorderLayout.LINE_START)
            return mainContent
        }

    }

    private fun getFilePath(file: String) = VcsUtil.getFilePath(file)

    override fun beforeCheckin(executor: CommitExecutor?, additionalDataConsumer: PairConsumer<Any, Any>?): ReturnResult {
        val dialog = TestDialog(project, files.map { it.virtualFile!! }.toSet())
        dialog.show()
        return ReturnResult.CANCEL
    }
}