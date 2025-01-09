package com.fetlifenew.imetlife

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fetlifenew.chatapp.ChatAdapter
import com.fetlifenew.imetlife.Model.Message
import com.fetlifenew.imetlife.Utils.CToast
import com.fetlifenew.imetlife.databinding.ActivityChattingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import java.util.UUID

class Chatting : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityChattingBinding
    private val db = FirebaseFirestore.getInstance()
    private val messages = mutableListOf<Message>()
    private lateinit var chatAdapter: ChatAdapter
    private var chatId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // Retrieve Intent data
        val uid = intent.getStringExtra("profileId") ?: ""
        val img = intent.getStringExtra("profileImage") ?: ""
        val name = intent.getStringExtra("profileName") ?: ""
        chatId = generateChatId(uid) // Unique chat ID for the conversation

        // Update chat header with user details
        updateHeaderData(img, name)
        binding.chatInput.requestFocus()
        // Set up RecyclerView
        setupRecyclerView()

        // Listen for messages
        listenForMessages()

        // Set up Send button functionality
        binding.send.setOnClickListener {
            val messageContent = binding.chatInput.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                sendMessage(messageContent)
                binding.chatInput.text.clear() // Clear input field after sending
            }
        }

        // Back button functionality
        binding.backbutton.setOnClickListener { finish() }
    }

    private fun generateChatId(partnerId: String): String {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return if (currentUserId < partnerId) {
            "$currentUserId$partnerId"
        } else {
            "$partnerId$currentUserId"
        }
    }

    private fun updateHeaderData(img: String, name: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.chatname.text = name

        val imageUrlWithCacheBust = "$img?cacheBust=${UUID.randomUUID()}"
        Picasso.get()
            .load(imageUrlWithCacheBust)
            .into(binding.profileImagechat, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    binding.progressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.progressBar.visibility = View.GONE
                    Log.e("Chatting", "Error loading profile image: ${e?.message}")
                }
            })
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messages)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Chatting).apply {
                stackFromEnd = true // This makes the most recent message appear at the bottom
            }
            adapter = chatAdapter
        }
    }

    private fun listenForMessages() {
        // Use chatId to filter messages between the two users
        db.collection("messages")
            .whereEqualTo("chatId", chatId)  // Filter messages by chatId
            .orderBy("timestamp", Query.Direction.ASCENDING)  // Order messages by timestamp
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                // Extract messages from snapshot
                val newMessages = mutableListOf<Message>()
                for (doc in snapshot?.documents ?: emptyList()) {
                    val message = doc.toObject(Message::class.java)
                    message?.let { newMessages.add(it) }
                }

                // Update the adapter with the new messages
                updateMessages(newMessages)
            }
    }

    private fun sendMessage(messageContent: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val timestamp = System.currentTimeMillis()

        val message = Message(
            senderId = currentUserId,
            receiverId = intent.getStringExtra("profileId") ?: "",
            content = messageContent,
            timestamp = timestamp,
            isRead = false,
            chatId = chatId // Add chatId to the message
        )

        // Log message data before sending
        Log.d("Chattingx", "Sending message: $message")

        val messageRef = db.collection("messages").document()
        messageRef.set(message)
            .addOnSuccessListener {
                //  CToast().showToast(this@Chatting, "Message saved")
                listenForMessages()
            }
            .addOnFailureListener { e ->
                Log.e("Chatting", "Error sending message: ${e.message}")
                CToast().showToast(this@Chatting, "Failed to send message!")
            }
    }

    private fun updateMessages(newMessages: List<Message>) {
        chatAdapter.updateMessages(newMessages) // Update messages in the adapter
        binding.recyclerView.scrollToPosition(newMessages.size - 1)  // Scroll to the latest message
    }
}
