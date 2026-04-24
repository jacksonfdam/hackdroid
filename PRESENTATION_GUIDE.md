# Hacking Android — Presentation Guide & Speaker Notes

> **Talk:** Hacking Android From the Inside Out  
> **Deck:** 17 slides (incl. Easter egg) + live HackDroid app demos  
> **Suggested total time:** 45–60 min  
> **Format:** Slides → concept → live demo → takeaway, repeated for each vuln

---

## Pre-Talk Checklist

Run this before the room fills up.

```bash
# 1. Device connected
adb devices                          # must show your device

# 2. App installed
adb shell am start -n com.hackdroid.demo/.MainActivity

# 3. Frida ready (Gadget embedded in APK)
#    Just launch the app — Gadget loads automatically

# 4. Slide deck open
open "slides/slides.pen"

# 5. Terminal font size bumped (audience needs to read it)
#    Recommended: 18–20pt, dark background
```

**Have open in separate terminal tabs:**
- Tab 1 — ADB commands (clean, ready to paste)
- Tab 2 — Frida scripts dir: `cd app/src/main/assets/frida_scripts`
- Tab 3 — logcat: `adb logcat | grep HackDroid`

**Have the app open on device, on the Home screen.**

---

## Slide Order & Run of Show

Slides are in `slides/slides.pen`. The order on canvas (left → right) is:

| # | Slide name in file | Headline | Demo | ~Time |
|---|---|---|---|---|
| 01 | Slide 01 - Title | HACKING ANDROID / FROM THE INSIDE OUT | — | 2 min |
| 02 | Slide 02 - Easter Egg ⭐ | THESE AREN'T THE DROIDS YOU'RE LOOKING FOR | — | 30 s |
| 03 | Slide 02 - Why Android | 70% of the World Runs Android | — | 3 min |
| 04 | Slide 03 - Architecture | The Architecture Nobody Talks About | — | 3 min |
| 05 | Slide 04 - App Sandbox | Every App Lives in Its Own Box | — | 3 min |
| 06 | Slide 05 - APK Structure | An APK Is Just a ZIP File | — | 2 min |
| 07 | Slide 06 - Toolkit | Tools of the Trade | — | 2 min |
| 08 | Slide 07 - Reverse Engineering | Reading Code That Was Never Meant to Be Read | JADX | 4 min |
| 09 | Slide 08 - Attack Surface | Where Things Go Wrong | — | 2 min |
| 10 | Slide 09 - Exported Components | Leaving the Door Unlocked | `adb am start` | 5 min |
| 11 | Slide 10 - Deep Links | Malicious Links That Open Your App | `adb am start VIEW` | 4 min |
| 12 | Slide 11 - WebViews | A Browser Inside Your App | WebView in app | 4 min |
| 13 | Slide 12 - Insecure Storage | Sensitive Data Left in Plain Sight | `adb run-as` | 4 min |
| 14 | Slide 13 - SQL Injection | Yes, SQLi Exists in Mobile Apps Too | `adb content query` | 4 min |
| 15 | Slide 14 - Frida | Hooking Into a Running App | Frida Gadget | 6 min |
| 16 | Slide 15 - Takeaways | What This Means for Builders and Defenders | — | 3 min |
| 17 | Slide 16 - Closing | Android Security Is a Mindset | — | 2 min |
| — | Q&A | | | 10 min |

> **Note:** "Slide 02 - Why Android" is named `02` in the file but sits in position 3 on canvas
> (the Easter Egg was inserted between them). Present them in canvas order above.

---

## Slide-by-Slide Speaker Notes

---

### Slide 01 — Title
**HACKING ANDROID FROM THE INSIDE OUT**

> *"This talk is about how Android apps get attacked from the inside — not from the network, not from some mysterious zero-day, but from the front door. By the end, every one of you will be able to pull credentials out of an app on your own phone."*

- Let that land. Don't rush to the next slide.
- Brief self-intro (30 seconds max).
- Mention: everything you see today is running on a real device, live, no pre-recorded videos.

