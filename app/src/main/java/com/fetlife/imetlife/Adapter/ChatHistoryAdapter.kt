package com.fetlife.imetlife.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fetlife.imetlife.Chatting
import com.fetlife.imetlife.Model.ChatHistoryModel
import com.fetlife.imetlife.R
import com.fetlife.imetlife.databinding.ChatItemBinding


class ChatHistoryAdapter(private val chatList: List<ChatHistoryModel>, private val context: android.content.Context) :
    RecyclerView.Adapter<ChatHistoryAdapter.ChatViewHolder>() {

    // ViewHolder to hold item views
    class ChatViewHolder(val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]
        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(chatItem.profileImage)
            .placeholder(R.drawable.profile) // Set a placeholder image
            .into(holder.binding.profileImagechat)

        // Set text data for the views
        holder.binding.username.text = chatItem.username
        holder.binding.lastmessage.text = chatItem.lastMessage
        holder.binding.unreadnotificationcount.text = chatItem.unreadCount.toString()
        holder.binding.lasttime.text = chatItem.lastTime
  holder.binding.max.setOnClickListener{
      val intent = Intent(context, Chatting::class.java)
      intent.putExtra("profileId", chatItem.userId) // Passing the profile id to ChatActivity
      intent.putExtra("profileImage", chatItem.profileImage) // Passing the profile image URL to ChatActivity
      intent.putExtra("profileName", chatItem.username) // Passing the profile name to ChatActivity
      context.startActivity(intent)
  }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}
