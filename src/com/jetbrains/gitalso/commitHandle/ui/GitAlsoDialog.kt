package com.jetbrains.gitalso.commitHandle.ui

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataProvider
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vcs.changes.ui.ChangesTree
import com.intellij.openapi.vcs.changes.ui.TreeActionsToolbarPanel
import com.intellij.ui.components.JBLabel
import com.intellij.ui.layout.panel
import com.jetbrains.gitalso.predict.PredictedFile
import javax.swing.Action
import javax.swing.JPanel

class GitAlsoDialog(private val project: Project, private val files: List<PredictedFile>) : DialogWrapper(project), DataProvider {
    init {
        init()
        title = "GitAlso Plugin"
    }

    private var tree: ChangesTree? = null

    override fun getData(dataId: String) =
            if (tree == null)
                null
            else
                tree!!.getData(dataId)


    override fun getDimensionServiceKey() = "com.jetbrains.gitalso.commitHandle.ui.GitAlsoDialog"

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

    private fun createTreePanel() = panel {
        val predictionTreeChange = PredictedFilesTreeImpl(project, false, false, files)
        tree = predictionTreeChange
        row {
            cell(true) {
                createActionsPanel(predictionTreeChange)(growX, growY)
                scrollPane(predictionTreeChange)
            }
        }
    }

    override fun createCenterPanel() = panel {
        row {
            cell(true) {
                JBLabel("You might have forgotten to modify/commit " +
                        "${if (files.size > 1) "these files" else "this file"}:")()
                createTreePanel()(growX)
            }
        }
    }

}