package com.github.xepozz.moonshine.references

import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.util.ProcessingContext
import com.jetbrains.php.lang.psi.elements.MethodReference
import com.jetbrains.php.lang.psi.elements.ParameterList
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression

class RouteReferenceContributor : PsiReferenceContributor() {
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        registrar.registerReferenceProvider(
            PlatformPatterns.psiElement(StringLiteralExpression::class.java)
                .withParent(ParameterList::class.java)
                .withSuperParent(
                    2,
                    PlatformPatterns
                        .psiElement(MethodReference::class.java)
                ),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<out PsiReference> {
                    val element = element as? StringLiteralExpression ?: return PsiReference.EMPTY_ARRAY
                    val methodReference = element.parent.parent as? MethodReference ?: return PsiReference.EMPTY_ARRAY
//                    \MoonShine\Contracts\Core\CrudResourceContract::getRoute
//                    println("directory: ${element.text}, function: ${function.name}")

                    return when (methodReference.name) {
                        "getRoute" -> arrayOf(RouteReference(element))
                        else -> PsiReference.EMPTY_ARRAY
                    }
//                        .apply { println("references: $this") }
                }
            }
        )
    }
}

