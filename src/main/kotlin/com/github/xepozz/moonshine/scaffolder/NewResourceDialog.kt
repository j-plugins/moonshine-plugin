package com.github.xepozz.moonshine.scaffolder

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class NewResourceDialog(
    callback: (State) -> Unit,
    project: Project,
    override var state: State = State(),
) : AbstractNewDialog<NewResourceDialog.State>(callback, project) {
    init {
        title = "New Resource"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row {
                textField()
                    .label("Model class")
                    .focused()
                    .bindText(state::modelClass)
            }
            buttonsGroup {
                row {
                    radioButton("Default model resource", 1)
                }
                row {
                    radioButton("Model resource with pages", 2)
                }
                row {
                    radioButton("Empty resource", 3)
                }
            }.bind(state::type)
        }
    }

    data class State(
        var modelClass: String = "",
        var type: Int = 1,
    )
}