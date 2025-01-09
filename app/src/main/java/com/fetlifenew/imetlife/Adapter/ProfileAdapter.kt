package com.fetlifenew.imetlife.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.fetlifenew.imetlife.Chatting
import com.fetlifenew.imetlife.DetailProfileView
import com.fetlifenew.imetlife.Model.Profile
import com.fetlifenew.imetlife.R
import kotlin.math.abs

class ProfileAdapter(
    private var profiles: MutableList<Profile>,
    private val context: Context,
    private val viewPager: ViewPager2 // Correctly pass the ViewPager2 instance
) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
        val profileName: TextView = itemView.findViewById(R.id.profileName)
        val profileAge: TextView = itemView.findViewById(R.id.profileAge)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val btnChat: ImageButton = itemView.findViewById(R.id.btnMessage)
        val btnLike: ImageButton = itemView.findViewById(R.id.btnLike)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.profile_item_layout, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val profile = profiles[position]
        holder.profileName.text = profile.name
        holder.profileAge.text = "${profile.age} years old"

        Glide.with(holder.itemView.context)
            .load(profile.imageUrl)
            .into(holder.profileImage)

        holder.btnDelete.setOnClickListener {
            // Check if we're at the last item
            if (position < profiles.size - 1) {
                // Switch to the next profile if it's not the last profile
                val nextItem = position + 1
                viewPager.setCurrentItem(nextItem, true) // Smooth transition to the next profile
            } else {
                // If it's the last profile, show a message or do something
                Toast.makeText(context, "You have reached the last profile!", Toast.LENGTH_SHORT).show()
            }

            // Set up page transformation for animations
            viewPager.setPageTransformer(ViewPager2.PageTransformer { page, position ->
                if (position < -1) {
                    page.alpha = 0f
                } else if (position <= 1) {
                    page.scaleX = (1 - abs(position.toDouble())).toFloat()
                    page.scaleY = (1 - abs(position.toDouble())).toFloat()
                }
            })
        }




        // Handle chat button click (open new activity)
        holder.btnChat.setOnClickListener {
            val intent = Intent(context, Chatting::class.java)
            intent.putExtra("profileId", profile.userId) // Passing the profile id to ChatActivity
            intent.putExtra("profileImage", profile.imageUrl) // Passing the profile image URL to ChatActivity
            intent.putExtra("profileName", profile.name) // Passing the profile name to ChatActivity
            context.startActivity(intent)
        }

        // Handle like button click (show a toast)
        holder.btnLike.setOnClickListener {
            Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show()
            // Check if we're at the last item
            if (position < profiles.size - 1) {
                // Switch to the next profile if it's not the last profile
                val nextItem = position + 1
                viewPager.setCurrentItem(nextItem, true) // Smooth transition to the next profile
            } else {
                // If it's the last profile, show a message or do something
                Toast.makeText(context, "You have reached the last profile!", Toast.LENGTH_SHORT).show()
            }

            // Set up page transformation for animations
            viewPager.setPageTransformer(ViewPager2.PageTransformer { page, position ->
                if (position < -1) {
                    page.alpha = 0f
                } else if (position <= 1) {
                    page.scaleX = (1 - abs(position.toDouble())).toFloat()
                    page.scaleY = (1 - abs(position.toDouble())).toFloat()
                }
            })
        }
            holder.profileImage.setOnClickListener{
                val intent = Intent(context, DetailProfileView::class.java)
                intent.putExtra("profileId", profile.userId) // Passing the profile id to ChatActivity
                intent.putExtra("profileImage", profile.imageUrl) // Passing the profile image URL to ChatActivity
                intent.putExtra("profileName", profile.name) // Passing the profile name to ChatActivity
                intent.putExtra("profileAbout", profile.about) // Passing the profile name to ChatActivity
                intent.putExtra("profileAge", profile.age) // Passing the profile name to ChatActivity
                context.startActivity(intent)
            }
    }


    override fun getItemCount() = profiles.size

    // Method to add more profiles to the list
    fun addProfiles(newProfiles: List<Profile>) {
        profiles.addAll(newProfiles)  // Add new profiles to the list
        notifyDataSetChanged()  // Notify the adapter about the data change
    }

}
