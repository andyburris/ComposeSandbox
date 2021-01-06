package com.andb.apps.composesandbox.data.model

import androidx.compose.runtime.Composable
import com.andb.apps.composesandbox.ui.common.AmbientProject
import com.andb.apps.composesandboxdata.model.PrototypeComponent

@Composable
val PrototypeComponent.name get() = this.name(AmbientProject.current)