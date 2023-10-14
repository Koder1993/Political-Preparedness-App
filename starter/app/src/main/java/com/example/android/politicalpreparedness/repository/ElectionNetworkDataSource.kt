package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.CivicsHttpClient
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ElectionNetworkDataSource @Inject constructor(
    private val retrofitService: CivicsApiService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun getUpcomingElections(): ElectionResponse {
        return withContext(dispatcher) {
            retrofitService.getElections(CivicsHttpClient.API_KEY)
        }
    }

    suspend fun getVoterInfo(address: String, electionId: String): VoterInfoResponse {
        return withContext(dispatcher) {
            CivicsApi.retrofitService.getVoterInfo(CivicsHttpClient.API_KEY, address, electionId)
        }
    }
}