package com.adultfriendbella.adultfre

import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.adultfriendbella.adultfre.databinding.ActivityRedirectBinding

class RedirectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRedirectBinding
    private var maxReloadAttempts = 5 // Maximum number of reload attempts
    private var reloadAttempts = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedirectBinding.inflate(layoutInflater)
        setContentView(binding.root)
val  url = intent.getStringExtra("url");
        if (url != null) {
            setupWebView()
            binding.v3s.loadUrl(url)
            binding.v3s.loadUrl(url)

        }
    }

    private fun setupWebView() {
        val webView: WebView = binding.v3s
        val placeholderLayout: View = binding.placeholderLayout // Reference to placeholder
        val progressBar: ProgressBar = binding.progressBar
        webView.settings.userAgentString =
            "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.152 Mobile Safari/537.36"

        // Enable essential settings
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            loadsImagesAutomatically = true
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                placeholderLayout.visibility = View.VISIBLE // Show placeholder
                webView.visibility = View.GONE // Hide WebView while loading
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                placeholderLayout.visibility = View.GONE // Hide placeholder
                webView.visibility = View.VISIBLE // Show WebView when page is loaded
                reloadAttempts = 0 // Reset reload attempts on successful load
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                placeholderLayout.visibility = View.VISIBLE // Show placeholder on error
                webView.visibility = View.GONE // Hide WebView on error
                retryLoadingPage(view)
            }
        }

        // Load URL
//        webView.loadUrl("https://adultfriendfinder.com/go/g1474900-pct?page_id=3011")
//        webView.loadUrl("https://adultfriendfinder.com/go/g1474900-pct?page_id=3011")
    }

    private fun shouldClearCache(): Boolean {
        // Logic to decide if cache should be cleared
        return false
    }

    private fun retryLoadingPage(view: WebView?) {
        if (reloadAttempts < maxReloadAttempts) {
            reloadAttempts++
            android.util.Log.i("WebView Reload", "Retrying page load: Attempt $reloadAttempts")
            view?.reload()

        } else {
            showErrorDialog()
        }
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Loading Error")
        builder.setMessage("The page could not be loaded. Please check your internet connection and try again.")
        builder.setPositiveButton("Retry") { _, _ ->
            reloadAttempts = 0
            binding.v3s.loadUrl("https://adultfriendfinder.com/go/g1474900-pct?page_id=3011")
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun showSslErrorDialog(handler: SslErrorHandler?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("SSL Certificate Error")
        builder.setMessage("The SSL certificate for this site is not valid. Do you want to continue?")
        builder.setPositiveButton("Continue") { _, _ ->
            handler?.proceed()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            handler?.cancel()
            dialog.dismiss()
        }
        builder.create().show()
    }
    override fun onBackPressed() {
        val webView: WebView = binding.v3s
        if (webView.canGoBack()) {
            webView.goBack() // Navigate to the previous page in WebView
        } else {
            // Show confirmation dialog to exit the app
            AlertDialog.Builder(this).apply {
                setTitle("Exit App")
                setMessage("Are you sure you want to exit?")
                setPositiveButton("Yes") { _, _ -> finish() }
                setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                create()
                show()
            }
        }
    }
}
