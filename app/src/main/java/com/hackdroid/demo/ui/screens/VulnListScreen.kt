package com.hackdroid.demo.ui.screens

import androidx.compose.foundation.background
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
import androidx.navigation.NavController
import com.hackdroid.demo.data.Severity
import com.hackdroid.demo.data.Vulnerability
import com.hackdroid.demo.navigation.Screen
import com.hackdroid.demo.ui.theme.*
import com.hackdroid.demo.viewmodel.HackDroidViewModel

private enum class Filter { ALL, CRITICAL, FIXED }

@Composable
fun VulnListScreen(navController: NavController, vm: HackDroidViewModel) {
    var activeFilter by remember { mutableStateOf(Filter.ALL) }
    val vulns = vm.allVulns

    val filtered = when (activeFilter) {
        Filter.ALL      -> vulns
        Filter.CRITICAL -> vulns.filter { it.severity == Severity.CRITICAL || it.severity == Severity.HIGH }
        Filter.FIXED    -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .statusBarsPadding()
    ) {
        // ── Header ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text       = "Vulnerabilities",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary,
                modifier   = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CyanAccent.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text          = "${vulns.size} total",
                    fontFamily    = JetBrainsMono,
                    fontSize      = 11.sp,
                    color         = CyanAccent
                )
            }
        }

        // ── Segmented filter ───────────────────────────────────────────────
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(InsetSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Filter.values().forEach { filter ->
                val isActive = activeFilter == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isActive) CyanAccent else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable { activeFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Text(
                        text       = filter.name.lowercase().replaceFirstChar { it.uppercase() },
                        fontFamily = JetBrainsMono,
                        fontSize   = 12.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color      = if (isActive) TextInverted else TextSecondary
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Vuln list ──────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SlateCard)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "No vulnerabilities in this category",
                        fontFamily = Inter,
                        fontSize   = 13.sp,
                        color      = TextTertiary
                    )
                }
            } else {
                filtered.forEach { vuln ->
                    VulnListRow(vuln = vuln) {
                        navController.navigate(Screen.VulnDetail.createRoute(vuln.id))
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun VulnListRow(vuln: Vulnerability, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(SlateCard)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Severity badge
        val (badgeColor, badgeText) = when (vuln.severity) {
            Severity.CRITICAL -> DangerRed to "CRITICAL"
            Severity.HIGH     -> WarnAmber to "HIGH"
            Severity.MEDIUM   -> TextSecondary to "MEDIUM"
            Severity.LOW      -> TextMuted to "LOW"
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(badgeColor.copy(alpha = 0.15f))
                .padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Text(
                text          = badgeText,
                fontFamily    = JetBrainsMono,
                fontSize      = 10.sp,
                fontWeight    = FontWeight.Bold,
                color         = badgeColor,
                letterSpacing = 1.sp
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = vuln.title,
                fontFamily = Inter,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = TextPrimary
            )
            Text(
                text       = vuln.subtitle,
                fontFamily = JetBrainsMono,
                fontSize   = 11.sp,
                color      = TextTertiary
            )
        }
        Text(text = "›", fontSize = 18.sp, color = TextMuted)
    }
}
