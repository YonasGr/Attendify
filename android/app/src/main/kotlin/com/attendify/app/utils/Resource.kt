package com.attendify.app.utils

/**
 * A sealed class representing the state of a network request
 * Used to handle loading, success, and error states in ViewModels and UI
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
