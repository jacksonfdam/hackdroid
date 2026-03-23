# HackDroid — Architecture

## Overview

HackDroid follows MVVM (Model-View-ViewModel) architecture with Jetpack Navigation Compose.
The app is split into two distinct concerns:

1. **The presentation layer** — Compose UI screens that demonstrate vulnerabilities with hack/defend content
2. **The vulnerable layer** — Real Android components (`Activity`, `Service`, `BroadcastReceiver`, `ContentProvider`) that are intentionally exploitable

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        MainActivity                              │
│                    setContent { AppNavigation() }                │
└──────────────────────────┬──────────────────────────────────────┘
                           │
                    ┌──────▼──────┐
                    │ AppNavigation│  ← NavHost + Bottom Tab Bar
                    └──────┬──────┘
                           │
           ┌───────────────┼───────────────────┐
           │               │                   │
    ┌──────▼─────┐  ┌──────▼──────┐  ┌────────▼───────┐
    │ HomeScreen │  │VulnListScreen│  │ ExploitLabScreen│
    └──────┬─────┘  └──────┬──────┘  └────────┬───────┘
           │               │                   │
           │        ┌──────▼──────┐    ┌───────▼────────┐
           │        │VulnDetail   │    │ HackDroidViewModel│
           │        │ Screen      │    │ ─ allVulns      │
           │        └─────────────┘    │ ─ terminalLines │
           │                           │ ─ runExploit()  │
    ┌──────▼──────────────────────┐    └───────┬────────┘
    │       DefenseGuideScreen    │            │
    │       ToolkitScreen         │     ┌──────▼────────┐
    └─────────────────────────────┘     │VulnerabilityData│
                                        │ (static list)  │
                                        └───────────────┘
```

---

## Module Structure

```
app/src/main/java/com/hackdroid/demo/
│
├── MainActivity.kt               Entry point, sets up Compose + edge-to-edge
│
├── data/
│   └── VulnerabilityData.kt      Static list of all 7 Vulnerability objects
│                                  Each has: id, title, severity, hackSteps,
│                                  defenseSteps, adbCommand, demoRoute
│
├── navigation/
│   └── AppNavigation.kt          sealed class Screen (route definitions)
│                                  NavHost with all composable destinations
│                                  BottomTabBar (pill tab bar, 4 tabs)
│
├── security/
│   └── RootChecker.kt            Naive root detection — target for Frida demo
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt              NavyBackground, CyanAccent, DangerRed, etc.
│   │   ├── Type.kt               JetBrainsMono + Inter font families
│   │   └── Theme.kt              HackDroidTheme (dark only), Radius tokens
│   │
│   └── screens/
│       ├── HomeScreen.kt         Hero, stats, quick access, vuln module list
│       ├── VulnListScreen.kt     Filtered list of 7 vulns with severity badges
│       ├── VulnDetailScreen.kt   HOW TO HACK box + HOW TO PROTECT box + CTA
│       ├── ExploitLabScreen.kt   Terminal output + 4 runnable demo modules
│       ├── DefenseGuideScreen.kt 7 defense cards (one highlighted)
│       ├── ToolkitScreen.kt      5 tool cards with command blocks
│       └── DemoScreens.kt        AdminPanelScreen, DeepLinkDemoScreen,
│                                  WebViewDemoScreen, StorageDemoScreen,
│                                  SqliDemoScreen, FridaDemoScreen
│
├── viewmodel/
│   └── HackDroidViewModel.kt     allVulns list, terminalLines state,
│                                  runExploit() with coroutine-based simulation
│
└── vulns/                        ← INTENTIONALLY VULNERABLE COMPONENTS
    ├── AdminActivity.kt          exported=true, no auth (CRITICAL)
    ├── DeepLinkActivity.kt       reads URI params raw, no validation (HIGH)
    ├── LeakyService.kt           logs tokens/keys to Logcat (HIGH)
    ├── AuthResetReceiver.kt      clears auth on any broadcast (LOW)
    ├── VulnerableContentProvider.kt  raw SQL string concat (MEDIUM)
    ├── InsecureStorageActivity.kt    plain SharedPreferences (MEDIUM)
    └── WebViewDemoActivity.kt    addJavascriptInterface() bridge (HIGH)
