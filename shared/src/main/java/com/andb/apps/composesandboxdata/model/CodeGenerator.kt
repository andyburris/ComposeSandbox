package com.andb.apps.composesandboxdata.model

import com.andb.apps.composesandboxdata.toCamelCase
import com.andb.apps.composesandboxdata.toPascalCase

class CodeGenerator(val project: Project) {
    fun PrototypeTree.toCode() = """
        |@Composable
        |fun ${name.toPascalCase()}() {
        |${component.toCode().prependIndent("    ")}
        |}
    """.trimMargin()

    fun PrototypeComponent.toCode(): String {
        val functionName = name(project).toPascalCase()
        val properties = properties.toCode()
        val modifiers = modifiers.toCode()
        val slots = when (this) {
            is PrototypeComponent.Slotted -> this.slotsToCode()
            else -> ""
        }
        val parameters = listOf(properties, modifiers, slots).filter { it.isNotBlank() }.joinToString(", \n")
        val body = when (this) {
            is PrototypeComponent.Group -> {
                val children = this.childrenToCode()
                when {
                    children.isNotEmpty() -> "{\n" + children.prependIndent("    ") + "\n}"
                    else -> "{}"
                }

            }
            else -> ""
        }
        val parenthesis = when {
            parameters.isNotEmpty() -> "(\n" + parameters.prependIndent("    ") + "\n)"
            body.isNotEmpty() -> " "
            else -> "() "
        }
        return (functionName + parenthesis + " " + body)
    }


    fun Properties.toCode(): String = when (this) {
        is Properties.Text -> """
        |text = "$text", 
        |color = ${color.toCode()}
    """.trimMargin()
        is Properties.Icon -> """
        |imageVector = Icons.Default.${icon.name}, 
        |tint = ${tint.toCode()}
    """.trimMargin()
        is Properties.Group.Row -> """
        |horizontalArrangement = ${horizontalArrangement.toCodeString()}, 
        |verticalAlignment = ${verticalAlignment.toCodeString()}
    """.trimMargin()
        is Properties.Group.Column -> """
        |verticalArrangement = ${verticalArrangement.toCodeString()},
        |horizontalAlignment = ${horizontalAlignment.toCodeString()}
    """.trimMargin()
        is Properties.Group.Box -> ""
        is Properties.Slotted.ExtendedFloatingActionButton -> """
        |backgroundColor = ${backgroundColor.toCode()}, 
        |defaultElevation = ${defaultElevation}.dp, 
        |pressedElevation = $pressedElevation.dp
    """.trimMargin()
        is Properties.Slotted.TopAppBar -> """
        |backgroundColor = ${backgroundColor.toCode()}, 
        |elevation = $elevation.dp
    """.trimMargin()
        is Properties.Slotted.BottomAppBar -> """
        |backgroundColor = ${backgroundColor.toCode()}, 
        |elevation = $elevation.dp
    """.trimMargin()
        is Properties.Slotted.Scaffold -> """
        |backgroundColor = ${backgroundColor.toCode()}, 
        |contentColor = ${contentColor.toCode()}, 
        |drawerBackgroundColor = ${drawerBackgroundColor.toCode()}, 
        |drawerContentColor = ${drawerContentColor.toCode()}, 
        |drawerElevation = $drawerElevation.dp, 
        |floatingActionButtonPosition = ${floatingActionButtonPosition.toCode()}, 
        |isFloatingActionButtonDocked = $isFloatingActionButtonDocked
    """.trimMargin()
        Properties.Blank -> ""
    }

    private fun Any?.toCodeString(): String = when (this) {
        is String -> "\"$this\""
        is PrototypeIcon -> "Icons.Default.${this.name}"
        is PrototypeColor -> this.toCode()
        is PrototypeAlignment -> this.toCode()
        is PrototypeArrangement -> this.toCode()
        else -> this.toString()
    }

    private fun PrototypeComponent.Group.childrenToCode() = children.joinToString("\n") { it.toCode() }

    private fun PrototypeComponent.Slotted.slotsToCode() = slots.filter { !it.optional || properties.slotsEnabled[it.name] == true }.joinToString(", \n") {
        val function = if (it.group.children.isEmpty()) "{}" else "{\n" + it.group.childrenToCode().prependIndent("    ") + "\n}"
        it.name.toCamelCase() + " = " + function
    }

    fun List<PrototypeModifier>.toCode(): String {
        if (isEmpty()) return ""

        return buildString {
            append("modifier = Modifier.")
            append(this@toCode.joinToString(".") { it.toCode() })
        }
    }

    fun PrototypeModifier.toCode() = when (this) {
        is PrototypeModifier.Border -> "border(width = $strokeWidth.dp, color = ${color.toCode()}, shape = RoundedCornerShape($cornerRadius.dp))"
        is PrototypeModifier.Background -> "background(color = ${color.toCode()}, shape = RoundedCornerShape($cornerRadius.dp))"
        is PrototypeModifier.Padding.Individual -> "padding(start = $start.dp, end = $end.dp, top = $top.dp, bottom = $bottom.dp)"
        is PrototypeModifier.Padding.Sides -> "padding(horizontal = $horizontal.dp, vertical = $vertical.dp)"
        is PrototypeModifier.Padding.All -> "padding(all = $padding.dp)"
        is PrototypeModifier.Height -> "height(height = $height.dp)"
        is PrototypeModifier.Width -> "width(width = $width.dp)"
        is PrototypeModifier.Size.All -> "size(size = $size.dp)"
        is PrototypeModifier.Size.Individual -> "size(width = $width.dp, height = $height.dp)"
        is PrototypeModifier.FillMaxWidth -> "fillMaxWidth()"
        is PrototypeModifier.FillMaxHeight -> "fillMaxHeight()"
        is PrototypeModifier.FillMaxSize -> "fillMaxSize()"
    }
}