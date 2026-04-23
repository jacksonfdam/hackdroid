// HackDroid — Frida Script: Bypass SSL Certificate Pinning
// ─────────────────────────────────────────────────────────────────────────────
// Usage:
//   frida -U -f com.hackdroid.demo -l bypass_ssl_pinning.js
//
// What this does:
//   Replaces the app's TrustManager with one that accepts ALL certificates.
//   This allows Burp Suite (or any proxy) to intercept HTTPS traffic even
//   when the app implements certificate pinning.
//
// Setup:
//   1. Run this script with Frida
//   2. Set your device proxy to Burp Suite's listener (e.g. 192.168.x.x:8080)
//   3. All HTTPS traffic is now interceptable
//
// OWASP M5: Insecure Communication
// ─────────────────────────────────────────────────────────────────────────────

Java.perform(function () {
    console.log("[HackDroid] Frida attached — loading SSL pinning bypass");

    // ── Method 1: Override TrustManager ──────────────────────────────────
    var TrustManager = Java.registerClass({
        name: "com.hackdroid.demo.BypassTrustManager",
        implements: [Java.use("javax.net.ssl.X509TrustManager")],
        methods: {
            checkClientTrusted: function (chain, authType) {
                console.log("[HackDroid] checkClientTrusted() — bypassed");
            },
            checkServerTrusted: function (chain, authType) {
                console.log("[HackDroid] checkServerTrusted() — bypassed");
            },
            getAcceptedIssuers: function () {
                return [];
            }
        }
    });

    var SSLContext = Java.use("javax.net.ssl.SSLContext");
    var sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, [TrustManager.$new()], null);
    SSLContext.getDefault.implementation = function () {
        return sslContext;
    };

    // ── Method 2: Override OkHttp CertificatePinner (if used) ────────────
    try {
        var CertificatePinner = Java.use("okhttp3.CertificatePinner");
        CertificatePinner.check.overload("java.lang.String", "java.util.List").implementation = function (hostname, peerCertificates) {
            console.log("[HackDroid] OkHttp CertificatePinner.check() bypassed for: " + hostname);
        };
    } catch (e) {
        console.log("[HackDroid] OkHttp not found — skipping CertificatePinner bypass");
    }

    // ── Method 3: Override Android Network Security Config check ─────────
    try {
        var NetworkSecurityTrustManager = Java.use("android.security.net.config.NetworkSecurityTrustManager");
        NetworkSecurityTrustManager.checkServerTrusted.implementation = function (chain, authType, hostname) {
            console.log("[HackDroid] NetworkSecurityTrustManager bypassed for: " + hostname);
        };
    } catch (e) {
        console.log("[HackDroid] NetworkSecurityTrustManager not found — skipping");
    }

    console.log("[HackDroid] ✓ SSL pinning bypassed — all HTTPS traffic interceptable");
    console.log("[HackDroid] Set proxy: 192.168.x.x:8080 and use Burp Suite to capture traffic");
});
