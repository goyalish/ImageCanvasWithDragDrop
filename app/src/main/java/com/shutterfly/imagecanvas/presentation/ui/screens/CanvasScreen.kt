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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.shutterfly.imagecanvas.R
import com.shutterfly.imagecanvas.presentation.ui.components.CanvasPlacedImage
import com.shutterfly.imagecanvas.presentation.ui.components.Carousel
import com.shutterfly.imagecanvas.presentation.viewmodel.CanvasViewModel

@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    viewModel: CanvasViewModel = hiltViewModel()
) {
    val canvasImages by viewModel.canvasImages.collectAsState()
    val tempDrag by viewModel.tempDrag.collectAsState()

    // sample images list (resource ids)
    val samples = listOf(
        R.drawable.league,
        R.drawable.ic_launcher,
        R.drawable.league,
        R.drawable.ic_launcher,
        R.drawable.league,
        R.drawable.ic_launcher,
        R.drawable.league,
        R.drawable.ic_launcher
    )

    // canvas global position and size
    var canvasLeft by remember { mutableStateOf(0) }
    var canvasTop by remember { mutableStateOf(0) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var rootLeft by remember { mutableStateOf(0f) }
    var rootTop by remember { mutableStateOf(0f) }


    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize()
            .onGloballyPositioned { coords ->
                val rootPos = coords.positionInRoot()
                rootLeft = rootPos.x
                rootTop = rootPos.y
            }) {
            // center square canvas
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(360.dp) // square canvas - you can make it responsive if you want
                    .onGloballyPositioned { coords ->
                        val pos = coords.positionInRoot()
                        canvasLeft = pos.x.toInt()
                        canvasTop = pos.y.toInt()
                        canvasSize = coords.size
                    }
                    .clipToBounds()
                    .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.TopStart
            ) {
                // Canvas area: show dropped images
                Box(modifier = Modifier.fillMaxSize()) {
                    canvasImages.forEach { img ->
                        CanvasPlacedImage(img = img, onTransform = { id, dx, dy, scale ->
                            viewModel.updateCanvasImageTransform(id, dx, dy, newScale = scale)
                        })
                    }
                }
            }

            // temporary overlay drag image that follows finger
            tempDrag?.let { temp ->
                val density = LocalDensity.current
                val localX = temp.x - rootLeft
                val localY = temp.y - rootTop
                val xDp = with(density) { localX.toDp() }
                val yDp = with(density) { localY.toDp() }

                Image(
                    painter = painterResource(id = temp.resId),
                    contentDescription = null,
                    modifier = Modifier
                        .offset(x = xDp - 60.dp, y = yDp - 60.dp) // centers image under finger
                        .size(120.dp)
                        .graphicsLayer { alpha = 0.9f }
                )
            }


            // Carousel at bottom
            Carousel(
                images = samples,
                onDragStart = { resId, startGlobal ->
                    viewModel.startTempDrag(resId, startGlobal)
                },
                onDragMove = { x, y -> viewModel.updateTempDrag(x, y) },
                onDragEnd = { dropX, dropY ->
                    // Pass canvas global Left/Top so VM can compute canvas-local coords
                    viewModel.endTempDrag(dropX, dropY, canvasLeft, canvasTop)
                },
                onDragCancel = { viewModel.cancelTempDrag() },
                modifier = modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}