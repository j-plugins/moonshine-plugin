package com.github.xepozz.moonshine.scaffolder

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
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
        return panel {
            row {
                textField()
                    .label("Class name")
                    .focused()
                    .bindText(state::className)
            }
            row {
                textField()
                    .label("View")
                    .bindText(state::view)
            }
            row {
                textField()
                    .label("Extends")
                    .bindText(state::extends)
            }
        }
    }

    data class State(
        var className: String = "",
        var view: String = "",
        var extends: String = "",
    )
}