---

### Slide 02 — Easter Egg ⭐ (May 4th)
**THESE AREN'T THE DROIDS YOU'RE LOOKING FOR**  
`— They all have android:exported='true'`

> *"Since it's May 4th — quick one. In Star Wars, R2-D2 is an Android droid. In Android security, `android:exported='true'` is how the Rebels extracted the Death Star plans. No auth. No permission. Just `adb shell am start` and you're in."*

- This slide is a warmup. Get a laugh, get the room on your side.
- Don't dwell — 30 seconds and move on.

---

### Slide 03 — Why Android
**70% of the World Runs Android**

> *"Three billion active devices. The most targeted mobile OS. And apps are handling your bank account, your health data, your identity. The stakes couldn't be higher — and yet security is almost always an afterthought. Let's talk about why."*

**Key points to hit:**
- Android holds around 72.77% of the global mobile OS market, with iOS trailing at approximately 26–28%.
- 70% global market share — this isn't a niche
- Apps are the new perimeter: banking, identity, health, payments all live in APKs
- Most Android devs learn security the hard way — after a breach

---

### Slide 04 — Architecture
**The Architecture Nobody Talks About**

> *"Before we attack anything, we need to understand what we're attacking. Android is a Linux kernel at the bottom, the Android Runtime in the middle — that's where your Kotlin and Java bytecode runs — and the app framework on top. Security decisions happen at every layer. And so do mistakes."*

**Key points:**
- Linux kernel: process isolation, permissions, file system controls
- ART (Android Runtime): Dalvik bytecode → compiled native code
- Framework: the APIs your app calls — and misuses
- The architecture is solid. The vulnerabilities are almost always in how developers *use* it.

---

### Slide 05 — App Sandbox
**Every App Lives in Its Own Box**

> *"Each app gets its own Linux user ID — a unique UID. App A literally cannot read App B's files. The kernel enforces it. The only way apps talk to each other is through a controlled IPC bridge called Binder — think of it as a bouncer that checks your credentials before you cross the rope."*

**Key points:**
- Sandbox is strong by default
- `android:exported` is the escape hatch — the valve that opens the door
- "Solid model — but only when devs use it correctly." (quote the slide)

> *"Everything we're about to see is a developer misusing that valve."*

---

### Slide 06 — APK Structure
**An APK Is Just a ZIP File**

> *"Here's something your app store doesn't tell your users: anyone can download your APK and unzip it. Right now. Let me show you what's inside."*

**On slide:**
- `classes.dex` — your compiled code
- `AndroidManifest.xml` — the blueprint (attack surface map)
- `res/`, `assets/` — resources, sometimes hardcoded secrets
- `lib/` — native `.so` files

> *"The manifest is particularly interesting. It's a declaration of every component in your app — Activities, Services, Receivers, Providers — and whether they're exported to the world."*

**Demo cue (optional, quick):**
```bash
unzip app-debug.apk -d /tmp/extracted
cat /tmp/extracted/AndroidManifest.xml | head -40
```

---

### Slide 07 — Toolkit
**Tools of the Trade**

> *"These are the five tools you need to attack any Android app. All free, all open source, all legal when used on apps you own."*

| Tool | One-liner |
|------|-----------|
| **ADB** | USB bridge to the device — runs shell commands, installs APKs, pulls files |
| **Frida** | Dynamic instrumentation — hooks into running processes, patches methods at runtime |
| **JADX** | Decompiler — turns `.dex` bytecode back into readable Java |
| **Burp Suite** | Intercepts HTTPS traffic — see exactly what the app sends to the server |
| **MobSF** | Automated scanner — gives you a full report in 5 minutes |

> *"Today we're using ADB and Frida live. JADX I'll demo briefly. The others are in the HackDroid toolkit screen."*

---

### Slide 08 — Reverse Engineering
**Reading Code That Was Never Meant to Be Read**

