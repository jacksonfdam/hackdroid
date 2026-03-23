package com.hackdroid.demo.vulns

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackdroid.demo.ui.theme.*

/**
 * INTENTIONALLY VULNERABLE: Exported Activity with no permission check.
 *
 * HOW TO HACK:
 *   adb shell am start -n com.hackdroid.demo/.vulns.AdminActivity
 *
 * WHAT HAPPENS:
 *   Admin panel opens directly — login screen completely bypassed.
 *   No authentication or permission was required.
 *
 * OWASP M3: Insecure Authentication
 */
class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HackDroidTheme {
                AdminPanelContent(onBack = { finish() })
            }
        }
    }
}

@Composable
private fun AdminPanelContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .statusBarsPadding()
    ) {
        // Warning banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DangerRed.copy(alpha = 0.15f))
                .border(1.dp, DangerRed.copy(alpha = 0.5f))
                .padding(12.dp)
        ) {
            Text(
                text       = "⚠ VULNERABLE — exported=true, no permission",
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
                text      = "← Back",
                fontFamily = JetBrainsMono,
                fontSize  = 13.sp,
                color     = CyanAccent,
                modifier  = Modifier.clickable { onBack() }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text       = "🔓 Admin Panel",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 28.sp,
                color      = TextPrimary
            )
            Text(
                text       = "Unauthorized Access Demo",
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = DangerRed
            )

            Spacer(Modifier.height(24.dp))

            AdminInfoRow("Status",      "ADMIN ACCESS GRANTED")
            Spacer(Modifier.height(8.dp))
            AdminInfoRow("Auth method", "NONE — bypassed via exported Activity")
            Spacer(Modifier.height(8.dp))
            AdminInfoRow("User",        "admin")
            Spacer(Modifier.height(8.dp))
            AdminInfoRow("Session",     "sess_admin_bypass_demo")

            Spacer(Modifier.height(24.dp))

            // Terminal log
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
                    text       = "$ adb shell am start -n com.hackdroid.demo/.vulns.AdminActivity",
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = CyanAccent
                )
                Text(
                    text       = "Starting: Intent { cmp=com.hackdroid.demo/.vulns.AdminActivity }",
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = TextSecondary
                )
                Text(
                    text       = "✓ Launched via exported Activity — no auth required",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 11.sp,
                    color      = CyanAccent
                )
                Text(
                    text       = "⚠ Login screen completely bypassed",
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = WarnAmber
                )
                Text(text = "$ _", fontFamily = JetBrainsMono, fontSize = 11.sp, color = CyanAccent)
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text       = "In a real app, this Activity would be behind a login screen. " +
                             "By setting exported=true with no android:permission, any app or " +
                             "ADB command can launch it directly.",
                fontFamily = Inter,
                fontSize   = 13.sp,
                color      = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AdminInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateCard)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text       = label,
            fontFamily = JetBrainsMono,
            fontSize   = 11.sp,
            color      = TextTertiary,
            modifier   = Modifier.width(110.dp)
        )
        Text(
            text       = value,
            fontFamily = JetBrainsMono,
            fontSize   = 11.sp,
            color      = CyanAccent
        )
    }
}
