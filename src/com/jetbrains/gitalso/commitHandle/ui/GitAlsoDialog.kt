package com.jetbrains.gitalso.commitHandle.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vcs.FilePath
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.JBColor
import com.intellij.ui.components.labels.LinkLabel
import com.intellij.ui.components.labels.LinkListener
import com.intellij.vcsUtil.VcsUtil
import com.jetbrains.gitalso.log.State
import com.jetbrains.gitalso.storage.log.Logger
import java.awt.*
import javax.swing.*

class GitAlsoDialog(private val project: Project, modifiedFiles: Set<VirtualFile>, unmodifiedFiles: Set<VirtualFile>) : DialogWrapper(project) {
    private val modifiedDrawable: Set<VirtualFile>
    private val unmodifiedDrawable: Set<VirtualFile>
    private val root = project.baseDir!!

    private val blueColor = JBColor(Color(56, 117, 214), Color(104, 151, 187))
    private val blackColor = JBColor.BLACK

    override fun getDimensionServiceKey() = "com.jetbrains.gitalso.commitHandle.ui.GitAlsoDialog"

    private fun getDrawable(files: Set<VirtualFile>): Set<VirtualFile> {
        val drawable = HashSet<VirtualFile>()
        for (file in files) {
            drawable.add(file)
            var parent = file.parent
            while (parent != null) {
                drawable.add(parent)
                parent = parent.parent
            }
        }

        return drawable
    }

    init {
        modifiedDrawable = getDrawable(modifiedFiles)
        unmodifiedDrawable = getDrawable(unmodifiedFiles)

        init()
        title = "GitAlso plugin"
    }

    override fun createActions(): Array<Action> {
        val cancel = DialogWrapperExitAction("Cancel", 1)
        cancel.putValue(DEFAULT_ACTION, 0)
        val commitAnyway = DialogWrapperExitAction("Commit anyway", 0)
        return arrayOf(cancel, commitAnyway)
    }

    private fun createPredictionPanel(textPrefix: String, drawable: Set<VirtualFile>, color: Color): JPanel {
        val panel = JPanel(FlowLayout(FlowLayout.LEFT))
        val leafs = drawable.count { !it.isDirectory }

        val textLabel = JLabel("$textPrefix $leafs ${if (leafs == 1) "file" else "files"}.")
        val link = LinkLabel("show", null, LinkListener<Any> { _, _ ->
            if (textPrefix == "commit") {
                Logger.simpleActionLog(com.jetbrains.gitalso.log.Action.SHOW_MODIFIED, State.SHOW_MAIN_DIALOG, State.SHOW_MODIFIED)
            } else {
                Logger.simpleActionLog(com.jetbrains.gitalso.log.Action.SHOW_UNMODIFIED, State.SHOW_MAIN_DIALOG, State.SHOW_UNMODIFIED)
            }
            PredictionDialog(project, root, drawable, color).show()
            if (textPrefix == "commit") {
                Logger.simpleActionLog(com.jetbrains.gitalso.log.Action.CLOSE_MODIFIED, State.SHOW_MODIFIED, State.SHOW_MAIN_DIALOG)
            } else {
                Logger.simpleActionLog(com.jetbrains.gitalso.log.Action.CLOSE_UNMODIFIED, State.SHOW_UNMODIFIED, State.SHOW_MAIN_DIALOG)
            }
        })

        panel.add(textLabel)
        panel.add(link)

        return panel
    }

    override fun createCenterPanel(): JComponent? {
        val mainPanel = JPanel(GridLayout(0, 1))
        val commonLabel = JLabel("You might have forgotten to")
        mainPanel.add(commonLabel)

        if (modifiedDrawable.isNotEmpty()) {
            val commitPanel = createPredictionPanel("commit", modifiedDrawable, blueColor)
            mainPanel.add(commitPanel)
        }

        if (unmodifiedDrawable.isNotEmpty()) {
            val changePanel = createPredictionPanel("modify", unmodifiedDrawable, blackColor)
            mainPanel.add(changePanel)
        }


        return mainPanel
    }

}