package com.andb.apps.composesandboxdata.local

import com.andb.apps.composesandboxdata.Database
import com.andb.apps.composesandboxdata.ProjectData
import com.andb.apps.composesandboxdata.model.Project
import com.andb.apps.composesandboxdata.model.PrototypeTree
import com.andb.apps.composesandboxdata.model.Theme
import com.andb.apps.composesandboxdata.state.ProjectAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import org.koin.core.KoinComponent
import org.koin.core.get
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@Serializable
data class HistoryEntry(
    val id: String = UUID.randomUUID().toString(),
    @Serializable(with = LocalDateTimeSerializer::class) val time: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val action: ProjectAction,
    val residual: ProjectAction
)

@Serializer(LocalDateTime::class)
class LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.LONG)
    override fun serialize(encoder: Encoder, value: LocalDateTime) = encoder.encodeLong(value.toInstant(TimeZone.UTC).toEpochMilliseconds())
    override fun deserialize(decoder: Decoder): LocalDateTime = Instant.fromEpochMilliseconds(decoder.decodeLong()).toLocalDateTime(TimeZone.UTC)
}

/*data class DatabaseHelper(val database: Database, val json: Json = Json.Default, val coroutineScope: CoroutineScope) {

}*/

@OptIn(ExperimentalTime::class)
object DatabaseHelper : KoinComponent {
    private val database: Database = get()
    private val json = Json.Default

    private val buffer = MutableStateFlow<Map<String, Project>>(database.projectQueries.selectAll().executeAsList().associate { it.id to it.toProject() })

    init {
        CoroutineScope(Dispatchers.IO).launch {
            buffer.collect { projects ->
                val time = measureTime {
                    projects.forEach { (_, project) ->
                        if (project != database.projectQueries.selectById(project.id).executeAsOneOrNull()?.toProject()) {
                            val serializedTrees = json.encodeToString(ListSerializer(PrototypeTree.serializer()), project.trees)
                            val serializedTheme = json.encodeToString(Theme.serializer(), project.theme)
                            val pastHistory = json.encodeToString(ListSerializer(HistoryEntry.serializer()), project.pastHistory)
                            val futureHistory = json.encodeToString(ListSerializer(HistoryEntry.serializer()), project.futureHistory)
                            database.projectQueries.insert(project.id, project.name, serializedTrees, serializedTheme, pastHistory, futureHistory)
                        }
                    }
                }
                println("database save took $time")
            }
        }
    }
    val allProjects: Flow<List<Project>> = buffer.map { it.values.toList() }//.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Lazily, emptyList())

    fun upsertProject(project: Project) {
        buffer.value = buffer.value.plus(project.id to project)
    }

    fun deleteProject(project: Project) {
        buffer.value = buffer.value.filterKeys { it != project.id }
        database.projectQueries.delete(project.id)
    }

    private fun ProjectData.toProject(): Project {
        val trees = json.decodeFromString(ListSerializer(PrototypeTree.serializer()), this.trees)
        val theme = json.decodeFromString(Theme.serializer(), this.theme)
        val pastHistory = json.decodeFromString(ListSerializer(HistoryEntry.serializer()), this.pastHistory)
        val futureHistory = json.decodeFromString(ListSerializer(HistoryEntry.serializer()), this.futureHistory)
        return Project(id, name, trees, theme, pastHistory, futureHistory)
    }
}