package com.github.xepozz.moonshine.scaffolder

import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class NewComponentDialog(
    callback: (State) -> Unit,
    override var state: State = State(),
) : AbstractNewDialog<NewComponentDialog.State>(callback) {
    init {
        title = "New Component"
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
        }
    }

    data class State(
        var className: String = "",
        var view: String = "",
    )
}
