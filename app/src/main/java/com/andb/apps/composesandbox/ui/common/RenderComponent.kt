package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
    }
}