package com.github.xepozz.moonshine.common.php

import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.CachedValuesManager
import com.jetbrains.php.lang.psi.elements.PhpClass

val PsiLanguageInjectionHost.contentRange: TextRange
    get() = ElementManipulators.getValueTextRange(this).shiftRight(textRange.startOffset)

val PhpClass.isMoonshineModel: Boolean
    get() = CachedValuesManager.getCachedValue(this, MoonshineModelProvider(this))

val PhpClass.isMoonshineModelResource: Boolean
    get() = CachedValuesManager.getCachedValue(this, MoonshineModelResourceProvider(this))

val PhpClass.isMoonshineLayout: Boolean
    get() = CachedValuesManager.getCachedValue(this, MoonshineLayoutProvider(this))
