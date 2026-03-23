package com.hackdroid.demo.vulns

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackdroid.demo.ui.theme.*

/**
 * INTENTIONALLY VULNERABLE: Stores auth tokens and PII in plain SharedPreferences.
 *
 * HOW TO HACK:
 *   adb pull /data/data/com.hackdroid.demo/shared_prefs/auth_prefs.xml
 *   cat auth_prefs.xml
 *
 * WHAT HAPPENS:
 *   The auth token, user email, and session ID are stored unencrypted.
 *   On a debug build or rooted device, ADB can pull and read the XML directly.
 *
 * OWASP M9: Insecure Data Storage
 * OWASP M6: Inadequate Privacy Controls
 */
class InsecureStorageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // INTENTIONALLY VULNERABLE: Store sensitive data in plain SharedPreferences
        val prefs = getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("auth_token",   "eyJhbGciOiJIUzI1NiJ9.demo_token_leak")
            putString("user_email",   "victim@example.com")
            putString("user_id",      "usr_12345")
            putString("session_id",   "sess_abc123_plaintext")
            putString("api_key",      "sk_live_DEMO_HARDCODED_KEY_1234")
            putBoolean("is_admin",    false)
        }.apply()

        val storedToken   = prefs.getString("auth_token", "") ?: ""
        val storedEmail   = prefs.getString("user_email", "") ?: ""
        val storedSession = prefs.getString("session_id", "") ?: ""

        setContent {
            HackDroidTheme {
                InsecureStorageContent(
                    token   = storedToken,
                    email   = storedEmail,
                    session = storedSession,
                    onBack  = { finish() }
                )
            }
        }
    }
}

@Composable
private fun InsecureStorageContent(
    token: String,
    email: String,
    session: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DangerRed.copy(alpha = 0.15f))
                .border(1.dp, DangerRed.copy(alpha = 0.5f))
                .padding(12.dp)
        ) {
            Text(
                text       = "⚠ VULNERABLE — stored in plain text, no encryption",
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                fontWeight = FontWeight.Bold,
                color      = DangerRed
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text     = "← Back",
                fontFamily = JetBrainsMono,
                fontSize = 13.sp,
                color    = CyanAccent,
                modifier = Modifier.clickable { onBack() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text       = "📂 Insecure Storage",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 28.sp,
                color      = TextPrimary
            )
            Text(
                text       = "Plain SharedPreferences Demo",
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = DangerRed
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text       = "These values are stored at:\n/data/data/com.hackdroid.demo/shared_prefs/auth_prefs.xml",
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = TextTertiary,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(20.dp))

            StorageRow("auth_token",   token)
            Spacer(Modifier.height(8.dp))
            StorageRow("user_email",   email)
            Spacer(Modifier.height(8.dp))
            StorageRow("session_id",   session)
            Spacer(Modifier.height(8.dp))
            StorageRow("api_key",      "sk_live_DEMO_HARDCODED_KEY_1234")
            Spacer(Modifier.height(8.dp))
            StorageRow("Storage type", "Plain SharedPreferences (XML)")

            Spacer(Modifier.height(24.dp))

            // ADB command
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TerminalBlack)
                    .border(1.dp, CyanAccent.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text       = "$ adb pull /data/data/com.hackdroid.demo/shared_prefs/",
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = CyanAccent
                )
                Text(
                    text       = "pull: auth_prefs.xml",
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = TextSecondary
                )
                Text(
                    text       = "1 file pulled, 0 skipped.",
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = TextSecondary
                )
                Text(
                    text       = "<string name=\"auth_token\">$token</string>",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 11.sp,
                    color      = CyanAccent
                )
                Text(
                    text       = "⚠ Credentials readable without root on debug builds",
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = WarnAmber
                )
                Text(text = "$ _", fontFamily = JetBrainsMono, fontSize = 11.sp, color = CyanAccent)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StorageRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateCard)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text       = key,
            fontFamily = JetBrainsMono,
            fontSize   = 11.sp,
            color      = TextTertiary,
            modifier   = Modifier.width(110.dp)
        )
        Text(
            text       = value,
            fontFamily = JetBrainsMono,
            fontSize   = 11.sp,
            color      = CyanAccent,
            modifier   = Modifier.weight(1f)
        )
    }
}
