package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.data.model.*
import com.andb.apps.composesandbox.model.Properties
import com.andb.apps.composesandbox.model.PrototypeComponent

/**
 * Composable that renders a prototype component. Can be used recursively to render nested prototype components
 * @param component prototype component to be rendered
 */
@Composable
fun RenderComponent(component: PrototypeComponent){
    when (val properties = component.properties){
        is Properties.Text -> Text(text = properties.text, modifier = component.modifiers.toModifier())
        is Properties.Icon -> Icon(asset = properties.icon.vectorAsset, tint = properties.tint.renderColor(), modifier = component.modifiers.toModifier())
        is Properties.Group.Column -> Column(modifier = component.modifiers.toModifier(), horizontalAlignment = properties.horizontalAlignment.toAlignment(), verticalArrangement = properties.verticalArrangement.toVerticalArrangement()) {
            for (child in properties.children) {
                RenderComponent(component = child)
            }
        }
        is Properties.Group.Row -> Row(modifier = component.modifiers.toModifier(), verticalAlignment = properties.verticalAlignment.toAlignment(), horizontalArrangement = properties.horizontalArrangement.toHorizontalArrangement()) {
            for (child in properties.children) {
                RenderComponent(component = child)
            }
        }
    }
}