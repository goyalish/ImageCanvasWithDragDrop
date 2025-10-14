package com.shutterfly.imagecanvas.presentation.viewmodel

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shutterfly.imagecanvas.R
import com.shutterfly.imagecanvas.domain.model.Image
import com.shutterfly.imagecanvas.domain.model.Resource
import com.shutterfly.imagecanvas.domain.usecase.ImagesUseCase
import com.shutterfly.imagecanvas.presentation.model.CanvasImage
import com.shutterfly.imagecanvas.presentation.model.TempDrag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel holds canvas items and a temporary drag state
 */
@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val imagesUseCase: ImagesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CanvasViewState())
    val state: StateFlow<CanvasViewState> = _state

    private val _intents = MutableSharedFlow<CanvasViewIntent>()

    init {
        handleIntents()
        sendIntent(CanvasViewIntent.GetImages)
    }

    fun sendIntent(intent: CanvasViewIntent) {
        viewModelScope.launch { _intents.emit(intent) }
    }

    private fun handleIntents() {
        viewModelScope.launch {
            _intents.collect { intent ->
                when (intent) {
                    is CanvasViewIntent.GetImages -> {
                        loadImages()
                    }

                    is CanvasViewIntent.StartTempDrag -> {
                        startTempDrag(intent.resId, intent.globalStart)
                    }

                    is CanvasViewIntent.UpdateTempDrag -> {
                        updateTempDrag(intent.x, intent.y)
                    }

                    is CanvasViewIntent.EndTempDrag -> {
                        endTempDrag(intent.dropX, intent.dropY, intent.canvasLeft, intent.canvasTop)
                    }

                    is CanvasViewIntent.CancelTempDrag -> {
                        cancelTempDrag()
                    }

                    is CanvasViewIntent.UpdateCanvasImageTransform -> {
                        updateCanvasImageTransform(
                            intent.id,
                            intent.dx,
                            intent.dy,
                            intent.newScale,
                            intent.newRotation
                        )
                    }
                }
            }
        }
    }

    private fun loadImages() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
//            delay(2000)
            imagesUseCase.getImages().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val images = imagesToDrawablesMapper(resource.data)
                        _state.value = _state.value.copy(isLoading = false, images = images)
                    }
                    is Resource.Failed -> {
                        _state.value = _state.value.copy(isLoading = false, error = resource.message)
                    }
                }
            }
        }
    }

    private fun imagesToDrawablesMapper(images: List<Image>): List<Int> {
        val drawables = mutableListOf<Int>()
        for (image in images) {
            when (image.resName) {
                "image1" -> drawables.add(R.drawable.image1)
                "image2" -> drawables.add(R.drawable.image2)
                "image3" -> drawables.add(R.drawable.image3)
                "image4" -> drawables.add(R.drawable.image4)
                "image5" -> drawables.add(R.drawable.image5)
                else -> drawables.add(R.drawable.image1)
            }

        }
        return drawables
    }

    private fun startTempDrag(@DrawableRes resId: Int, globalStart: Offset) {
        _state.value = _state.value.copy(tempDrag = TempDrag(resId, globalStart.x, globalStart.y))
    }

    private fun updateTempDrag(x: Float, y: Float) {
        _state.value = _state.value.copy(tempDrag = _state.value.tempDrag?.copy(x = x, y = y))
    }

    /**
     * End temp drag: if dropped inside canvas, add image to canvas with canvas-local coordinates.
     * canvasBounds is in global coordinates (left, top).
     */
    private fun endTempDrag(dropX: Float, dropY: Float, canvasLeft: Int, canvasTop: Int) {
        _state.value.tempDrag?.let { temp ->
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
                _state.value = _state.value.copy(canvasImages = _state.value.canvasImages + newImg)
            }
        }
        _state.value = _state.value.copy(tempDrag = null)
    }


    // Remove temp drag without adding
    private fun cancelTempDrag() {
        _state.value = _state.value.copy(tempDrag = null)
    }

    // Update a canvas image transform (pan/zoom)
    private fun updateCanvasImageTransform(
        id: String,
        dx: Float,
        dy: Float,
        newScale: Float? = null,
        newRotation: Float? = null
    ) {
        _state.value.canvasImages = _state.value.canvasImages.map { img ->
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

    private fun setCanvasImageTransform(id: String, offsetX: Float, offsetY: Float, scale: Float) {
        _state.value.canvasImages = _state.value.canvasImages.map { img ->
            if (img.id == id) img.copy(offsetX = offsetX, offsetY = offsetY, scale = scale) else img
        }
    }
}
