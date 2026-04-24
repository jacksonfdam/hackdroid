package com.hackdroid.demo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hackdroid.demo.ui.theme.*

private data class DefenseItem(
    val title: String,
    val description: String,
    val isHighlighted: Boolean = false
)

private val defenseItems = listOf(
    DefenseItem(
        title       = "Lock Exported Components",
        description = "Set android:exported=\"false\" on all Activities, Services, Receivers, and Providers " +
                      "unless they explicitly need to receive external intents. Add android:permission for those that do.",
        isHighlighted = false
    ),
    DefenseItem(
        title       = "Validate Deep Link Params",
        description = "Validate every parameter received via deep links or intent extras. Use an allowlist. " +
                      "Never perform financial or account operations directly from URL-supplied data.",
        isHighlighted = false
    ),
    DefenseItem(
        title       = "Secure WebViews",
        description = "Disable JavaScript by default with setJavaScriptEnabled(false). Remove addJavascriptInterface() " +
                      "where possible. Only load content from trusted, allowlisted origins. Never load user-supplied URLs.",
        isHighlighted = true
    ),
    DefenseItem(
        title       = "Migrate to DataStore — and Encrypt It",
        description = "SharedPreferences is legacy — synchronous, no type safety, no coroutines. Migrate to Jetpack DataStore. " +
                      "But DataStore alone is not encrypted: wrap it with EncryptedFile or use EncryptedSharedPreferences for sensitive keys. " +
                      "Most developers do neither. Never store tokens, session IDs, or PII unencrypted. " +
                      "Use Android Keystore for all cryptographic key material.",
        isHighlighted = false
    ),
    DefenseItem(
        title       = "Use Parameterized Queries",
        description = "Always use parameterized queries (? placeholders) or Room @Query parameter binding. " +
                      "Never concatenate user input into SQL strings. Validate and sanitize all ContentProvider selection args.",
        isHighlighted = false
    ),
    DefenseItem(
        title       = "Assume APK Will Be Reversed",
        description = "Never hardcode secrets, API keys, or credentials in source. Use server-side APIs. " +
                      "Enable R8/ProGuard obfuscation. Design your security model assuming an attacker has full source access.",
        isHighlighted = false
    ),
    DefenseItem(
        title       = "Restrict Broadcast Receivers",
        description = "Add android:permission to all exported receivers. Use LocalBroadcastManager for internal broadcasts. " +
                      "Prefer explicit intents for intra-app communication. Validate the intent source in onReceive().",
        isHighlighted = false
    )
)

@Composable
fun DefenseGuideScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .statusBarsPadding()
    ) {
        // Header
        Column(
            modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
        ) {
            Text(
                text       = "Defense Guide",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = "> 7 defenses. Assume breach.",
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = CyanAccent
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            defenseItems.forEach { item ->
                DefenseCard(item = item)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DefenseCard(item: DefenseItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (item.isHighlighted) CyanAccent else SlateCard)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text       = "✓  ${item.title}",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 14.sp,
                color      = if (item.isHighlighted) TextInverted else CyanAccent
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text       = item.description,
                fontFamily = Inter,
                fontSize   = 12.sp,
                color      = if (item.isHighlighted) TextInverted.copy(alpha = 0.85f) else TextTertiary,
                lineHeight = 17.sp
            )
        }
    }
}
