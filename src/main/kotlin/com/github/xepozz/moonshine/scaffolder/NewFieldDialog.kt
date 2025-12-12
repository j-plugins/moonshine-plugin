package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.MoonshineClasses
import com.github.xepozz.moonshine.common.dsl.phpClassComboBox
import com.github.xepozz.moonshine.common.toSnakeCase
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.text
import com.intellij.ui.dsl.builder.toMutableProperty
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass
import javax.swing.JComponent

class NewFieldDialog(
    callback: (State) -> Unit,
    project: Project,
    override var state: State = State(),
) : AbstractNewDialog<NewFieldDialog.State>(callback, project) {
    init {
        title = "New Field"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val phpIndex = PhpIndex.getInstance(project)

        val fieldClasses = mutableListOf<PhpClass>()
        phpIndex.processAllSubclasses(MoonshineClasses.FIELD) {
            if (!it.isFinal) {
                fieldClasses.add(it)
            }
            true
        }

        return panel {
            lateinit var viewField: Cell<JBTextField>
            row {
                textField()
                    .label("Class name")
                    .focused()
                    .bindText(state::className)
                    .onChanged {
                        val className = it.text.substringAfterLast("\\")
                        val newText = className.toSnakeCase()
                        viewField.text("admin.fields.$newText")
                    }
            }
            row {
                viewField = textField()
                    .label("View")
                    .bindText(state::view)
            }
            row {
                phpClassComboBox(fieldClasses, project)
                    .label("Extends")
                    .bind({ state.extends }, { _, value -> state.extends = value }, state::extends.toMutableProperty())

            }
        }
    }

    data class State(
        var className: String = "",
        var view: String = "",
        var extends: String = "",
    )
}
