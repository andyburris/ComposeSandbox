package com.andb.apps.composesandbox.ui.common

import android.app.Activity
import android.view.View
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import com.andb.apps.composesandbox.util.isDark

@Composable
fun Activity.StatusBar(color: Color) {
    this.window.statusBarColor = color.toArgb()
    this.window.decorView.systemUiVisibility = if ((color.compositeOver(MaterialTheme.colors.background)).isDark()) 0 else View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

@Composable
fun Activity.NavigationBar(color: Color) {
    this.window.navigationBarColor = color.toArgb()
}