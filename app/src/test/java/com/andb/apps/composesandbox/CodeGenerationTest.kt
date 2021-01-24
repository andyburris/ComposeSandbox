package com.andb.apps.composesandbox

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
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
        assertEquals(textComponentCode, with(generator) { textComponent.toCode().trimEndWhitespace() })
        assertEquals(textComponentWithModifiersCode, with(generator) { textComponentWithModifiers.toCode().trimEndWhitespace() })
    }

    @Test
    fun iconComponentTest() {
        assertEquals(iconComponentCode, with(generator) { iconComponent.toCode().trimEndWhitespace() })
        assertEquals(iconComponentWithModifiersCode, with(generator) { iconComponentWithModifiers.toCode().trimEndWhitespace() })
    }

    @Test
    fun rowComponentTest() {
        assertEquals(emptyRowComponentCode, with(generator) { emptyRowComponent.toCode().trimEndWhitespace() })
        assertEquals(rowComponentWithChildrenCode, with(generator) { rowComponentWithChildren.toCode().trimEndWhitespace() })
    }

    @Test
    fun columnComponentTest() {
        assertEquals(spacedByColumnCode, with(generator) { spacedByColumn.toCode().trimEndWhitespace() })
    }

    @Test
    fun slottedTest() {
        assertEquals(emptySlottedCode, with(generator) { emptySlotted.toCode().trimEndWhitespace() })
        assertEquals(filledSlottedCode, with(generator) { filledSlotted.toCode().trimEndWhitespace() })
    }
}

/**Text*/
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


/**Icon*/
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


/**Row*/
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


/** Column*/
private val spacedByColumn = PrototypeComponent.Group.Column(properties = Properties.Group.Column(verticalArrangement = PrototypeArrangement.Vertical.SpacedBy(16, PrototypeAlignment.Vertical.Top)), children = listOf(textComponent))
private val spacedByColumnCode = """
    Column(
        verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Test",
            color = MaterialTheme.colors.onBackground
        )
    }
""".trimIndent().trimEndWhitespace()


/**Slotted*/
private val emptySlotted = PrototypeComponent.Slotted.Scaffold()
private val emptySlottedCode = """
    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        drawerBackgroundColor = MaterialTheme.colors.background,
        drawerContentColor = MaterialTheme.colors.onBackground,
        drawerElevation = 16.dp,
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = false,
        topBar = {},
        floatingActionButton = {},
        bodyContent = {}
    )
""".trimIndent()

private val filledSlotted = PrototypeComponent.Slotted.TopAppBar().let {
    it.withSlots(it.slots.mapIndexed { index, slot ->
        val children = when (index) {
            0 -> PrototypeComponent.Icon(properties = Properties.Icon(PrototypeIcon.Menu, tint = PrototypeColor.ThemeColor.OnPrimary), modifiers = listOf(PrototypeModifier.Padding.All(12)))
            1 -> PrototypeComponent.Text(properties = Properties.Text("Title", color = PrototypeColor.ThemeColor.OnPrimary))
            2 -> PrototypeComponent.Group.Row(children = listOf(PrototypeComponent.Icon(properties = Properties.Icon(PrototypeIcon.MoreVert, tint = PrototypeColor.ThemeColor.OnPrimary), modifiers = listOf(PrototypeModifier.Padding.All(12)))))
            else -> throw Error("too many slots")
        }
        slot.copy(group = slot.group.withChildren(listOf(children)))
    })
}
private val filledSlottedCode = """
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 4.dp,
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(all = 12.dp)
            )
        },
        title = {
            Text(
                text = "Title",
                color = MaterialTheme.colors.onPrimary
            )
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(all = 12.dp)
                )
            }
        }
    )
""".trimIndent()


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
        verticalArrangement = Arrangement.spacedBy(space = 16.dp, alignment = Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Test",
            color = MaterialTheme.colors.onBackground
        )
    }

    Scaffold(
        backgroundColor = MaterialTheme.colors.background,
        contentColor = MaterialTheme.colors.onBackground,
        drawerBackgroundColor = MaterialTheme.colors.background,
        drawerContentColor = MaterialTheme.colors.onBackground,
        drawerElevation = 16.dp,
        floatingActionButtonPosition = FabPosition.End,
        isFloatingActionButtonDocked = false,
        topBar = {},
        floatingActionButton = {},
        bodyContent = {}
    )

    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        elevation = 4.dp,
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Menu,
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(all = 12.dp)
            )
        },
        title = {
            Text(
                text = "Title",
                color = MaterialTheme.colors.onPrimary
            )
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(all = 12.dp)
                )
            }
        }
    )
}

private fun String.trimEndWhitespace(): String {
    return lines().joinToString("\n") { it.trimEnd() }
}