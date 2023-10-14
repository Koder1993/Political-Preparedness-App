package com.example.android.politicalpreparedness.election

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.repository.ElectionRepository
import com.example.android.politicalpreparedness.util.BaseViewModel
import com.example.android.politicalpreparedness.util.Constants
import com.example.android.politicalpreparedness.util.Result
import com.example.android.politicalpreparedness.util.toVoterInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class VoterInfoViewModel @Inject constructor(
    private val electionRepository: ElectionRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

    companion object {
        private const val TEST_STATE = "la"
    }

    private var _voterInfoLiveData = MutableLiveData<VoterInfo>()
    val voterInfoLiveData: LiveData<VoterInfo>
        get() = _voterInfoLiveData

    private var _isSavedElectionLiveData = MutableLiveData<Boolean>()
    val isSavedElectionLiveData: LiveData<Boolean>
        get() = _isSavedElectionLiveData

    val openURLLiveData = MutableLiveData<String?>()

    val election: Election =
        savedStateHandle[Constants.KEY_NAV_ARG_ELECTION] ?: createMockElection()

    init {
        updateElectionState()
        getVoterInfoData()
    }

    private fun updateElectionState() {
        viewModelScope.launch {
            _isSavedElectionLiveData.value = electionRepository.getElection(election.id) != null
        }
    }

    private fun getVoterInfoData() {
        viewModelScope.launch {
            Timber.d("election: $election")
            val state = election.division.state.ifEmpty { TEST_STATE }
            val address = "${state},${election.division.country}"
            Timber.d("address: $address")
            when (val result =
                electionRepository.getVoterInfoFromNetwork(address, election.id.toString())) {
                is Result.Success -> {
                    _voterInfoLiveData.value = result.data.toVoterInfo()
                }

                is Result.Failure -> {
                    _voterInfoLiveData.value = createMockVoterInfo()
                    _showSnackbar.value = context.getString(R.string.error_voter_info)
                }
            }
        }
    }

    fun onURLSelected(urlToOpen: String) {
        openURLLiveData.value = urlToOpen
    }

    fun onElectionStateChanged() {
        viewModelScope.launch {
            if (isSavedElectionLiveData.value == true) {
                electionRepository.deleteElection(election)
            } else {
                electionRepository.saveElection(election)
            }
            updateElectionState()
        }
    }

    private fun createMockElection(): Election {
        val division = Division("-1", "India", "Delhi")
        return Election(-1, "Mock election", Calendar.getInstance().time, division)
    }

    private fun createMockVoterInfo(): VoterInfo = VoterInfo(
        "", "", "", "", "")
}