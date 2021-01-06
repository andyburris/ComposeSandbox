package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.emptyContent
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andb.apps.composesandbox.data.model.*
import com.andb.apps.composesandbox.data.model.toFabPosition
import com.andb.apps.composesandboxdata.model.PrototypeComponent
import com.andb.apps.composesandboxdata.model.Theme
import com.andb.apps.composesandboxdata.model.TreeType

@Composable
fun RenderComponentParent(theme: Theme, component: PrototypeComponent) {
    MaterialTheme(colors = theme.toColors(), typography = Typography(), shapes = Shapes()) {
        Box(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
            RenderComponent(component = component)
        }
    }
}

/**
 * Composable that renders a prototype component. Can be used recursively to render nested prototype components
 * @param component prototype component to be rendered
 */
@Composable
fun RenderComponent(component: PrototypeComponent){
    when (component){
        is PrototypeComponent.Text -> Text(text = component.properties.text, fontWeight = component.properties.weight.toFontWeight(), fontSize = component.properties.size.sp,color = component.properties.color.renderColor(), modifier = component.modifiers.toModifier())
        is PrototypeComponent.Icon -> Icon(imageVector = component.properties.icon.imageVector, tint = component.properties.tint.renderColor(), modifier = component.modifiers.toModifier())
        is PrototypeComponent.Group.Column -> Column(modifier = component.modifiers.toModifier(), horizontalAlignment = component.properties.horizontalAlignment.toAlignment(), verticalArrangement = component.properties.verticalArrangement.toVerticalArrangement()) {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
        is PrototypeComponent.Group.Row -> Row(modifier = component.modifiers.toModifier(), verticalAlignment = component.properties.verticalAlignment.toAlignment(), horizontalArrangement = component.properties.horizontalArrangement.toHorizontalArrangement()) {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
        is PrototypeComponent.Group.Box -> Box(modifier = component.modifiers.toModifier()) {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
        is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> ExtendedFloatingActionButton(
            icon = component.renderEnabledSlotOrNull(name = "Icon"),
            text = component.renderSlot(name = "Text"),
            modifier = component.modifiers.toModifier(),
            onClick = {}
        )
        is PrototypeComponent.Slotted.TopAppBar -> TopAppBar(
            modifier = component.modifiers.toModifier(),
            backgroundColor = component.properties.backgroundColor.renderColor(),
            contentColor = component.properties.contentColor.renderColor(),
            elevation = component.properties.elevation.dp,
            navigationIcon = component.renderEnabledSlotOrNull(name = "Navigation Icon"),
            title = component.renderSlot(name = "Title"),
            actions = component.renderScopedSlot(name = "Actions")
        )
        is PrototypeComponent.Slotted.BottomAppBar -> BottomAppBar(
            modifier = component.modifiers.toModifier(),
            backgroundColor = component.properties.backgroundColor.renderColor(),
            contentColor = component.properties.contentColor.renderColor(),
            elevation = component.properties.elevation.dp,
            content = component.renderScopedSlot(name = "Content")
        )
        is PrototypeComponent.Slotted.Scaffold -> Scaffold(
            modifier = component.modifiers.toModifier(),
            topBar = component.renderSlot(name = "Top App Bar"),
            bottomBar = component.renderSlot(name = "Bottom App Bar"),
            drawerContent = component.renderEnabledScopedSlotOrNull(name = "Drawer"),
            drawerBackgroundColor = component.properties.drawerBackgroundColor.renderColor(),
            drawerContentColor = component.properties.drawerContentColor.renderColor(),
            drawerElevation = component.properties.drawerElevation.dp,
            floatingActionButton = component.renderSlot(name = "Floating Action Button"),
            floatingActionButtonPosition = component.properties.floatingActionButtonPosition.toFabPosition(),
            isFloatingActionButtonDocked = component.properties.isFloatingActionButtonDocked,
            bodyContent = component.renderScopedSlot(name = "Body Content"),
            backgroundColor = component.properties.backgroundColor.renderColor(),
            contentColor = component.properties.contentColor.renderColor(),
        )
        is PrototypeComponent.Custom -> {
            val treeComponent = AmbientProject.current.trees.filter { it.treeType == TreeType.Component }.first { it.id == component.treeID }.tree
            RenderComponent(component = treeComponent.copy(modifiers = component.modifiers + treeComponent.modifiers)) //instance modifiers wrap the component's modifiers
        }
    }
}

@Composable
private fun PrototypeComponent.Slotted.renderSlot(name: String): @Composable () -> Unit {
    val slot = remember(this) { this.slots.first { it.name == name } }
    if (slot.enabled) {
        return { slot.tree.children.forEach { RenderComponent(component = it) } }
    } else {
        return emptyContent()
    }
}

@Composable
private fun <T> PrototypeComponent.Slotted.renderScopedSlot(name: String): @Composable T.() -> Unit {
    val slot = remember(this) { this.slots.first { it.name == name } }
    if (slot.enabled) {
        return { slot.tree.children.forEach { RenderComponent(component = it) } }
    } else {
        return { emptyContent() }
    }
}

@Composable
private fun PrototypeComponent.Slotted.renderEnabledSlotOrNull(name: String): (@Composable () -> Unit)? {
    val slot = remember(this) { this.slots.first { it.name == name } }
    if (slot.enabled) {
        return { slot.tree.children.forEach { RenderComponent(component = it) } }
    } else {
        return null
    }
}

@Composable
private fun <T> PrototypeComponent.Slotted.renderEnabledScopedSlotOrNull(name: String): (@Composable T.() -> Unit)? {
    val slot = remember(this) { this.slots.first { it.name == name } }
    if (slot.enabled) {
        return { slot.tree.children.forEach { RenderComponent(component = it) } }
    } else {
        return null
    }
}