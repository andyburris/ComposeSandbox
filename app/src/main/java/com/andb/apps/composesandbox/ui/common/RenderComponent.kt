package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andb.apps.composesandbox.data.model.*
import com.andb.apps.composesandbox.model.PrototypeComponent

/**
 * Composable that renders a prototype component. Can be used recursively to render nested prototype components
 * @param component prototype component to be rendered
 */
@Composable
fun RenderComponent(component: PrototypeComponent){
    when (component){
        is PrototypeComponent.Text -> Text(text = component.properties.text, fontWeight = component.properties.weight.toFontWeight(), fontSize = component.properties.size.sp,color = component.properties.color.renderColor(), modifier = component.modifiers.toModifier())
        is PrototypeComponent.Icon -> Icon(asset = component.properties.icon.vectorAsset, tint = component.properties.tint.renderColor(), modifier = component.modifiers.toModifier())
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
            icon = {
                RenderComponent(component = component.slots[0].tree)
            },
            text = {
                RenderComponent(component = component.slots[1].tree)
            },
            modifier = component.modifiers.toModifier(),
            onClick = {}
        )
        is PrototypeComponent.Slotted.TopAppBar -> TopAppBar(
            modifier = component.modifiers.toModifier(),
            backgroundColor = component.properties.backgroundColor.renderColor(),
            contentColor = component.properties.contentColor.renderColor(),
            elevation = component.properties.elevation.dp,
            navigationIcon = {
                component.slots[0].tree.children.forEach { RenderComponent(component = it) }
             },
            title = {
                component.slots[1].tree.children.forEach { RenderComponent(component = it) }
            },
            actions = {
                component.slots[2].tree.children.forEach { RenderComponent(component = it) }
            }
        )
        is PrototypeComponent.Slotted.BottomAppBar -> BottomAppBar(
            modifier = component.modifiers.toModifier(),
            backgroundColor = component.properties.backgroundColor.renderColor(),
            contentColor = component.properties.contentColor.renderColor(),
            elevation = component.properties.elevation.dp,
            content = {
                component.slots[0].tree.children.forEach { RenderComponent(component = it) }
            }
        )
    }
}