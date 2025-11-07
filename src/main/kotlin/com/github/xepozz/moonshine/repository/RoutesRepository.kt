package com.github.xepozz.moonshine.repository

import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class RoutesRepository(val project: com.intellij.openapi.project.Project) {
    private val routes = mutableListOf<Route>()

    fun getRoutes(): Collection<Route> = routes

    fun updateRoutes(newRoutes: Collection<Route>) {
        routes.clear()
        routes.addAll(newRoutes)
    }
}