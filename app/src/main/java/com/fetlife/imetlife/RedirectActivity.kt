package com.fetlife.imetlife

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fetlife.imetlife.databinding.ActivityRedirectBinding

class RedirectActivity : AppCompatActivity() {
    lateinit var binding: ActivityRedirectBinding

    // Progress bar for loading indication
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedirectBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        progressBar = binding.progressBar // Ensure you have a ProgressBar in your layout

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Retrieve the URL passed from the Intent
        val webUrl = intent.getStringExtra("url") ?: "https://www.google.com" // Default URL if not passed

        // Get the WebView reference
        val webView: WebView = binding.v3s // Ensure you have a WebView with this ID in your layout

        // Enable edge-to-edge mode for WebView if required
        ViewCompat.setOnApplyWindowInsetsListener(webView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up WebView settings for optimal performance
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // Enable JavaScript
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // Use HTTP cache
        webSettings.domStorageEnabled = true // Enable DOM storage API
        webSettings.loadsImagesAutomatically = true // Automatically load images

        // Set WebViewClient to handle page navigation within WebView (avoid opening in external browser)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                progressBar.visibility = ProgressBar.VISIBLE // Show the progress bar when page starts loading
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = ProgressBar.INVISIBLE // Hide the progress bar when page finishes loading
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                // You can show an error message here if the page fails to load
            }
        }

        // Load the URL passed through the Intent
        webView.loadUrl(webUrl)
    }

    // Optionally handle back navigation within WebView
    override fun onBackPressed() {
        val webView: WebView = findViewById(R.id.v3s)
        if (webView.canGoBack()) {
            webView.goBack()  // Navigate back within WebView
        } else {
           showExitDialog()
        }
    }

    private fun showExitDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Exit App")
        builder.setMessage("Are you sure you want to exit?")

        // Option to close the app
        builder.setPositiveButton("Exit") { dialog, _ ->
            dialog.dismiss()
            finishAffinity() // Close all activities and exit the app
        }

        // Option to stay in the app
        builder.setNegativeButton("Stay") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog and keep the app running
        }

        builder.setCancelable(false) // Prevent dialog from being dismissed by tapping outside
        val dialog = builder.create()
        dialog.show()
    }
}
