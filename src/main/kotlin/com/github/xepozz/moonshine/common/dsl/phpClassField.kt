package com.github.xepozz.moonshine.common.dsl

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.EditorTextField
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.listCellRenderer.textListCellRenderer
import com.jetbrains.php.completion.PhpCompletionUtil
import com.jetbrains.php.lang.psi.elements.PhpClass

fun Row.phpClassField(
    project: Project,
    disposable: Disposable,
    defaultValue: String,
    filter: ((com.jetbrains.php.lang.psi.elements.PhpClass) -> Boolean)? = null
): Cell<EditorTextField> {
    val textField = EditorTextField("", project, com.intellij.openapi.fileTypes.FileTypes.PLAIN_TEXT)

    return cell(textField)
        .apply {
            PhpCompletionUtil.installClassCompletion(
                component,
                defaultValue,
                disposable,
                filter,
            )
        }
}


fun Row.phpClassComboBox(
    fieldClasses: Collection<PhpClass>,
    project: Project,
): Cell<ComboBox<*>> {
    val textField = EditorTextField("", project, com.intellij.openapi.fileTypes.FileTypes.PLAIN_TEXT)

    return comboBox(fieldClasses, textListCellRenderer({ it?.fqn }))
}

