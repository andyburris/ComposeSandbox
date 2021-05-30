import com.andb.apps.composesandboxdata.model.PrototypeComponent
import org.junit.Test

class TreeManipulationTest {
    @Test
    fun extractAndReplaceContinuityTest() {
        val baseComponent = PrototypeComponent.Group.Column(
            children = listOf(PrototypeComponent.Text("Test"))
        )
    }
}