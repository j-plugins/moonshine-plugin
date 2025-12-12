package com.github.xepozz.moonshine.scaffolder.project

data class MoonshineProjectGeneratorSettings(
    var version: String = "latest",
    var createGit: Boolean = true,
    var template: String = "moonshine/app",
)