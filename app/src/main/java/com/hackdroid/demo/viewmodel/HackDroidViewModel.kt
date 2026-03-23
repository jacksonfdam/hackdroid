package com.hackdroid.demo.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hackdroid.demo.data.VulnerabilityData
import com.hackdroid.demo.data.Vulnerability
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HackDroidViewModel : ViewModel() {

    val allVulns: List<Vulnerability> = VulnerabilityData.all

    val terminalLines = mutableStateListOf<TerminalLine>()
    var isRunning = false
        private set

    data class TerminalLine(
        val text: String,
        val type: LineType
    )

    enum class LineType { COMMAND, OUTPUT, WARNING, SUCCESS, CURSOR, ERROR }

    fun getVulnById(id: String): Vulnerability? = allVulns.find { it.id == id }

    fun runExploit(vuln: Vulnerability) {
        if (isRunning) return
        isRunning = true
        terminalLines.clear()

        viewModelScope.launch {
            // Initial prompt
            terminalLines.add(TerminalLine("$ _", LineType.CURSOR))
            delay(400)
            terminalLines.clear()

            val cmd = vuln.adbCommand
            if (cmd != null) {
                // Type out command
                terminalLines.add(TerminalLine("$ $cmd", LineType.COMMAND))
                delay(600)

                // Simulate output based on vuln
                when (vuln.id) {
                    "exported_components" -> {
                        terminalLines.add(TerminalLine("Starting: Intent { cmp=com.hackdroid.demo/.vulns.AdminActivity }", LineType.OUTPUT))
                        delay(300)
                        terminalLines.add(TerminalLine("Activity launched successfully", LineType.OUTPUT))
                        delay(200)
                        terminalLines.add(TerminalLine("✓ Admin panel opened — no authentication required", LineType.SUCCESS))
                        delay(200)
                        terminalLines.add(TerminalLine("⚠ Login screen completely bypassed", LineType.WARNING))
                    }
                    "deep_links" -> {
                        terminalLines.add(TerminalLine("Starting: Intent { act=android.intent.action.VIEW dat=hackdroid://transfer }", LineType.OUTPUT))
                        delay(300)
                        terminalLines.add(TerminalLine("Activity com.hackdroid.demo/.vulns.DeepLinkActivity handling URI", LineType.OUTPUT))
                        delay(200)
                        terminalLines.add(TerminalLine("amount=9999, to=attacker", LineType.OUTPUT))
                        delay(200)
                        terminalLines.add(TerminalLine("✓ Transfer initiated with attacker-controlled values", LineType.SUCCESS))
                        delay(200)
                        terminalLines.add(TerminalLine("⚠ No parameter validation performed", LineType.WARNING))
                    }
                    "insecure_storage" -> {
                        terminalLines.add(TerminalLine("pull: building file list...", LineType.OUTPUT))
                        delay(400)
                        terminalLines.add(TerminalLine("pull: /data/data/com.hackdroid.demo/shared_prefs/auth_prefs.xml", LineType.OUTPUT))
                        delay(300)
                        terminalLines.add(TerminalLine("1 file pulled, 0 skipped.", LineType.OUTPUT))
                        delay(200)
                        terminalLines.add(TerminalLine("--- auth_prefs.xml ---", LineType.OUTPUT))
                        delay(100)
                        terminalLines.add(TerminalLine("<string name=\"auth_token\">eyJhbGciOiJIUzI1NiJ9.demo</string>", LineType.SUCCESS))
                        delay(100)
                        terminalLines.add(TerminalLine("<string name=\"user_email\">victim@example.com</string>", LineType.SUCCESS))
                        delay(100)
                        terminalLines.add(TerminalLine("<string name=\"session_id\">sess_abc123_plaintext</string>", LineType.SUCCESS))
                        delay(200)
                        terminalLines.add(TerminalLine("⚠ All credentials stored in plain text", LineType.WARNING))
                    }
                    "sql_injection" -> {
                        terminalLines.add(TerminalLine("Executing raw query: SELECT * FROM users WHERE name='x' OR '1'='1'", LineType.OUTPUT))
                        delay(400)
                        terminalLines.add(TerminalLine("Row: id=1, name=alice, email=alice@example.com, token=tok_alice_secret", LineType.SUCCESS))
                        delay(150)
                        terminalLines.add(TerminalLine("Row: id=2, name=bob, email=bob@example.com, token=tok_bob_secret", LineType.SUCCESS))
                        delay(200)
                        terminalLines.add(TerminalLine("✓ Full table dump — 2 rows returned", LineType.SUCCESS))
                        delay(200)
                        terminalLines.add(TerminalLine("⚠ Raw string concatenation — classic SQLi", LineType.WARNING))
                    }
                    "broadcast_receivers" -> {
                        terminalLines.add(TerminalLine("Broadcasting: Intent { act=com.hackdroid.RESET_AUTH }", LineType.OUTPUT))
                        delay(300)
                        terminalLines.add(TerminalLine("Broadcast completed: result=0", LineType.OUTPUT))
                        delay(200)
                        terminalLines.add(TerminalLine("✓ Auth state cleared — victim logged out", LineType.SUCCESS))
                        delay(200)
                        terminalLines.add(TerminalLine("⚠ No permission required to trigger this broadcast", LineType.WARNING))
                    }
                    else -> {
                        terminalLines.add(TerminalLine("Executing exploit...", LineType.OUTPUT))
                        delay(600)
                        terminalLines.add(TerminalLine("✓ Exploit completed successfully", LineType.SUCCESS))
                    }
                }
            } else {
                terminalLines.add(TerminalLine("# Manual exploit — see steps in Vuln Detail", LineType.OUTPUT))
                delay(300)
                terminalLines.add(TerminalLine("# No ADB command available for this vuln", LineType.OUTPUT))
            }

            delay(200)
            terminalLines.add(TerminalLine("$ _", LineType.CURSOR))
            isRunning = false
        }
    }

    fun clearTerminal() {
        terminalLines.clear()
        terminalLines.add(TerminalLine("$ _", LineType.CURSOR))
    }
}
