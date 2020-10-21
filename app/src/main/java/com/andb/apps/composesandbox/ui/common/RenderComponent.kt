package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.data.model.Properties
import com.andb.apps.composesandbox.data.model.PrototypeComponent
import com.andb.apps.composesandbox.data.model.toModifier

/**
 * Composable that renders a prototype component. Can be used recursively to render nested prototype components
 * @param component prototype component to be rendered
 */
@Composable
fun RenderComponent(component: PrototypeComponent){
    when (component.properties){
        is Properties.Text -> Text(text = component.properties.text, modifier = component.modifiers.toModifier())
        is Properties.Icon -> Icon(asset = component.properties.icon, modifier = component.modifiers.toModifier())
        is Properties.Group.Column -> Column(modifier = component.modifiers.toModifier(), horizontalGravity = component.properties.horizontalAlignment, verticalArrangement = component.properties.verticalArrangement) {
            for (child in component.properties.children) {
                RenderComponent(component = child)
            }
        }
        is Properties.Group.Row -> Row(modifier = component.modifiers.toModifier(), verticalGravity = component.properties.verticalAlignment, horizontalArrangement = component.properties.horizontalArrangement) {
            for (child in component.properties.children) {
                RenderComponent(component = child)
            }
        }
    }
}