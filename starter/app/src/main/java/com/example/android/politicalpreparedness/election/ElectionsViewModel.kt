package com.example.android.politicalpreparedness.election

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.util.BaseViewModel
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.android.politicalpreparedness.util.Result
import dagger.hilt.android.qualifiers.ApplicationContext

//TODO: Construct ViewModel and provide election datasource
@HiltViewModel
@SuppressLint("StaticFieldLeak")
class ElectionsViewModel @Inject constructor(
    private val electionRepository: ElectionRepository,
    @ApplicationContext private val context: Context
): BaseViewModel() {

    private val _upcomingElectionsLiveData = MutableLiveData<List<Election>>()
    val upcomingElectionsLiveData: LiveData<List<Election>>
        get() = _upcomingElectionsLiveData

    val savedElectionsLiveData = electionRepository.getSavedElections()

    init {
        refreshUpcomingElectionsList()
    }

    private fun refreshUpcomingElectionsList() {
        _showLoadingLiveData.value = true
        viewModelScope.launch {
            when (val result = electionRepository.getUpcomingElectionsListFromNetwork()) {
                is Result.Success -> _upcomingElectionsLiveData.value = result.data.elections
                is Result.Failure -> {
                    _upcomingElectionsLiveData.value = emptyList()
                    _showSnackbar.value = context.getString(R.string.error_elections)
                }
            }
            _showLoadingLiveData.value = false
        }
    }
}