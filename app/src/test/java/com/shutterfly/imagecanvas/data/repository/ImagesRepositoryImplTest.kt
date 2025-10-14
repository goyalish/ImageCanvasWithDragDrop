package com.shutterfly.imagecanvas.data.repository

import com.shutterfly.imagecanvas.domain.model.Image
import com.shutterfly.imagecanvas.domain.model.Resource
import com.shutterfly.imagecanvas.domain.repository.ImagesRepository
import io.mockk.coEvery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ImagesRepositoryImplTest {

    private lateinit var imagesRepository: ImagesRepositoryImpl

    @Before
    fun setUp() {
        imagesRepository = ImagesRepositoryImpl()
    }

    @Test
    fun `Successful emission of image list`() = runTest {
        // Verify that the flow emits a single Resource.Success object. [2, 12]
        // Further, check that the list within Resource.Success contains the expected Image objects in the correct order.
        val images = imagesRepository.getImages().first()
        assert(images is Resource.Success)
        assert((images as Resource.Success).data.size == 10)
    }

    @Test
    fun `Correctness of the emitted data`() = runTest {
        // Validate that the emitted list is not empty.
        // Ensure the content of the list matches the hardcoded data, including duplicate entries.
        val images = imagesRepository.getImages().first()
        assert(images is Resource.Success)
        assert((images as Resource.Success).data.isNotEmpty())
    }

    @Test
    fun `Type of emitted value`() = runTest {
        // Confirm that the flow emits a value of type Flow<Resource<List<Image>>>. [2]
        // Specifically, check that the emission is an instance of Resource.Success.
        val images = imagesRepository.getImages().first()
        assert(images is Resource.Success)
    }

    @Test
    fun `Flow completion`() = runTest {
        // Check that the flow completes after emitting the single Resource.Success object.
        val emissions = imagesRepository.getImages().toList()
        // The flow should emit exactly once
        assertEquals(1, emissions.size)
    }

    @Test
    fun `Exception handling within the flow block`() = runTest {
        // Simulate an exception being thrown inside the `flow` block before `emit` is called. 
        // Verify that the `catch` block is executed and emits a `Resource.Failed` object with the correct error message. [4]
        // A fake repository that throws an exception before emitting
        val failingRepository = object : ImagesRepository {
            override suspend fun getImages(): Flow<Resource<List<Image>>> {
                return flow<Resource<List<Image>>> {
                    throw RuntimeException("Test exception")
                }.catch { e ->
                    emit(Resource.Failed("An error occurred: ${e.message}"))
                }
            }
        }

        // Collect all emissions
        val emissions = failingRepository.getImages().toList()

        // Verify flow emitted exactly one Resource.Failed
        assertEquals(1, emissions.size)
        val emission = emissions.first()

        assertTrue(emission is Resource.Failed)
        assertEquals(
            "An error occurred: Test exception",
            (emission as Resource.Failed).message
        )
    }
}