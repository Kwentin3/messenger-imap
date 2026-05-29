# Android Internal Smoke APK Release 0.1.1 Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

Release: https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.1

## 1. Executive Summary

Android internal smoke APK `0.1.1` was built and published as a GitHub pre-release.

This release replaces broken `android-internal-smoke-0.1.0`, which installed on a Huawei device but crashed on launch because it was built without the Delta Chat native core step.

The `0.1.1` APK includes `lib/arm64-v8a/libnative-utils.so` and is ready for owner runtime smoke. It is not a production release.

## 2. Why 0.1.0 Was Broken

Release `0.1.0` was built with Gradle only:

```powershell
.\gradlew.bat assembleDebug --stacktrace
```

Delta Chat Android also requires the native core build step before Gradle:

```bash
scripts/ndk-make.sh
```

Static APK inspection of `0.1.0` showed native APK libraries contained `libjingle_peerconnection_so.so` only. The required JNI wrapper `libnative-utils.so` was missing.

## 3. Native Build Environment

Local build environment used:

- JDK: Microsoft OpenJDK 17.
- Android SDK: `C:\Android\android-sdk`.
- Android NDK: `27.0.12077973`.
- Rust toolchain: `1.91.1`.
- Rust/Cargo home moved to ASCII paths:
  - `C:\tools\rustup`
  - `C:\tools\cargo`
- MSYS2 shell used for Unix-like Perl and `make`.
- Windows NDK clang wrapper directory used for local Windows-host native build:
  - `C:\tools\android-ndk-clang-wrappers`

## 4. Native Build Command

Built a single ABI for Huawei smoke:

```bash
scripts/ndk-make.sh arm64-v8a
```

Native output verified:

```text
libs/arm64-v8a/libnative-utils.so
```

ABI coverage:

```text
arm64-v8a
```

## 5. Gradle Build Command

Verification:

```powershell
.\gradlew.bat verifyNativeCoreLibraries --stacktrace
```

Result:

```text
BUILD SUCCESSFUL
```

APK build:

```powershell
.\gradlew.bat assembleFossDebug -PABI_FILTER=arm64-v8a --stacktrace
```

Result:

```text
BUILD SUCCESSFUL
```

## 6. APK Inspection Result

APK inspected:

```text
build/outputs/apk/foss/debug/messenger-imap-android-foss-debug-2.50.0.apk
```

Native libraries found:

```text
lib/arm64-v8a/libjingle_peerconnection_so.so
lib/arm64-v8a/libnative-utils.so
```

The APK contains the required Delta Chat native core wrapper.

## 7. Uploaded Release

Release URL:

https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.1

Release type:

- GitHub pre-release.
- Not production.
- Internal debug build only.

Release target commit:

```text
61f9c4a8d1f6fc1de2fec8189ac4b16b996ef6a3
```

Branch:

```text
fix/native-core-smoke-build
```

## 8. APK Filename

Uploaded asset:

```text
messenger-imap-android-foss-debug-2.50.0.apk
```

Recommended APK:

```text
messenger-imap-android-foss-debug-2.50.0.apk
```

## 9. SHA-256

```text
FB7FA4913A4E8161472B2C2A94D68F84927538D9A92782A336E2A5346F361110
```

## 10. Runtime Smoke Checklist

1. Uninstall previous broken `0.1.0` build if installed.
2. Install `0.1.1` FOSS debug APK.
3. Launch app.
4. Confirm no crash on startup.
5. Confirm standard Delta Chat welcome/account setup path is reachable.
6. Confirm corporate onboarding entry is visible.
7. Open corporate onboarding placeholder.
8. Open fallback invite code entry.
9. Enter dummy code.
10. Confirm raw token is not displayed.
11. Confirm back navigation works.

If the app crashes, collect logcat and stop release validation.

## 11. Safety Checks

- APK committed to git: no.
- AAB committed to git: no.
- Build outputs committed to git: no.
- `.env` committed to git: no.
- Secrets committed to git: no.
- Signing keys committed to git: no.
- Package ID changed: no.
- Signing config changed: no.
- Server / Traefik changes: no.
- Control Plane backend changes: no.
- `verifyNativeCoreLibraries` disabled or bypassed: no.

## 12. Remaining Blockers

- Runtime smoke on a real Android device is still pending.
- `0.1.1` is arm64-only; broader ABI coverage requires all-ABI native build.
- Production signing and release pipeline are still out of scope.
- Control Plane backend is not implemented.
- Directory/API integration remains placeholder/fixture-level.
- Invite activation remains placeholder-level.
