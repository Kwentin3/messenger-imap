# Android Internal Smoke APK Crash Triage Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

Release: https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0

## 1. Executive Summary

Runtime smoke failed. The owner installed both published Android debug APK assets on a Huawei phone. Both APKs installed successfully and crashed on launch.

The release must be treated as broken. It is not a valid smoke build.

## 2. Affected Assets

- `messenger-imap-android-foss-debug-2.50.0.apk`
- `messenger-imap-android-gplay-debug-2.50.0.apk`

## 3. Device Result

- Device family: Huawei
- Install result: successful for both assets
- Launch result: crash on open for both assets
- Runtime smoke verdict: failed

## 4. Static APK Inspection

Both APKs include ABI entries for:

- `arm64-v8a`
- `armeabi-v7a`
- `x86`
- `x86_64`

Both APKs include only this native library family:

- `libjingle_peerconnection_so.so`

The APKs do not include expected Delta Chat core/native wrapper libraries built from `jni/`, such as the output produced by `scripts/ndk-make.sh`.

## 5. Likely Root Cause

The release APKs were built with:

```powershell
.\gradlew.bat assembleDebug --stacktrace
```

Delta Chat Android build documentation requires the native core build step before Gradle:

```bash
scripts/ndk-make.sh
./gradlew assembleDebug
```

Because `scripts/ndk-make.sh` was not run before the published build, the APKs likely miss required native Delta Chat core libraries and crash when the app loads JNI/native code.

This conclusion should be confirmed with logcat, but the static APK contents already show the build was incomplete.

## 6. Current Build Environment Blocker

The local Windows environment used for this triage does not currently expose the full native build toolchain in PATH:

- Rust/Cargo not found in PATH.
- Android NDK / `ndk-build` not found under the current Android SDK directory.
- WSL is present but no usable Linux distribution/toolchain was confirmed.

Therefore, a corrected APK was not rebuilt in this step.

## 7. Required Fix

Produce a replacement internal smoke APK with the full native build sequence:

1. Install/configure Android NDK matching the project requirements.
2. Install Rust and Android cross-compilation toolchains.
3. Initialize submodules if needed:

```bash
git submodule update --init --recursive
```

4. Build native core:

```bash
scripts/ndk-make.sh
```

5. Build APK:

```bash
./gradlew assembleDebug
```

6. Verify the resulting APK contains Delta Chat native libraries, not only `libjingle_peerconnection_so.so`.
7. Publish a new pre-release, for example `android-internal-smoke-0.1.1`.
8. Mark `android-internal-smoke-0.1.0` as broken.

## 7.1 Build Guard Refactor

The Android fork now includes a Gradle build guard:

- task: `verifyNativeCoreLibraries`;
- required library: `libs/<abi>/libnative-utils.so`;
- default expected ABIs: `armeabi-v7a`, `arm64-v8a`, `x86`, `x86_64`;
- if `ndkArch` exists from a single-ABI `scripts/ndk-make.sh <abi>` run, only that ABI is required;
- `preBuild` depends on the guard, so APK packaging fails before producing an installable broken APK.

This prevents repeating the `android-internal-smoke-0.1.0` failure mode where Gradle produced APKs without the Delta Chat native core wrapper.

Verification on the incomplete local native-build environment:

- `.\gradlew.bat verifyNativeCoreLibraries --stacktrace` fails with the expected missing `libnative-utils.so` message.
- `.\gradlew.bat assembleFossDebug --stacktrace` stops at `:verifyNativeCoreLibraries` before APK packaging.
- no replacement APK was produced during this refactor.

## 8. Logcat Needed

To confirm the exact exception on the Huawei device:

```powershell
adb logcat -c
adb shell monkey -p com.b44t.messenger.beta 1
adb logcat -d -t 1000 > huawei-foss-crash-logcat.txt
```

For GPlay debug:

```powershell
adb logcat -c
adb shell monkey -p chat.delta.beta 1
adb logcat -d -t 1000 > huawei-gplay-crash-logcat.txt
```

Do not commit raw logcat if it contains account names, tokens, addresses, or device identifiers. Redact before sharing.

## 9. Immediate Documentation Action

- Mark the current release as broken.
- Update README status so the APK is not treated as a valid smoke build.
- Keep APK binaries out of git.

## 10. Verdict

`android-internal-smoke-0.1.0` is rejected for runtime smoke. A corrected build is required.
