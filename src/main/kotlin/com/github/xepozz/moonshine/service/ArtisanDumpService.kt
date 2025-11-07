package com.github.xepozz.moonshine.service

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Service(Service.Level.PROJECT)
class ArtisanDumpService(var project: Project) {
    suspend fun dump(command: String, arguments: Collection<String>): String {
        return withContext(Dispatchers.IO) {
            val output = StringBuilder()

            PhpCommandExecutor.execute(
                project,
                project.basePath + "/artisan",
                phpCommandArguments = emptyList(),
                commandArguments = listOf(command) + arguments,
                StringBufferProcessAdapter(output)
            )

            output.toString()
        }
    }
}