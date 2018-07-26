package com.jetbrains.gitalso.commitHandle.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.ScrollPaneFactory
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*

class PredictionDialog(project: Project, private val root: VirtualFile, private val drawable: Set<VirtualFile>, private val color: Color) : DialogWrapper(project) {
    init {
        init()
        title = "Files"
    }

    override fun createActions(): Array<Action> {
        return arrayOf(DialogWrapperExitAction("Close", 0))
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(BorderLayout())
        val predictionTreeChange = PredictionTree(root, drawable, color)
        panel.add(ScrollPaneFactory.createScrollPane(predictionTreeChange.getTree()))

        return panel
    }
}