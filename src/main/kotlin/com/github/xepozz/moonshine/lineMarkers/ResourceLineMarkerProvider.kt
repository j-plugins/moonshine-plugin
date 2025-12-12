package com.github.xepozz.moonshine.lineMarkers

import com.github.xepozz.moonshine.MoonshineIcons
import com.github.xepozz.moonshine.common.config.isPluginEnabled
import com.github.xepozz.moonshine.common.php.isMoonshineModelResource
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder
import com.intellij.openapi.util.NotNullLazyValue
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.ClassConstantReference
import com.jetbrains.php.lang.psi.elements.ClassReference
import com.jetbrains.php.lang.psi.elements.PhpClass

class ResourceLineMarkerProvider : RelatedItemLineMarkerProvider() {
    override fun getLineMarkerInfo(element: PsiElement): RelatedItemLineMarkerInfo<*>? {
        val project = element.project
        if (!isPluginEnabled(project)) return null


        val phpClass = element as? PhpClass ?: return null
        val nameIdentifier = phpClass.nameIdentifier ?: return null
        val modelField = phpClass.findFieldByName("model", false) ?: return null
        val modelClassReference = modelField.defaultValue as? ClassConstantReference ?: return null

        if (!phpClass.isMoonshineModelResource) return null

        // todo: replace with more suitable icon
        return NavigationGutterIconBuilder.create(MoonshineIcons.MOONSHINE)
            .setTargets(NotNullLazyValue.createValue {
                val reference = modelClassReference.classReference as? ClassReference
                println("reference $reference")
                val res = reference?.resolve()
                println("res $res")
                listOf(res)
            })
            .setTooltipText("Open model")
            .createLineMarkerInfo(nameIdentifier)
    }
}