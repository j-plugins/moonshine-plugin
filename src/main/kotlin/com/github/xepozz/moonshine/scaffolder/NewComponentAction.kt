package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.actionSystem.AnActionEvent

class NewComponentAction : AbstractNewAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        NewComponentDialog({ state ->
            PhpCommandUtil.invokeCommand(
                project,
                listOfNotNull(
                    "artisan",
                    "moonshine:component",
                    state.className,
                    "--view".takeIf { state.view.isNotEmpty() },
                    state.view.takeIf { it.isNotEmpty() },
                    "-n",
                ),
            )
        }).show()

    }
}
