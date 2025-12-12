package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.actionSystem.AnActionEvent

class NewPageAction : AbstractNewAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        NewPageDialog({ state ->
            PhpCommandUtil.invokeCommand(
                project,
                listOfNotNull(
                    "artisan",
                    "moonshine:page",
                    state.modelClass,
                    "--force",
                    "--crud".takeIf { state.crud },
                    "--skip-menu".takeIf { state.skipMenu },
                    "-n",
                ),
            )
        }, project).show()

    }
}