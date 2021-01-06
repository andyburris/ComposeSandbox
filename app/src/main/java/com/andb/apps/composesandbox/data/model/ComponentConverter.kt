package com.andb.apps.composesandbox.data.model

import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.model.PrototypeComponent
import com.andb.apps.composesandbox.ui.common.AmbientProject

@Composable
val PrototypeComponent.name get() = this.name(AmbientProject.current)