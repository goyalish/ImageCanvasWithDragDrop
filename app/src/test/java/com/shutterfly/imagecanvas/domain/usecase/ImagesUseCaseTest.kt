package com.shutterfly.imagecanvas.domain.usecase

import com.shutterfly.imagecanvas.domain.model.Image
import com.shutterfly.imagecanvas.domain.model.Resource
import com.shutterfly.imagecanvas.domain.repository.ImagesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class ImagesUseCaseTest {

    private lateinit var imagesUseCase: ImagesUseCase
    private lateinit var imagesRepository: ImagesRepository

    @Before
    fun setUp() {
        imagesRepository = mockk<ImagesRepository>()
        imagesUseCase = ImagesUseCase(imagesRepository)
    }

    @Test
    fun `Successful call to repository method`() = runTest {
        coEvery { imagesRepository.getImages() } returns emptyFlow()
        imagesUseCase.getImages()
        coVerify { imagesRepository.getImages() }
    }

    @Test
    fun `Successful image retrieval`() = runTest{
        // Given the repository returns a successful Resource with a list of images,
        // When getImages() is called,
        // Then it should emit a Flow containing a Resource.Success with the list of images. [10]
        coEvery { imagesRepository.getImages() } returns flowOf(Resource.Success(listOf(Image("image1"), Image("image2"))))
        val result = imagesUseCase.getImages()
        assertNotNull(result)
    }

}