package com.andb.apps.composesandbox.data.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.InternalLayoutApi
import androidx.compose.ui.Alignment
import com.andb.apps.composesandbox.model.PrototypeAlignment
import com.andb.apps.composesandbox.model.PrototypeArrangement

/***** Arrangements *****/

fun PrototypeArrangement.toReadableString() = when (this) {
    PrototypeArrangement.Horizontal.Start -> "Start"
    PrototypeArrangement.Horizontal.Center -> "Center"
    PrototypeArrangement.Horizontal.End -> "End"
    PrototypeArrangement.Vertical.Top -> "Top"
    PrototypeArrangement.Vertical.Center -> "Center"
    PrototypeArrangement.Vertical.Bottom -> "Bottom"
    PrototypeArrangement.Both.SpaceBetween -> "Space Between"
    PrototypeArrangement.Both.SpaceAround -> "Space Around"
    PrototypeArrangement.Both.SpaceEvenly -> "Space Evenly"
}


@OptIn(InternalLayoutApi::class)
fun PrototypeArrangement.toHorizontalArrangement(): Arrangement.Horizontal = when(this) {
    PrototypeArrangement.Horizontal.Start -> Arrangement.Start
    PrototypeArrangement.Horizontal.Center -> Arrangement.Center
    PrototypeArrangement.Horizontal.End -> Arrangement.End
    PrototypeArrangement.Both.SpaceBetween -> Arrangement.SpaceBetween
    PrototypeArrangement.Both.SpaceAround -> Arrangement.SpaceAround
    PrototypeArrangement.Both.SpaceEvenly -> Arrangement.SpaceEvenly
    else -> throw Error("Not a horizontal arrangement!")
}

@OptIn(InternalLayoutApi::class)
fun PrototypeArrangement.toVerticalArrangement(): Arrangement.Vertical = when(this) {
    PrototypeArrangement.Vertical.Top -> Arrangement.Top
    PrototypeArrangement.Vertical.Center -> Arrangement.Center
    PrototypeArrangement.Vertical.Bottom -> Arrangement.Bottom
    PrototypeArrangement.Both.SpaceBetween -> Arrangement.SpaceBetween
    PrototypeArrangement.Both.SpaceAround -> Arrangement.SpaceAround
    PrototypeArrangement.Both.SpaceEvenly -> Arrangement.SpaceEvenly
    else -> throw Error("Not a vertical arrangement!")
}


/***** Alignments *****/

fun PrototypeAlignment.Vertical.toReadableString() = when (this){
    PrototypeAlignment.Vertical.Top -> "Top"
    PrototypeAlignment.Vertical.CenterVertically -> "Center Vertically"
    PrototypeAlignment.Vertical.Bottom -> "Bottom"
    else -> ""
}

fun PrototypeAlignment.Horizontal.toReadableString() = when (this) {
    PrototypeAlignment.Horizontal.Start -> "Start"
    PrototypeAlignment.Horizontal.CenterHorizontally -> "Center Horizontally"
    PrototypeAlignment.Horizontal.End -> "End"
    else -> ""
}


fun PrototypeAlignment.Horizontal.toAlignment() = when (this) {
    PrototypeAlignment.Horizontal.Start -> Alignment.Start
    PrototypeAlignment.Horizontal.CenterHorizontally -> Alignment.CenterHorizontally
    PrototypeAlignment.Horizontal.End -> Alignment.End
}

fun PrototypeAlignment.Vertical.toAlignment() = when (this) {
    PrototypeAlignment.Vertical.Top -> Alignment.Top
    PrototypeAlignment.Vertical.CenterVertically -> Alignment.CenterVertically
    PrototypeAlignment.Vertical.Bottom -> Alignment.Bottom
}