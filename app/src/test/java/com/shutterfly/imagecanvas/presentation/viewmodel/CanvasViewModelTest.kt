package com.shutterfly.imagecanvas.presentation.viewmodel

import app.cash.turbine.test
import com.shutterfly.imagecanvas.domain.model.Image
import com.shutterfly.imagecanvas.domain.model.Resource
import com.shutterfly.imagecanvas.domain.usecase.ImagesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CanvasViewModelTest {

    // Rule for coroutines
    private val testDispatcher = UnconfinedTestDispatcher()

    // Mocks
    private lateinit var imagesUseCase: ImagesUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // Set main dispatcher for ViewModel
        imagesUseCase = mockk()
        coEvery { imagesUseCase.getImages() } returns flowOf(Resource.Success(emptyList()))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset main dispatcher
    }

    @Test
    fun `init should trigger GetImages intent and load images successfully`() = runTest {
        // Arrange
        val images = listOf(Image("image1"))
        val successResource = Resource.Success(images)
        coEvery { imagesUseCase.getImages() } returns flowOf(successResource)

        // Act
        val vm = CanvasViewModel(imagesUseCase)

        // Assert
        vm.state.test {
            // The first state we receive is NOT the default one. It's the final,
            // successful state because the init block has already completed.
            val resultState = awaitItem()

            // Assert properties of this final state
            assertFalse(resultState.isLoading)
            assertNull(resultState.error)
            assertEquals(1, resultState.images.size)
            assertEquals(2131165287, resultState.images.first())

            // We can also verify that no other states were emitted unexpectedly.
            // This proves we didn't get an intermediate loading state.
            ensureAllEventsConsumed()

            // Verify the use case was called
            coVerify { imagesUseCase.getImages() }
        }
    }

    @Test
    fun `GetImages intent should emit error state when use case returns failure`() = runTest {
        // Arrange
        val errorMessage = "Failed to fetch images"
        val failedResource = Resource.Failed<List<Image>>(errorMessage)
        coEvery { imagesUseCase.getImages() } returns flowOf(failedResource)

        // Act
        val vm = CanvasViewModel(imagesUseCase)

        // Assert
        vm.state.test {
            // As with the success test, the init block completes before collection
            // starts, so we only see the final error state.
            val finalState = awaitItem()

            // Assert properties of this final state
            assertFalse(finalState.isLoading)
            assertEquals(errorMessage, finalState.error)
            assertTrue(finalState.images.isEmpty())

            ensureAllEventsConsumed()
            coVerify { imagesUseCase.getImages() }
        }
    }

    @Test
    fun `StartTempDrag intent should update state with temp drag object`() = runTest {
        // Arrange
        val vm = CanvasViewModel(imagesUseCase)
        val resId = 123
        val startOffset = androidx.compose.ui.geometry.Offset(100f, 100f)

        // Act
        vm.sendIntent(CanvasViewIntent.StartTempDrag(resId, startOffset))

        // Assert
        vm.state.test {
            val state = awaitItem() // Get the latest state
            assertNotNull(state.tempDrag)
            assertEquals(resId, state.tempDrag?.resId)
            assertEquals(100f, state.tempDrag?.x)
        }
    }
}
