package com.shutterfly.imagecanvas.presentation.viewmodel

import com.shutterfly.imagecanvas.presentation.model.CanvasImage
import com.shutterfly.imagecanvas.presentation.model.TempDrag

data class CanvasViewState(
    val isLoading: Boolean = false,
    var canvasImages: List<CanvasImage> = emptyList(),
    var tempDrag: TempDrag? = null,
    var images: List<Int> = emptyList(),
    val error: String? = null
)
