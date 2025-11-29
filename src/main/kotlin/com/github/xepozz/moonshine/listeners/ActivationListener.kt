package com.github.xepozz.moonshine.listeners

import com.github.xepozz.moonshine.common.config.MoonshineActivityService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.jetbrains.php.composer.ComposerConfigUtils
import com.jetbrains.php.composer.ComposerDataService

class ActivationListener : ProjectActivity {
    override suspend fun execute(project: Project) {
        val file = ComposerDataService.getInstance(project).configFile ?: return
        val installedPackagesFromConfig = ComposerConfigUtils.getInstalledPackagesFromConfig(file)

        if (installedPackagesFromConfig.any { it.name == "moonshine/moonshine" }) {
            project.getService(MoonshineActivityService::class.java).enable()
        }
    }
}