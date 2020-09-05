package com.andb.apps.composesandbox.data.model

import androidx.ui.material.ColorPalette
import androidx.ui.material.lightColorPalette

data class Project(
    val name: String,
    val screens: List<Component> = listOf(
        Component.Column(
            children = listOf(
                Component.Text("Hello"),
                Component.Text("World")
            )
        )
    ),
    val components: List<Component> = listOf(),
    val theme: Theme = Theme(lightColorPalette()),
)