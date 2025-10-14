package com.shutterfly.imagecanvas.domain.model

/**
 * Sealed class representing the result of an resource fetch operation.
 */
sealed class Resource<T> {
    class Success<T>(val data: T) : Resource<T>()
    class Failed<T>(val message: String) : Resource<T>()
}