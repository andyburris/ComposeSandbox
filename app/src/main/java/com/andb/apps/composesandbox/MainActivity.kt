package com.andb.apps.composesandbox

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.setContent
import com.andb.apps.composesandbox.data.model.renderColor
import com.andb.apps.composesandbox.state.ActionHandler
import com.andb.apps.composesandbox.state.ActionHandlerProvider
import com.andb.apps.composesandbox.state.UserAction
import com.andb.apps.composesandbox.state.ViewState
import com.andb.apps.composesandbox.ui.addproject.AddProjectScreen
import com.andb.apps.composesandbox.ui.code.CodeScreen
import com.andb.apps.composesandbox.ui.common.StatusBar
import com.andb.apps.composesandbox.ui.preview.PreviewScreen
import com.andb.apps.composesandbox.ui.projects.ProjectsScreen
import com.andb.apps.composesandbox.ui.sandbox.SandboxScreen
import com.andb.apps.composesandbox.ui.test.TestScreen
import com.andb.apps.composesandbox.ui.theme.AppTheme
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val screenStates = viewModel.state.stack.collectAsState()
            val state = screenStates.value.last()
            val handler: ActionHandler = { viewModel.state += it }
            ActionHandlerProvider(actionHandler = handler) {
                AppTheme {
                    StatusBar(state = state)
                    Surface(color = MaterialTheme.colors.background) {
                        when(state){
                            is ViewState.Projects -> ProjectsScreen(state.projects)
                            is ViewState.AddProject -> AddProjectScreen {
                                handler.invoke(UserAction.AddProject(it))
                            }
                            is ViewState.Sandbox -> SandboxScreen(state) {
                                handler.invoke(UserAction.UpdateProject(it))
                            }
                            is ViewState.Preview -> PreviewScreen(state.project, state.currentTree)
                            is ViewState.Code -> CodeScreen(state.project)
                            is ViewState.Test -> TestScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.state.stack.value.size > 1){
            viewModel.state += UserAction.Back
        } else {
            super.onBackPressed()
        }
    }

    @Composable
    private fun StatusBar(state: ViewState) {
        val color = when(state) {
            is ViewState.Projects -> MaterialTheme.colors.secondary
            ViewState.AddProject -> MaterialTheme.colors.secondary
            is ViewState.Sandbox -> MaterialTheme.colors.primaryVariant
            is ViewState.Preview -> state.project.theme.primaryVariant.renderColor()
            is ViewState.Code -> MaterialTheme.colors.primaryVariant
            ViewState.Test -> MaterialTheme.colors.secondary
        }
        StatusBar(color = color)
    }
}
