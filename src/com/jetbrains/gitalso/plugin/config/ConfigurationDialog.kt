package com.jetbrains.gitalso.plugin.config

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.ui.components.JBTextField
import com.jetbrains.gitalso.plugin.UserStorage
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.util.*
import javax.swing.*
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class ConfigurationDialog(project: Project) : DialogWrapper(project), ChangeListener, DocumentListener {
    init {
        init()
        title = "GitAlso configurations"
    }

    companion object {
        private fun isFieldCorrect(field: JBTextField?): Boolean {
            if (field == null) {
                return false
            }
            val value = field.text.toDoubleOrNull() ?: return false
            return value in 0.0..1.0
        }
    }

    private lateinit var field: JBTextField
    private lateinit var slider: JSlider

    override fun doValidateAll(): List<ValidationInfo> {
        return if (!isFieldCorrect(field))
            listOf(ValidationInfo("Decimal number (from 0.0 to 1.0) expected", field))
        else
            listOf()
    }

    override fun stateChanged(e: ChangeEvent?) {
        val newValue = slider.value.toDouble() / 1000
        if (!textChanged && field.text != newValue.toString()) {
            field.text = newValue.toString()
        }
    }

    private var textChanged = false

    private fun sliderUpdate() {
        textChanged = true
        if (isFieldCorrect(field)) {
            slider.value = (field.text.toDouble() * 1000).toInt()
        }
        textChanged = false
    }

    override fun changedUpdate(e: DocumentEvent?) {
    }

    override fun insertUpdate(e: DocumentEvent?) {
        sliderUpdate()
    }

    override fun removeUpdate(e: DocumentEvent?) {
        sliderUpdate()
    }

    private fun createSlider(): JComponent {
        val startValue = UserStorage.state.threshold

        slider = JSlider(0, 1000, (startValue * 1000).toInt())
        slider.addChangeListener(this)

        val table = Hashtable<Int, JLabel>()
        table[0] = JLabel("Always show")
        table[1000] = JLabel("Never show")
        slider.labelTable = table
        slider.paintLabels = true
        slider.majorTickSpacing = 1000
        slider.paintTicks = true

        return slider
    }

    private fun createField(): JComponent {
        val startValue = UserStorage.state.threshold
        field = JBTextField(startValue.toString())
        field.document.addDocumentListener(this)

        return field
    }

    override fun createCenterPanel(): JComponent? {
        val mainPanel = JPanel(BorderLayout())
        val settingPanel = JPanel(FlowLayout())

        val sliderPanel = createSlider()
        val fieldPanel = createField()

        settingPanel.add(sliderPanel)
        settingPanel.add(fieldPanel)

        mainPanel.add(JLabel("Adjust triggering level"), BorderLayout.PAGE_START)
        mainPanel.add(settingPanel, BorderLayout.CENTER)

        return mainPanel
    }
}