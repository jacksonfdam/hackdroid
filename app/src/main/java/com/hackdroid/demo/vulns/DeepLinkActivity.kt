package com.hackdroid.demo.vulns

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hackdroid.demo.ui.theme.*

/**
 * INTENTIONALLY VULNERABLE: Deep link parameters are read without validation.
 *
 * HOW TO HACK:
 *   adb shell am start -a android.intent.action.VIEW \
 *     -d "hackdroid://transfer?amount=9999&to=attacker"
 *
 * WHAT HAPPENS:
 *   The Activity reads `amount` and `to` directly from the URI and
 *   displays/executes the transfer with attacker-controlled values.
 *
 * OWASP M4: Insufficient Input/Output Validation
 */
class DeepLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // INTENTIONALLY VULNERABLE: Read raw URI parameters with no validation
        val uri    = intent?.data
        val amount = uri?.getQueryParameter("amount") ?: "0"
        val to     = uri?.getQueryParameter("to")     ?: "unknown"

        setContent {
            HackDroidTheme {
                DeepLinkContent(
                    amount = amount,
                    to     = to,
                    rawUri = uri?.toString() ?: "hackdroid://transfer?amount=$amount&to=$to",
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
private fun DeepLinkContent(
    amount: String,
    to: String,
    rawUri: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
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
                text       = "⚠ VULNERABLE — parameters not validated",
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
                text       = "💸 Transfer Initiated",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 28.sp,
                color      = TextPrimary
            )
            Text(
                text       = "Deep Link Injection Demo",
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = DangerRed
            )

            Spacer(Modifier.height(24.dp))

            TransferRow("Amount",    "\$$amount")
            Spacer(Modifier.height(8.dp))
            TransferRow("Recipient", to)
            Spacer(Modifier.height(8.dp))
            TransferRow("Validated", "NO — raw URI data used directly")

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
                    text       = "✓ Deep link received: $rawUri",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 11.sp,
                    color      = CyanAccent
                )
                Text(
                    text       = "amount=$amount",
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = TextSecondary
                )
                Text(
                    text       = "to=$to",
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = TextSecondary
                )
                Text(
                    text       = "⚠ No validation — attacker controls both params",
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
private fun TransferRow(label: String, value: String) {
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
            color      = CyanAccent
        )
    }
}
