package com.shutterfly.imagecanvas.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
    else if (canvasViewState.value.error != null) {
        // Show error message
        Box(
            modifier = modifier.fillMaxSize(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Failed to fetch the data", style = MaterialTheme.typography.headlineMedium)
        }
    } else {
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coords ->
                        val rootPos = coords.positionInRoot()
                        rootLeft = rootPos.x
                        rootTop = rootPos.y
                    }) {
                // center square canvas
                Box(
                    modifier = modifier
                        .align(Alignment.Center)
                        .size(360.dp) // square canvas - you can make it responsive if you want
                        .onGloballyPositioned { coords ->
                            val pos = coords.positionInRoot()
                            canvasLeft = pos.x.toInt()
                            canvasTop = pos.y.toInt()
//                            canvasSize = coords.size
                        }
                        .clipToBounds()
                        .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.TopStart
                ) {
                    // Canvas area: show dropped images
                    Box(modifier = modifier.fillMaxSize()) {
                        canvasViewState.value.canvasImages.forEach { img ->
                            CanvasPlacedImage(
                                img = img,
                                onTransform = { id, dx, dy, scale, rotation ->
                                    viewModel.onEvent(
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
                            .offset(x = xDp - 60.dp, y = yDp - 60.dp) // centers image under finger
                            .size(120.dp)
                            .graphicsLayer { alpha = 0.9f }
                    )
                }


                // Carousel at bottom
                Carousel(
                    images = canvasViewState.value.images,
                    onDragStart = { resId, startGlobal ->
                        viewModel.onEvent(CanvasViewIntent.StartTempDrag(resId, startGlobal))
                    },
                    onDragMove = { x, y ->
                        viewModel.onEvent(CanvasViewIntent.UpdateTempDrag(x, y))
                    },
                    onDragEnd = { dropX, dropY ->
                        // Pass canvas global Left/Top so VM can compute canvas-local coords
                        viewModel.onEvent(
                            CanvasViewIntent.EndTempDrag(dropX, dropY, canvasLeft, canvasTop)
                        )
                    },
                    onDragCancel = { viewModel.onEvent(CanvasViewIntent.CancelTempDrag) },
                    modifier = modifier.align(Alignment.BottomCenter)
                )
            }
        }

    }
}