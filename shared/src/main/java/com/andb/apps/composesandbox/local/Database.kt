package com.andb.apps.composesandbox.local

import com.andb.apps.composesandbox.Database
import com.andb.apps.composesandbox.ProjectData
import com.andb.apps.composesandbox.model.Project
import com.andb.apps.composesandbox.model.PrototypeTree
import com.andb.apps.composesandbox.model.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.koin.core.KoinComponent
import org.koin.core.get

object DatabaseHelper : KoinComponent {
    private val database: Database = get()
    private val json = Json.Default

    private val buffer = MutableStateFlow<List<Project>>(database.projectQueries.selectAll().executeAsList().map { it.toProject() })
    val allProjects: StateFlow<List<Project>> = buffer

    fun upsertProject(project: Project) {
        var updated = false
        buffer.value = buffer.value.map {
            when(it.id) {
                project.id -> { updated = true; project }
                else -> it
            }
        }
        if (!updated) {
            buffer.value += project
        }

        val serializedTrees = json.encodeToString(ListSerializer(PrototypeTree.serializer()), project.trees)
        val serializedTheme = json.encodeToString(Theme.serializer(), project.theme)
        database.projectQueries.insert(project.id, project.name, serializedTrees, serializedTheme)
    }

    fun deleteProject(project: Project) {
        buffer.value = buffer.value.filter { it.id != project.id }
        database.projectQueries.delete(project.id)
    }

    private fun ProjectData.toProject(): Project {
        val trees = json.decodeFromString(ListSerializer(PrototypeTree.serializer()), this.trees)
        val theme = json.decodeFromString(Theme.serializer(), this.theme)
        return Project(id, name, trees, theme)
    }
}