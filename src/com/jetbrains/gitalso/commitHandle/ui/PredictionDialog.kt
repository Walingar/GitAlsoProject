package com.jetbrains.gitalso.commitHandle.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ScrollPaneFactory
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Toolkit
import javax.swing.*

class PredictionDialog(project: Project, private val root: VirtualFile, private val drawable: Set<VirtualFile>, private val color: Color) : DialogWrapper(project) {
    init {
        init()
        title = "GitAlso plugin"
    }

    override fun createActions(): Array<Action> {
        return arrayOf(DialogWrapperExitAction("Close", 0))
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(BorderLayout())
        val predictionTreeChange = PredictionTree(root, drawable, color)
        val scrollPane = ScrollPaneFactory.createScrollPane(predictionTreeChange.getTree())

        scrollPane.preferredSize = Dimension(
                50 + scrollPane.preferredSize.width,
                50 * predictionTreeChange.getTree().rowCount)

        panel.add(scrollPane)

        return panel
    }
}