> *"JADX takes compiled bytecode — Smali assembly, which looks like this — and turns it back into something remarkably close to the original Java source. Here's what I found in a real app last year."*

**Point at the BEFORE column (Smali):**
> *"This is what your code looks like after compilation — machine-readable gibberish."*

**Point at the AFTER column (Java):**
> *"And this is what JADX gives me in about 30 seconds. `sk_live_8f2a...` — that's a live API key. `/internal/admin` — an undocumented endpoint. `hunter2` — a hardcoded secret. All sitting in a public APK."*

**Key message:**
> *"Assume your APK is readable. Because it is."*

**Demo (optional):**
```bash
jadx -d /tmp/out app/build/outputs/apk/debug/app-debug.apk
open /tmp/out/sources/com/hackdroid/demo/data/VulnerabilityData.java
```

---

### Slide 09 — Attack Surface
**Where Things Go Wrong**

> *"Here's the map of today's talk. Seven entry points into a typical Android app. We're going to walk through each one — theory first, then live exploit on the real device."*

**Walk through the arrows on the diagram:**
- Left: Exported Activities, Exported Services, Broadcast Receivers
- Right: Deep Links, WebViews, Insecure Storage, SQL Injection

> *"Every one of these is in the HackDroid app — intentionally broken, so we can demonstrate it safely. Let's start with the most critical."*

---

### Slide 10 — Exported Components
**Leaving the Door Unlocked**

**Concept (60 seconds):**
> *"Every component in your manifest can have `exported=true`. That means any other app — or anyone with ADB — can trigger it directly. No authentication. No permission check. Just a shell command."*

> *"Typical mistake: the dev sets the login screen to `exported=false`, but leaves the dashboard at `exported=true`. Attacker skips the login and goes straight to the dashboard."*

**🔴 LIVE DEMO 1 — Bypass Admin Panel:**

```bash
adb shell am start -n com.hackdroid.demo/.vulns.AdminActivity
```

> *"Admin panel. No login. One command."*

**On device:** Show the app jumping straight to the Admin Panel screen.

**Then show in app:** Open HackDroid → Exploit Lab → Bypass Exported Activity to show the in-app explanation.

**Fix (15 seconds):**
```xml
<!-- Vulnerable -->
<activity android:name=".AdminActivity" android:exported="true"/>

<!-- Fixed -->
<activity android:name=".AdminActivity" android:exported="false"/>
```

---

### Slide 11 — Deep Links
**Malicious Links That Open Your App**

**Concept (45 seconds):**
> *"Apps register custom URL schemes — `hackdroid://transfer` — so the OS knows to open your app when that URL is clicked. In a browser, an email, an SMS. The problem is when apps read those URL parameters without validating them."*

**🟠 LIVE DEMO 2 — Deep Link Injection:**

```bash
adb shell am start -a android.intent.action.VIEW \
  -d "hackdroid://transfer?amount=9999&to=attacker"
```

**On device:** Transfer screen shows `$9999` going to `attacker` — with zero validation.

> *"Imagine this is a banking app. The attacker sends a link in an SMS. The user taps it. The app pre-fills a transfer form with attacker-controlled values."*

**Fix:**
- Validate and whitelist all deep link parameters
- Never trust URL parameters for sensitive actions
- Re-authenticate for any financial or destructive action triggered by a deep link

---

### Slide 12 — WebViews
**A Browser Inside Your App**

**Concept (60 seconds):**
> *"WebView is a full browser engine embedded in your app. When you call `addJavascriptInterface()`, you're punching a hole from JavaScript straight into native Android code. Any JS running in that WebView — including JS from a page you don't control — can call those native methods."*

**Point at the diagram:**
> *"Left box is the WebView. Right box is your native app. The bridge in the middle is `addJavascriptInterface`. If an attacker can load their own HTML into that WebView, they own the bridge."*

**🟠 LIVE DEMO 3 — WebView JS Bridge:**

In HackDroid: Vulns → WebViews / JS Bridge → Run Demo Exploit

