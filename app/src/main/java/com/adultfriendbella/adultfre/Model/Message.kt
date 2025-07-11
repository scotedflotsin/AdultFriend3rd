package com.adultfriendbella.adultfre.Model

data class Message(
    val content: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = 0L,
    val chatId: String = "" // Added chatId to uniquely identify the conversation
)
