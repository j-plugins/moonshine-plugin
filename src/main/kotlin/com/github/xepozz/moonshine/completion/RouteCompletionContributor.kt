package com.github.xepozz.moonshine.completion

import com.github.xepozz.moonshine.MoonshineIcons
import com.github.xepozz.moonshine.repository.RoutesRepository
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.CompletionType
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class RouteCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withParent(StringLiteralExpression::class.java),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    results: CompletionResultSet
                ) {
                    val element = parameters.position
                    val stringLiteral = element.parent as? StringLiteralExpression ?: return

                    val routesRepo = element.project.getService(RoutesRepository::class.java)

                    routesRepo.getRoutes()
                        .filter { it.name?.startsWith("moonshine.") == true }
                        .mapNotNull {
                            val name = it.name?.substringAfter("moonshine.")
                            if (name.isNullOrEmpty()) return@mapNotNull null

                            LookupElementBuilder.create(name)
                                .withIcon(MoonshineIcons.MOONSHINE)
                                .withTypeText(it.uri)
                                .withTailText(" " + it.method)
                        }
                        .apply { results.addAllElements(this) }
                }
            }
        )
    }
}