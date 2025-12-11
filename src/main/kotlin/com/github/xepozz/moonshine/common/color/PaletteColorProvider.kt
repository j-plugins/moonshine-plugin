package com.github.xepozz.moonshine.common.color

import com.github.xepozz.moonshine.common.php.isMoonshineLayout
import com.intellij.openapi.editor.ElementColorProvider
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.asSafely
import com.jetbrains.php.lang.psi.elements.ArrayCreationExpression
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.PhpReturn
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import java.awt.Color

class PaletteColorProvider : ElementColorProvider {
    override fun getColorFrom(element: PsiElement): Color? {
        val classReference = element as? ClassReference ?: return null
        val classConstantReference = classReference.parent as? ClassConstantReference ?: return null
        val field = classConstantReference.parent as? Field ?: return null
        val phpClass = field.containingClass ?: return null

        if (!phpClass.isMoonshineLayout) return null

        val pairs = findColors(classReference) ?: return null

        val primary = pairs.find { it.first == "primary" }?.second ?: return null
//        val secondary = pairs.find { it.first == "secondary" }?.second ?: return null

        return primary
//        return JBColor(
//            primary,
//            secondary,
//        )
    }

    override fun setColorTo(element: PsiElement, color: Color) {
//        if (element.isValid && element is StringLiteralExpression) {
//            val project: Project = element.project
//            val hexColor = String.format("0x%02x%02x%02x", color.red, color.green, color.blue)
//            val document: Document? =
//                PsiDocumentManager.getInstance(project).getDocument(element.containingFile)
//
//            val command = Runnable {
//                val newToken =
//                    PhpPsiElementFactory.createStringLiteralExpression(project, hexColor, element.isSingleQuote)
//                element.replace(newToken)
//            }
//            CommandProcessor.getInstance()
//                .executeCommand(project, command, "JavaBundle.message(\"change.color.command.text\")", null, document)
//        }
    }


    fun findColors(classReference: ClassReference): List<Pair<String, Color?>>? {
        return CachedValuesManager.getCachedValue(classReference) {
            val result = classReference
                .resolve()
                .asSafely<PhpClass>()
                ?.findMethodByName("getColors")
                ?.let { PsiTreeUtil.findChildOfType(it, PhpReturn::class.java) }
                ?.let { PsiTreeUtil.findChildOfType(it, ArrayCreationExpression::class.java) }
                ?.hashElements
                ?.mapNotNull {
                    val key = it.key?.asSafely<StringLiteralExpression>() ?: return@mapNotNull null
                    val value = it.value?.asSafely<StringLiteralExpression>() ?: return@mapNotNull null

                    StringUtil.unquoteString(key.text) to StringUtil.unquoteString(value.text)
                }
                ?.filter { it.first in arrayOf("primary", "secondary") && OklchColorUtil.isOklchColor(it.second) }
                ?.map { it.first to OklchColorUtil.parseOklchColor(it.second)?.toRgb() }

            CachedValueProvider.Result.create(
                result,
                PsiModificationTracker.MODIFICATION_COUNT,
            )
        }
    }
}