> *"Open the demo page and tap the buttons. Each one calls a native Android method directly from JavaScript — reading the package name, reading files, showing a toast. In a real app this could be reading your contacts, your location, your stored credentials."*

**Fix:**
- Never pass untrusted URLs to a WebView that has `addJavascriptInterface`
- Restrict what the bridge exposes — use `@JavascriptInterface` annotation only on safe methods
- Set `webView.settings.javaScriptEnabled = false` if you don't need JS

---

### Slide 13 — Insecure Storage
**Sensitive Data Left in Plain Sight**

**Concept (45 seconds):**
> *"SharedPreferences is the most common way Android apps store small bits of data. Auth tokens, session IDs, user emails. By default, these live as plain XML files in the app's private directory. On a debug build — and lots of internal/beta builds are debug builds — you can read them without root."*

**🟡 LIVE DEMO 4 — Read SharedPreferences:**

```bash
adb shell run-as com.hackdroid.demo \
  cat /data/data/com.hackdroid.demo/shared_prefs/auth_prefs.xml
```

**On screen:** XML file with `auth_token`, `user_email`, `session_id`, `api_key` all in plain text.

> *"`run-as` gives you the app's own user context — no root needed on any debug build. What you see here: a JWT token, an email, a live API key. All readable in one command."*

**Fix:**
- Use **EncryptedSharedPreferences** from the Jetpack Security library
- Never store raw credentials — store a reference, not the secret itself
- Mark production builds with `debuggable=false`

---

### Slide 14 — SQL Injection
**Yes, SQLi Exists in Mobile Apps Too**

**Concept (45 seconds):**
> *"ContentProviders expose SQLite databases to other apps. When the query selection string is built by concatenating user input — instead of using parameterized queries — classic SQL injection works exactly the same as it does on the web."*

**Point at the code on screen:** Show the raw string concatenation.

**🟡 LIVE DEMO 5 — SQL Injection via ContentProvider:**

```bash
adb shell content query \
  --uri content://com.hackdroid.demo.provider/users \
  --where "1=1"
```

**On screen:** Every row in the users table — names, emails, and tokens — dumped.

> *"`WHERE 1=1` always evaluates to true. Every row returned. This is a public endpoint — any app on the device, or anyone with ADB, can run this."*

**Fix:**
```kotlin
// Vulnerable
db.rawQuery("SELECT * FROM users WHERE name = '$input'", null)

// Fixed — parameterized
db.query("users", null, "name = ?", arrayOf(input), null, null, null)
```

---

### Slide 15 — Frida
**Hooking Into a Running App**

**Concept (90 seconds):**
> *"Everything we've done so far was static — file reads, ADB commands. Frida is dynamic. It injects a JavaScript engine directly into the running process. Once inside, you can intercept any method call, read its arguments, change its return value — all without touching the source code or recompiling."*

> *"The tag on the slide says it: NO SOURCE CODE NEEDED. This works on any app, including ones from the Play Store."*

**This app uses the Frida Gadget**, so no root is required — explain briefly:
> *"We embedded a 24MB library called the Gadget into this APK. When the app launches, it pauses and opens a Frida server on TCP port 27042 inside its own process. We forward that port via ADB and attach over it — no root, no frida-server needed."*

**🔧 LIVE DEMO 6 — Bypass Root Detection:**

```bash
# 1. Launch the app — screen will freeze (Gadget is waiting)
adb shell am start -n com.hackdroid.demo/.MainActivity

# 2. Forward the Gadget TCP port to localhost
adb forward tcp:27042 tcp:27042

# 3. Attach Frida over TCP
frida -H 127.0.0.1:27042 \
  -l app/src/main/assets/frida_scripts/bypass_root_detection.js
```

**Watch the output:**
```
[HackDroid] Hooking RootChecker.isRooted()...
[HackDroid] isRooted() called — returning false
[HackDroid] ✓ Root detection bypassed
```

