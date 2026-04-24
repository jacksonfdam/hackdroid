package com.hackdroid.demo

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hackdroid.demo.navigation.AppNavigation
import com.hackdroid.demo.ui.theme.HackDroidTheme

class MainActivity : ComponentActivity() {

    companion object {
        init {
            // Load Frida Gadget when present (jniLibs/*/libfrida-gadget.so).
            // On non-rooted devices: adb forward tcp:27042 tcp:27042, then
            // frida -H 127.0.0.1:27042 -l script.js (Gadget listens on TCP).
            // Safe to ignore if the .so was not included in the build.
            try {
                System.loadLibrary("frida-gadget")
            } catch (_: UnsatisfiedLinkError) {
                // Gadget not embedded — Frida requires a rooted device or objection-patched APK
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make both status bar and navigation bar fully transparent so the
        // app's NavyBackground (#0A0F1C) shows through on both ends.
        // SystemBarStyle.dark → forces light-colored icons (correct for a dark app).
        enableEdgeToEdge(
            statusBarStyle     = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            HackDroidTheme {
                AppNavigation()
            }
        }
    }
}
