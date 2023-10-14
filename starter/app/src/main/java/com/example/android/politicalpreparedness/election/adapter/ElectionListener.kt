package com.example.android.politicalpreparedness.election.adapter

import com.example.android.politicalpreparedness.network.models.Election

interface ElectionListener {
    fun onItemClicked(election: Election)
}