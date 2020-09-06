package com.andb.apps.composesandbox.ui.common

import androidx.ui.material.BottomDrawerLayout
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ModalDrawerLayout
import androidx.ui.material.Surface
import androidx.animation.SpringSpec
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.state
import androidx.ui.core.DensityAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.core.gesture.scrollorientationlocking.Orientation
import androidx.ui.core.gesture.tapGestureFilter
import androidx.ui.foundation.Box
import androidx.ui.foundation.Canvas
import androidx.ui.graphics.Shape
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.offsetPx
import androidx.ui.layout.padding
import androidx.ui.layout.preferredSizeIn
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import androidx.ui.util.lerp

/**
 * Possible states of the [BottomDrawerLayout]
 */
enum class BottomSheetState {
    Hidden,
    Peek,
    Opened,
    Expanded
}


/**
 * Navigation drawers provide access to destinations in your app.
 *
 * Bottom navigation drawers are modal drawers that are anchored
 * to the bottom of the screen instead of the left or right edge.
 * They are only used with bottom app bars.
 *
 * These drawers open upon tapping the navigation menu icon in the bottom app bar.
 * They are only for use on mobile.
 *
 * See [ModalDrawerLayout] for a layout that introduces a classic from-the-side drawer.
 *
 * @sample androidx.ui.material.samples.BottomDrawerSample
 *
 * @param sheetState state of the drawer
 * @param onStateChange lambda to be invoked when the drawer requests to change its state,
 * e.g. when the drawer is being swiped to the new state or when the scrim is clicked
 * @param gesturesEnabled whether or not drawer can be interacted by gestures
 * @param drawerShape shape of the drawer sheet
 * @param drawerElevation drawer sheet elevation. This controls the size of the shadow below the
 * drawer sheet
 * @param drawerContent composable that represents content inside the drawer
 * @param bodyContent content of the rest of the UI
 *
 * @throws IllegalStateException when parent has [Float.POSITIVE_INFINITY] height
 */
@Composable
fun BottomSheetLayout(
    sheetState: BottomSheetState,
    onStateChange: (BottomSheetState) -> Unit,
    gesturesEnabled: Boolean = true,
    peekHeight: Dp = 56.dp,
    hideable: Boolean = true,
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerConstants.DefaultElevation,
    drawerContent: @Composable () -> Unit,
    bodyContent: @Composable () -> Unit
) {
    WithConstraints(Modifier.fillMaxSize()) {
        // TODO : think about Infinite max bounds case
        if (!constraints.hasBoundedHeight) {
            throw IllegalStateException("Drawer shouldn't have infinite height")
        }
        val dpConstraints = with(DensityAmbient.current) {
            DpConstraints(constraints)
        }
        val minValue = 0f
        val maxValue = constraints.maxHeight.toFloat()
        val peekValue = with(DensityAmbient.current) { peekHeight.toPx() }

        // TODO: add proper landscape support
        val isLandscape = constraints.maxWidth > constraints.maxHeight
        val openedValue = if (isLandscape) minValue else lerp(
            minValue,
            maxValue,
            BottomDrawerOpenFraction
        )
        val anchors =
            if (isLandscape) {
                (if (hideable) listOf(maxValue to BottomSheetState.Hidden) else emptyList()) + listOf(
                    (maxValue - peekValue) to BottomSheetState.Peek,
                    minValue to BottomSheetState.Opened
                )
            } else {
                (if (hideable) listOf(maxValue to BottomSheetState.Hidden) else emptyList()) + listOf(
                    (maxValue - peekValue) to BottomSheetState.Peek,
                    openedValue to BottomSheetState.Opened,
                    minValue to BottomSheetState.Expanded
                )
            }
        val drawerPosition = state { maxValue }
        val offset = with(DensityAmbient.current) { BottomDrawerThreshold.toPx() }
        Stack(
            Modifier.stateDraggable(
                state = sheetState,
                onStateChange = onStateChange,
                anchorsToState = anchors,
                thresholds = fixedThresholds(offset),
                animationSpec = AnimationSpec,
                orientation = Orientation.Vertical,
                minValue = minValue,
                maxValue = maxValue,
                enabled = gesturesEnabled,
                onNewValue = { drawerPosition.value = it }
            )
        ) {
            Stack {
                bodyContent()
            }
            BottomDrawerContent(
                drawerPosition, dpConstraints, drawerShape, drawerElevation, drawerContent
            )
        }
    }
}

/**
 * Object to hold default values for [ModalDrawerLayout] and [BottomDrawerLayout]
 */
object DrawerConstants {

    /**
     * Default Elevation for drawer sheet as specified in material specs
     */
    val DefaultElevation = 16.dp
}

@Composable
private fun DrawerContent(
    xOffset: State<Float>,
    constraints: DpConstraints,
    shape: Shape,
    elevation: Dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier =
        Modifier
            .preferredSizeIn(constraints)
            .offsetPx(x = xOffset)
            .padding(end = VerticalDrawerPadding),
        shape = shape,
        elevation = elevation
    ) {
        Box(Modifier.fillMaxSize(), children = content)
    }
}

@Composable
private fun BottomDrawerContent(
    yOffset: State<Float>,
    constraints: DpConstraints,
    shape: Shape,
    elevation: Dp,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .preferredSizeIn(constraints)
            .offsetPx(y = yOffset),
        shape = shape,
        elevation = elevation
    ) {
        Box(Modifier.fillMaxSize(), children = content)
    }
}

private fun calculateFraction(a: Float, b: Float, pos: Float) =
    ((pos - a) / (b - a)).coerceIn(0f, 1f)

@Composable
private fun Scrim(
    opened: Boolean,
    onClose: () -> Unit,
    fraction: () -> Float
) {
    val color = MaterialTheme.colors.onSurface
    val dismissDrawer = if (opened) {
        Modifier.tapGestureFilter { onClose() }
    } else {
        Modifier
    }

    Canvas(
        Modifier
            .fillMaxSize()
            .plus(dismissDrawer)
    ) {
        drawRect(color, alpha = fraction() * ScrimDefaultOpacity)
    }
}

private const val ScrimDefaultOpacity = 0.32f
private val VerticalDrawerPadding = 56.dp

private const val DrawerStiffness = 1000f

private val AnimationSpec = SpringSpec<Float>(stiffness = DrawerStiffness)

internal const val BottomDrawerOpenFraction = 0.5f
internal val BottomDrawerThreshold = 56.dp
