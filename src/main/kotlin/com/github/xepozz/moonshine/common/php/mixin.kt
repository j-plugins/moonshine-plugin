package com.github.xepozz.moonshine.common.php

import com.intellij.openapi.util.TextRange
import com.intellij.psi.ElementManipulators
import com.intellij.psi.PsiLanguageInjectionHost

val PsiLanguageInjectionHost.contentRange: TextRange
    get() = ElementManipulators.getValueTextRange(this).shiftRight(textRange.startOffset)
