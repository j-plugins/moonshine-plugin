package com.github.xepozz.moonshine.common

fun String.toCamelCase() =
    split('_').joinToString("", transform = String::capitalize)

fun String.toSnakeCase() = replace(humps, "_").lowercase()
private val humps = "(?<=.)(?=\\p{Upper})".toRegex()
