package com.github.xepozz.moonshine.listeners

import com.github.xepozz.moonshine.repository.RoutesRepository
import com.github.xepozz.moonshine.repository.toRouteCollection
import com.github.xepozz.moonshine.service.ArtisanDumpService
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class DumpLaravelData : ProjectActivity {
    override suspend fun execute(project: Project) {
        val artisan = project.getService(ArtisanDumpService::class.java)
        val result = artisan.dump("route:list", listOf("--json"))

        val data = toRouteCollection(result)

        val repository = project.getService(RoutesRepository::class.java)
        repository.updateRoutes(data)
    }
}