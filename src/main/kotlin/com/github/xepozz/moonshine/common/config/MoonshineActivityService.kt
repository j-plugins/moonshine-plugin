package com.github.xepozz.moonshine.common.config

import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class MoonshineActivityService {
    private var enabled = false
    fun enable() {
        enabled = true
    }

    fun isEnabled() = enabled
}

fun isPluginEnabled(project: com.intellij.openapi.project.Project): Boolean {
    return project.getService(MoonshineActivityService::class.java).isEnabled()
}