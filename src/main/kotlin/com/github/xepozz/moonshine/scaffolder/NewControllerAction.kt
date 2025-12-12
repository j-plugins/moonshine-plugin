package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.actionSystem.AnActionEvent

class NewControllerAction : AbstractNewAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        NewControllerDialog({ state ->
            PhpCommandUtil.invokeCommand(
                project,
                listOfNotNull(
                    "artisan",
                    "moonshine:controller",
                    state.className.takeIf { it.isNotEmpty() },
                    "-n",
                ),
            )
        }, project).show()

    }
}
