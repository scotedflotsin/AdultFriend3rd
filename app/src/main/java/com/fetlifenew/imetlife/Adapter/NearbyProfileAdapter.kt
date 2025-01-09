package com.fetlifenew.imetlife.Adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fetlifenew.imetlife.DetailProfileView

import com.fetlifenew.imetlife.Model.Profile
import com.fetlifenew.imetlife.R

class NearbyProfileAdapter(
    private val profileList: MutableList<Profile>, // Changed to MutableList for dynamic updates
    private val context: Context
) : RecyclerView.Adapter<NearbyProfileAdapter.NearbyProfileViewHolder>() {

    class NearbyProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val profileName: TextView = itemView.findViewById(R.id.profileName)
        val profileAge: TextView = itemView.findViewById(R.id.profileAge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reycler_profile_item, parent, false)
        return NearbyProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: NearbyProfileViewHolder, position: Int) {
        val profile = profileList[position]

        // Load image from URL using Glide
        Glide.with(holder.itemView.context)
            .load(profile.imageUrl)
            .placeholder(R.drawable.girl) // Optional placeholder
            .error(R.drawable.girl) // Optional error image
            .into(holder.profileImage)

        holder.profileName.text = profile.name
        holder.profileAge.text = "${profile.age} years old"
        holder.profileImage.setOnClickListener{
            val intent = Intent(context, DetailProfileView::class.java)
            intent.putExtra("profileId", profile.userId) // Passing the profile id to ChatActivity
            intent.putExtra("profileImage", profile.imageUrl) // Passing the profile image URL to ChatActivity
            intent.putExtra("profileName", profile.name) // Passing the profile name to ChatActivity
            intent.putExtra("profileAbout", profile.about) // Passing the profile name to ChatActivity
            intent.putExtra("profileAge", profile.age) // Passing the profile name to ChatActivity
            context.startActivity(intent)
        }
        // Apply scale and fade animation for each item
        holder.itemView.animate()
            .scaleX(0.95f) // Slight scale-down effect
            .scaleY(0.95f) // Slight scale-down effect
            .alpha(0.8f)  // Fade out a bit
            .setDuration(200) // Duration for scale and fade-out
            .withEndAction {
                // Scale back to normal and fade-in
                holder.itemView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)  // Fully visible again
                    .setDuration(200)  // Duration for scale and fade-in
                    .start()
            }
            .start()
    }

    override fun getItemCount(): Int {
        return profileList.size
    }

    /**
     * Adds new profiles to the list and notifies the adapter to refresh the view.
     */
    fun addProfiles(newProfiles: List<Profile>) {
        val startPosition = profileList.size
        profileList.addAll(newProfiles)
        notifyItemRangeInserted(startPosition, newProfiles.size)
    }
}
