package com.example.android.politicalpreparedness.util

import android.widget.Button
import androidx.databinding.BindingAdapter
import com.example.android.politicalpreparedness.R

@BindingAdapter("followElectionText")
fun Button.bindFollowText(isFollowingElection: Boolean) {
    text = if (isFollowingElection) {
        context.getString(R.string.unfollow_election_button_text)
    } else {
        context.getString(R.string.follow_election_button)
    }
}
