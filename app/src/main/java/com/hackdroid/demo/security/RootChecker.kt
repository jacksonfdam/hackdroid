package com.hackdroid.demo.security

import java.io.File

/**
 * INTENTIONALLY NAIVE root detection — for demonstration purposes only.
 * Frida demo: bypass_root_detection.js hooks isRooted() to always return false.
 *
 * HOW TO BYPASS:
 *   frida -U -f com.hackdroid.demo -l bypass_root_detection.js
 */
class RootChecker {

    fun isRooted(): Boolean {
        return checkSuBinary() || checkTestKeys() || checkSuExists()
    }

    private fun checkSuBinary(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su"
        )
        return paths.any { File(it).exists() }
    }

    private fun checkTestKeys(): Boolean {
        val buildTags = android.os.Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkSuExists(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            process.inputStream.available() > 0
        } catch (e: Exception) {
            false
        }
    }
}
