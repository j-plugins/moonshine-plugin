package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.actionSystem.AnActionEvent

class NewFieldAction : AbstractNewAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        NewFieldDialog({ state ->
            PhpCommandUtil.invokeCommand(
                project,
                listOfNotNull(
                    "artisan",
                    "moonshine:field",
                    state.className.takeIf { it.isNotEmpty() },
                    "--view".takeIf { state.view.isNotEmpty() },
                    state.view.takeIf { it.isNotEmpty() },
                    "--extends".takeIf { state.extends.isNotEmpty() },
                    state.extends.takeIf { it.isNotEmpty() },
                    "-n",
                ),
            )
        }).show()

    }
}
