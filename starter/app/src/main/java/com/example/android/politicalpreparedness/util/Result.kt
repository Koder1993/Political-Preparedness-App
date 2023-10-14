package com.example.android.politicalpreparedness.util

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val errorMessage: String) : Result<Nothing>()
}
