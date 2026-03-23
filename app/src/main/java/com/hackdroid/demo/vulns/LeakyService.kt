package com.hackdroid.demo.vulns

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * INTENTIONALLY VULNERABLE: Exported service that logs sensitive data to Logcat.
 *
 * HOW TO HACK:
 *   adb shell am startservice -n com.hackdroid.demo/.vulns.LeakyService
 *
 * HOW TO SEE THE LEAK:
 *   adb logcat | grep HackDroid_LEAK
 *
 * WHAT HAPPENS:
 *   Session tokens, email, and hardcoded API keys are written to Logcat
 *   readable by any app holding READ_LOGS permission (or via ADB).
 *
 * OWASP M1: Improper Credential Usage
 * OWASP M9: Insecure Data Storage (Logcat is persistent on some devices)
 */
class LeakyService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // INTENTIONALLY VULNERABLE: Sensitive data logged to Logcat
        Log.d("HackDroid_LEAK", "=== SESSION DATA DUMP ===")
        Log.d("HackDroid_LEAK", "SESSION_TOKEN: eyJhbGciOiJIUzI1NiJ9.demo_token_leak")
        Log.d("HackDroid_LEAK", "USER_EMAIL: victim@example.com")
        Log.d("HackDroid_LEAK", "USER_ID: usr_12345")
        Log.d("HackDroid_LEAK", "API_KEY: sk_live_DEMO_HARDCODED_KEY_1234")
        Log.d("HackDroid_LEAK", "REFRESH_TOKEN: rt_demo_refresh_token_secret")
        Log.d("HackDroid_LEAK", "=== END DUMP ===")

        // Also log via VULN tag for broader demo
        Log.w("HackDroid_VULN", "LeakyService started via exported component — secrets logged above")

        stopSelf()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
