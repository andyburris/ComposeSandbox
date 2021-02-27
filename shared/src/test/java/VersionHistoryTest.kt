import com.andb.apps.composesandboxdata.model.*
import com.andb.apps.composesandboxdata.state.ProjectAction
import org.junit.Assert.assertEquals
import org.junit.Test

private val baseProject = Project(
    name = "Demo Project",
    theme = Theme(),
    history = emptyList(),
    trees = listOf(
        PrototypeTree(name = "Screen 1", treeType = TreeType.Screen, component = PrototypeComponent.Group.Column(id = "columnID", modifiers = listOf(PrototypeModifier.FillMaxSize())))
    )
)
class VersionHistoryTest {
    @Test
    fun testUndo() {
        val addingText = PrototypeComponent.Text("Test")
        val added = baseProject.reduce(ProjectAction.TreeAction.AddComponent(addingText, baseProject.trees.first().component.findByIDInTree("columnID") as PrototypeComponent.Group, 0))
        println("added = $added")
        assertEquals(baseProject, added.undo())
    }

    @Test
    fun testVersionHistory() {
        val addingText = PrototypeComponent.Text(id = "firstText", text = "Test")
        val applied =
            baseProject
                .reduce(ProjectAction.TreeAction.AddComponent(addingText, baseProject.trees.first().component.findByIDInTree("columnID") as PrototypeComponent.Group, 0))
                .reduce(ProjectAction.ExtractComponent(addingText))
                .also { println("extractedText = $it") }
                .let {
                    it.reduce(ProjectAction.TreeAction.AddComponent(PrototypeComponent.Custom(treeID = it.id), it.trees.first().component.findByIDInTree("columnID") as PrototypeComponent.Group, 1))
                }
                .let {
                    it.reduce(ProjectAction.DeleteTree(it.trees.last()))
                }
                .also { println("after delete custom text, project = $it") }

        assertEquals(baseProject, applied.undo().undo().undo().undo())

    }
}