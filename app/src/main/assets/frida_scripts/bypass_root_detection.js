// HackDroid — Frida Script: Bypass Root Detection
// ─────────────────────────────────────────────────────────────────────────────
// HOW FRIDA GADGET WORKS (non-rooted device):
//
//   This app embeds libfrida-gadget.so in its native libraries. When the app
//   starts, MainActivity calls System.loadLibrary("frida-gadget"), which loads
//   the Gadget into the process. The Gadget opens a Frida server *inside* the
//   app's own process — no root required, because we're running as the app user.
//
//   You then connect from your laptop over USB and inject this script:
//
//   $ frida -U -n Gadget -l bypass_root_detection.js
//
//   (-n Gadget  →  attach to the waiting Gadget server by name)
//   (-f com.hackdroid.demo  →  use this form on rooted devices instead)
//
// WHAT THIS SCRIPT DOES:
//
//   Java.perform() runs inside the app's Dalvik/ART runtime. Java.use() gives
//   us a JavaScript proxy for the RootChecker class. We replace the
//   implementation of each check method so it always returns false — the app
//   now believes it is running on a clean, stock device, regardless of reality.
//
//   This exact technique is used against production apps to:
//     • Bypass licence/DRM checks        • Defeat jailbreak/root detection
//     • Unlock debug menus               • Intercept encrypted traffic (w/ SSL bypass)
//     • Dump memory / extract secrets
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
