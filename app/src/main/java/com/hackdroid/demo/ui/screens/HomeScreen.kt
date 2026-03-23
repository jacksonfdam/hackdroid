package com.hackdroid.demo.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hackdroid.demo.data.Severity
import com.hackdroid.demo.data.VulnerabilityData
import com.hackdroid.demo.navigation.Screen
import com.hackdroid.demo.ui.theme.*

@Composable
fun HomeScreen(navController: NavController) {
    val vulns = VulnerabilityData.all

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Hero section ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            CyanAccent.copy(alpha = 0.08f),
                            NavyBackground
                        ),
                        radius = 800f
                    )
                )
                .padding(top = 52.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Column {
                Text(
                    text          = "> ANDROID_HACKING_LAB v1.0",
                    fontFamily    = JetBrainsMono,
                    fontSize      = 11.sp,
                    color         = CyanAccent,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text       = "HackDroid",
                    fontFamily = JetBrainsMono,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 32.sp,
                    color      = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = "7 vulnerabilities · 7 defenses · live demos",
                    fontFamily = Inter,
                    fontSize   = 13.sp,
                    color      = TextTertiary
                )
                Spacer(Modifier.height(16.dp))
                // Status badges
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge("✓ ROOTED")
                    StatusBadge("◐ ADB CONNECTED")
                }
            }
        }

        // ── Stats row ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("7", "VULNS", isCyan = true, modifier = Modifier.weight(1f))
            StatCard("4", "TOOLS", modifier = Modifier.weight(1f))
            StatCard("3", "DEMOS", modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        // ── Quick Access ───────────────────────────────────────────────────
        SectionLabel("QUICK ACCESS", modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickCard(
                icon    = "⚡",
                title   = "Exploit Lab",
                filled  = true,
                modifier = Modifier.weight(1f)
            ) { navController.navigate(Screen.ExploitLab.createRoute("exported_components")) }

            QuickCard(
                icon    = "🛡",
                title   = "Defense Guide",
                modifier = Modifier.weight(1f)
            ) { navController.navigate(Screen.DefenseGuide.route) }

            QuickCard(
                icon    = "⚙",
                title   = "Toolkit",
                modifier = Modifier.weight(1f)
            ) { navController.navigate(Screen.Toolkit.route) }
        }

        Spacer(Modifier.height(24.dp))

        // ── Vulnerability Modules ─────────────────────────────────────────
        SectionLabel("VULNERABILITY MODULES", modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            vulns.forEachIndexed { index, vuln ->
                val isSelected = index == 0
                VulnRow(
                    title      = vuln.title,
                    subtitle   = vuln.subtitle,
                    severity   = vuln.severity,
                    isSelected = isSelected
                ) { navController.navigate(Screen.VulnDetail.createRoute(vuln.id)) }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun StatusBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(SlateCard)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text          = text,
            fontFamily    = JetBrainsMono,
            fontSize      = 11.sp,
            color         = CyanAccent,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    isCyan: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCard)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text       = value,
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.Bold,
            fontSize   = 24.sp,
            color      = if (isCyan) CyanAccent else TextPrimary
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text          = label,
            fontFamily    = JetBrainsMono,
            fontSize      = 10.sp,
            color         = TextTertiary,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun QuickCard(
    icon: String,
    title: String,
    filled: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (filled) CyanAccent else SlateCard)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(Modifier.height(6.dp))
        Text(
            text       = title,
            fontFamily = Inter,
            fontWeight = FontWeight.SemiBold,
            fontSize   = 12.sp,
            color      = if (filled) TextInverted else TextPrimary,
            maxLines   = 1,
            overflow   = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun VulnRow(
    title: String,
    subtitle: String,
    severity: Severity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dotColor = when (severity) {
        Severity.CRITICAL -> DangerRed
        Severity.HIGH     -> WarnAmber
        Severity.MEDIUM   -> TextSecondary
        Severity.LOW      -> TextMuted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) InsetSurface else SlateCard
            )
            .run {
                if (isSelected) {
                    this.then(
                        Modifier.background(
                            InsetSurface,
                            shape = RoundedCornerShape(10.dp)
                        )
                    )
                } else this
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dot
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(if (isSelected) CyanAccent else dotColor)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = title,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = if (isSelected) CyanAccent else TextPrimary
            )
            Text(
                text       = subtitle,
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = if (isSelected) CyanAccent.copy(alpha = 0.7f) else TextTertiary
            )
        }
        Text(
            text  = "›",
            fontSize = 18.sp,
            color = if (isSelected) CyanAccent else TextMuted
        )
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text          = text,
        fontFamily    = JetBrainsMono,
        fontSize      = 10.sp,
        color         = TextTertiary,
        letterSpacing = 2.sp,
        modifier      = modifier
    )
}
