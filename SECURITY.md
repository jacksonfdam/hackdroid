# Security Notes

This app **intentionally** implements vulnerabilities from the OWASP Mobile Security Testing Guide (MSTG).
It is designed for use in controlled security presentations and educational environments only.

---

## ⚠️ Intended Insecurities

Every vulnerability in this app is deliberate. The following flags are set intentionally:

```xml
android:debuggable="true"          <!-- Allows ADB shell access to app data -->
android:allowBackup="true"         <!-- Allows ADB backup of app data -->
android:usesCleartextTraffic="true" <!-- Allows HTTP (not HTTPS) traffic -->
android:exported="true"            <!-- On AdminActivity, DeepLinkActivity, LeakyService, etc. -->
```

---

## OWASP Mobile Top 10 Coverage

| OWASP ID | Name | Implemented As | Severity |
|---|---|---|---|
| M1 | Improper Credential Usage | Hardcoded token in `LeakyService`, plaintext in `InsecureStorageActivity` | HIGH |
| M2 | Inadequate Supply Chain Security | Debug build enabled, cleartext traffic allowed | MEDIUM |
| M3 | Insecure Authentication | `AdminActivity` exported with no auth — bypass via ADB | CRITICAL |
| M4 | Insufficient Input/Output Validation | Deep link params unvalidated, SQL injection in `VulnerableContentProvider` | HIGH |
| M5 | Insecure Communication | `usesCleartextTraffic=true`, no SSL pinning | MEDIUM |
| M6 | Inadequate Privacy Controls | PII stored in unencrypted `SharedPreferences` | MEDIUM |
| M7 | Insufficient Binary Protections | `debuggable=true`, no obfuscation, Frida hookable | LOW |
| M8 | Security Misconfiguration | Multiple exported components, no permissions | CRITICAL |
| M9 | Insecure Data Storage | Plain `SharedPreferences`, raw SQLite, Logcat leaks | HIGH |
| M10 | Insufficient Cryptography | No encryption on stored secrets, no key management | HIGH |

---

## Vulnerability Details

### 1. Exported AdminActivity (CRITICAL — M3, M8)

**File:** `vulns/AdminActivity.kt`
**Manifest:** `android:exported="true"` with no `android:permission`

Any app or ADB command can launch this Activity without authentication.
Bypasses any login screen protecting admin functionality.

```bash
# Exploit:
adb shell am start -n com.hackdroid.demo/.vulns.AdminActivity
```

**Fix:** Set `android:exported="false"`, or add `android:permission="com.hackdroid.ADMIN"` and check `checkCallingPermission()` inside the Activity.

---

### 2. Deep Link Injection (HIGH — M4)

**File:** `vulns/DeepLinkActivity.kt`

The Activity reads `amount` and `to` from the intent URI without any validation or sanitization. An attacker can craft a link that triggers the transfer flow with arbitrary values.

```bash
# Exploit:
adb shell am start -a android.intent.action.VIEW \
  -d "hackdroid://transfer?amount=9999&to=attacker"
```

**Fix:** Validate all parameters. Use an allowlist. Require user confirmation for financial operations.

---

### 3. WebView JS Bridge (HIGH — M4, M8)

**Files:** `vulns/WebViewDemoActivity.kt`, `assets/webview_demo.html`

`addJavascriptInterface()` exposes native Android methods (`readFile`, `getPackageName`, `showToast`) to all JavaScript running in the WebView. Any page loaded — including malicious redirects — can call these methods.

**Fix:** Remove `addJavascriptInterface()`. Disable JavaScript where not needed. Only load trusted origins.

---

### 4. Logcat Secret Leak (HIGH — M1, M9)

**File:** `vulns/LeakyService.kt`

Session tokens and API keys are logged via `Log.d()` to Logcat. Logcat is readable by any app holding `READ_LOGS` permission, and always readable via ADB.

```bash
# Exploit:
adb logcat | grep HackDroid_LEAK
```

**Fix:** Never log sensitive data. Use `BuildConfig.DEBUG` guards around any debug logging.

---

### 5. Insecure Storage (MEDIUM — M9, M6)

**File:** `vulns/InsecureStorageActivity.kt`

Auth tokens, session IDs, and PII are stored in plain `SharedPreferences` (XML files on disk). On debug builds, ADB can pull these files without root access.

```bash
# Exploit:
adb pull /data/data/com.hackdroid.demo/shared_prefs/
```

**Fix:** Use `EncryptedSharedPreferences` from Jetpack Security. Use Android Keystore for key material.

---

### 6. SQL Injection via ContentProvider (MEDIUM — M4, M9)

**File:** `vulns/VulnerableContentProvider.kt`

The `query()` method concatenates the `selection` parameter directly into a SQL string:
```kotlin
val query = "SELECT * FROM users WHERE $sel"  // VULNERABLE
db.rawQuery(query, null)
```

```bash
# Exploit:
adb shell content query \
  --uri content://com.hackdroid.demo.provider/users \
  --where "name='x' OR '1'='1'"
```

**Fix:** Use parameterized queries: `db.query(TABLE, proj, selection, selectionArgs, ...)`.

---

### 7. Exported Broadcast Receiver (LOW — M8)

**File:** `vulns/AuthResetReceiver.kt`

Any app or ADB can send `com.hackdroid.RESET_AUTH` and clear all authentication state without any permission.

```bash
# Exploit:
adb shell am broadcast -a com.hackdroid.RESET_AUTH
```

**Fix:** Add `android:permission` attribute. Use `LocalBroadcastManager` for internal broadcasts.

---

## References

- OWASP MSTG: https://owasp.org/www-project-mobile-security-testing-guide/
- OWASP Mobile Top 10: https://owasp.org/www-project-mobile-top-10/
- Android Security Best Practices: https://developer.android.com/topic/security/best-practices
- Android Keystore: https://developer.android.com/training/articles/keystore
- EncryptedSharedPreferences: https://developer.android.com/reference/androidx/security/crypto/EncryptedSharedPreferences
