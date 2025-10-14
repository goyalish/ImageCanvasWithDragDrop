package com.shutterfly.imagecanvas.presentation.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.IntOffset
import com.shutterfly.imagecanvas.R
import com.shutterfly.imagecanvas.presentation.model.CanvasImage
import kotlin.ranges.coerceIn

@Composable
fun CanvasPlacedImage(
    img: CanvasImage,
    onTransform: (id: String, dx: Float, dy: Float, newScale: Float, rotation: Float) -> Unit,
) {
    // local state of transform to provide smooth gestures
    var offsetX by remember { mutableFloatStateOf(img.offsetX) }
    var offsetY by remember { mutableFloatStateOf(img.offsetY) }
    var scale by remember { mutableFloatStateOf(img.scale) }
    var angle by remember { mutableFloatStateOf(img.rotation) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
            .pointerInput(img.id) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    // pan: pixels moved
                    offsetX += pan.x
                    offsetY += pan.y
                    scale = (scale * zoom).coerceIn(0.3f, 4f)
                    angle += rotation
                    onTransform(img.id, pan.x, pan.y, scale, angle)
                }
            }
    ) {
        Image(
            painter = painterResource(id = img.resId),
            contentDescription = null,
            modifier = Modifier
                .size(dimensionResource(R.dimen.x_large_80))
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = angle
                }
        )
    }
}
