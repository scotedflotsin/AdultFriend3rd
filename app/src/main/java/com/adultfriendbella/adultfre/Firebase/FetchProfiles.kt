package com.adultfriendbella.adultfre.Firebase

import com.adultfriendbella.adultfre.Model.Profile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentSnapshot

class FetchProfiles {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    /**
     * Fetch user profiles based on the specified filter, with randomness and pagination.
     *
     * @param genderFilter The value to filter users by (e.g., "guy" or "girl").
     * @param limit The maximum number of profiles to fetch.
     * @param callback A lambda to handle the result or error.
     */


    var lastVisible: DocumentSnapshot? = null

    fun fetchProfiles(genderFilter: String, limit: Int, page: Int, callback: (List<Profile>?, String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        var query = db.collection("users")
            .whereEqualTo("fetch", "secAF3")
            .orderBy("fetch")  // Order the results by username or another field
            .limit(15.toLong()) // Limit the number of results per page

        // If there is a last visible document (from the previous page), start after that document
        lastVisible?.let {
            // Use the last document snapshot for pagination
            query = query.startAfter(it)
        }

        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val profiles = task.result?.documents?.map { document ->
                    val username = document.getString("username") ?: "Unknown"
                    var age = document.get("age")?.toString() ?: "0"
                    val imageUrl = document.getString("imageUrl") ?: ""
                    val userId = document.getString("userId") ?: ""
                    val about = document.getString("about") ?: ""
                    // convert age into string
                    val ageSt = age.toString()

                    Profile(username, ageSt, imageUrl, userId,about )
                } ?: emptyList()

                // Update the last visible document for the next fetch
                lastVisible = task.result?.documents?.lastOrNull()

                callback(profiles, null)
            } else {
                callback(null, "Failed to fetch profiles")
            }
        }
    }

}


