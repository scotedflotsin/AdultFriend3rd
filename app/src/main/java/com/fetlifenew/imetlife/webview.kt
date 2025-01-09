package com.fetlifenew.imetlife

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WebviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_webview)

        // Retrieve the URL passed from the Intent
        val webUrl = intent.getStringExtra("url") ?: "https://www.example.com"  // Default URL if not passed

        // Get the WebView reference
        val webView: WebView = findViewById(R.id.v3s) // Ensure you have a WebView with this ID in your layout

        // Enable edge-to-edge mode for WebView if required
        ViewCompat.setOnApplyWindowInsetsListener(webView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up WebView settings for optimal performance
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true // Enable JavaScript
       // webSettings.setAppCacheEnabled(false) // Don't use AppCache
        webSettings.cacheMode = WebSettings.LOAD_DEFAULT // Use HTTP cache
        webSettings.domStorageEnabled = true // Enable DOM storage API
        webSettings.loadsImagesAutomatically = true // Automatically load images

        // Set WebViewClient to handle page navigation within WebView (avoid opening in external browser)
        webView.webViewClient = WebViewClient()

        // Load the URL passed through the Intent
        webView.loadUrl(webUrl)
    }

    // Optionally handle back navigation within WebView
    override fun onBackPressed() {
        val webView: WebView = findViewById(R.id.v3s)
        if (webView.canGoBack()) {
            webView.goBack()  // Navigate back within WebView
        } else {
            super.onBackPressed()  // Exit Activity if no back history
        }
    }
}
