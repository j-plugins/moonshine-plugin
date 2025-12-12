package com.github.xepozz.moonshine.repository

import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class RoutesRepository(val project: com.intellij.openapi.project.Project) {
    private val routes = mutableListOf<Route>()

    fun getRoutes(): Collection<Route> = routes
    fun getByName(name: String): Route? = routes.find { it.name == name }
    fun getByMoonshineName(name: String): Route? = getByName("moonshine.$name")

    fun updateRoutes(newRoutes: Collection<Route>) {
        routes.clear()
        routes.addAll(newRoutes)
    }
}