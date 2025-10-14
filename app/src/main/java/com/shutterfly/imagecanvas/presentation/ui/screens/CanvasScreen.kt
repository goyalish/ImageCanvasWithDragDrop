package com.shutterfly.imagecanvas.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shutterfly.imagecanvas.R
import com.shutterfly.imagecanvas.presentation.ui.components.CanvasPlacedImage
import com.shutterfly.imagecanvas.presentation.ui.components.Carousel
import com.shutterfly.imagecanvas.presentation.ui.components.CircularProgressbar
import com.shutterfly.imagecanvas.presentation.viewmodel.CanvasViewIntent
import com.shutterfly.imagecanvas.presentation.viewmodel.CanvasViewModel

@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    viewModel: CanvasViewModel = hiltViewModel()
) {
    // canvas global position and size
    var canvasLeft by remember { mutableIntStateOf(0) }
    var canvasTop by remember { mutableIntStateOf(0) }
    var rootLeft by remember { mutableFloatStateOf(0f) }
    var rootTop by remember { mutableFloatStateOf(0f) }

    val canvasViewState = viewModel.state.collectAsState()

    if (canvasViewState.value.isLoading) CircularProgressbar()
    else if (canvasViewState.value.error != null) ErrorScreen()
    else {

        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
                .onGloballyPositioned { coords ->
                    val rootPos = coords.positionInRoot()
                    rootLeft = rootPos.x
                    rootTop = rootPos.y
                }
        ) {
            val (canvas, carousel) = createRefs()
            val margin = dimensionResource(R.dimen.small_12)

            // Carousel at bottom
            Carousel(
                modifier = modifier.constrainAs(carousel) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                images = canvasViewState.value.images,
                onDragStart = { resId, startGlobal ->
                    viewModel.sendIntent(CanvasViewIntent.StartTempDrag(resId, startGlobal))
                },
                onDragMove = { x, y ->
                    viewModel.sendIntent(CanvasViewIntent.UpdateTempDrag(x, y))
                },
                onDragEnd = { dropX, dropY ->
                    // Pass canvas global Left/Top so VM can compute canvas-local coords
                    viewModel.sendIntent(
                        CanvasViewIntent.EndTempDrag(dropX, dropY, canvasLeft, canvasTop)
                    )
                },
                onDragCancel = { viewModel.sendIntent(CanvasViewIntent.CancelTempDrag) },
            )

            // center square canvas
            Box(
                modifier = modifier
                    .constrainAs(canvas) {
                        top.linkTo(parent.top)
                        bottom.linkTo(carousel.top) // Constrain bottom to the top of the carousel
                        start.linkTo(parent.start, margin = margin)
                        end.linkTo(parent.end, margin = margin)
                        width = Dimension.fillToConstraints
                        height = Dimension.ratio("1:1")
                    }
                    .aspectRatio(1f)
                    .onGloballyPositioned { coords ->
                        val pos = coords.positionInRoot()
                        canvasLeft = pos.x.toInt()
                        canvasTop = pos.y.toInt()
                    }
                    .clipToBounds()
                    .border(
                        width = dimensionResource(R.dimen.x_small_2),
                        color = Color.Gray,
                        shape = RoundedCornerShape(dimensionResource(R.dimen.small_8))
                    ),
            ) {
                // Canvas area: show dropped images
                Box(modifier = modifier.fillMaxSize()) {
                    canvasViewState.value.canvasImages.forEach { img ->
                        CanvasPlacedImage(
                            img = img,
                            onTransform = { id, dx, dy, scale, rotation ->
                                viewModel.sendIntent(
                                    CanvasViewIntent.UpdateCanvasImageTransform(
                                        id, dx, dy, scale, rotation
                                    )
                                )
                            })
                    }
                }
            }

            // temporary overlay drag image that follows finger
            canvasViewState.value.tempDrag?.let { temp ->
                val density = LocalDensity.current
                val localX = temp.x - rootLeft
                val localY = temp.y - rootTop
                val xDp = with(density) { localX.toDp() }
                val yDp = with(density) { localY.toDp() }

                Image(
                    painter = painterResource(id = temp.resId),
                    contentDescription = null,
                    modifier = modifier
                        .offset(
                            x = xDp - dimensionResource(R.dimen.large_60),
                            y = yDp - dimensionResource(R.dimen.large_60)
                        ) // centers image under finger
                        .size(dimensionResource(R.dimen.x_large_80))
                        .graphicsLayer { alpha = 0.9f }
                )
            }
        }
    }
}
