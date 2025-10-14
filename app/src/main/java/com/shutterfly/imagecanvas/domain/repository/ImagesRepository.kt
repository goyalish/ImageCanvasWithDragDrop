package com.shutterfly.imagecanvas.domain.repository

import com.shutterfly.imagecanvas.domain.model.Resource
import com.shutterfly.imagecanvas.domain.model.Image
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {
    suspend fun getImages(): Flow<Resource<List<Image>>>
}