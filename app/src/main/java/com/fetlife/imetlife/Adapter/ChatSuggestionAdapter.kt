package com.fetlife.imetlife.Adapter

import android.content.Context
import android.content.Intent
import com.fetlife.imetlife.Model.ChatSuggestionModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fetlife.imetlife.Chatting
import com.fetlife.imetlife.R

class ChatSuggestionAdapter(
    private val demoDataList: List<ChatSuggestionModel>,
    private val context:Context
) : RecyclerView.Adapter<ChatSuggestionAdapter.ChatSuggestionViewHolder>() {

    // ViewHolder class to hold the views
    class ChatSuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.name)
        val imageView: ImageView = itemView.findViewById(R.id.profile_imagechat)
    }

    // Inflate the item layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatSuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_suggestion_items, parent, false) // Inflate your item layout
        return ChatSuggestionViewHolder(view)
    }

    // Bind the data to the views in each item
    override fun onBindViewHolder(holder: ChatSuggestionViewHolder, position: Int) {
        val chatSuggestionModel = demoDataList[position]

        // Set the text
        holder.textView.text = chatSuggestionModel.name

        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .load(chatSuggestionModel.profile) // Profile image URL
            .placeholder(R.drawable.girl) // Placeholder image
            .error(R.drawable.girl) // Error image in case of failure
            .into(holder.imageView)
        holder.imageView.setOnClickListener{
            val intent = Intent(context, Chatting::class.java)
            intent.putExtra("profileId", chatSuggestionModel.userId) // Passing the profile id to ChatActivity
            intent.putExtra("profileImage", chatSuggestionModel.profile) // Passing the profile image URL to ChatActivity
            intent.putExtra("profileName", chatSuggestionModel.name) // Passing the profile name to ChatActivity
            context.startActivity(intent)
        }


    }

    // Return the total number of items in the list
    override fun getItemCount(): Int {
        return demoDataList.size
    }
}
