package com.andb.apps.composesandbox.ui.util

import androidx.compose.foundation.background
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntSize

fun Modifier.genericDroppable(onMeasure: (positionInWindow: Offset, size: IntSize) -> Unit, onDispose: () -> Unit) = composed {
    //println("genericDroppable modifier")
    DisposableEffect(Unit) {
        onDispose(onDispose)
    }
    this.onGloballyPositioned {
        onMeasure(it.positionInWindow(),  it.size)
    }
}

fun Modifier.disposableEffectTest() = composed {
    println("disposableEffectModifier")
    val color = remember { mutableStateOf(Color.Red) }
    DisposableEffect(Unit) {
        println("effect called")
        color.value = Color.Blue
        onDispose {
            println("disposed")
        }
    }
    LaunchedEffect(Unit) {
        println("launched")
    }
    this.background(color.value)
}