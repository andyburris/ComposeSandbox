package com.andb.apps.composesandbox.ui.addproject

import androidx.compose.material.lightColors
import com.andb.apps.composesandbox.data.model.toTheme
import com.andb.apps.composesandboxdata.model.*

private val empty = newProject(
    name = "Empty",
    trees = listOf(
        PrototypeTree(name = "Screen 1", treeType = TreeType.Screen)
    ),
    theme = lightColors().toTheme(),
)

private val helloWorld = newProject(
    name = "Hello World",
    trees = listOf(
        PrototypeTree(
            name = "Screen 1",
            component = PrototypeComponent.Group.Column(
                horizontalAlignment = PrototypeAlignment.Horizontal.CenterHorizontally,
                verticalArrangement = PrototypeArrangement.Vertical.Center,
                modifiers = listOf(PrototypeModifier.FillMaxSize()),
                children = listOf(
                    PrototypeComponent.Text(
                        text = "Hello World"
                    )
                )
            ),
            treeType = TreeType.Screen
        )
    ),
    theme = lightColors().toTheme()
)

private val scaffoldDemo = newProject(
    name = "Scaffold Demo",
    trees = listOf(
        PrototypeTree(
            name = "Screen 1",
            treeType = TreeType.Screen,
            component = PrototypeComponent.Slotted.Scaffold(
                modifiers = listOf(PrototypeModifier.FillMaxSize()),
                slots = Slots.Scaffold(
                    topBar = Slot(
                        "Top Bar",
                        enabled = true,
                        group = PrototypeComponent.Group.Box(
                            children = listOf(
                                PrototypeComponent.Slotted.TopAppBar(
                                    slots = Slots.TopAppBar(
                                        navigationIcon = Slot(
                                            "Navigation Icon",
                                            enabled = true,
                                            group = PrototypeComponent.Group.Box(
                                                children = listOf(
                                                    PrototypeComponent.Icon(icon = PrototypeIcon.Menu, tint = PrototypeColor.ThemeColor.OnPrimary, modifiers = listOf(PrototypeModifier.Padding.All(12)))
                                                )
                                            )
                                        ),
                                        title = Slot(
                                            "Title",
                                            optional = false,
                                            enabled = true,
                                            group = PrototypeComponent.Group.Box(
                                                children = listOf(
                                                    PrototypeComponent.Text("App Name", weight = PrototypeComponent.Text.Weight.Medium, size = 20, color = PrototypeColor.ThemeColor.OnPrimary)
                                                )
                                            )
                                        ),
                                    )
                                )
                            )
                        )
                    ),
                    floatingActionButton = Slot(
                        "Floating Action Button",
                        enabled = true,
                        group = PrototypeComponent.Group.Box(
                            children = listOf(
                                PrototypeComponent.Slotted.ExtendedFloatingActionButton(
                                    slots = Slots.ExtendedFloatingActionButton(
                                        icon = Slot(
                                            "Icon",
                                            enabled = true,
                                            group = PrototypeComponent.Group.Box(
                                                children = listOf(
                                                    PrototypeComponent.Icon(icon = PrototypeIcon.Add, tint = PrototypeColor.ThemeColor.OnSecondary)
                                                )
                                            )
                                        ),
                                        text = Slot(
                                            "Text",
                                            optional = false,
                                            enabled = true,
                                            group = PrototypeComponent.Group.Box(
                                                children = listOf(
                                                    PrototypeComponent.Text("EXTENDED FAB", weight = PrototypeComponent.Text.Weight.Medium, color = PrototypeColor.ThemeColor.OnSecondary)
                                                )
                                            )
                                        ),
                                    )
                                )
                            )
                        )
                    ),
                    content = Slot(
                        "Content",
                        optional = false,
                        enabled = true,
                        group = PrototypeComponent.Group.Box(
                            children = listOf(
                                PrototypeComponent.Group.Column(
                                    modifiers = listOf(PrototypeModifier.FillMaxSize()),
                                    verticalArrangement = PrototypeArrangement.Vertical.Center,
                                    horizontalAlignment = PrototypeAlignment.Horizontal.CenterHorizontally,
                                    children = listOf(
                                        PrototypeComponent.Text("Hello World")
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )
)

val templates = listOf(empty, helloWorld, scaffoldDemo)
