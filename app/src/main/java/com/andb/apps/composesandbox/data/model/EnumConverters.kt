package com.andb.apps.composesandbox.data.model

import androidx.compose.material.FabPosition
import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.model.Properties

@Composable
fun Properties.Slotted.Scaffold.FabPosition.toFabPosition() = when(this) {
    Properties.Slotted.Scaffold.FabPosition.Center -> FabPosition.Center
    Properties.Slotted.Scaffold.FabPosition.End -> FabPosition.End
}