package com.example.android.politicalpreparedness.util

import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

fun VoterInfoResponse.toVoterInfo(): VoterInfo {
    val electionInfo = this.state?.first()?.electionAdministrationBody
    return VoterInfo(
        electionName = electionInfo?.name ?: "",
        electionInfoUrL = electionInfo?.electionInfoUrl ?: "",
        votingLocationFinderUrl = electionInfo?.votingLocationFinderUrl ?: "",
        ballotInfoUrl = electionInfo?.ballotInfoUrl ?: "",
        correspondenceAddress = electionInfo?.correspondenceAddress?.toFormattedString() ?: ""
    )
}