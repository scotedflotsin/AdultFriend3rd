package com.adultfriendbella.adultfre

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adultfriendbella.adultfre.Utils.CustomIntent
import com.adultfriendbella.adultfre.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.NetworkInterface

class Splash_Screen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Show the progress bar initially
        binding.progressBar.visibility = android.view.View.VISIBLE

        // Delayed task to check app settings and perform security checks
        Handler(Looper.getMainLooper()).postDelayed({
            performSecurityChecks()
        }, 2000)
    }

    private fun performSecurityChecks() {
        CoroutineScope(Dispatchers.IO).launch {
            val isVPN = isUsingVPN()
            val country = getCountryFromIP()

            if (isVPN) {
                showAlertDialog("VPN Detected", "Please disable VPN to use this app.")
            } else if (country == "China") {
                showAlertDialog("Access Denied", "This app is not available in China.")
            } else {
                checkAppSettings() // Proceed to check Firestore settings
            }
        }
    }

    // Check for VPN usage
    private fun isUsingVPN(): Boolean {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in networkInterfaces) {
                if (networkInterface.isUp && (networkInterface.name.contains("tun") || networkInterface.name.contains(
                        "pptp"
                    ) || networkInterface.name.contains("ppp"))
                ) {
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    // Get country from IP using a free API
    private fun getCountryFromIP(): String? {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://ip-api.com/json") // Free GeoIP API
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.use { // Automatically closes the response after use
                if (response.isSuccessful) {
                    val responseBody = it.body() // Use body() method to get the response body
                    val responseData = responseBody?.string() // Extract the body as a string
                    if (!responseData.isNullOrEmpty()) {
                        val json = JSONObject(responseData)
                        json.getString("country") // Extract the country name
                    } else {
                        null // Return null if the response body is empty
                    }
                } else {
                    null // Return null for unsuccessful responses
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null in case of exceptions
        }
    }


    private fun showAlertDialog(title: String, message: String) {
        runOnUiThread {
            AlertDialog.Builder(this@Splash_Screen)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Exit") { _, _ ->
                    finish() // Exit the app
                }
                .show()
        }
    }

    private fun checkAppSettings() {
        // Fetch app settings from Firestore
        db.collection("appsettings").document("uc6WOxI1dsgZkJk30KJo")
            .get()
            .addOnSuccessListener { document ->
                binding.progressBar.visibility = android.view.View.GONE
                if (document.exists()) {
                    val redirectUrl = document.getString("redirectUrl") ?: ""
                    val defaultPassword = document.getString("defaultPassword") ?: ""
                    val currentStatus = document.get("currentStatus") ?: false

                    if (currentStatus.toString() == "true") {
                        openWebViewActivity(redirectUrl)
                    } else {
                        openWelcomeActivity()
                    }
                } else {
                    openWelcomeActivity()
                }
            }
            .addOnFailureListener {
                binding.progressBar.visibility = android.view.View.GONE
                openWelcomeActivity()
            }
    }

    private fun openWebViewActivity(redirectUrl: String) {
        val intent = Intent(this@Splash_Screen, RedirectActivity::class.java)
        intent.putExtra("url", redirectUrl)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun openWelcomeActivity() {
        val isUserExist = FirebaseAuth.getInstance().currentUser
        if (isUserExist != null) {
            welcome()
        } else {
            showUGCDialog()
        }
    }

    private fun showUGCDialog() {
        // Inflate the custom layout
        val dialogView = layoutInflater.inflate(R.layout.ugc_dailog, null)

        // Create the AlertDialog
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setView(dialogView)

        val dialog = builder.create()

        // Get references to the buttons in the custom layout
        val agreeButton = dialogView.findViewById<TextView>(R.id.agree_button)
        val disagreeButton = dialogView.findViewById<TextView>(R.id.disagree_button)
        val viewPolicyButton = dialogView.findViewById<TextView>(R.id.view_policy_button)
        val messageView = dialogView.findViewById<TextView>(R.id.dialog_message)
        val message = """
    Welcome to our app! To maintain a positive community environment, you need to agree to the following core terms:

    1. Comply with laws and regulations. Do not post illegal or inappropriate content.
    2. You are responsible for the content you upload. The platform has the right to delete violating content.
    3. Do not spread false information, harass others, or infringe on third-party rights.

    Click "View Full Terms" to read more details, or "Agree and continue" to accept the terms.
""".trimIndent()
        messageView.text = message
        // Set button actions
        agreeButton.setOnClickListener {
            dialog.dismiss()
            welcome()
        }

        disagreeButton.setOnClickListener {
            finish()
            dialog.dismiss()
        }

        viewPolicyButton.setOnClickListener {
            openLinkInBrowser("https://sites.google.com/view/userss-ugc-policy/home")
        }

        dialog.show()
    }


    fun welcome() {
        val customIntent = CustomIntent()
        customIntent.createIntent(this@Splash_Screen, Welcome::class.java.name)
        finish()
    }

    private fun openLinkInBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this@Splash_Screen, "Invalid URL", Toast.LENGTH_SHORT).show()
        }
    }
}
