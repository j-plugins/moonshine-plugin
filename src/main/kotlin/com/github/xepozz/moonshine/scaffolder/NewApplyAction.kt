package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.actionSystem.AnActionEvent

class NewApplyAction : AbstractNewAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        NewApplyDialog({ state ->
            PhpCommandUtil.invokeCommand(
                project,
                listOfNotNull(
                    "artisan",
                    "moonshine:apply",
                    state.className.takeIf { it.isNotEmpty() },
                    "-n",
                ),
            )
        }, project).show()

    }
}
