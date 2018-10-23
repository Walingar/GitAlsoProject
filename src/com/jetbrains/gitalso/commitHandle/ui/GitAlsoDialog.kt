package com.jetbrains.gitalso.commitHandle.ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vcs.changes.ui.ChangesTree
import com.intellij.openapi.vcs.changes.ui.ChangesTreeImpl
import com.intellij.openapi.vcs.changes.ui.TreeActionsToolbarPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ScrollPaneFactory
import java.awt.*
import javax.swing.*

class GitAlsoDialog(private val project: Project, modifiedFiles: Set<VirtualFile>, unmodifiedFiles: Set<VirtualFile>) : DialogWrapper(project), DataProvider {
    override fun getData(dataId: String) =
            if (tree == null)
                null
            else
                tree!!.getData(dataId)

    private var tree: ChangesTree? = null
    private val modifiedDrawable: Set<VirtualFile> = modifiedFiles
    private val unmodifiedDrawable: Set<VirtualFile> = unmodifiedFiles

    override fun getDimensionServiceKey() = "com.jetbrains.gitalso.commitHandle.ui.GitAlsoDialog"

    init {
        init()
        title = "GitAlso plugin"
    }

    override fun createActions(): Array<Action> {
        val cancel = DialogWrapperExitAction("Cancel", 1)
        cancel.putValue(DEFAULT_ACTION, 0)
        val commitAnyway = DialogWrapperExitAction("Commit anyway", 0)
        return arrayOf(cancel, commitAnyway)
    }

    private fun createActionsPanel(predictionTreeChange: ChangesTree): JPanel {
        val group = DefaultActionGroup()
        group.add(ActionManager.getInstance().getAction(ChangesTree.GROUP_BY_ACTION_GROUP))
        val toolbar = ActionManager.getInstance().createActionToolbar("GitAlso.PredictionDialog", group, true)
        toolbar.setTargetComponent(predictionTreeChange)
        return TreeActionsToolbarPanel(toolbar, predictionTreeChange)
    }

    private fun createTreePanel(): JPanel {
        val predictionTreeChange = ChangesTreeImpl.VirtualFiles(project, false, false, (modifiedDrawable + unmodifiedDrawable).toList())
        tree = predictionTreeChange
        val panel = JPanel(BorderLayout())
        panel.add(createActionsPanel(predictionTreeChange), BorderLayout.PAGE_START)
        val scrollPane = ScrollPaneFactory.createScrollPane(predictionTreeChange)
        val screenSize = Toolkit.getDefaultToolkit().screenSize

        scrollPane.preferredSize = Dimension(
                (screenSize.width * 0.2).toInt(),
                (screenSize.height * 0.3).toInt())


        panel.add(scrollPane, BorderLayout.CENTER)

        return panel
    }

    override fun createCenterPanel(): JComponent? {
        val mainPanel = JPanel(BorderLayout())
        val commonLabel = JLabel("You might have forgotten to modify/${if (modifiedDrawable.isNotEmpty()) "commit" else ""} ${if (modifiedDrawable.size + unmodifiedDrawable.size > 1) "these files" else "this file"}:")
        mainPanel.add(commonLabel, BorderLayout.PAGE_START)
        mainPanel.add(createTreePanel(), BorderLayout.CENTER)

        return mainPanel
    }

}