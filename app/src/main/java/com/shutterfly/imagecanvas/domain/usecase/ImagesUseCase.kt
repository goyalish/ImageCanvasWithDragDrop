package com.shutterfly.imagecanvas.domain.usecase

import com.shutterfly.imagecanvas.domain.repository.ImagesRepository
import javax.inject.Inject

class ImagesUseCase @Inject constructor(private val imagesRepository: ImagesRepository) {

    suspend fun getImages() = imagesRepository.getImages()
}