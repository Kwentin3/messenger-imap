# Implementation Report

## 1. What Was Implemented

Implemented standalone MVP-0a Android diagnostics project in `prototypes/android-diagnostics`.

Key pieces:

- Android app scaffold.
- Provider presets.
- Runtime credential UI.
- Single-account smoke mode.
- Two-account canonical delivery mode.
- DNS/TCP/TLS checks.
- IMAP login/select/IDLE checks.
- SMTP auth/send checks.
- Message-ID correlation.
- Spam/Junk heuristic scan.
- Timeout defaults.
- Sanitized JSON export.
- Export filename convention.
- Field validity logic.
- JSON schema.
- Synthetic/redacted sample reports.
- README, QA checklist, implementation notes and self-audit.

## 2. What Was Not Implemented

- Background receive.
- Locked-screen receive.
- Notifications.
- Foreground service.
- Delta Chat Android integration.
- chatmail/core changes.
- STARTTLS 587 fallback.
- OAuth/provider-specific auth flows.
- Cloud dashboard.

These are outside MVP-0a or intentionally deferred.

## 3. How To Build

Validated command on 2026-05-13:

```powershell
cd prototypes/android-diagnostics
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
$env:ANDROID_HOME='C:\Android\android-sdk'
$env:ANDROID_SDK_ROOT='C:\Android\android-sdk'
.\gradlew.bat assembleDebug
```

## 4. APK Artifact

APK artifact was created:

```text
prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk
```

Observed size:

```text
36052 bytes
```

The APK is a local build artifact and is ignored by `prototypes/android-diagnostics/.gitignore`.

## 4.1 Build/Runtime Validation Update

Build/runtime validation was performed on 2026-05-13.

Toolchain status:

- JDK 17 installed/configured for build: Microsoft OpenJDK 17.0.19.
- Android SDK installed at `C:\Android\android-sdk`.
- Android SDK platform installed: `android-36`.
- Android Build Tools installed: `36.0.0` and AGP also auto-installed `35.0.0`.
- Platform tools/adb installed: Android Debug Bridge 37.0.0.
- Gradle wrapper added: Gradle 8.13.

Build status:

- `.\gradlew.bat clean assembleDebug --no-daemon`: passed.
- APK path: `app/build/outputs/apk/debug/app-debug.apk`.

Runtime status:

- `adb devices -l`: no connected Android devices.
- Emulator command became available after partial SDK install, but no AVD exists.
- `system-images;android-35;google_apis;x86_64` installation was attempted and timed out after 20 minutes; the system image directory is incomplete.
- APK install/launch: blocked by no device/emulator.
- Wi-Fi provider test: blocked by no device/emulator and no test credentials supplied.
- JSON export runtime test: blocked by no device/emulator.
- Logcat no-secret runtime test: blocked by no device/emulator.

Build issues fixed during validation:

- Added `gradle.properties` with `android.overridePathCheck=true` because Android Gradle Plugin blocks Windows builds in non-ASCII paths by default.
- Enabled `buildFeatures { buildConfig true }` so `BuildConfig` references compile under AGP 8.
- Fixed `SSLSocketFactory` typing in `NetProbe.openTlsSocket`.

Manifest permissions confirmed from built APK:

```text
android.permission.INTERNET
android.permission.ACCESS_NETWORK_STATE
```

## 5. How To Run A Test

1. Install APK on Android device with SIM.
2. Open app.
3. Select provider.
4. Select single-account smoke or two-account canonical.
5. Enter runtime credentials.
6. Select network mode.
7. Enter operator/region.
8. Confirm Wi-Fi preflight if done.
9. Run diagnostics.
10. Export JSON.

## 6. How To Export JSON

After a run, tap `Export JSON`. The app opens Android system document picker and suggests a filename:

```text
imapdiag_YYYYMMDD_HHMMSS_provider_operator_mode_foreground_result.json
```

## 7. Checks Performed

Performed locally after toolchain setup:

- file structure verification;
- manifest permission inspection from source and built APK;
- static no-secret grep;
- sample report review;
- JSON parse check for sample reports;
- Gradle wrapper generation;
- debug APK build;
- adb device discovery.

Blocked:

- APK install;
- runtime UI test;
- logcat secret check;
- real provider transport test.

Current blockers:

- no connected Android device;
- no complete local emulator/AVD;
- no test provider accounts/app passwords supplied.

## 8. Self-Audit Result

See `docs/SELF_AUDIT_REPORT.md`.

Summary: implementation is aligned with MVP-0a scope. Build validation now passes. Runtime validation is still blocked by no connected device/emulator.

## 9. Residual Risks

- Manual protocol implementation needs real provider/device validation.
- Some providers may require auth variants beyond `AUTH PLAIN`.
- Folder parsing and Spam/Junk scan may need provider-specific fixes.
- FieldValidity export-success nuance should be revisited after runtime testing.

## 10. Recommended Next Step

Connect a real Android phone with SIM, install the built APK, run the manual QA checklist on Wi-Fi control with controlled test accounts, then perform normal mobile and whitelist/restricted field tests only with redacted JSON exports.
