package com.andb.apps.composesandbox.ui.common

import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import com.andb.apps.composesandbox.data.model.Component

@Composable
fun RenderComponent(component: Component){
    when(component){
        is Component.Text -> Text(text = component.text)
        is Component.Icon -> Icon(asset = component.icon)
        is Component.Column -> Column {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
        is Component.Row -> Row {
            for (child in component.children) {
                RenderComponent(component = child)
            }
        }
    }
}