package com.github.xepozz.moonshine.common.color

import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.ElementColorProvider
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.PhpPsiElementFactory
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import java.awt.Color
import java.util.regex.Pattern

class OklchColorProvider : ElementColorProvider {
    override fun getColorFrom(element: PsiElement): Color? {
        val element = element as? StringLiteralExpression ?: return null

        val text = element.contents
        if (OklchColorUtil.isOklchColor(text)) {
            val parseOklchColor = OklchColorUtil.parseOklchColor(text)
            println("parseOklchColor: $parseOklchColor, text: $text")
            return parseOklchColor?.toRgb()
        }

        return null
    }

    override fun setColorTo(element: PsiElement, color: Color) {
        if (element.isValid && element is StringLiteralExpression) {
            val project: Project = element.project
            val hexColor = String.format("0x%02x%02x%02x", color.red, color.green, color.blue)
            val document: Document? =
                PsiDocumentManager.getInstance(project).getDocument(element.containingFile)

            val command = Runnable {
                val newToken =
                    PhpPsiElementFactory.createStringLiteralExpression(project, hexColor, element.isSingleQuote)
                element.replace(newToken)
            }
            CommandProcessor.getInstance()
                .executeCommand(project, command, "JavaBundle.message(\"change.color.command.text\")", null, document)
        }
    }

    companion object Companion {
        private val colorPattern = Pattern.compile("^\\d+(\\.\\d+) \\d+(\\.\\d+) \\d+(\\.\\d+)$")
    }
}