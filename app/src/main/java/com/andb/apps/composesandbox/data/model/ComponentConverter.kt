package com.andb.apps.composesandbox.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Toll
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.andb.apps.composesandbox.R
import com.andb.apps.composesandbox.ui.common.LocalProject
import com.andb.apps.composesandboxdata.model.PrototypeComponent

val PrototypeComponent.name @Composable get() = this.name(LocalProject.current)


val PrototypeComponent.icon @Composable get() = when (this) {
    is PrototypeComponent.Text -> Icons.Default.TextFields
    is PrototypeComponent.Icon -> Icons.Default.Image
    is PrototypeComponent.Group.Column -> ImageVector.vectorResource(id = R.drawable.ic_column)
    is PrototypeComponent.Group.Row -> ImageVector.vectorResource(id = R.drawable.ic_row)
    is PrototypeComponent.Group.Box -> Icons.Default.Layers
    is PrototypeComponent.Slotted.ExtendedFloatingActionButton -> ImageVector.vectorResource(id = R.drawable.ic_extended_fab)
    is PrototypeComponent.Slotted.TopAppBar -> ImageVector.vectorResource(id = R.drawable.ic_top_app_bar)
    is PrototypeComponent.Slotted.BottomAppBar -> ImageVector.vectorResource(id = R.drawable.ic_bottom_app_bar)
    is PrototypeComponent.Slotted.Scaffold -> ImageVector.vectorResource(id = R.drawable.ic_scaffold)
    is PrototypeComponent.Custom -> Icons.Default.Toll
}