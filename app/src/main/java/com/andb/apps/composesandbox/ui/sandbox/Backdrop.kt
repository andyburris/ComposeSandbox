package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.animation.animate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.onPositioned
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.dp

/**
 * A backdrop appears behind all other surfaces in an app,
 * displaying contextual and actionable content.
 *
 * A backdrop is composed of two surfaces: a back layer and a front layer.
 * The back layer displays actions and context, and these control and
 * inform the front layer's content.
 *
 * For more information, see https://material.io/components/backdrop
 *
 * @param backdropState state of the backdrop
 * @param modifier optional modifier for the backdrop
 * @param peekContent composable for the content on the back layer that stays visible when the front layer is fully visible
 * @param backdropContent composable for the content on the back layer that is only exposed when the front layer is moved
 * @param backdropColor color behind the content on the back layer and peek composables
 * @param bodyContent composable for the content on the front layer
 * @param bodyColor color of the front layer surface
 */
@Composable
fun Backdrop(
    backdropState: BackdropState = remember { BackdropState.CONCEALED },
    modifier: Modifier = Modifier,
    peekContent: @Composable() (BackdropState) -> Unit,
    backdropContent: @Composable() (BackdropState) -> Unit,
    backdropColor: Color = MaterialTheme.colors.primary,
    bodyContent: @Composable() (BackdropState) -> Unit,
    bodyColor: Color = MaterialTheme.colors.surface
) {
    Column(modifier.background(backdropColor)) {
        peekContent(backdropState)
        Stack(Modifier.fillMaxWidth().weight(1f)) {
            val (backdropContentSize, setBackdropContentSize) = remember { mutableStateOf(0) }
            val frontSlide = animate(if (backdropState == BackdropState.CONCEALED) 0 else backdropContentSize)
            Stack(
                modifier = Modifier.onPositioned { setBackdropContentSize(it.size.height) }
            ) {
                backdropContent(backdropState)
            }
            Surface(
                modifier = modifier.offset(y = with(DensityAmbient.current){ frontSlide.toDp() }),
                color = bodyColor,
                shape = RoundedCornerShape(topLeft = 16.dp, topRight = 16.dp)
            ) {
                bodyContent(backdropState)
            }
        }
    }
}

enum class BackdropState {
    CONCEALED, REVEALED;

    fun other() = if (this == CONCEALED) REVEALED else CONCEALED
}