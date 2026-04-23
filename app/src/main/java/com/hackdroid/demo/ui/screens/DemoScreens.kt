package com.hackdroid.demo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import com.hackdroid.demo.ui.theme.*

// ── Admin Panel Demo (Compose version, mirrors AdminActivity) ────────────────
@Composable
fun AdminPanelScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        BackBar(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            VulnBanner("⚠ VULNERABLE — exported=true, no permission")

            Spacer(Modifier.height(16.dp))
            Text(
                text       = "🔓 Admin Panel",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary
            )
            Text(
                text       = "Unauthorized Access Demo",
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = DangerRed
            )

            Spacer(Modifier.height(20.dp))

            InfoCard("Logged in as", "admin  (no credentials required)")
            Spacer(Modifier.height(10.dp))
            InfoCard("Auth method", "NONE — component was exported")
            Spacer(Modifier.height(10.dp))
            InfoCard("Access level", "FULL ADMIN")

            Spacer(Modifier.height(20.dp))

            TerminalBlock(
                lines = listOf(
                    "$ adb shell am start -n com.hackdroid.demo/.vulns.AdminActivity" to true,
                    "Starting: Intent { cmp=com.hackdroid.demo/.vulns.AdminActivity }" to false,
                    "✓ Launched via exported Activity — no auth required" to true
                )
            )
        }
    }
}

// ── Deep Link Demo ───────────────────────────────────────────────────────────
@Composable
fun DeepLinkDemoScreen(navController: NavController) {
    val amount = "9999"
    val to     = "attacker"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        BackBar(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            VulnBanner("⚠ VULNERABLE — parameters not validated")

            Spacer(Modifier.height(16.dp))
            Text(
                text       = "💸 Transfer Initiated",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary
            )
            Text(
                text       = "Deep Link Injection Demo",
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = DangerRed
            )

            Spacer(Modifier.height(20.dp))

            InfoCard("Amount",    "\$$amount  (attacker-controlled)")
            Spacer(Modifier.height(10.dp))
            InfoCard("Recipient", to)
            Spacer(Modifier.height(10.dp))
            InfoCard("Validated", "NO — raw intent data used directly")

            Spacer(Modifier.height(20.dp))

            TerminalBlock(
                lines = listOf(
                    """$ adb shell am start -a android.intent.action.VIEW -d "hackdroid://transfer?amount=$amount&to=$to"""" to true,
                    "URI received: hackdroid://transfer?amount=$amount&to=$to" to false,
                    "amount=$amount, to=$to" to false,
                    "✓ Transfer executed with attacker-controlled values" to true,
                    "⚠ No parameter validation performed" to false
                )
            )
        }
    }
}

// ── WebView Demo (Compose placeholder — actual demo uses WebViewDemoActivity) ─
@Composable
fun WebViewDemoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        BackBar(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            VulnBanner("⚠ VULNERABLE — addJavascriptInterface() enabled")

            Spacer(Modifier.height(16.dp))
            Text(
                text       = "🌐 WebView JS Bridge",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary
            )

            Spacer(Modifier.height(12.dp))
            Text(
                text       = "The WebViewDemoActivity loads webview_demo.html which has full " +
                             "access to native Android code via the addJavascriptInterface() bridge.",
                fontFamily = Inter,
                fontSize   = 13.sp,
                color      = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(20.dp))

            TerminalBlock(
                lines = listOf(
                    "# WebView with JS bridge enabled" to false,
                    "webView.settings.javaScriptEnabled = true" to true,
                    "webView.addJavascriptInterface(bridge, \"Android\")" to true,
                    "# Any JS on the page can now call native methods" to false,
                    "# Android.readFile('/data/data/...')" to false,
                    "# Android.getPackageName()" to false,
                    "# Android.showToast('Hacked!')" to false
                )
            )
        }
    }
}

// ── Storage Demo ─────────────────────────────────────────────────────────────
@Composable
fun StorageDemoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        BackBar(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            VulnBanner("⚠ VULNERABLE — stored in plain text, no encryption")

            Spacer(Modifier.height(16.dp))
            Text(
                text       = "📂 Insecure Storage",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary
            )

            Spacer(Modifier.height(20.dp))

            InfoCard("auth_token",  "eyJhbGciOiJIUzI1NiJ9.demo_token_leak")
            Spacer(Modifier.height(10.dp))
            InfoCard("user_email",  "victim@example.com")
            Spacer(Modifier.height(10.dp))
            InfoCard("session_id",  "sess_abc123_plaintext")
            Spacer(Modifier.height(10.dp))
            InfoCard("Storage",     "Plain SharedPreferences (XML)")

            Spacer(Modifier.height(20.dp))

            TerminalBlock(
                lines = listOf(
                    "$ adb shell run-as com.hackdroid.demo \\" to true,
                    "    cat .../shared_prefs/auth_prefs.xml" to true,
                    "<string name=\"auth_token\">eyJhbG...</string>" to true,
                    "<string name=\"user_email\">victim@example.com</string>" to true,
                    "<string name=\"session_id\">sess_abc123_plaintext</string>" to true,
                    "⚠ No root needed — works on any debug build" to false
                )
            )
        }
    }
}

