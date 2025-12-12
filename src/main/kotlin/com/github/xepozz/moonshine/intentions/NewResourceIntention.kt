package com.github.xepozz.moonshine.intentions

import com.github.xepozz.moonshine.common.config.isPluginEnabled
import com.github.xepozz.moonshine.utils.PhpCommandUtil
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpClass

class NewResourceIntention : AbstractNewIntention() {
    override fun getText() = "Create Moonshine Resource"
    override fun getFamilyName() = "Create Moonshine Resource"
    override fun isAvailable(project: Project, editor: Editor, element: PsiElement) = isPluginEnabled(project)

    override fun invoke(
        project: Project,
        editor: Editor,
        element: PsiElement
    ) {
        val element = element.parent as? PhpClass ?: return

        PhpCommandUtil.invokeCommand(
            project,
            listOf(
                "artisan",
                "moonshine:resource",
                element.name,
                "--model",
                element.fqn,
                "--type",
                "2",
                "-n",
            ),
        )
    }
}