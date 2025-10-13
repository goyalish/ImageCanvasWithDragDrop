package com.shutterfly.imagecanvas.presentation.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import com.shutterfly.imagecanvas.presentation.model.CanvasImage
import kotlin.ranges.coerceIn

@Composable
fun CanvasPlacedImage(
    img: CanvasImage,
    onTransform: (id: String, dx: Float, dy: Float, newScale: Float) -> Unit,
) {
    // local state of transform to provide smooth gestures
    var offsetX by remember { mutableFloatStateOf(img.offsetX) }
    var offsetY by remember { mutableFloatStateOf(img.offsetY) }
    var scale by remember { mutableFloatStateOf(img.scale) }
    var angle by remember { mutableFloatStateOf(0f) }
    // update local state if VM updated external values (rare here)
    LaunchedEffect(img.offsetX, img.offsetY, img.scale) {
        offsetX = img.offsetX
        offsetY = img.offsetY
        scale = img.scale
    }

    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
//            .background(color = Color.Red)
            .pointerInput(img.id) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    // pan: pixels moved
                    offsetX += pan.x
                    offsetY += pan.y
                    scale = (scale * zoom).coerceIn(0.3f, 4f)
                    angle += rotation
//                    onTransform(img.id, pan.x, pan.y, scale)
                }
            }
    ) {
        // show image with scale applied and maintain aspect ratio automatically by box size
        val sizeDp = 120.dp // default display size for image on canvas (tweak as needed)
        Image(
            painter = painterResource(id = img.resId),
            contentDescription = null,
            modifier = Modifier
                .size(sizeDp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = angle
                }
        )
    }
}
