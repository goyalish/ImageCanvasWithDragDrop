package com.shutterfly.imagecanvas.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.shutterfly.imagecanvas.R

@Composable
fun Carousel(
    images: List<Int>,
    onDragStart: (resId: Int, startGlobal: Offset) -> Unit,
    onDragMove: (x: Float, y: Float) -> Unit,
    onDragEnd: (x: Float, y: Float) -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scroll)
            .padding(dimensionResource(R.dimen.small_12)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.small_12))
    ) {
        for (image in images) {
            // Keep track of this image’s global layout position
            var coordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
            var lastDrag by remember { mutableStateOf(Offset.Zero) }

            Box(
                modifier = modifier
                    .size(dimensionResource(R.dimen.x_large_80))
                    .onGloballyPositioned { coords -> coordinates = coords }
                    .pointerInput(image) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { offset ->
                                // Convert local offset to global using saved coordinates
                                val globalPos: Offset =
                                    coordinates?.localToRoot(offset) ?: Offset.Zero
                                onDragStart(image, globalPos)
                                lastDrag = globalPos
                            },
                            onDrag = { pointerInputChange, _ ->
                                val pos: Offset = pointerInputChange.position
                                val global = coordinates?.localToRoot(pos) ?: pos
                                lastDrag = global
                                onDragMove(global.x, global.y)
                            },
                            onDragEnd = {
                                onDragEnd(lastDrag.x, lastDrag.y)
                            },
                            onDragCancel = { onDragCancel() }
                        )
                    }
            ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = null,
                    modifier = modifier
                        .fillMaxSize()
                        .border(
                            dimensionResource(R.dimen.x_small_1), Color.LightGray,
                            shape = RoundedCornerShape(dimensionResource(R.dimen.small_6))
                        )
                )
            }
        }
    }
}