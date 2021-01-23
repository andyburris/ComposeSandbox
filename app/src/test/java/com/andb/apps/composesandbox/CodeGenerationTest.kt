package com.andb.apps.composesandbox

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.andb.apps.composesandbox.data.model.toTheme
import com.andb.apps.composesandboxdata.model.*
import org.junit.Assert.assertEquals
import org.junit.Test

class CodeGenerationTest {

    private val project = Project(name = "Test Project", trees = emptyList(), theme = lightColors().toTheme())
    private val generator = CodeGenerator(project)

    @Test
    fun textComponentTest() {
        assertEquals(with(generator) { textComponent.toCode().trimEndWhitespace() }, textComponentCode)
        assertEquals(with(generator) { textComponentWithModifiers.toCode().trimEndWhitespace() }, textComponentWithModifiersCode)
    }

    @Test
    fun iconComponentTest() {
        assertEquals(with(generator) { iconComponent.toCode().trimEndWhitespace() }, iconComponentCode)
        assertEquals(with(generator) { iconComponentWithModifiers.toCode().trimEndWhitespace() }, iconComponentWithModifiersCode)
    }

    @Test
    fun rowComponentTest() {
        assertEquals(with(generator) { emptyRowComponent.toCode().trimEndWhitespace() }, emptyRowComponentCode)
        assertEquals(with(generator) { rowComponentWithChildren.toCode().trimEndWhitespace() }, rowComponentWithChildrenCode)
    }
}

/************** Text **************/
private val textComponent = PrototypeComponent.Text(properties = Properties.Text("Test"))
private val textComponentCode = """
    Text(
        text = "Test",
        color = MaterialTheme.colors.onBackground
    )
""".trimIndent().trimEndWhitespace()

private val textComponentWithModifiers = PrototypeComponent.Text(properties = Properties.Text("Test"), modifiers = listOf(PrototypeModifier.Border(4, PrototypeColor.ThemeColor.Primary, 0), PrototypeModifier.Padding.All(16)))
private val textComponentWithModifiersCode = """
    Text(
        text = "Test",
        color = MaterialTheme.colors.onBackground,
        modifier = Modifier.border(width = 4.dp, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(0.dp)).padding(all = 16.dp)
    )
""".trimIndent().trimEndWhitespace()


/************** Icon **************/
private val iconComponent = PrototypeComponent.Icon(properties = Properties.Icon(PrototypeIcon.Add))
private val iconComponentCode = """
    Icon(
        imageVector = Icons.Default.Add,
        tint = MaterialTheme.colors.onBackground
    )
""".trimIndent().trimEndWhitespace()

private val iconComponentWithModifiers = PrototypeComponent.Icon(properties = Properties.Icon(PrototypeIcon.Add), modifiers = listOf(PrototypeModifier.Padding.Sides(horizontal = 16, vertical = 8)))
private val iconComponentWithModifiersCode = """
    Icon(
        imageVector = Icons.Default.Add,
        tint = MaterialTheme.colors.onBackground,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
""".trimIndent().trimEndWhitespace()

/************** Row **************/
private val emptyRowComponent = PrototypeComponent.Group.Row(properties = Properties.Group.Row())
private val emptyRowComponentCode = """
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {}
""".trimIndent().trimEndWhitespace()

private val rowComponentWithChildren = PrototypeComponent.Group.Row(properties = Properties.Group.Row(), children = listOf(textComponent))
private val rowComponentWithChildrenCode = """
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Test",
            color = MaterialTheme.colors.onBackground
        )
    }
""".trimIndent().trimEndWhitespace()

@Composable
private fun Test() {
    Text(
        text = "Test",
        color = MaterialTheme.colors.onBackground,
        modifier = Modifier.border(width = 4.dp, color = MaterialTheme.colors.primary, shape = RoundedCornerShape(0.dp)).padding(all = 16.dp)
    )

    Icon(
        imageVector = Icons.Default.Add,
        tint = MaterialTheme.colors.onBackground,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Test",
            color = MaterialTheme.colors.onBackground
        )
    }

    Column(

    ) {

    }
}

private fun String.trimEndWhitespace(): String {
    return lines().joinToString("\n") { it.trimEnd() }
}