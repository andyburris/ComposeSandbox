package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.emptyContent
import androidx.compose.runtime.staticAmbientOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andb.apps.composesandbox.data.model.*
import com.andb.apps.composesandboxdata.model.*

private val AmbientSelected = staticAmbientOf<String?>()
private val selectionColor = Color(0xFF56CCF2)

@Composable
fun RenderComponentParent(theme: Theme, component: PrototypeComponent, selected: String? = null) {
    MaterialTheme(colors = theme.toColors(), typography = Typography(), shapes = Shapes()) {
        Providers(AmbientSelected provides selected) {
            Box(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
                RenderComponent(component = component)
            }
        }
    }
}

/**
 * Composable that renders a prototype component. Can be used recursively to render nested prototype components
 * @param component prototype component to be rendered
 */
@Composable
fun RenderComponent(component: PrototypeComponent){
    val selected = AmbientSelected.current == component.id
    val selectedModifier = if (selected) Modifier.border(1.dp, selectionColor) else Modifier
    val modifier = selectedModifier then component.modifiers.toModifier()
    when (component){
        is PrototypeComponent.Text -> Text(
            text = component.text,
            fontWeight = component.weight.toFontWeight(),
            fontSize = component.size.sp,
            color = component.color.renderColor(),
            modifier = modifier
        )
        is PrototypeComponent.Icon -> Icon(
            imageVector = component.icon.imageVector,
            contentDescription = null,
            tint = component.tint.renderColor(),
            modifier = modifier
        )
        is PrototypeComponent.Group.Column -> Column(
            modifier = modifier,
            horizontalAlignment = component.horizontalAlignment.toAlignment(),
            verticalArrangement = component.verticalArrangement.toVerticalArrangement()
        ) {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
        is PrototypeComponent.Group.Row -> Row(
            modifier = modifier,
            verticalAlignment = component.verticalAlignment.toAlignment(),
            horizontalArrangement = component.horizontalArrangement.toHorizontalArrangement()
        ) {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
        is PrototypeComponent.Group.Box -> Box(modifier = modifier) {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
        is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> ExtendedFloatingActionButton(
            backgroundColor = component.backgroundColor.renderColor(),
            contentColor = component.contentColor.renderColor(),
            elevation = FloatingActionButtonDefaults.elevation(component.defaultElevation.dp, component.pressedElevation.dp),
            icon = renderEnabledSlotOrNull (component.slots.icon),
            text = renderSlot(component.slots.text),
            modifier = modifier,
            onClick = {}
        )
        is PrototypeComponent.Slotted.TopAppBar -> TopAppBar(
            modifier = modifier,
            backgroundColor = component.backgroundColor.renderColor(),
            contentColor = component.contentColor.renderColor(),
            elevation = component.elevation.dp,
            navigationIcon = renderEnabledSlotOrNull(component.slots.navigationIcon),
            title = renderSlot(component.slots.title),
            actions = renderScopedSlot(component.slots.actions)
        )
        is PrototypeComponent.Slotted.BottomAppBar -> BottomAppBar(
            modifier = modifier,
            backgroundColor = component.backgroundColor.renderColor(),
            contentColor = component.contentColor.renderColor(),
            elevation = component.elevation.dp,
            content = renderScopedSlot(component.slots.content)
        )
        is PrototypeComponent.Slotted.Scaffold -> Scaffold(
            modifier = modifier,
            topBar = renderSlot(component.slots.topBar),
            bottomBar = renderSlot(component.slots.bottomBar),
            drawerContent = renderEnabledScopedSlotOrNull(component.slots.drawer),
            drawerBackgroundColor = component.drawerBackgroundColor.renderColor(),
            drawerContentColor = component.drawerContentColor.renderColor(),
            drawerElevation = component.drawerElevation.dp,
            floatingActionButton = renderSlot(component.slots.floatingActionButton),
            floatingActionButtonPosition = component.floatingActionButtonPosition.toFabPosition(),
            isFloatingActionButtonDocked = component.isFloatingActionButtonDocked,
            bodyContent = renderScopedSlot(component.slots.bodyContent),
            backgroundColor = component.backgroundColor.renderColor(),
            contentColor = component.contentColor.renderColor(),
        )
        is PrototypeComponent.Custom -> {
            val treeComponent = AmbientProject.current.trees.filter { it.treeType == TreeType.Component }.first { it.id == component.treeID }.component
            val selectedModifier = if (selected) listOf(PrototypeModifier.Border(1, PrototypeColor.FixedColor(selectionColor.toArgb()), 0)) else emptyList()
            RenderComponent(component = treeComponent.copy(modifiers = selectedModifier + component.modifiers + treeComponent.modifiers)) //instance modifiers wrap the component's modifiers
        }
    }
}

@Composable
private fun renderSlot(slot: Slot): @Composable () -> Unit {
    if (slot.enabled) {
        return { slot.group.children.forEach { RenderComponent(component = it) } }
    } else {
        return emptyContent()
    }
}

@Composable
private fun <T> renderScopedSlot(slot: Slot): @Composable T.() -> Unit {
    if (slot.enabled) {
        return { slot.group.children.forEach { RenderComponent(component = it) } }
    } else {
        return { emptyContent() }
    }
}

@Composable
private fun renderEnabledSlotOrNull(slot: Slot): (@Composable () -> Unit)? {
    if (slot.enabled) {
        return { slot.group.children.forEach { RenderComponent(component = it) } }
    } else {
        return null
    }
}

@Composable
private fun <T> renderEnabledScopedSlotOrNull(slot: Slot): (@Composable T.() -> Unit)? {
    if (slot.enabled) {
        return { slot.group.children.forEach { RenderComponent(component = it) } }
    } else {
        return null
    }
}