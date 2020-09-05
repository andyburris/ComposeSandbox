package com.andb.apps.composesandbox.ui.sandbox

import androidx.compose.Composable
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.animation.animate
import androidx.ui.animation.animatedFloat
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.core.onPositioned
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.offset
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Surface
import androidx.ui.unit.dp
import com.andb.apps.composesandbox.util.Content

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
    Column(modifier.drawBackground(backdropColor)) {
        peekContent(backdropState)
        Stack(Modifier.fillMaxWidth().weight(1f)) {
            val (backdropContentSize, setBackdropContentSize) = state { 0 }
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