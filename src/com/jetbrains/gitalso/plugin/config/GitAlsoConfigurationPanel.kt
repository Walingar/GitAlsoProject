package com.jetbrains.gitalso.plugin.config

import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.JBTextField
import com.intellij.ui.layout.panel
import com.jetbrains.gitalso.plugin.UserStorage
import java.util.*
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

class GitAlsoConfigurationPanel : ChangeListener, DocumentListener, Configurable {
    override fun getDisplayName() = "GitAlso"

    companion object {
        private fun isFieldCorrect(field: JBTextField?): Boolean {
            if (field == null) {
                return false
            }
            val value = field.text.toDoubleOrNull() ?: return false
            return value in 0.0..1.0
        }
    }

    private fun getValueForSlider(value: String) = (value.toDouble() * 100).toInt()
    private fun getValueForField(value: Int) = (value.toDouble() / 100).toString()

    private val thresholdField by lazy {
        val startValue = UserStorage.state.threshold
        JBTextField(startValue.toString()).apply {
            document.addDocumentListener(this@GitAlsoConfigurationPanel)
        }
    }


    private val thresholdSlider by lazy {
        JSlider(0, 100, (savedValue * 100).toInt()).apply {
            addChangeListener(this@GitAlsoConfigurationPanel)
            val table = Hashtable<Int, JLabel>()
            table[0] = JLabel("Never show")
            table[100] = JLabel("Always show")
            labelTable = table
            paintLabels = true
            majorTickSpacing = 100
            paintTicks = true
        }
    }


    private val state = UserStorage.state
    private var savedValue = min(1.0, max(0.0, round(((1.0 - state.threshold) * 100)) / 100))


    override fun stateChanged(e: ChangeEvent?) {
        val newValue = getValueForField(thresholdSlider.value)
        if (!textChanged && thresholdField.text != newValue) {
            thresholdField.text = newValue
        }
    }

    private var textChanged = false

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

    override fun isModified(): Boolean {
        return isFieldCorrect(thresholdField) && savedValue != thresholdField.text.toDouble()
    }

    override fun apply() {
        if (isFieldCorrect(thresholdField)) {
            val newValue = thresholdField.text.toDouble()
            state.threshold = 1.0 - newValue
            savedValue = newValue
        }
    }

    override fun createComponent() = createCenterPanel()

    override fun reset() {
        thresholdSlider.value = (savedValue * 100).toInt()
        thresholdField.text = savedValue.toString()
    }
}