package com.example.android.politicalpreparedness.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(): ViewModel() {
    protected val _showLoadingLiveData = MutableLiveData<Boolean>()
    val showLoadingLiveData: LiveData<Boolean>
        get() = _showLoadingLiveData

    protected val _showSnackbar = MutableLiveData<String>()
    val showSnackbar: LiveData<String>
        get() = _showSnackbar
}