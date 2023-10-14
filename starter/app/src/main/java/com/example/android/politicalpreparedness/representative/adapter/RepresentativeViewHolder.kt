package com.example.android.politicalpreparedness.representative.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.ItemRepresentativeBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionViewHolder
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeViewHolder(val binding: ItemRepresentativeBinding): RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): RepresentativeViewHolder {
            return RepresentativeViewHolder(
                ItemRepresentativeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    fun bind(item: Representative) {
        binding.representative = item
        binding.representativePhoto.setImageResource(R.drawable.ic_profile)

        showSocialLinks(item.official.channels ?: emptyList())
        showWWWLinks(item.official.urls ?: emptyList())

        binding.executePendingBindings()
    }

    private fun showSocialLinks(channels: List<Channel>) {
        val facebookUrl = getFacebookUrl(channels)
        if (!facebookUrl.isNullOrBlank()) {
            enableLink(binding.facebookIcon, facebookUrl)
        }

        val twitterUrl = getTwitterUrl(channels)
        if (!twitterUrl.isNullOrBlank()) {
            enableLink(binding.twitterIcon, twitterUrl)
        }
    }

    private fun showWWWLinks(urls: List<String>) {
        enableLink(binding.websiteIcon, urls.first())
    }

    private fun getFacebookUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Facebook" }
            .map { channel -> "https://www.facebook.com/${channel.id}" }
            .firstOrNull()
    }

    private fun getTwitterUrl(channels: List<Channel>): String? {
        return channels.filter { channel -> channel.type == "Twitter" }
            .map { channel -> "https://www.twitter.com/${channel.id}" }
            .firstOrNull()
    }

    private fun enableLink(view: ImageView, url: String) {
        view.visibility = View.VISIBLE
        view.setOnClickListener { setIntent(url) }
    }

    private fun setIntent(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        itemView.context.startActivity(intent)
    }
}