// ── SQLi Demo ────────────────────────────────────────────────────────────────
@Composable
fun SqliDemoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        BackBar(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            VulnBanner("⚠ VULNERABLE — raw SQL string concatenation")

            Spacer(Modifier.height(16.dp))
            Text(
                text       = "💉 SQL Injection",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary
            )

            Spacer(Modifier.height(20.dp))

            InfoCard("id=1", "alice | alice@example.com | tok_alice_secret")
            Spacer(Modifier.height(10.dp))
            InfoCard("id=2", "bob | bob@example.com | tok_bob_secret")

            Spacer(Modifier.height(20.dp))

            TerminalBlock(
                lines = listOf(
                    "$ adb shell content query \\" to true,
                    "    --uri content://com.hackdroid.demo.provider/users \\" to true,
                    "    --where \"1=1\"" to true,
                    "Executing: SELECT * FROM users WHERE 1=1" to false,
                    "Row: id=1, name=alice, token=tok_alice_secret" to true,
                    "Row: id=2, name=bob, token=tok_bob_secret" to true,
                    "✓ Full table dump — classic SQL injection" to true
                )
            )
        }
    }
}

// ── Frida Demo ───────────────────────────────────────────────────────────────
@Composable
fun FridaDemoScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        BackBar(navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(4.dp))
            Text(
                text       = "🔧 Frida Hook",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary
            )
            Text(
                text       = "Bypass Root Detection — Runtime Hook",
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = CyanAccent
            )

            Spacer(Modifier.height(16.dp))
            Text(
                text       = "Frida attaches to the running process and hooks the isRooted() " +
                             "method, forcing it to always return false regardless of device state.",
                fontFamily = Inter,
                fontSize   = 13.sp,
                color      = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(20.dp))

            TerminalBlock(
                lines = listOf(
                    "$ frida -U -f com.hackdroid.demo --no-pause -l bypass_root_detection.js" to true,
                    "[HackDroid] Frida attached" to false,
                    "[HackDroid] Hooking RootChecker.isRooted()..." to false,
                    "[HackDroid] isRooted() called — returning false" to true,
                    "[HackDroid] Root detection bypassed ✓" to true
                )
            )

            Spacer(Modifier.height(16.dp))
            SectionLabel("SCRIPT: bypass_root_detection.js")
            Spacer(Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(TerminalBlack)
                    .padding(14.dp)
            ) {
                listOf(
                    "Java.perform(function() {",
                    "    var RootCheck = Java.use(",
                    "        \"com.hackdroid.demo.security.RootChecker\"",
                    "    );",
                    "    RootCheck.isRooted.implementation = function() {",
                    "        console.log(\"[HackDroid] isRooted() — returning false\");",
                    "        return false;",
                    "    };",
                    "    console.log(\"[HackDroid] Root bypass active ✓\");",
                    "});"
                ).forEach { line ->
                    Text(
                        text       = line,
                        fontFamily = JetBrainsMono,
                        fontSize   = 11.sp,
                        color      = CyanAccent,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

// ── Shared composables ───────────────────────────────────────────────────────

@Composable
private fun BackBar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text      = "← Back",
            fontFamily = JetBrainsMono,
            fontSize  = 13.sp,
            color     = CyanAccent,
            modifier  = Modifier.clickable { navController.popBackStack() }
        )
    }
}

@Composable
private fun VulnBanner(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DangerRed.copy(alpha = 0.12f))
            .border(1.dp, DangerRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text       = text,
            fontFamily = JetBrainsMono,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Bold,
            color      = DangerRed
        )
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
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
            modifier   = Modifier.width(100.dp)
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

@Composable
private fun TerminalBlock(lines: List<Pair<String, Boolean>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TerminalBlack)
            .border(1.dp, CyanAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        lines.forEach { (text, isCyan) ->
            Text(
                text       = text,
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = if (isCyan) CyanAccent else TextSecondary,
                lineHeight = 16.sp
            )
        }
        Text(
            text       = "$ _",
            fontFamily = JetBrainsMono,
            fontSize   = 11.sp,
            color      = CyanAccent
        )
    }
}
