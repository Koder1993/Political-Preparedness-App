package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.network.models.Election
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ElectionsFragment: Fragment() {

    private val electionsViewModel: ElectionsViewModel by viewModels()
    private lateinit var binding: FragmentElectionBinding
    private lateinit var electionsAdapter: ElectionListAdapter
    private lateinit var savedElectionsAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?)
    : View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_election,
            container, false
        )

        binding.lifecycleOwner = this
        binding.viewModel = electionsViewModel

        electionsAdapter = ElectionListAdapter(object: ElectionListener {
            override fun onItemClicked(election: Election) {
                navigateToVoterFragment(election)
            }
        })
        binding.upcomingElectionsRecyclerview.adapter = electionsAdapter

        savedElectionsAdapter = ElectionListAdapter(object: ElectionListener {
            override fun onItemClicked(election: Election) {
                navigateToVoterFragment(election)
            }
        })
        binding.savedElectionsRecyclerview.adapter = savedElectionsAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        electionsViewModel.upcomingElectionsLiveData.observe(viewLifecycleOwner) { elections ->
            electionsAdapter.submitList(elections)
        }
        electionsViewModel.savedElectionsLiveData.observe(viewLifecycleOwner) { elections ->
            savedElectionsAdapter.submitList(elections)
        }

        electionsViewModel.showSnackbar.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
        }
    }

    private fun navigateToVoterFragment(election: Election) {
        findNavController().navigate(
            ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                election
            )
        )
    }
}