package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.data.model.Component
import com.andb.apps.composesandbox.data.model.toModifier

/**
 * Composable that renders a prototype component. Can be used recursively to render nested prototype components
 * @param component prototype component to be rendered
 */
@Composable
fun RenderComponent(component: Component){
    when(component){
        is Component.Text -> Text(text = component.text, modifier = component.modifiers.toModifier())
        is Component.Icon -> Icon(asset = component.icon, modifier = component.modifiers.toModifier())
        is Component.Group.Column -> Column(modifier = component.modifiers.toModifier(), horizontalGravity = component.horizontalAlignment, verticalArrangement = component.verticalArrangement) {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
        is Component.Group.Row -> Row(modifier = component.modifiers.toModifier(), verticalGravity = component.verticalAlignment, horizontalArrangement = component.horizontalArrangement) {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
    }
}