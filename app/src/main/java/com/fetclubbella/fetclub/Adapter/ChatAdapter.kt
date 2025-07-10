package com.fetclubbella.chatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fetclubbella.fetclub.Model.Message
import com.fetclubbella.fetclub.R
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val messageList: MutableList<Message>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // ViewHolder class for binding views
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receivedMessage: TextView = itemView.findViewById(R.id.revived)
        val sentMessage: TextView = itemView.findViewById(R.id.send)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_recycler, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messageList[position]

        // Check if the message is sent by the current user or the other user
        if (message.senderId == currentUserId) {
            // This is a sent message, display it on the right
            holder.sentMessage.visibility = View.VISIBLE
            holder.sentMessage.text = message.content
            holder.receivedMessage.visibility = View.GONE
        } else {
            // This is a received message, display it on the left
            holder.receivedMessage.visibility = View.VISIBLE
            holder.receivedMessage.text = message.content
            holder.sentMessage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    // Method to update the list of messages
    fun updateMessages(newMessages: List<Message>) {
        messageList.clear()
        messageList.addAll(newMessages)
        notifyDataSetChanged()  // Notify adapter of changes
    }
}
