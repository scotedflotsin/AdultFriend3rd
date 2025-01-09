package com.fetlifenew.imetlife.Utils

import android.content.Context
import android.content.SharedPreferences
class SaveUserDetails {

    fun saveUserDetailsLocally(
        context: Context,
        email: String,
        password: String,
        username: String,
        gender: String,
        find: String,
        age: String,
        about: String,
        imageBase64: String,
        isAccepted:String
    ) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("email", email)
        editor.putString("password", password)
        editor.putString("username", username)
        editor.putString("gender", gender)
        editor.putString("find", find)
        editor.putString("age", age)
        editor.putString("about", about)
        editor.putString("imageBase64", imageBase64)
        editor.putString("ugcPolicyIsAccepted",isAccepted)

        editor.apply() // Commit changes
    }
    fun getUserDetailsLocally(context: Context): Map<String, String?> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

        return mapOf(
            "email" to sharedPreferences.getString("email", null),
            "password" to sharedPreferences.getString("password", null),
            "username" to sharedPreferences.getString("username", null),
            "gender" to sharedPreferences.getString("gender", null),
            "find" to sharedPreferences.getString("find", null),
            "age" to sharedPreferences.getString("age", null),
            "about" to sharedPreferences.getString("about", null),
            "imageBase64" to sharedPreferences.getString("imageBase64", null),
            "ugcPolicyIsAccepted" to sharedPreferences.getString("ugcPolicyIsAccepted", null)

        )
    }
    fun clearUserDetailsLocally(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear()  // This will clear all data in the SharedPreferences
        editor.apply()  // Commit changes
    }

}

