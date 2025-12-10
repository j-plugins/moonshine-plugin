package com.github.xepozz.moonshine.common.php

import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.jetbrains.php.PhpClassHierarchyUtils
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass

val PsiLanguageInjectionHost.contentRange: TextRange
    get() = ElementManipulators.getValueTextRange(this).shiftRight(textRange.startOffset)

fun PhpClass.extendsCached(fqn: String): Boolean {
    return CachedValuesManager.getCachedValue(this) {
        val phpIndex = PhpIndex.getInstance(project)

        val modelClass = phpIndex.getClassesByFQN(fqn).firstOrNull()

        val result = when {
            modelClass == null -> false
            else -> PhpClassHierarchyUtils.isSuperClass(modelClass, this, true)
        }

        CachedValueProvider.Result.create(
            result,
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }
}