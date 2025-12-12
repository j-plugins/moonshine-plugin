package com.github.xepozz.moonshine.scaffolder

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class NewLayoutDialog(
    callback: (State) -> Unit,
    project: Project,
    override var state: State = State(),
) : AbstractNewDialog<NewLayoutDialog.State>(callback, project) {
    init {
        title = "New Layout"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row {
                textField()
                    .label("Class name")
                    .focused()
                    .bindText(state::className)
            }
            row {
                checkBox("Default")
                    .bindSelected(state::default)
            }
            row {
                textField()
                    .label("Palette")
                    .bindText(state::palette)
            }
        }
    }

    data class State(
        var className: String = "",
        var default: Boolean = false,
        var palette: String = "",
    )
}
