package com.fetlifenew.imetlife.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fetlifenew.imetlife.Adapter.ChatHistoryAdapter
import com.fetlifenew.imetlife.Adapter.ChatSuggestionAdapter
import com.fetlifenew.imetlife.Firebase.FetchProfiles
import com.fetlifenew.imetlife.Model.ChatHistoryModel
import com.fetlifenew.imetlife.Model.ChatSuggestionModel
import com.fetlifenew.imetlife.Model.Message
import com.fetlifenew.imetlife.Utils.SaveUserDetails
import com.fetlifenew.imetlife.databinding.FragmentChatBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment() {

    private lateinit var DataList: List<ChatHistoryModel>
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private var currentPage = 1
    private val itemsPerPage = 1000
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataList = listOf()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        binding.chatProfile.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        fetchProfilesAndUpdateAdapter()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding when the fragment's view is destroyed
    }

    private fun fetchProfilesAndUpdateAdapter() {

        if (!isAdded || _binding == null) return

        binding.progressBar.visibility = View.VISIBLE

        val userDetails = SaveUserDetails().getUserDetailsLocally(requireContext())
        val genderFilter = if (userDetails["find"] == "Guy") "Male" else "Female"

        FetchProfiles().fetchProfiles(
            genderFilter = genderFilter,
            limit = itemsPerPage,
            page = currentPage
        ) { profiles, error ->

            if (!isAdded || _binding == null) return@fetchProfiles

            binding.progressBar.visibility = View.GONE
            if (error != null) {
                Log.e("FetchProfiles", "Error: $error")
            } else {

                val shuffledProfiles = profiles?.shuffled()

                val chatSuggestionList = shuffledProfiles?.map { profile ->
                    ChatSuggestionModel(
                        name = profile.name,
                        profile = profile.imageUrl,
                        userId = profile.userId
                    )
                } ?: emptyList()

                val adapter = ChatSuggestionAdapter(chatSuggestionList, requireContext())
                binding.chatProfile.adapter = adapter

                val usersInvolved = profiles?.map { it.userId }?.toSet() ?: emptySet()
                val newMessages = listOf<Message>() // You should fetch the actual messages

                getUserProfilesAndLastMessage(usersInvolved, newMessages)
            }
        }
    }

    private fun getUserProfilesAndLastMessage(
        usersInvolved: Set<String>,
        newMessages: List<Message>
    ) {
        val chatHistoryList = mutableListOf<ChatHistoryModel>()

        usersInvolved.forEach { userId ->
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (!isAdded || _binding == null) return@addOnSuccessListener

                    val userName = document.getString("username") ?: ""
                    val userImage = document.getString("imageUrl") ?: ""
                    val userId = document.getString("userId") ?: ""

                    val lastMessage = newMessages.filter { message ->
                        message.senderId == userId || message.receiverId == userId
                    }.maxByOrNull { it.timestamp }

                    val lastTime = lastMessage?.timestamp?.let { formatTimestamp(it) } ?: "11:23"

                    val unreadCount = newMessages.count { message ->
                        (message.receiverId == userId && !message.isRead)
                    }

                    val chatItem = ChatHistoryModel(
                        profileImage = userImage,
                        username = userName,
                        lastMessage = lastMessage?.content ?: "",
                        unreadCount = unreadCount,
                        lastTime = lastTime,
                        userId = userId
                    )

                    chatHistoryList.add(chatItem)

                    if (chatHistoryList.size == usersInvolved.size) {

                        binding.chatGistory.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.VERTICAL,
                            false
                        )

                        val adapter = ChatHistoryAdapter(chatHistoryList, requireContext())
                        binding.chatGistory.adapter = adapter
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Chatting", "Error fetching user profile: ${e.message}")
                }
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val date =
            Date(timestamp)
        val formatter = SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault())
        return formatter.format(date)
    }
}
