package com.github.xepozz.moonshine.scaffolder

import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.actionSystem.AnActionEvent

class NewResourceAction : AbstractNewAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        NewResourceDialog({ state ->
            PhpCommandUtil.invokeCommand(
                project,
                listOfNotNull(
                    "artisan",
                    "moonshine:resource",
                    state.modelClass,
//                    "--model",
//                    "element.fqn",
                    "--type",
                    "${state.type}",
                    "-n",
                ),
            )
        }, project).show()

    }
}