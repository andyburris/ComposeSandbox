import com.andb.apps.composesandboxdata.model.*
import com.andb.apps.composesandboxdata.state.ProjectAction
import org.junit.Assert.assertEquals
import org.junit.Test

private val baseProject = newProject(
    name = "Demo Project",
    theme = Theme(),
    trees = listOf(
        PrototypeTree(name = "Screen 1", treeType = TreeType.Screen, component = PrototypeComponent.Group.Column(id = "columnID", modifiers = listOf(PrototypeModifier.FillMaxSize())))
    )
)

class VersionHistoryTest {
    @Test
    fun testUndo() {
        val addingText = PrototypeComponent.Text("Test")
        val added = baseProject.apply(ProjectAction.TreeAction.AddComponent(addingText, baseProject.trees.first().component.findByIDInTree("columnID") as PrototypeComponent.Group, 0))
        println("added = $added")
        assertEquals(baseProject, added.undo().copy(futureHistory = emptyList()))
    }

    @Test
    fun testVersionHistory() {
        val addingText = PrototypeComponent.Text(id = "firstText", text = "Test")
        val applied = baseProject
            .also { println("----Adding Text Component-----") }
            .apply(ProjectAction.TreeAction.AddComponent(addingText, baseProject.trees.first().component.findByIDInTree("columnID") as PrototypeComponent.Group, 0))
            .also { println("----Extracting Text Component-----") }
            .apply(ProjectAction.ExtractComponent(addingText.toTree(baseProject), listOf(addingText)))
            .also { println("extractedText = ${it.stringify()}") }
            .also { println("----Adding Custom Component-----") }
            .let {
                it.apply(ProjectAction.TreeAction.AddComponent(PrototypeComponent.Custom(treeID = it.trees.last().id), it.trees.first().component.findByIDInTree("columnID") as PrototypeComponent.Group, 1))
            }
            .also { println("before deleting tree = ${it.stringify()}") }
            .also { println("----Deleting Component Tree-----") }
            .let {
                it.apply(ProjectAction.DeleteTree(it.trees.last()))
            }
            .also { println("after delete custom text, project = ${it.stringify()}") }
        val undone = applied
            .also { println("----Undoing Delete Component Tree-----") }
            .undo()
            .also { println("unDeletedTree = ${it.stringify()}") }
            .also { println("----Undoing Add Custom Component-----") }
            .undo()
            .also { println("before unextractedText = ${it.stringify()}") }
            .also { println("----Undoing Extract Text Component-----") }
            .undo()
            .also { println("unextractedText = ${it.stringify()}") }
            .also { println("----Undoing Add Text Component-----") }
            .undo()
        val redone = undone
            .also { println("----Redoing Add Text Component-----") }
            .redo()
            .also { println("----Redoing Extract Text Component-----") }
            .redo()
            .also { println("----Redoing Add Custom Component-----") }
            .redo()
            .also { println("----Redoing Delete Component Tree-----") }
            .redo()

        assertEquals(baseProject, undone.copy(futureHistory = emptyList()))
        assertEquals(undone, redone
            .also { println("----Undoing Delete Component Tree-----") }
            .undo()
            .also { println("unDeletedTree = ${it.stringify()}") }
            .also { println("----Undoing Add Custom Component-----") }
            .undo()
            .also { println("----Undoing Extract Text Component-----") }
            .undo()
            .also { println("----Undoing Add Text Component-----") }
            .undo()
        )
    }
}