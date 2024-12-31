package com.fetlife.imetlife.Utils

import android.app.Activity
import android.content.Intent

class CustomIntent {
    fun createIntent(activity: Activity, className: String) {
        try {
            val intent = Intent(activity, Class.forName(className))
            activity.startActivity(intent)
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } catch (e: Exception) {
            // Ignore the error or log it
            e.printStackTrace()
        }
    }
}
