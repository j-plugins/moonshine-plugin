package com.github.xepozz.moonshine.completion

import com.github.xepozz.moonshine.common.config.isPluginEnabled
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.util.ProcessingContext
import com.jetbrains.php.PhpIndexImpl
import com.jetbrains.php.completion.PhpClassLookupElement
import com.jetbrains.php.lang.psi.elements.ConstantReference
import com.jetbrains.php.lang.psi.elements.Field
import com.jetbrains.php.lang.psi.elements.PhpClass
import com.jetbrains.php.lang.psi.elements.impl.FieldImpl

class LayoutCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withParent(ConstantReference::class.java)
                .withSuperParent(2, Field::class.java),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    results: CompletionResultSet
                ) {
                    val project = parameters.position.project
                    if (!isPluginEnabled(project)) return

                    val element = parameters.position
                    val constantReference = element.parent as? ConstantReference ?: return
                    val field = constantReference.parent as? FieldImpl ?: return

                    if (!hasClassStringParametrizedTypes(field)) return

                    println("stopping completion")
                    results.stopHere()

                    getCachedSubclasses(field)
                        .map { PhpClassLookupElement(it, true, null) }
                        .apply { results.addAllElements(this) }
                }

                private fun getCachedSubclasses(field: FieldImpl) = CachedValuesManager.getCachedValue(field) {
                    CachedValueProvider.Result.create(
                        getSubclasses(field),
                        PsiModificationTracker.MODIFICATION_COUNT
                    )
                }

                private fun getSubclasses(field: FieldImpl): Collection<PhpClass> {
                    val phpIndex = PhpIndexImpl.getInstance(field.project) as PhpIndexImpl

                    return findClassStringParametrizedTypes(field)
                        .map { it.substring(CLASS_STRING_TYPE.length, it.length - CLASS_STRING_TYPE_END.length) }
                        .flatMap { it.split("|") }
                        .flatMap { phpIndex.getAllSubclasses(it) }
                }

                private fun hasClassStringParametrizedTypes(field: FieldImpl) =
                    findClassStringParametrizedTypes(field).isNotEmpty()

                private fun findClassStringParametrizedTypes(field: FieldImpl): Collection<String> = field
                    .docType
                    .typesWithParametrisedParts
                    .filter { it.startsWith(CLASS_STRING_TYPE) && it.endsWith(CLASS_STRING_TYPE_END) }
            }
        )
    }

    companion object {
        const val CLASS_STRING_TYPE = "\\class-string<"
        const val CLASS_STRING_TYPE_END = ">"
    }
}