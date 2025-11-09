package com.github.xepozz.moonshine.references

import com.github.xepozz.moonshine.MoonshineIcons
import com.github.xepozz.moonshine.repository.RoutesRepository
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementResolveResult
import com.intellij.psi.PsiPolyVariantReferenceBase
import com.intellij.psi.ResolveResult
import com.intellij.psi.impl.source.tree.injected.changesHandler.contentRange
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.StringLiteralExpression
import com.jetbrains.php.lang.psi.resolve.types.PhpTypeSignatureKey

class RouteReference(
//    val directory: String,
    val myElement: StringLiteralExpression,
) : PsiPolyVariantReferenceBase<PsiElement>(myElement, myElement.contentRange.shiftLeft(myElement.textRange.startOffset)) {
    override fun multiResolve(p0: Boolean): Array<out ResolveResult> {
        val project = element.project

        val routesRepo = project.getService(RoutesRepository::class.java)
        val phpIndex = PhpIndex.getInstance(project)

        val route = routesRepo.getByMoonshineName(myElement.contents) ?: return emptyArray()

        val psiElements = phpIndex.getBySignature(
            PhpTypeSignatureKey.METHOD.sign(PhpTypeSignatureKey.CLASS.sign(
                route.action.replace('@', '.')
            ))
        );

        return PsiElementResolveResult.createResults(psiElements)
    }

//    override fun isSoft() = true

    override fun getVariants(): Array<out Any?> {
        val routesRepo = element.project.getService(RoutesRepository::class.java)
        val phpIndex = PhpIndex.getInstance(element.project)

        println("variants")
        return routesRepo.getRoutes()
            .filter { it.name?.startsWith("moonshine.") == true }
            .mapNotNull {
                val name = it.name?.substringAfter("moonshine.")
                if (name.isNullOrEmpty()) return@mapNotNull null

                val psiElements = phpIndex.getBySignature(
                    PhpTypeSignatureKey.METHOD.sign(PhpTypeSignatureKey.CLASS.sign(
                        it.action.replace('@', '.')
                    ))
                );
                println("psiElements $psiElements")
                LookupElementBuilder.create(name)
                    .withIcon(MoonshineIcons.MOONSHINE)
                    .withTypeText(it.uri)
                    .withPsiElement(psiElements.firstOrNull())
                    .withTailText(" " + it.method)
            }
            .toTypedArray()
    }
}