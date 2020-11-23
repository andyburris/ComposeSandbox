package com.andb.apps.composesandbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andb.apps.composesandbox.state.Action
import com.andb.apps.composesandbox.state.Machine

class MainActivityViewModel : ViewModel() {
    val state = Machine(viewModelScope)
    fun handleAction(action: Action) = state::handleAction
}