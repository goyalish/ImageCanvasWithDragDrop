package com.shutterfly.imagecanvas.data.repository

import com.shutterfly.imagecanvas.domain.model.Image
import com.shutterfly.imagecanvas.domain.model.Resource
import com.shutterfly.imagecanvas.domain.repository.ImagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ImagesRepositoryImpl @Inject constructor() : ImagesRepository {
    override suspend fun getImages(): Flow<Resource<List<Image>>> {
        return flow<Resource<List<Image>>> {
            emit(
                Resource.Success(
                    listOf(
                        Image("image1"),
                        Image("image2"),
                        Image("image3"),
                        Image("image4"),
                        Image("image5"),
                        Image("image1"),
                        Image("image2"),
                        Image("image3"),
                        Image("image4"),
                        Image("image5"),
                    )
                )
            )
        }.catch { e ->
            emit(Resource.Failed("An error occurred: ${e.message}"))
        }
    }
}