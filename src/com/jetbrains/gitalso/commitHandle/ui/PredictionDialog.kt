package com.jetbrains.gitalso.commitHandle.ui


import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.treeStructure.Tree
import com.intellij.ui.treeStructure.actions.CollapseAllAction
import com.intellij.ui.treeStructure.actions.ExpandAllAction
import java.awt.*
import javax.swing.*

class PredictionDialog(project: Project, private val root: VirtualFile, private val drawable: Set<VirtualFile>, private val color: Color) : DialogWrapper(project) {
    init {
        init()
        title = "GitAlso plugin"
    }

    override fun createActions(): Array<Action> {
        return arrayOf(DialogWrapperExitAction("Close", 0))
    }

    private fun createActionsPanel(predictionTreeChange: Tree): JPanel {
        val panel = JPanel(BorderLayout())

        val actionGroup = DefaultActionGroup()
        actionGroup.add(ExpandAllAction(predictionTreeChange))
        actionGroup.add(CollapseAllAction(predictionTreeChange))

        val toolbar = ActionManager.getInstance().createActionToolbar("GitAlso.PredictionDialog", actionGroup, true).component
        panel.add(toolbar, BorderLayout.LINE_END)
        return panel
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(BorderLayout())
        val predictionTreeChange = PredictionTree(root, drawable, color).getTree()
        panel.add(createActionsPanel(predictionTreeChange), BorderLayout.PAGE_START)
        val scrollPane = ScrollPaneFactory.createScrollPane(predictionTreeChange)

        val screenSize = Toolkit.getDefaultToolkit().screenSize

        scrollPane.preferredSize = Dimension(
                (screenSize.width * 0.2).toInt(),
                (screenSize.height * 0.3).toInt())


        panel.add(scrollPane, BorderLayout.CENTER)

        return panel
    }
}