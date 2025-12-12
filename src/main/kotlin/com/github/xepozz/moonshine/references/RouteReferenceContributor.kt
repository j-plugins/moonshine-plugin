package com.github.xepozz.moonshine.references

import com.github.xepozz.moonshine.MoonshineClasses
import com.github.xepozz.moonshine.common.config.isPluginEnabled
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceContributor
import com.intellij.psi.PsiReferenceProvider
import com.intellij.psi.PsiReferenceRegistrar
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.util.ProcessingContext
import com.intellij.util.asSafely
import com.jetbrains.php.lang.psi.elements.Method
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
                    PlatformPatterns.psiElement(MethodReference::class.java)
                ),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(
                    element: PsiElement,
                    context: ProcessingContext
                ): Array<out PsiReference> {
                    val project = element.project
                    if (!isPluginEnabled(project)) return emptyArray()

                    val element = element as? StringLiteralExpression ?: return PsiReference.EMPTY_ARRAY
                    val methodReference = element.parent.parent as? MethodReference ?: return PsiReference.EMPTY_ARRAY

                    val methodName = methodReference.name
                    if (methodName != "getRoute") return PsiReference.EMPTY_ARRAY

                    val originalElement = methodReference.originalElement

                    val possibleClasses = CachedValuesManager.getCachedValue(originalElement) {
                        CachedValueProvider.Result.create(
                            originalElement
                                .asSafely<MethodReference>()
                                ?.resolveGlobal(false)
                                ?.filterIsInstance<Method>()
                                ?.mapNotNull { it.containingClass?.fqn }
                                ?: emptyList(),
                            methodReference,
                            originalElement,
                        )
                    }

                    if (!possibleClasses.contains(MoonshineClasses.CRUD_RESOURCE_CONTRACT)) return PsiReference.EMPTY_ARRAY

                    return arrayOf(RouteReference(element))
                }
            },
            PsiReferenceRegistrar.HIGHER_PRIORITY
        )
    }
}

