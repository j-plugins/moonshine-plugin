package com.github.xepozz.moonshine.scaffolder

import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class NewPublishDialog(
    callback: (State) -> Unit,
    project: Project,
    override var state: State = State(),
) : AbstractNewDialog<NewPublishDialog.State>(callback, project) {
    init {
        title = "Publish Assets"
        init()
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            buttonsGroup {
                row {
                    radioButton("Assets", "assets")
                }
                row {
                    radioButton("Assets Template", "assets-template")
                }
                row {
                    radioButton("Resources", "resources")
                }
                row {
                    radioButton("Forms", "forms")
                }
                row {
                    radioButton("Pages", "pages")
                }
            }.bind(state::type)
        }
    }

    data class State(
        var type: String = "assets",
    ) {
        fun getTypeValue(): String = type
    }
}
