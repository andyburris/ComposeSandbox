package com.andb.apps.composesandbox.ui.common

import androidx.compose.animation.asDisposableClock
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.Saver
import androidx.compose.runtime.savedinstancestate.rememberSavedInstanceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.WithConstraints
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.gesture.tapGestureFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.DensityAmbient
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

/**
 * Possible states of the [BottomSheetLayout]
 */
enum class BottomSheetValue {
    Closed,
    Peek,
    Open,
    Expanded
}

/**
 * State of the [BottomSheetLayout] composable.
 *
 * @param initialValue The initial value of the state.
 * @param clock The animation clock that will be used to drive the animations.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Suppress("NotCloseable")
@OptIn(ExperimentalMaterialApi::class)
class BottomSheetState(
    initialValue: BottomSheetValue,
    clock: AnimationClockObservable,
    confirmStateChange: (BottomSheetValue) -> Boolean = { true }
) : SwipeableState<BottomSheetValue>(
    initialValue = initialValue,
    clock = clock,
    animationSpec = AnimationSpec,
    confirmStateChange = confirmStateChange
) {
    /**
     * Whether the drawer is open.
     */
    val isOpen: Boolean
        get() = value == BottomSheetValue.Open

    /**
     * Whether the drawer is peeking.
     */
    val isPeek: Boolean
        get() = value == BottomSheetValue.Peek

    /**
     * Whether the drawer is closed.
     */
    val isClosed: Boolean
        get() = value == BottomSheetValue.Closed

    /**
     * Whether the drawer is expanded.
     */
    val isExpanded: Boolean
        get() = value == BottomSheetValue.Expanded

    /**
     * Open the drawer with an animation.
     *
     * @param onOpened Optional callback invoked when the drawer has finished opening.
     */
    fun open(onOpened: (() -> Unit)? = null) {
        animateTo(BottomSheetValue.Open, onEnd = { endReason, endValue ->
            if (endReason != AnimationEndReason.Interrupted &&
                endValue == BottomSheetValue.Open
            ) {
                onOpened?.invoke()
            }
        })
    }

    /**
     * Peek the drawer with an animation.
     *
     * @param onPeeked Optional callback invoked when the drawer has finished peeking.
     */
    fun peek(onPeeked: (() -> Unit)? = null) {
        animateTo(BottomSheetValue.Peek, onEnd = { endReason, endValue ->
            if (endReason != AnimationEndReason.Interrupted &&
                endValue == BottomSheetValue.Peek
            ) {
                onPeeked?.invoke()
            }
        })
    }

    /**
     * Close the drawer with an animation.
     *
     * @param onClosed Optional callback invoked when the drawer has finished closing.
     */
    fun close(onClosed: (() -> Unit)? = null) {
        animateTo(BottomSheetValue.Closed, onEnd = { endReason, endValue ->
            if (endReason != AnimationEndReason.Interrupted &&
                endValue == BottomSheetValue.Closed
            ) {
                onClosed?.invoke()
            }
        })
    }

    /**
     * Expand the drawer with an animation.
     *
     * @param onExpanded Optional callback invoked when the drawer has finished expanding.
     */
    fun expand(onExpanded: (() -> Unit)? = null) {
        animateTo(BottomSheetValue.Expanded, onEnd = { endReason, endValue ->
            if (endReason != AnimationEndReason.Interrupted &&
                endValue == BottomSheetValue.Expanded
            ) {
                onExpanded?.invoke()
            }
        })
    }

    companion object {
        /**
         * The default [Saver] implementation for [BottomSheetState].
         */
        fun Saver(
            clock: AnimationClockObservable,
            confirmStateChange: (BottomSheetValue) -> Boolean
        ) = Saver<BottomSheetState, BottomSheetValue>(
            save = { it.value },
            restore = { BottomSheetState(it, clock, confirmStateChange) }
        )
    }
}

/**
 * Create and [remember] a [BottomSheetState] with the default animation clock.
 *
 * @param initialValue The initial value of the state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
fun rememberBottomSheetState(
    initialValue: BottomSheetValue,
    confirmStateChange: (BottomSheetValue) -> Boolean = { true }
): BottomSheetState {
    val clock = AnimationClockAmbient.current.asDisposableClock()
    return rememberSavedInstanceState(
        clock,
        saver = BottomSheetState.Saver(clock, confirmStateChange)
    ) {
        BottomSheetState(initialValue, clock, confirmStateChange)
    }
}

/**
 * Bottom sheets are surfaces containing supplementary content that are anchored to the bottom of the screen.
 *
 * Standard bottom sheets display content that complements the screen’s primary content.
 * They remain visible while users interact with the primary content.
 *
 * Modal bottom sheets are an alternative to inline menus or simple dialogs on mobile and provide room
 * for additional items, longer descriptions, and iconography. They must be dismissed in order to interact with the underlying content.
 *
 * Expanding bottom sheets provide a small, collapsed surface that can be expanded by the user to access a key feature or task.
 * They offer the persistent access of a standard sheet with the space and focus of a modal sheet.
 *
 * See [ModalDrawerLayout] for a layout that introduces a classic from-the-side drawer.
 *
 * @sample androidx.compose.material.samples.BottomDrawerSample
 *
 * @param sheetState state of the sheet
 * @param modifier optional modifier for the sheet
 * @param gesturesEnabled whether or not sheet can be interacted by gestures
 * @param sheetShape shape of the sheet sheet
 * @param sheetElevation sheet sheet elevation. This controls the size of the shadow below the
 * sheet sheet
 * @param sheetContent composable that represents content inside the sheet
 * @param sheetBackgroundColor background color to be used for the sheet sheet
 * @param sheetContentColor color of the content to use inside the sheet sheet. Defaults to
 * either the matching `onFoo` color for [sheetBackgroundColor], or, if it is not a color from
 * the theme, this will keep the same value set above this Surface.
 * @param scrimColor color of the scrim that obscures content when the sheet is open
 * @param bodyContent content of the rest of the UI
 *
 * @throws IllegalStateException when parent has [Float.POSITIVE_INFINITY] height
 */
