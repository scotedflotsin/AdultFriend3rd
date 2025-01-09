package com.fetlifenew.imetlife.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class FetchUserData {
    fun fetchUserDataByUid(uid: String, onSuccess: (Map<String, Any>) -> Unit, onFailure: (Exception) -> Unit) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    onSuccess(document.data ?: emptyMap()) // Return the user data
                } else {
                    onFailure(Exception("User not found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Handle errors
            }
    }

}