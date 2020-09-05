package com.andb.apps.composesandbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.ui.core.DensityAmbient
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.tooling.preview.Preview
import com.andb.apps.composesandbox.state.*
import com.andb.apps.composesandbox.ui.projects.ProjectsScreen
import com.andb.apps.composesandbox.ui.sandbox.SandboxScreen
import com.andb.apps.composesandbox.ui.theme.AppTheme
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val screens = viewModel.state.screens.collectAsState()
            val handler: ActionHandler = { viewModel.state += it }
            ActionHandlerProvider(actionHandler = handler) {
                AppTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        when(val screen = screens.value.last()){
                            is Screen.Projects -> ProjectsScreen(screen.projects)
                            is Screen.Sandbox -> SandboxScreen(screen.project)
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.state.screens.value.size > 1){
            viewModel.state += UserAction.Back
        } else {
            super.onBackPressed()
        }
    }
}