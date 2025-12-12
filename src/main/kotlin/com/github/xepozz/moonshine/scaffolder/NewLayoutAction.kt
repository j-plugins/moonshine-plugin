package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.actionSystem.AnActionEvent

class NewLayoutAction : AbstractNewAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        NewLayoutDialog({ state ->
            PhpCommandUtil.invokeCommand(
                project,
                listOfNotNull(
                    "artisan",
                    "moonshine:layout",
                    state.className.takeIf { it.isNotEmpty() },
                    "--default".takeIf { state.default },
                    "--palette".takeIf { state.palette.isNotEmpty() },
                    state.palette.takeIf { it.isNotEmpty() },
                    "-n",
                ),
            )
        }).show()

    }
}
