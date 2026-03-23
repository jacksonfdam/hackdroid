package com.hackdroid.demo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Corner radius tokens (dp)
object Radius {
    const val Small  = 4
    const val Medium = 8
    const val Card   = 12
    const val List   = 10
    const val Pill   = 36
}

// Single dark theme — no light variant
private val DarkColorScheme = darkColorScheme(
    background        = NavyBackground,
    surface           = SlateCard,
    surfaceVariant    = InsetSurface,
    primary           = CyanAccent,
    onPrimary         = TextInverted,
    onBackground      = TextPrimary,
    onSurface         = TextPrimary,
    error             = DangerRed,
    secondary         = TextSecondary,
    onSecondary       = TextPrimary,
    tertiary          = TextTertiary,
    outline           = TextMuted,
    scrim             = TerminalBlack
)

@Composable
fun HackDroidTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = HackDroidTypography,
        content     = content
    )
}
