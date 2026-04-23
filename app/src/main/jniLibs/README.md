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
2. Install the APK
3. Launch the app manually on device
4. Connect: `frida -U -n Gadget -l frida_scripts/bypass_root_detection.js`

The `.so` files are gitignored (they're ~10 MB each).