```

---

## Data Flow

### Vulnerability Display Flow

```
VulnerabilityData.all
    → HackDroidViewModel.allVulns
        → VulnListScreen (list)
        → VulnDetailScreen (detail, hack/defend boxes)
        → ExploitLabScreen (terminal simulation)
```

### Exploit Demo Flow

```
ExploitLabScreen
    → User taps demo module
        → Option A: navigate to Compose DemoScreen (DeepLinkDemo, StorageDemo, etc.)
        → Option B: startActivity() to real vulnerable Activity (AdminActivity)
        → Option C: HackDroidViewModel.runExploit(vuln) → terminalLines updated
```

### Navigation Routes

| Route | Screen | Tab |
|---|---|---|
| `home` | HomeScreen | HOME |
| `vuln_list` | VulnListScreen | VULNS |
| `vuln_detail/{vulnId}` | VulnDetailScreen | VULNS |
| `exploit_lab/{vulnId}` | ExploitLabScreen | LAB |
| `defense_guide` | DefenseGuideScreen | — |
| `toolkit` | ToolkitScreen | TOOLS |
| `admin_panel` | AdminPanelScreen | — |
| `deep_link_demo` | DeepLinkDemoScreen | — |
| `webview_demo` | WebViewDemoScreen | — |
| `storage_demo` | StorageDemoScreen | — |
| `sqli_demo` | SqliDemoScreen | — |
| `frida_demo` | FridaDemoScreen | — |

---

## Design System

### Colors

| Token | Hex | Use |
|---|---|---|
| `NavyBackground` | `#0A0F1C` | Screen backgrounds |
| `SlateCard` | `#1E293B` | Card backgrounds |
| `InsetSurface` | `#0F172A` | Inset containers, tab bar |
| `CyanAccent` | `#22D3EE` | Primary accent, active states |
| `DangerRed` | `#FF6B6B` | Hack boxes, warnings, CRITICAL |
| `WarnAmber` | `#F59E0B` | HIGH severity, terminal warnings |
| `TerminalBlack` | `#040810` | Terminal output backgrounds |
| `TextPrimary` | `#FFFFFF` | Main text |
| `TextSecondary` | `#94A3B8` | Descriptions, terminal output |
| `TextTertiary` | `#64748B` | Labels, subtitles |
| `TextMuted` | `#475569` | Inactive tabs, placeholder text |

### Typography

| Usage | Font | Size |
|---|---|---|
| Screen headers | Inter Bold | 24sp |
| Hero metric | JetBrains Mono Bold | 32sp |
| Card titles | Inter SemiBold | 14-15sp |
| Body text | Inter Regular | 13sp |
| Terminal / code | JetBrains Mono | 11sp |
| Tab labels | JetBrains Mono Bold | 10sp |

### Corner Radii

| Token | Value | Use |
|---|---|---|
| `Radius.Small` | 4dp | Severity badges |
| `Radius.Medium` | 8dp | Code blocks, small cards |
| `Radius.List` | 10dp | List item rows |
| `Radius.Card` | 12dp | Main cards, boxes |
| `Radius.Pill` | 36dp | Tab bar container |

---

## Vulnerable Component Reference

| Class | Type | Exported | Intent / URI | Exploit |
|---|---|---|---|---|
| `AdminActivity` | Activity | ✅ yes | direct am start | No auth bypass |
| `DeepLinkActivity` | Activity | ✅ yes | `hackdroid://transfer` | Unvalidated params |
| `LeakyService` | Service | ✅ yes | am startservice | Logcat secret dump |
| `AuthResetReceiver` | Receiver | ✅ yes | `com.hackdroid.RESET_AUTH` | Auth state wipe |
| `VulnerableContentProvider` | Provider | ✅ yes | `content://com.hackdroid.demo.provider/users` | SQL injection |
| `InsecureStorageActivity` | Activity | ❌ no | internal only | ADB pull prefs |
| `WebViewDemoActivity` | Activity | ❌ no | internal only | JS bridge RCE |
