package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.actionSystem.AnActionEvent

class NewUserAction : AbstractNewAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        NewUserDialog({ state ->
            PhpCommandUtil.invokeCommand(
                project,
                listOfNotNull(
                    "artisan",
                    "moonshine:user",
                    "--username".takeIf { state.username.isNotEmpty() },
                    state.username.takeIf { it.isNotEmpty() },
                    "--name".takeIf { state.name.isNotEmpty() },
                    state.name.takeIf { it.isNotEmpty() },
                    "--password".takeIf { state.password.isNotEmpty() },
                    state.password.takeIf { it.isNotEmpty() },
                    "-n",
                ),
            )
        }, project).show()

    }
}