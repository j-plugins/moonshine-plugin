package com.github.xepozz.moonshine.scaffolder

import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class NewHandlerDialog(
    callback: (State) -> Unit,
    override var state: State = State(),
) : AbstractNewDialog<NewHandlerDialog.State>(callback) {
    init {
        title = "New Handler"
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
        }
    }

    data class State(
        var className: String = "",
    )
}
