package com.adultfriendbella.adultfre.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.adultfriendbella.adultfre.Model.Profile
import com.adultfriendbella.adultfre.R


    class AutomaticAdapter(private val profiles: List<Profile>) :
        RecyclerView.Adapter<AutomaticAdapter.AutomaticViewHolder>() {

        class AutomaticViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val profileImage: ImageView = itemView.findViewById(R.id.profileImage)
            val profileName: TextView = itemView.findViewById(R.id.profileName)
            val profileAge: TextView = itemView.findViewById(R.id.profileAge)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutomaticViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.like_profile_item, parent, false)
            return AutomaticViewHolder(view)
        }

        override fun onBindViewHolder(holder: AutomaticViewHolder, position: Int) {
            val profile = profiles[position]
            holder.profileName.text = profile.name
            holder.profileAge.text = "${profile.age} years old"

            // Load image with a library like Glide or Picasso
            Glide.with(holder.itemView.context)
                .load(profile.imageUrl)
                .into(holder.profileImage)
        }

        override fun getItemCount() = profiles.size
    }

