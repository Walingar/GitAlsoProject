package com.jetbrains.gitalso.plugin.config

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import com.jetbrains.gitalso.plugin.UserSettings
import java.util.*
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class GitAlsoConfigurationPanel : Configurable {
    companion object {
        private fun isFieldCorrect(field: JBTextField?): Boolean {
            if (field == null) {
                return false
            }
            val value = field.text.toDoubleOrNull() ?: return false
            return value in 0.0..1.0
        }

        private fun getValueForSlider(value: String) = (value.toDouble() * 100).toInt()

        private fun getValueForField(value: Int) = (value.toDouble() / 100).toString()
    }

    private val userSettings = ServiceManager.getService(UserSettings::class.java)
    private var textChanged = false
    private var reversedValue = 1.0 - userSettings.threshold

    private val thresholdField by lazy {
        JBTextField(reversedValue.toString()).apply {
            document.addDocumentListener(FieldListener())
        }
    }

    private val thresholdSlider by lazy {
        JSlider(0, 100, (reversedValue * 100).toInt()).apply {
            addChangeListener(SliderListener())
            val table = Hashtable<Int, JLabel>()
            table[0] = JLabel("Never show")
            table[100] = JLabel("Always show")
            labelTable = table
            paintLabels = true
            majorTickSpacing = 100
            paintTicks = true
        }
    }

    override fun getDisplayName() = "GitAlso"

    private fun getThresholdPanel() = panel {
        row("Adjust triggering level: ") {
            thresholdSlider(grow, push)
            thresholdField(push)
        }
    }

    private fun createCenterPanel() = panel {
        row {
            getThresholdPanel()()
        }
    }

    override fun createComponent() = createCenterPanel()

    override fun isModified(): Boolean {
        return isFieldCorrect(thresholdField) && reversedValue != thresholdField.text.toDouble()
    }

    override fun apply() {
        if (isFieldCorrect(thresholdField)) {
            val newValue = thresholdField.text.toDouble()
            userSettings.threshold = 1.0 - newValue
            reversedValue = newValue
        }
    }

    override fun reset() {
        thresholdSlider.value = (reversedValue * 100).toInt()
        thresholdField.text = reversedValue.toString()
    }

    private inner class SliderListener : ChangeListener {
        override fun stateChanged(e: ChangeEvent?) {
            val newValue = getValueForField(thresholdSlider.value)
            if (!textChanged && thresholdField.text != newValue) {
                thresholdField.text = newValue
            }
        }
    }

    private inner class FieldListener : DocumentListener {
        private fun sliderUpdate() {
            textChanged = true
            if (isFieldCorrect(thresholdField)) {
                thresholdSlider.value = getValueForSlider(thresholdField.text)
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
    }
}