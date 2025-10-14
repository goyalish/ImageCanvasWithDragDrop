package com.shutterfly.imagecanvas.presentation.viewmodel

import androidx.compose.ui.geometry.Offset

sealed class CanvasViewIntent {
    object GetImages : CanvasViewIntent()
    object CancelTempDrag : CanvasViewIntent()
    data class StartTempDrag(val resId: Int, val globalStart: Offset) : CanvasViewIntent()
    data class UpdateTempDrag(val x: Float, val y: Float) : CanvasViewIntent()
    data class EndTempDrag(
        val dropX: Float, val dropY: Float,
        val canvasLeft: Int,
        val canvasTop: Int
    ) : CanvasViewIntent()


    data class UpdateCanvasImageTransform(
        val id: String,
        val dx: Float,
        val dy: Float,
        val newScale: Float? = null,
        val newRotation: Float? = null
    ) : CanvasViewIntent()
}