package com.github.xepozz.moonshine.common.php

import com.jetbrains.php.PhpIndex
import com.jetbrains.php.lang.psi.elements.PhpClass

fun PhpIndex.findSubclassesIncluding(
    fqn: String,
    filter: (PhpClass) -> Boolean
): MutableList<PhpClass> {
    val fieldClasses = mutableListOf<PhpClass>()
    val fieldClass = this.getClassesByFQN(fqn)
    fieldClasses.addAll(fieldClass)
    this.processAllSubclasses(fqn) {
        if (filter(it)) {
            fieldClasses.add(it)
        }
        true
    }
    return fieldClasses
}
