package com.andb.apps.composesandbox.data.model

import androidx.compose.material.lightColors


data class Project(
    val name: String,
    val screens: List<Component> = listOf(
        Component.Group.Column(
            children = listOf(
                Component.Group.Row(
                    children = listOf(
                        Component.Text("Hello"),
                        Component.Text("World")
                    )
                ),
                Component.Text("Hello"),
                Component.Text("World")
            )
        )
    ),
    val components: List<Component> = listOf(),
    val theme: Theme = Theme(lightColors()),
)