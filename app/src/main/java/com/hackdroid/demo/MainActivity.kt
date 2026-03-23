package com.hackdroid.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.hackdroid.demo.navigation.AppNavigation
import com.hackdroid.demo.ui.theme.HackDroidTheme
import com.hackdroid.demo.ui.theme.NavyBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HackDroidTheme {
                AppNavigation()
            }
        }
    }
}
