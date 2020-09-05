package com.andb.apps.composesandbox.data.model

import androidx.ui.core.Modifier
import androidx.ui.graphics.vector.VectorAsset

sealed class Component () {
    data class Text(val text: String) : Component()
    data class Icon(val icon: VectorAsset) : Component()
    data class Column(val children: List<Component>) : Component()
    data class Row(val children: List<Component>) : Component()
}