package com.example.android.politicalpreparedness.representative

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.RepresentativeRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.util.BaseViewModel
import com.example.android.politicalpreparedness.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class RepresentativeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val representativeRepository: RepresentativeRepository
) : BaseViewModel() {

    private var _representativesListLiveData = MutableLiveData<List<Representative>>(emptyList())
    val representativesListLiveData: LiveData<List<Representative>>
        get() = _representativesListLiveData

    val addressLine1 = MutableLiveData<String>()
    val addressLine2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<String>()

    private val currentAddress: String
        get() = Address(
            addressLine1.value ?: "",
            addressLine2.value ?: "",
            city.value ?: "",
            state.value ?: "",
            zip.value ?: ""
        ).toFormattedString()

    fun fetchRepresentativesUsingFields() {
        validateAndFetchRepresentativeList()
    }

    fun fetchRepresentativesUsingCurrentLocation(address: Address) {
        addressLine1.value = address.line1 ?: ""
        addressLine2.value = address.line2 ?: ""
        city.value = address.city
        state.value = address.state
        zip.value = address.zip
        validateAndFetchRepresentativeList()
    }

    private fun fetchRepresentativesList() {
        viewModelScope.launch {
            when (val result =
                representativeRepository.getRepresentativeFromNetwork(currentAddress)) {
                is Result.Success -> _representativesListLiveData.value =
                    result.data.offices.flatMap { office ->
                        office.getRepresentatives(result.data.officials)
                    }

                is Result.Failure -> {
                    _representativesListLiveData.value = emptyList()
                    _showSnackbar.value = context.getString(R.string.error_representatives)
                }
            }
        }
    }

    private fun validateAndFetchRepresentativeList() {
        if (isAddressValid()) {
            fetchRepresentativesList()
        } else {
            _showSnackbar.value =
                context.getString(R.string.error_representatives)
        }
    }

    private fun isAddressValid() = !state.value.isNullOrEmpty()

    fun updateStateSelection(selectedState: String) {
        state.value = selectedState
    }
}
