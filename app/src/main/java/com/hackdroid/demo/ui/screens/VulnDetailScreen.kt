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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hackdroid.demo.data.Severity
import com.hackdroid.demo.navigation.Screen
import com.hackdroid.demo.ui.theme.*
import com.hackdroid.demo.viewmodel.HackDroidViewModel

@Composable
fun VulnDetailScreen(vulnId: String, navController: NavController, vm: HackDroidViewModel) {
    val vuln = vm.getVulnById(vulnId) ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .statusBarsPadding()
    ) {
        // ── Top bar ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Severity badge
            val (badgeColor, badgeText) = when (vuln.severity) {
                Severity.CRITICAL -> DangerRed to "CRITICAL SEVERITY"
                Severity.HIGH     -> WarnAmber to "HIGH SEVERITY"
                Severity.MEDIUM   -> WarnAmber.copy(alpha = 0.7f) to "MEDIUM SEVERITY"
                Severity.LOW      -> TextMuted to "LOW SEVERITY"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text          = badgeText,
                    fontFamily    = JetBrainsMono,
                    fontSize      = 11.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = badgeColor,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            // Title
            Text(
                text       = vuln.title,
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = vuln.subtitle,
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = CyanAccent
            )
            Spacer(Modifier.height(12.dp))

            // Description
            Text(
                text       = vuln.description,
                fontFamily = Inter,
                fontSize   = 13.sp,
                color      = TextSecondary,
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(20.dp))

            // ── HOW TO HACK box ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HackBoxBg)
                    .border(1.dp, HackBoxBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⚡", fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text          = "HOW TO HACK",
                            fontFamily    = JetBrainsMono,
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = DangerRed,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    vuln.hackSteps.forEachIndexed { index, step ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text(
                                text       = "${index + 1}.",
                                fontFamily = JetBrainsMono,
                                fontSize   = 12.sp,
                                color      = if (step.isHighlighted) DangerRed else TextTertiary,
                                modifier   = Modifier.width(24.dp)
                            )
                            Text(
                                text       = step.text,
                                fontFamily = JetBrainsMono,
                                fontSize   = 12.sp,
                                color      = if (step.isHighlighted) DangerRed else TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // ── HOW TO PROTECT box ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DefendBoxBg)
                    .border(1.dp, DefendBoxBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🛡", fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text          = "HOW TO PROTECT",
                            fontFamily    = JetBrainsMono,
                            fontSize      = 11.sp,
                            fontWeight    = FontWeight.Bold,
                            color         = CyanAccent,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    vuln.defenseSteps.forEach { step ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text(
                                text       = "✓",
                                fontFamily = JetBrainsMono,
                                fontSize   = 12.sp,
                                color      = if (step.isHighlighted) CyanAccent else TextTertiary,
                                modifier   = Modifier.width(20.dp)
                            )
                            Text(
                                text       = step.text,
                                fontFamily = JetBrainsMono,
                                fontSize   = 12.sp,
                                color      = if (step.isHighlighted) CyanAccent else TextPrimary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            // ADB command box (if present)
            if (vuln.adbCommand != null) {
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TerminalBlack)
                        .padding(14.dp)
                ) {
                    Text(
                        text       = "$ ${vuln.adbCommand}",
                        fontFamily = JetBrainsMono,
                        fontSize   = 11.sp,
                        color      = CyanAccent,
                        lineHeight = 16.sp
                    )
                }
            }

            // ── Run Demo Exploit CTA ───────────────────────────────────────
            if (vuln.demoRoute != null) {
                Spacer(Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyanAccent)
                        .clickable {
                            navController.navigate(Screen.ExploitLab.createRoute(vuln.id))
                        }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text          = "▶  Run Demo Exploit",
                        fontFamily    = JetBrainsMono,
                        fontWeight    = FontWeight.Bold,
                        fontSize      = 14.sp,
                        color         = NavyBackground,
                        textAlign     = TextAlign.Center,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
