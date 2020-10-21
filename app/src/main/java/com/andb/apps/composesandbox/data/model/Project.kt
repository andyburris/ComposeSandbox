package com.andb.apps.composesandbox.data.model

import androidx.compose.material.lightColors


data class Project(
    val name: String,
    val screens: List<PrototypeComponent> = listOf(
        PrototypeComponent(
            properties = Properties.Group.Column(
                children = listOf(
                    PrototypeComponent(
                        properties = Properties.Group.Row(children = listOf(
                            PrototypeComponent(properties = Properties.Text("Hello")),
                            PrototypeComponent(properties = Properties.Text("Text")),
                        ))
                    ),
                    PrototypeComponent(properties = Properties.Text("Hello")),
                    PrototypeComponent(properties = Properties.Text("Text")),
                )
            )
        )
    ),
    val components: List<PrototypeComponent> = listOf(),
    val theme: Theme = Theme(lightColors()),
)