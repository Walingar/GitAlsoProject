package com.jetbrains.gitalso.commit.handle.ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vcs.changes.ui.ChangesTree
import com.intellij.openapi.vcs.changes.ui.TreeActionsToolbarPanel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.layout.panel
import com.intellij.util.ui.JBDimension
import com.jetbrains.gitalso.predict.PredictedChange
import com.jetbrains.gitalso.predict.PredictedFile
import java.awt.BorderLayout
import javax.swing.Action
import javax.swing.JPanel

class GitAlsoDialog(private val project: Project, private val files: List<PredictedFile>) : DialogWrapper(project), DataProvider {
    init {
        init()
        title = "GitAlso Plugin"
    }

    private lateinit var tree: ChangesTree
    override fun getData(dataId: String) = tree.getData(dataId)


    override fun getDimensionServiceKey() = "com.jetbrains.gitalso.commit.handle.ui.GitAlsoDialog"

    override fun createActions(): Array<Action> {
        val cancel = DialogWrapperExitAction("Cancel", 1)
        cancel.putValue(DEFAULT_ACTION, 0)
        val commitAnyway = DialogWrapperExitAction("Commit Anyway", 0)
        return arrayOf(cancel, commitAnyway)
    }

    private fun createActionsPanel(predictionTreeChange: ChangesTree): JPanel {
        val group = DefaultActionGroup()
        group.add(ActionManager.getInstance().getAction(ChangesTree.GROUP_BY_ACTION_GROUP))
        val toolbar = ActionManager.getInstance()
                .createActionToolbar("GitAlso.PredictionDialog", group, true)
        toolbar.setTargetComponent(predictionTreeChange)
        return TreeActionsToolbarPanel(toolbar, predictionTreeChange)
    }

    private fun createTreeScrollPane(predictionTreeChange: ChangesTree): JBScrollPane {
        val scrollPane = JBScrollPane(predictionTreeChange)
        scrollPane.preferredSize = JBDimension(400, 300)
        return scrollPane
    }

    private fun createTreePanel(): JPanel {
        val predictionTreeChange = PredictedFilesTreeImpl(project, false, false, files)
        tree = predictionTreeChange
        val panel = JPanel(BorderLayout())
        panel.add(createActionsPanel(predictionTreeChange), BorderLayout.PAGE_START)
        panel.add(createTreeScrollPane(predictionTreeChange), BorderLayout.CENTER)

        return panel
    }

    private fun createForgottenFilesString() = buildString {
        val modifiedFilesCount = files.filterIsInstance<PredictedChange>().size
        val unmodifiedFilesCount = files.size - modifiedFilesCount
        if (modifiedFilesCount > 0) {
            append("commit ")
        }
        if (modifiedFilesCount > 0 && unmodifiedFilesCount > 0) {
            append("or ")
        }
        if (unmodifiedFilesCount > 0) {
            append("modify ")
        }
        if (files.size > 1) {
            append("these files")
        } else {
            append("this file")
        }
    }

    override fun createCenterPanel() = panel {
        row {
            JBLabel("You might have forgotten to ${createForgottenFilesString()}:")()
        }
        row {
            createTreePanel()(grow, push)
        }
    }

}