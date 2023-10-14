package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.util.Result
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RepresentativeRepository @Inject constructor(
    private val representativeNetworkDataSource: RepresentativeNetworkDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getRepresentativeFromNetwork(address: String): Result<RepresentativeResponse> =
        withContext(defaultDispatcher) {
            try {
                val list = representativeNetworkDataSource.getRepresentativeList(address)
                list.officials.forEach {
                    Timber.d("name: ${it.name}, url: ${it.photoUrl}")
                }
                Result.Success(representativeNetworkDataSource.getRepresentativeList(address))
            } catch (ex: Exception) {
                Result.Failure(ex.localizedMessage ?: "")
            }
        }
}