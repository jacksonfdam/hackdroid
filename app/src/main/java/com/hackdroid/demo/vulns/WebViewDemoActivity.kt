package com.hackdroid.demo.vulns

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

/**
 * INTENTIONALLY VULNERABLE: WebView with an exposed JavaScript bridge.
 *
 * HOW TO HACK:
 *   1. Launch this activity (via ExploitLab demo)
 *   2. Use the buttons in the WebView to call native methods via JS
 *   3. Alternatively, load a malicious HTML page that calls Android.*
 *
 * WHAT HAPPENS:
 *   Any JavaScript executing in the WebView can call:
 *     Android.readFile(path)    → reads arbitrary files
 *     Android.getPackageName()  → returns app package name
 *     Android.showToast(msg)    → shows a Toast notification
 *
 * OWASP M4: Insufficient Input/Output Validation
 * OWASP M8: Security Misconfiguration
 */
class WebViewDemoActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)

        // INTENTIONALLY VULNERABLE: JavaScript enabled + bridge added
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess   = true  // INTENTIONALLY VULNERABLE

        // INTENTIONALLY VULNERABLE: addJavascriptInterface exposes native code to JS
        webView.addJavascriptInterface(AndroidBridge(this), "Android")

        webView.webViewClient = WebViewClient()

        // Load the demo HTML from assets
        webView.loadUrl("file:///android_asset/webview_demo.html")

        setContentView(webView)
    }
}

/**
 * INTENTIONALLY VULNERABLE JavaScript bridge.
 * All methods annotated @JavascriptInterface are callable from any JS in the WebView.
 */
class AndroidBridge(private val context: Context) {

    /**
     * Reads a file from the filesystem and returns its contents.
     * VULNERABLE: Allows reading any file accessible to the app process.
     */
    @JavascriptInterface
    fun readFile(path: String): String {
        return try {
            File(path).readText()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    /**
     * Returns the app's package name.
     * VULNERABLE: Leaks app identity to untrusted JS.
     */
    @JavascriptInterface
    fun getPackageName(): String = context.packageName

    /**
     * Shows a Toast message.
     * VULNERABLE: Allows JS to display arbitrary messages to the user.
     */
    @JavascriptInterface
    fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
    }

    /**
     * Returns the app's internal files directory path.
     * VULNERABLE: Reveals internal storage structure to JS.
     */
    @JavascriptInterface
    fun getFilesDir(): String = context.filesDir.absolutePath

    /**
     * Returns the SharedPreferences file path.
     * VULNERABLE: Combined with readFile(), allows full credential theft.
     */
    @JavascriptInterface
    fun getDataDir(): String = context.applicationInfo.dataDir
}
