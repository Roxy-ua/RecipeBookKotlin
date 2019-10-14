package com.example.tapasoft.recipekotlin.repository

/**
 * Created by udav on 27-Jul-19.
 */
sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}