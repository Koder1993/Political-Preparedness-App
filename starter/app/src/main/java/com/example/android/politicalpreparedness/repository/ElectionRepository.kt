package com.example.android.politicalpreparedness.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.util.Result
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class ElectionRepository @Inject constructor(
    private val electionDao: ElectionDao,
    private val electionNetworkDataSource: ElectionNetworkDataSource,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun getUpcomingElectionsListFromNetwork(): Result<ElectionResponse> =
        withContext(defaultDispatcher) {
            try {
                Result.Success(electionNetworkDataSource.getUpcomingElections())
            } catch (ex: Exception) {
                Timber.d("getUpcomingElectionsListFromNetwork Exception: $ex")
                Result.Failure(ex.localizedMessage ?: "")
            }
        }

    suspend fun getVoterInfoFromNetwork(
        address: String,
        electionId: String
    ): Result<VoterInfoResponse> =
        withContext(defaultDispatcher) {
            try {
                Result.Success(electionNetworkDataSource.getVoterInfo(address, electionId))
            } catch (ex: Exception) {
                Timber.d("getVoterInfoFromNetwork Exception: $ex")
                Result.Failure(ex.localizedMessage ?: "")
            }
        }

    suspend fun getElection(id: Int): Election? =
        withContext(defaultDispatcher) { electionDao.getElectionById(id) }

    fun getSavedElections(): LiveData<List<Election>> = electionDao.getAllElections()

    suspend fun saveElection(election: Election) = withContext(defaultDispatcher) {
        electionDao.saveElection(election)
    }

    suspend fun deleteElection(election: Election) = withContext(defaultDispatcher) {
        electionDao.deleteElection(election)
    }
}