package com.example.android.politicalpreparedness.network.models

data class VoterInfo (
    val electionName: String,
    val electionInfoUrL: String,
    val votingLocationFinderUrl: String,
    val ballotInfoUrl: String,
    val correspondenceAddress: String
)