package com.andb.apps.composesandbox.ui.common

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.data.model.Component

/**
 * Composable that renders a prototype component. Can be used recursively to render nested prototype components
 * @param component prototype component to be rendered
 */
@Composable
fun RenderComponent(component: Component){
    when(component){
        is Component.Text -> Text(text = component.text)
        is Component.Icon -> Icon(asset = component.icon)
        is Component.Group.Column -> Column {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
        is Component.Group.Row -> Row {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
    }
}