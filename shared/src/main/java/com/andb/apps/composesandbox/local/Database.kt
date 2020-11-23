package com.andb.apps.composesandbox.local

import com.andb.apps.composesandbox.Database
import com.andb.apps.composesandbox.ProjectData
import com.andb.apps.composesandbox.model.Project
import com.andb.apps.composesandbox.model.PrototypeComponent
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

        val serializedScreens = json.encodeToString(ListSerializer(PrototypeComponent.serializer()), project.screens)
        val serializedComponents = json.encodeToString(ListSerializer(PrototypeComponent.serializer()), project.components)
        val serializedTheme = json.encodeToString(Theme.serializer(), project.theme)
        database.projectQueries.insert(project.id, project.name, serializedScreens, serializedComponents, serializedTheme)
    }

    private fun ProjectData.toProject(): Project {
        val screens = json.decodeFromString(ListSerializer(PrototypeComponent.serializer()), this.screens)
        val components = json.decodeFromString(ListSerializer(PrototypeComponent.serializer()), this.components)
        val theme = json.decodeFromString(Theme.serializer(), this.theme)
        return Project(id, name, screens, components, theme)
    }
}