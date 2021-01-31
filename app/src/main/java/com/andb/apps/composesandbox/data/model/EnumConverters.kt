package com.andb.apps.composesandbox.data.model

import androidx.compose.material.FabPosition
import androidx.compose.runtime.Composable
import com.andb.apps.composesandboxdata.model.PrototypeComponent

@Composable
fun PrototypeComponent.Slotted.Scaffold.FabPosition.toFabPosition() = when(this) {
    PrototypeComponent.Slotted.Scaffold.FabPosition.Center -> FabPosition.Center
    PrototypeComponent.Slotted.Scaffold.FabPosition.End -> FabPosition.End
}