package com.andb.apps.composesandbox.util

import androidx.compose.runtime.MutableState

fun MutableState<Boolean>.toggle() { this.value = !this.value }