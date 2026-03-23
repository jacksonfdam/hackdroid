package com.hackdroid.demo.vulns

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * INTENTIONALLY VULNERABLE: Exported BroadcastReceiver with no permission check.
 *
 * HOW TO HACK:
 *   adb shell am broadcast -a com.hackdroid.RESET_AUTH
 *
 * WHAT HAPPENS:
 *   All SharedPreferences auth data is cleared — victim is logged out.
 *   Any app or ADB can trigger this without any permission.
 *
 * OWASP M8: Security Misconfiguration
 */
class AuthResetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != "com.hackdroid.RESET_AUTH") return

        // INTENTIONALLY VULNERABLE: Clear all auth state without verifying caller identity
        val authPrefs    = context.getSharedPreferences("auth_prefs",    Context.MODE_PRIVATE)
        val sessionPrefs = context.getSharedPreferences("session_prefs", Context.MODE_PRIVATE)

        authPrefs.edit().clear().apply()
        sessionPrefs.edit().clear().apply()

        Log.w("HackDroid_VULN", "AUTH RESET via exported broadcast — victim logged out")
        Log.d("HackDroid_LEAK", "Broadcast received: com.hackdroid.RESET_AUTH — auth state wiped")

        Toast.makeText(
            context,
            "⚠ Auth state cleared via broadcast!",
            Toast.LENGTH_LONG
        ).show()
    }
}
