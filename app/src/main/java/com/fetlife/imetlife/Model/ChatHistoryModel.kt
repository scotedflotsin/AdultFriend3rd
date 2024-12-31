package com.fetlife.imetlife.Model

data class ChatHistoryModel(
    val profileImage: String,  // URL for the profile image
    val username: String,
    val lastMessage: String,
    val unreadCount: Int,
    val lastTime: String,
    val userId:String
)
