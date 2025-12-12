package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.actionSystem.AnActionEvent

class NewPolicyAction : AbstractNewAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        NewPolicyDialog({ state ->
            PhpCommandUtil.invokeCommand(
                project,
                listOfNotNull(
                    "artisan",
                    "moonshine:policy",
                    state.className.takeIf { it.isNotEmpty() },
                    "-n",
                ),
            )
        }).show()

    }
}
