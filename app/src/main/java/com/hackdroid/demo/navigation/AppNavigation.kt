package com.hackdroid.demo.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hackdroid.demo.ui.screens.*
import com.hackdroid.demo.ui.theme.*
import com.hackdroid.demo.viewmodel.HackDroidViewModel

// ── Route definitions ──────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    object Home          : Screen("home")
    object VulnList      : Screen("vuln_list")
    object VulnDetail    : Screen("vuln_detail/{vulnId}") {
        fun createRoute(id: String) = "vuln_detail/$id"
    }
    object ExploitLab    : Screen("exploit_lab/{vulnId}") {
        fun createRoute(id: String) = "exploit_lab/$id"
    }
    object DefenseGuide  : Screen("defense_guide")
    object Toolkit       : Screen("toolkit")

    // Demo screens (launched by exploit demos):
    object AdminPanel    : Screen("admin_panel")
    object DeepLinkDemo  : Screen("deep_link_demo")
    object WebViewDemo   : Screen("webview_demo")
    object StorageDemo   : Screen("storage_demo")
    object SqliDemo      : Screen("sqli_demo")
    object FridaDemo     : Screen("frida_demo")
}

// ── Bottom tab descriptor ──────────────────────────────────────────────────
private data class BottomTab(
    val label: String,
    val icon: String,
    val route: String
)

private val bottomTabs = listOf(
    BottomTab("HOME",  "⌂", Screen.Home.route),
    BottomTab("VULNS", "⚠", Screen.VulnList.route),
    BottomTab("LAB",   "⚡", Screen.ExploitLab.createRoute("exported_components")),
    BottomTab("TOOLS", "⚙", Screen.Toolkit.route)
)

// Root routes that own a bottom tab slot
private val tabRoots = setOf(
    Screen.Home.route,
    Screen.VulnList.route,
    Screen.Toolkit.route
)

// ── App Navigation entry point ─────────────────────────────────────────────
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val vm: HackDroidViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
    ) {
        // ── Nav graph ──────────────────────────────────────────────────────
        NavHost(
            navController    = navController,
            startDestination = Screen.Home.route,
            modifier         = Modifier.weight(1f)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController = navController)
            }
            composable(Screen.VulnList.route) {
                VulnListScreen(navController = navController, vm = vm)
            }
            composable(
                route     = Screen.VulnDetail.route,
                arguments = listOf(navArgument("vulnId") { type = NavType.StringType })
            ) { backStack ->
                val vulnId = backStack.arguments?.getString("vulnId") ?: return@composable
                VulnDetailScreen(
                    vulnId        = vulnId,
                    navController = navController,
                    vm            = vm
                )
            }
            composable(
                route     = Screen.ExploitLab.route,
                arguments = listOf(navArgument("vulnId") { type = NavType.StringType })
            ) { backStack ->
                val vulnId = backStack.arguments?.getString("vulnId") ?: "exported_components"
                ExploitLabScreen(
                    vulnId        = vulnId,
                    navController = navController,
                    vm            = vm
                )
            }
            composable(Screen.DefenseGuide.route) {
                DefenseGuideScreen(navController = navController)
            }
            composable(Screen.Toolkit.route) {
                ToolkitScreen(navController = navController)
            }
            // Demo screens
            composable(Screen.AdminPanel.route) {
                AdminPanelScreen(navController = navController)
            }
            composable(Screen.DeepLinkDemo.route) {
                DeepLinkDemoScreen(navController = navController)
            }
            composable(Screen.WebViewDemo.route) {
                WebViewDemoScreen(navController = navController)
            }
            composable(Screen.StorageDemo.route) {
                StorageDemoScreen(navController = navController)
            }
            composable(Screen.SqliDemo.route) {
                SqliDemoScreen(navController = navController)
            }
            composable(Screen.FridaDemo.route) {
                FridaDemoScreen(navController = navController)
            }
        }

        // ── Bottom pill tab bar ────────────────────────────────────────────
        BottomTabBar(navController = navController)
    }
}

@Composable
private fun BottomTabBar(navController: NavHostController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute   = backStackEntry?.destination?.route ?: Screen.Home.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavyBackground)
            // Pad below the tab bar content so it sits above the system navigation bar
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(36.dp))
                .background(InsetSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            bottomTabs.forEach { tab ->
                val isActive = when (tab.route) {
                    Screen.Home.route    -> currentRoute == Screen.Home.route
                    Screen.VulnList.route -> currentRoute == Screen.VulnList.route ||
                                             currentRoute.startsWith("vuln_detail") ||
                                             currentRoute.startsWith("vuln_")
                    Screen.Toolkit.route -> currentRoute == Screen.Toolkit.route
                    else                 -> currentRoute.startsWith("exploit_lab")
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (isActive) CyanAccent else androidx.compose.ui.graphics.Color.Transparent)
                        .clickable {
                            if (!isActive) {
                                navController.navigate(tab.route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text       = tab.icon,
                            fontSize   = 14.sp,
                            color      = if (isActive) TextInverted else TextMuted
                        )
                        Text(
                            text       = tab.label,
                            fontFamily = JetBrainsMono,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 10.sp,
                            color      = if (isActive) TextInverted else TextMuted,
                            textAlign  = TextAlign.Center,
                            letterSpacing = 2.sp
                        )
                    }
                }
            }
        }
    }
}
