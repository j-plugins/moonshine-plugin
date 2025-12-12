package com.github.xepozz.moonshine.scaffolder

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class NewUserDialog(
    callback: (State) -> Unit,
    project: Project,
    override var state: State = State(),
) : AbstractNewDialog<NewUserDialog.State>(callback, project) {
    init {
        title = "New User"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row {
                textField()
                    .label("Username")
                    .focused()
                    .bindText(state::username)
            }
            row {
                textField()
                    .label("Name")
                    .bindText(state::name)
            }
            row {
                passwordField()
                    .label("Password")
                    .bindText(state::password)
            }
        }
    }

    data class State(
        var username: String = "",
        var name: String = "",
        var password: String = "",
    )
}