> *"The app now thinks it's running on a clean, stock device — even if it's rooted. The same technique bypasses SSL pinning, licence checks, payment gates, biometric guards. Any logic, any app, any method — patchable at runtime."*

**In-app:** Show the Frida screen in HackDroid (Exploit Lab → Frida Hook) — it shows the step-by-step explanation.

---

### Slide 16 — Takeaways
**What This Means for Builders and Defenders**

> *"Let me leave you with two columns. Left is the attacker mindset — things an attacker knows about your app before they write a single line of code. Right is what you can do about it."*

**ATTACKER MINDSET (read slowly, let each one land):**
- "Your APK is public and readable." — Always.
- "Your manifest declares the attack surface." — It's a map of entry points.
- "Local storage is trivially accessible on rooted devices." — And on debug builds, no root needed.
- "Runtime hooks bypass your logic completely." — Frida doesn't care about your defence code.

**DEFENDER ACTION:**
- "Audit your manifest — lock exported components." — This is a 30-minute job on most apps.
- "Encrypt sensitive data, store only what's needed." — EncryptedSharedPreferences is one import.
- "Validate every input." — Deep links, parameters, WebView URLs. All of them.
- **"Test your own app the way an attacker would."** — (emphasize this one)

---

### Slide 17 — Closing
**Android Security Is a Mindset**

> *"The four pills at the bottom are the summary. Understand the architecture. Know your attack surface. Think like an attacker. Build like a defender."*

> *"The vulnerabilities we saw today are not exotic. They're not zero-days. They're mistakes that appear in production apps every week — apps with millions of users. The difference between a vulnerable app and a secure one is usually a single line of XML, a parameterized query, or an encrypted preference."*

> *"The HackDroid app and all the scripts are on GitHub — link in the README. Pull it, install it, break it. That's the point."*

> *"Questions?"*

---

## Demo Quick Reference Card

Print this and keep it at the podium.

```
DEMO 1 — Bypass Admin Panel
adb shell am start -n com.hackdroid.demo/.vulns.AdminActivity

DEMO 2 — Deep Link Injection
adb shell am start -a android.intent.action.VIEW \
  -d "hackdroid://transfer?amount=9999&to=attacker"

DEMO 3 — WebView JS Bridge
→ In app: Vulns → WebViews → Run Demo Exploit

DEMO 4 — Read SharedPreferences
adb shell run-as com.hackdroid.demo \
  cat /data/data/com.hackdroid.demo/shared_prefs/auth_prefs.xml

DEMO 5 — SQL Injection
adb shell content query \
  --uri content://com.hackdroid.demo.provider/users \
  --where "1=1"

DEMO 6 — Frida Root Bypass
adb shell am start -n com.hackdroid.demo/.MainActivity
adb forward tcp:27042 tcp:27042
frida -H 127.0.0.1:27042 \
  -l app/src/main/assets/frida_scripts/bypass_root_detection.js

EXTRA — Broadcast Reset
adb shell am broadcast -a com.hackdroid.RESET_AUTH

EXTRA — Logcat Leak
adb shell am startservice -n com.hackdroid.demo/.vulns.LeakyService
adb logcat | grep HackDroid_LEAK
```

---

## Backup Plans

| Problem | Fix |
|---------|-----|
| Device not connecting | Replug USB, `adb kill-server && adb start-server` |
| App crashed | `adb install -r app/build/outputs/apk/debug/app-debug.apk` |
| Frida not attaching | Kill and relaunch app, re-run `adb forward tcp:27042 tcp:27042`, then retry `frida -H 127.0.0.1:27042 ...` |
| ADB permission denied | Run-as fix: always use `adb shell run-as com.hackdroid.demo ...` |
| Audience can't read terminal | Zoom in: Cmd+= or increase terminal font on the fly |
| Running short on time | Skip Demos 5 (SQLi) and the Broadcast/Logcat extras — core story holds |
| Running long | Skip Slides 07 (Toolkit detail) and 08 (Reverse Engineering demo) |
