package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.CivicsHttpClient
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepresentativeNetworkDataSource @Inject constructor(
    private val retrofitService: CivicsApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getRepresentativeList(address: String): RepresentativeResponse {
        return withContext(dispatcher) {
            retrofitService.getRepresentativeList(CivicsHttpClient.API_KEY, address)
        }
    }
}