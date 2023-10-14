package com.example.android.politicalpreparedness.election.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.android.politicalpreparedness.network.models.Election

class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election) = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Election, newItem: Election) = oldItem == newItem
}