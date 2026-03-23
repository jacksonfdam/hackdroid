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
