package com.shutterfly.imagecanvas.presentation.viewmodel

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.shutterfly.imagecanvas.presentation.model.CanvasImage
import com.shutterfly.imagecanvas.presentation.model.TempDrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * ViewModel holds canvas items and a temporary drag state
 */
@HiltViewModel
class CanvasViewModel @Inject constructor(): ViewModel() {

    // images currently on canvas
    private val _canvasImages = MutableStateFlow<List<CanvasImage>>(emptyList())
    val canvasImages: StateFlow<List<CanvasImage>> = _canvasImages

    // temporary "dragging from carousel" overlay
    private val _tempDrag = MutableStateFlow<TempDrag?>(null)
    val tempDrag: StateFlow<TempDrag?> = _tempDrag

    fun startTempDrag(@DrawableRes resId: Int, globalStart: Offset) {
        _tempDrag.value = TempDrag(resId, globalStart.x, globalStart.y)
    }

    fun updateTempDrag(x: Float, y: Float) {
        _tempDrag.value = _tempDrag.value?.copy(x = x, y = y)
    }

    /**
     * End temp drag: if dropped inside canvas, add image to canvas with canvas-local coordinates.
     * canvasBounds is in global coordinates (left, top).
     */
    fun endTempDrag(dropX: Float, dropY: Float, canvasLeft: Int, canvasTop: Int) {
        _tempDrag.value?.let { temp ->
            val inCanvasX = dropX - canvasLeft
            val inCanvasY = dropY - canvasTop

            // Adjust by half of the preview image size in pixels (60.dp)
            val previewHalfPx = 60 * Resources.getSystem().displayMetrics.density
            val adjustedX = inCanvasX - previewHalfPx
            val adjustedY = inCanvasY - previewHalfPx

            // Only add if inside canvas
            if (adjustedX >= 0f && adjustedY >= 0f) {
                val newImg = CanvasImage(
                    resId = temp.resId,
                    offsetX = adjustedX,
                    offsetY = adjustedY,
                    scale = 1f
                )
                _canvasImages.value = _canvasImages.value + newImg
            }
        }
        _tempDrag.value = null
    }


    // Remove temp drag without adding
    fun cancelTempDrag() {
        _tempDrag.value = null
    }

    // Update a canvas image transform (pan/zoom)
    fun updateCanvasImageTransform(id: String, dx: Float, dy: Float, newScale: Float? = null, newRotation: Float? = null) {
        _canvasImages.value = _canvasImages.value.map { img ->
            if (img.id == id) {
                img.copy(
                    offsetX = img.offsetX + dx,
                    offsetY = img.offsetY + dy,
                    scale = newScale ?: img.scale,
                    rotation = newRotation ?: img.rotation
                )
            } else img
        }
    }

    fun setCanvasImageTransform(id: String, offsetX: Float, offsetY: Float, scale: Float) {
        _canvasImages.value = _canvasImages.value.map { img ->
            if (img.id == id) img.copy(offsetX = offsetX, offsetY = offsetY, scale = scale) else img
        }
    }

}
