// HackDroid — Frida Script: Bypass Root Detection
// ─────────────────────────────────────────────────────────────────────────────
// Usage:
//   frida -U -f com.hackdroid.demo --no-pause -l bypass_root_detection.js
//
// What this does:
//   Hooks RootChecker.isRooted() and forces it to always return false,
//   regardless of the actual device state. The app believes it is running
//   on a clean, non-rooted device.
//
// OWASP M7: Insufficient Binary Protections
// ─────────────────────────────────────────────────────────────────────────────

Java.perform(function () {
    console.log("[HackDroid] Frida attached to com.hackdroid.demo");
    console.log("[HackDroid] Loading bypass_root_detection.js...");

    // Hook the RootChecker class
    var RootChecker = Java.use("com.hackdroid.demo.security.RootChecker");

    // Override isRooted() to always return false
    RootChecker.isRooted.implementation = function () {
        console.log("[HackDroid] isRooted() called — returning false (bypassed)");
        return false;
    };

    // Also hook the private helper methods for complete bypass
    RootChecker.checkSuBinary.implementation = function () {
        console.log("[HackDroid] checkSuBinary() called — returning false");
        return false;
    };

    RootChecker.checkTestKeys.implementation = function () {
        console.log("[HackDroid] checkTestKeys() called — returning false");
        return false;
    };

    RootChecker.checkSuExists.implementation = function () {
        console.log("[HackDroid] checkSuExists() called — returning false");
        return false;
    };

    console.log("[HackDroid] ✓ Root detection bypassed — all checks return false");
    console.log("[HackDroid] App now believes device is NOT rooted");
});
