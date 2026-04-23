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

private data class Tool(
    val name: String,
    val description: String,
    val commands: List<String>
)

private val tools = listOf(
    Tool(
        name        = "ADB",
        description = "Android Debug Bridge — communicate with connected devices/emulators over USB or TCP",
        commands    = listOf(
            "$ adb devices",
            "$ adb shell am start -n pkg/.Activity",
            "$ adb pull /data/data/pkg/shared_prefs/",
            "$ adb shell am broadcast -a com.hackdroid.RESET_AUTH",
            "$ adb logcat | grep HackDroid_LEAK"
        )
    ),
    Tool(
        name        = "FRIDA",
        description = "Runtime hook framework — instrument running Android apps to modify behavior at runtime",
        commands    = listOf(
            "$ frida -U -f com.app -l script.js",
            "$ frida-ps -Ua",
            "$ frida-trace -U -n com.app -m '*[NSString *]'"
        )
    ),
    Tool(
        name        = "JADX",
        description = "APK decompiler — converts .dex bytecode back to readable Java/Kotlin source",
        commands    = listOf(
            "$ jadx -d out/ target.apk",
            "$ jadx-gui target.apk",
            "$ grep -r 'apiKey\\|secret\\|password' out/"
        )
    ),
    Tool(
        name        = "BURP",
        description = "Traffic interceptor — MitM proxy to capture and modify HTTPS traffic from Android apps",
        commands    = listOf(
            "Set proxy: 192.168.x.x:8080",
            "Install Burp cert on device",
            "Intercept HTTPS traffic",
            "Settings → Developer → HTTP proxy"
        )
    ),
    Tool(
        name        = "MobSF",
        description = "Auto scanner — static + dynamic analysis of Android APKs, produces full security report",
        commands    = listOf(
            "$ docker run -it opensecurity/mobile-security-framework-mobsf",
            "Upload APK → static+dynamic scan",
            "Results at localhost:8000",
            "Checks: permissions, exported components, hardcoded secrets"
        )
    )
)

@Composable
fun ToolkitScreen(navController: NavController) {
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
                text       = "Toolkit",
                fontFamily = Inter,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp,
                color      = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = "> Attacker tools reference",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            tools.forEach { tool ->
                ToolCard(tool = tool)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun ToolCard(tool: Tool) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SlateCard)
            .padding(16.dp)
    ) {
        Row {
            Text(
                text          = tool.name,
                fontFamily    = JetBrainsMono,
                fontWeight    = FontWeight.Bold,
                fontSize      = 11.sp,
                color         = CyanAccent,
                letterSpacing = 1.sp
            )
            Text(
                text       = "  — ${tool.description}",
                fontFamily = Inter,
                fontSize   = 13.sp,
                color      = TextSecondary
            )
        }

        Spacer(Modifier.height(10.dp))

        // Command block
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(TerminalBlack)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tool.commands.forEach { cmd ->
                val isCommand = cmd.startsWith("$")
                Text(
                    text       = cmd,
                    fontFamily = JetBrainsMono,
                    fontSize   = 11.sp,
                    color      = if (isCommand) CyanAccent else TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
