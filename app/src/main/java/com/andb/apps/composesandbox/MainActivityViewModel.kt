package com.andb.apps.composesandbox

import androidx.lifecycle.ViewModel
import com.andb.apps.composesandbox.state.Action
import com.andb.apps.composesandbox.state.Machine

class MainActivityViewModel : ViewModel() {
    val state = Machine()
    fun handleAction(action: Action) = state::handleAction
}