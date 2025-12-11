package com.github.xepozz.moonshine.common.php

import com.github.xepozz.moonshine.MoonshineClasses
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.PsiModificationTracker
import com.jetbrains.php.PhpClassHierarchyUtils
import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass

abstract class BaseClassProvider(val phpClass: PhpClass, val modelClass: String) : CachedValueProvider<Boolean> {
    override fun compute(): CachedValueProvider.Result<Boolean> {
        val phpIndex = PhpIndex.getInstance(phpClass.project)

        val modelClass = phpIndex.getClassesByFQN(modelClass).firstOrNull()

        val result = when {
            modelClass == null -> false
            else -> PhpClassHierarchyUtils.isSuperClass(modelClass, phpClass, true)
        }

        return CachedValueProvider.Result.create(
            result,
            modelClass,
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }
}

class MoonshineModelProvider(phpClass: PhpClass) : BaseClassProvider(phpClass, MoonshineClasses.MODEL)
class MoonshineLayoutProvider(phpClass: PhpClass) : BaseClassProvider(phpClass, MoonshineClasses.ABSTRACT_LAYOUT)
class MoonshineModelResourceProvider(phpClass: PhpClass) : BaseClassProvider(phpClass, MoonshineClasses.MODEL_RESOURCE)