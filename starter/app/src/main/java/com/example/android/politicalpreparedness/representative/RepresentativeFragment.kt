package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class RepresentativeFragment : Fragment() {

    val representativeViewModel: RepresentativeViewModel by viewModels()
    private lateinit var binding: FragmentRepresentativeBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var locationPermissionResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermissionGranted ->
            if (isPermissionGranted) {
                enableDeviceLocation()
            } else {
                Snackbar.make(
                    binding.root, R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
                ).show()
            }
        }

    private val enableLocationLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            getLocation()
        } else {
            Snackbar.make(
                binding.root, R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        //TODO: Add Constant for Location request
    }

    //TODO: Declare ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_representative, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = representativeViewModel

        setupEventListeners()

        return binding.root
    }

    private fun setupEventListeners() {
        binding.stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                representativeViewModel.updateStateSelection(binding.stateSpinner.selectedItem as String)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // not needed
            }
        }

        val adapter = RepresentativeListAdapter()
        binding.representativesRecyclerView.adapter = adapter
        representativeViewModel.representativesListLiveData.observe(viewLifecycleOwner) { representativesList ->
            adapter.submitList(representativesList)
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            checkLocationPermissions()
        }
        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            representativeViewModel.fetchRepresentativesUsingFields()
        }
    }

    private fun checkLocationPermissions() {
        if (isLocationPermissionGranted()) {
            // permission granted, check device location
            Timber.i("checkLocationPermissions, isLocationPermissionGranted true")
            enableDeviceLocation()
        } else {
            // permission not granted, show location permission dialog
            Timber.i("checkLocationPermissions, isLocationPermissionGranted false")
            locationPermissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("VisibleForTests")
    private fun enableDeviceLocation() {
        val locationRequest = LocationRequest.create().apply {
            this.priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val locationSettingsRequestBuilder =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val locationServicesSettingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            locationServicesSettingsClient.checkLocationSettings(locationSettingsRequestBuilder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    enableLocationLauncher.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            } else {
                Snackbar.make(
                    binding.root, R.string.location_required_error, Snackbar.LENGTH_LONG
                ).show()
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }

    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val lastLocation = fusedLocationProviderClient.lastLocation

        lastLocation.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val taskResult = task.result
                taskResult?.run {
                    Timber.d("location: $this")
                    val address = geoCodeLocation(this)
                    address?.let {
                        representativeViewModel.fetchRepresentativesUsingCurrentLocation(it)
                    }
                }
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address? {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)?.map { address ->
                Timber.d("address: $address")
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }?.first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }
}