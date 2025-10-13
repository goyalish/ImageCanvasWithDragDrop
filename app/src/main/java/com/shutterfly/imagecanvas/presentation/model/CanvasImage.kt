package com.shutterfly.imagecanvas.presentation.model

import androidx.annotation.DrawableRes
import java.util.*

data class CanvasImage(
    val id: String = UUID.randomUUID().toString(),
    @DrawableRes val resId: Int,
    // offset relative to canvas top-left (pixels)
    var offsetX: Float = 0f,
    var offsetY: Float = 0f,
    // visual transform
    var scale: Float = 1f,
    var rotation: Float = 0f,
    // width/height in px when first added (optional for layout)
    var originalWidth: Int = 0,
    var originalHeight: Int = 0
)
