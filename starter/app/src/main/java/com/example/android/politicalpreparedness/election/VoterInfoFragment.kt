package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class VoterInfoFragment : Fragment() {

    private val voterInfoViewModel: VoterInfoViewModel by viewModels()
    private lateinit var binding: FragmentVoterInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_voter_info, container, false
        )
        binding.lifecycleOwner = this
        binding.viewModel = voterInfoViewModel
        
        createUnderlinedText(binding.stateLocations)
        createUnderlinedText(binding.stateBallot)

        return binding.root
    }

    private fun createUnderlinedText(textView: TextView) {
        textView.paintFlags = textView.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        voterInfoViewModel.openURLLiveData.observe(viewLifecycleOwner) { url ->
            Timber.d("url: $url")
            if (url.isNullOrEmpty()) {
                Snackbar.make(requireView(), "URL not found: network error or incorrect server response", Snackbar.LENGTH_LONG).show()
            } else {
                val viewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                requireActivity().startActivity(viewIntent)
            }
        }

        voterInfoViewModel.showSnackbar.observe(viewLifecycleOwner) {
            Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
        }
    }
}