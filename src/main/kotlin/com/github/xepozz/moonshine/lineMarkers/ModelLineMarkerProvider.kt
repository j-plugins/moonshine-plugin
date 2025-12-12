package com.github.xepozz.moonshine.lineMarkers

import com.github.xepozz.moonshine.MoonshineClasses
import com.github.xepozz.moonshine.MoonshineIcons
import com.github.xepozz.moonshine.common.config.isPluginEnabled
import com.github.xepozz.moonshine.common.php.isMoonshineModel
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.util.NotNullLazyValue
import com.intellij.psi.PsiElement
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.PhpClass

class ModelLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun getLineMarkerInfo(element: PsiElement): RelatedItemLineMarkerInfo<*>? {
        val project = element.project
        if (!isPluginEnabled(project)) return null

        val phpClass = element as? PhpClass ?: return null
        val nameIdentifier = phpClass.nameIdentifier ?: return null

        if (!phpClass.isMoonshineModel) return null

        // todo: replace with more suitable icon
        return NavigationGutterIconBuilder.create(MoonshineIcons.MOONSHINE)
            .setTargets(NotNullLazyValue.createValue {
                CachedValuesManager.getCachedValue(phpClass) {
                    val phpIndex = PhpIndex.getInstance(project)
                    CachedValueProvider.Result.create(
                        findResourceClasses(phpIndex, phpClass)
                            .flatMap {
                                listOf(it, *findPagesInResource(it).toTypedArray())
                            },
                        PsiModificationTracker.MODIFICATION_COUNT,
                    )
                }
            })
            .setTooltipText("Open MoonShine pages")
            .createLineMarkerInfo(nameIdentifier)
    }

    private fun findResourceClasses(
        phpIndex: PhpIndex,
        element: PhpClass
    ): MutableList<PhpClass> {
        val resources = mutableListOf<PhpClass>()

        phpIndex.processAllSubclasses(MoonshineClasses.MODEL_RESOURCE, { phpClass ->
            phpClass
                .findFieldByName("model", false)
                ?.run { defaultValue as? ClassConstantReference }
                ?.run { classReference as? ClassReference }
                ?.takeIf { it.fqn == element.fqn }
                ?.apply { resources.add(phpClass) }

            true
        })

        return resources
    }

    private fun findPagesInResource(
        element: PhpClass
    ): Collection<PhpClass> {
        return element.findMethodByName("pages")
            ?.takeIf { it.containingClass?.fqn != MoonshineClasses.CRUD_RESOURCE }
            ?.run { PsiTreeUtil.findChildrenOfType(this, ClassReference::class.java) }
            ?.mapNotNull { it.resolve() as? PhpClass }
            ?: emptyList()
    }
}