@Composable
@OptIn(ExperimentalMaterialApi::class)
fun BottomSheetLayout(
    sheetContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    sheetState: BottomSheetState = rememberBottomSheetState(BottomSheetValue.Closed),
    closeable: Boolean = true,
    gesturesEnabled: Boolean = true,
    peekHeight: Dp = 56.dp,
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = DrawerConstants.DefaultElevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    scrimColor: Color = Color.Transparent,
    bodyContent: @Composable () -> Unit
) {
    WithConstraints(modifier.fillMaxSize()) {
        // TODO : think about Infinite max bounds case
        if (!constraints.hasBoundedHeight) {
            throw IllegalStateException("Drawer shouldn't have infinite height")
        }
        val dpConstraints = with(DensityAmbient.current) {
            DpConstraints(constraints)
        }
        val minValue = 0f
        val maxValue = constraints.maxHeight.toFloat()

        // TODO: add proper landscape support
        val isLandscape = constraints.maxWidth > constraints.maxHeight
        val openValue = if (isLandscape) minValue else lerp(
            minValue,
            maxValue,
            BottomDrawerOpenFraction
        )
        val closedIfAble = if (closeable) mapOf(maxValue to BottomSheetValue.Closed) else emptyMap()
        val peekValue = maxValue - with(DensityAmbient.current) { peekHeight.toPx() }

        val anchors =
            if (isLandscape) {
                closedIfAble + mapOf(
                    peekValue to BottomSheetValue.Peek,
                    minValue to BottomSheetValue.Open
                )
            } else {
                closedIfAble + mapOf(
                    peekValue to BottomSheetValue.Peek,
                    openValue to BottomSheetValue.Open,
                    minValue to BottomSheetValue.Expanded
                )
            }
        Stack(
            Modifier.swipeable(
                state = sheetState,
                anchors = anchors,
                thresholds = { _, _ -> FixedThreshold(BottomDrawerThreshold) },
                orientation = Orientation.Vertical,
                enabled = gesturesEnabled,
                resistanceFactorAtMin = 0f,
                resistanceFactorAtMax = 0f
            )
        ) {
            Stack {
                bodyContent()
            }
/*            Scrim(
                open = sheetState.isOpen,
                onClose = { sheetState.close() },
                fraction = {
                    // as we scroll "from height to 0" , need to reverse fraction
                    1 - calculateFraction(openValue, maxValue, sheetState.offset.value)
                },
                color = scrimColor
            )*/
            Surface(
                modifier = Modifier
                    .preferredSizeIn(dpConstraints)
                    .offsetPx(y = sheetState.offset),
                shape = sheetShape,
                color = sheetBackgroundColor,
                contentColor = sheetContentColor,
                elevation = sheetElevation
            ) {
                Column(Modifier.fillMaxSize(), children = sheetContent)
            }
        }
    }
}

/**
 * Object to hold default values for [ModalDrawerLayout] and [BottomSheetLayout]
 */
object DrawerConstants {

    /**
     * Default Elevation for drawer sheet as specified in material specs
     */
    val DefaultElevation = 16.dp

    /**
     * Default alpha for scrim color
     */
    const val ScrimDefaultOpacity = 0.32f
}

private fun calculateFraction(a: Float, b: Float, pos: Float) =
    ((pos - a) / (b - a)).coerceIn(0f, 1f)

@Composable
private fun Scrim(
    open: Boolean,
    onClose: () -> Unit,
    fraction: () -> Float,
    color: Color
) {
    val dismissDrawer = if (open) {
        Modifier.tapGestureFilter { onClose() }
    } else {
        Modifier
    }

    Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissDrawer)
    ) {
        drawRect(color, alpha = fraction())
    }
}

private val VerticalDrawerPadding = 56.dp
private val DrawerVelocityThreshold = 400.dp

private const val DrawerStiffness = 1000f

private val AnimationSpec = SpringSpec<Float>(stiffness = DrawerStiffness)

internal const val BottomDrawerOpenFraction = 0.5f
internal val BottomDrawerThreshold = 56.dp
