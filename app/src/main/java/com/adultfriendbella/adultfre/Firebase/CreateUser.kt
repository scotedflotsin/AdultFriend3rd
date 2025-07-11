package com.adultfriendbella.adultfre.Firebase

import android.content.Context
import android.net.Uri
import com.adultfriendbella.adultfre.Utils.SaveUserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CreateUser {

    fun createAccountAndSaveDetails(
        context: Context,
        email: String,
        password: String,
        username: String,
        gender: String,
        find: String,
        age: String,
        about: String,
        imgUri: Uri,
        callback: (Boolean, String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Upload image to Firebase Storage
                        val storageRef = storage.reference.child("user_images/$userId.jpg")
                        val uploadTask = storageRef.putFile(imgUri)

                        uploadTask.addOnSuccessListener {
                            // Get the image download URL
                            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                val userDetails = hashMapOf(
                                    "userId" to userId,
                                    "email" to email,
                                    "password" to password,
                                    "username" to username,
                                    "gender" to gender,
                                    "find" to find,
                                    "age" to age,
                                    "about" to about,
                                    "imageUrl" to downloadUri.toString(),
                                    "fetch" to "secNo"
                                )
                                firestore.collection("users").document(userId)
                                    .set(userDetails)
                                    .addOnSuccessListener {
                                        SaveUserDetails().saveUserDetailsLocally(
                                            context,
                                            email,
                                            password,
                                            username,
                                            gender,
                                            find,
                                            age,
                                            about,
                                            downloadUri.toString(), // Save image URL locally
                                            "true"
                                        )
                                        callback(true, "Account created successfully!")
                                    }
                                    .addOnFailureListener { e ->
                                        callback(false, "Error saving user details: ${e.message}")
                                    }
                            }.addOnFailureListener { e ->
                                callback(false, "Error getting download URL: ${e.message}")
                            }
                        }.addOnFailureListener { e ->
                            callback(false, "Error uploading image: ${e.message}")
                        }
                    } else {
                        callback(false, "Error retrieving user ID.")
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Account creation failed."
                    callback(false, errorMessage)
                }
            }
    }
}
