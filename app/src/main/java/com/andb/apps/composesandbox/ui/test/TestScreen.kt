package com.andb.apps.composesandbox.ui.test

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TestScreen() {
    Text(text = "Test")
}
