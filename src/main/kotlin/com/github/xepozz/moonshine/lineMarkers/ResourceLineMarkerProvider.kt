package com.github.xepozz.moonshine.lineMarkers

import com.github.xepozz.moonshine.MoonshineClasses
import com.github.xepozz.moonshine.MoonshineIcons
import com.github.xepozz.moonshine.common.config.isPluginEnabled
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.util.NotNullLazyValue
import com.intellij.psi.PsiElement
import com.jetbrains.php.PhpClassHierarchyUtils
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.PhpClass

class ResourceLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun getLineMarkerInfo(element: PsiElement): RelatedItemLineMarkerInfo<*>? {
        val project = element.project
        if (!isPluginEnabled(project)) return null


        val element = element as? PhpClass ?: return null
        val nameIdentifier = element.nameIdentifier ?: return null
        val modelField = element.findFieldByName("model", false) ?: return null
        val modelClassReference = modelField.defaultValue as? ClassConstantReference ?: return null

        val phpIndex = PhpIndex.getInstance(project)

        val resourceClass = phpIndex.getClassesByFQN(MoonshineClasses.MODEL_RESOURCE).firstOrNull() ?: return null

        if (!PhpClassHierarchyUtils.isSuperClass(resourceClass, element, true)) return null

        // todo: replace with more suitable icon
        return NavigationGutterIconBuilder.create(MoonshineIcons.MOONSHINE)
            .setTargets(NotNullLazyValue.createValue {
                listOf((modelClassReference.classReference as? ClassReference)?.resolve())
            })
            .setTooltipText("Open model")
            .createLineMarkerInfo(nameIdentifier)

    }
}