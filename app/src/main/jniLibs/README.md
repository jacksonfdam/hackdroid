# Frida Gadget

Drop the correct `libfrida-gadget.so` here to enable Frida on non-rooted devices.

## Download

Get the matching version from https://github.com/frida/frida/releases

```bash
FRIDA_VERSION=$(frida --version)

# arm64-v8a  (most modern Android devices)
curl -L "https://github.com/frida/frida/releases/download/${FRIDA_VERSION}/frida-gadget-${FRIDA_VERSION}-android-arm64.so.xz" \
  | xz -d > app/src/main/jniLibs/arm64-v8a/libfrida-gadget.so

# armeabi-v7a  (32-bit devices)
curl -L "https://github.com/frida/frida/releases/download/${FRIDA_VERSION}/frida-gadget-${FRIDA_VERSION}-android-arm.so.xz" \
  | xz -d > app/src/main/jniLibs/armeabi-v7a/libfrida-gadget.so

# x86_64  (emulator)
curl -L "https://github.com/frida/frida/releases/download/${FRIDA_VERSION}/frida-gadget-${FRIDA_VERSION}-android-x86_64.so.xz" \
  | xz -d > app/src/main/jniLibs/x86_64/libfrida-gadget.so
```

## Usage

1. Place the `.so` files above and rebuild: `./gradlew assembleDebug`
2. Install: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
3. Launch the app — the screen will **freeze** (Gadget is waiting for a debugger)
4. Forward the Gadget TCP port:
   ```bash
   adb forward tcp:27042 tcp:27042
   ```
5. Attach Frida over TCP:
   ```bash
   frida -H 127.0.0.1:27042 -l app/src/main/assets/frida_scripts/bypass_root_detection.js
   ```
   The app unfreezes and the hook is live.

> **Why `-H` instead of `-U -n Gadget`?**  
> The embedded Gadget listens on a TCP socket (not the USB frida-server transport).  
> `-U -n` scans a running frida-server — that requires root.  
> `-H 127.0.0.1:27042` talks directly to the Gadget via ADB port-forwarding — no root needed.

The `.so` files are gitignored (they're ~25 MB each).  
The `.config.so` config file is committed — it tells the Gadget to listen on port 27042 and pause on load.
