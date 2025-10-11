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

    /**
     * Maps a Resource<T> to a Resource<R> by applying a transform function to the data.
     */
    fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data!!))
            is Error -> Error(message ?: "Unknown error", data?.let(transform))
            is Loading -> Loading(data?.let(transform))
        }
    }
}
