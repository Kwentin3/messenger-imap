# Android Native Build Guard Refactor Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

## 1. Summary

The Android build was refactored to fail fast when the Delta Chat native core wrapper library is missing. This prevents publishing APKs that install but crash on launch because `scripts/ndk-make.sh` was skipped.

## 2. Root Cause Addressed

The broken `android-internal-smoke-0.1.0` APKs were built with Gradle only:

```powershell
.\gradlew.bat assembleDebug --stacktrace
```

Static APK inspection showed only `libjingle_peerconnection_so.so` in the APK native libraries. The expected Delta Chat JNI wrapper library from the native build step was missing.

## 3. Android Refactor

Changed in `Kwentin3/messenger-imap-android`:

- `build.gradle`;
- `BUILDING.md`;
- `README.md`.

The new Gradle task is:

```text
verifyNativeCoreLibraries
```

It checks for:

```text
libs/<abi>/libnative-utils.so
```

Default required ABIs:

- `armeabi-v7a`
- `arm64-v8a`
- `x86`
- `x86_64`

If `ndkArch` exists from a single-ABI native build, only that ABI is required.

`preBuild` depends on `verifyNativeCoreLibraries`, so APK packaging fails before producing a broken installable APK.

## 4. Documentation Updates

Android repo:

- `README.md` now documents the native build requirement and build guard.
- `BUILDING.md` now states that Gradle fails if `libnative-utils.so` is missing.

Meta repo:

- `ANDROID_INTERNAL_SMOKE_APK_CRASH_TRIAGE.report.md` now records the build guard.
- This refactor report documents the safety change.

## 5. Verification

Verification was run in `C:\work\messenger-imap-android` on branch `main`.

```powershell
.\gradlew.bat verifyNativeCoreLibraries --stacktrace
```

Result:

- Gradle fails before packaging;
- failure message lists missing `libs/<abi>/libnative-utils.so`;
- message instructs to run `scripts/ndk-make.sh` before Gradle.
- this is expected in the current local environment because the native core has not been built.

APK packaging was also checked:

```powershell
.\gradlew.bat assembleFossDebug --stacktrace
```

Result:

- build stops at `:verifyNativeCoreLibraries`;
- no replacement APK is produced;
- the old failure mode is blocked before an installable broken APK can be packaged.

The current local environment still lacks the complete native build toolchain, so a corrected APK was not produced in this refactor step.

## 6. Remaining Action

Build and publish replacement `android-internal-smoke-0.1.1` only after:

1. Android NDK and Rust cross-compilation toolchain are configured.
2. `scripts/ndk-make.sh` succeeds.
3. `./gradlew assembleDebug` succeeds.
4. APK inspection confirms `libnative-utils.so` is packaged.
5. Runtime smoke passes on a real Android device.

## 7. Safety Confirmation

- APK committed to git: no.
- AAB committed to git: no.
- Build outputs committed to git: no.
- `.env` committed to git: no.
- Secrets committed to git: no.
- Signing keys committed to git: no.
- Server / Traefik changes: no.
