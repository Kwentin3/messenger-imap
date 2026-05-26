# Build/Runtime Validation Report

## 1. Executive Summary

MVP-0a Android Diagnostics APK build workspace was raised on 2026-05-13. The Android toolchain is now installed/configured, Gradle wrapper is added and debug APK builds successfully.

Runtime validation was initially blocked because no Android device was connected and no complete emulator/AVD was available. After manual transfer to a Huawei phone, the APK launched and produced the first exported JSON report.

The first runtime report is a Mail.ru `single_account_smoke` run on normal mobile network. It confirms foreground connectivity/auth for IMAP/SMTP on that device/network, but it is not a canonical transport proof because no two-account delivery was performed.

## 2. Environment

- OS: Windows Server 2019 / Windows 10.0.17763 runtime.
- Workspace: `d:\Users\Roman\Desktop\Проекты\mesenger\prototypes\android-diagnostics`.
- Android SDK: `C:\Android\android-sdk`.
- Build JDK: `C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot`.
- SDK manager compatibility JDK used for package installation: `C:\Program Files\Java\jdk1.8.0_211`.

The workspace path contains Cyrillic characters. Android Gradle Plugin blocks such paths on Windows by default, so `gradle.properties` now includes `android.overridePathCheck=true`.

## 3. Toolchain Versions

- JDK: Microsoft OpenJDK 17.0.19.
- Gradle wrapper: Gradle 8.13.
- Global Gradle installed for wrapper generation: Gradle 9.5.0.
- Android SDK platform: `android-36`.
- Android Build Tools: `36.0.0`; AGP also installed/used `35.0.0` during build.
- Android Platform Tools / adb: 37.0.0.

## 4. Build Commands

Environment setup used for build:

```powershell
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
$env:ANDROID_HOME='C:\Android\android-sdk'
$env:ANDROID_SDK_ROOT='C:\Android\android-sdk'
```

Wrapper generation:

```powershell
gradle wrapper --gradle-version 8.13 --distribution-type bin --no-daemon
```

APK build:

```powershell
cd prototypes/android-diagnostics
.\gradlew.bat clean assembleDebug --no-daemon
```

## 5. Build Result

Build status: passed.

The successful build completed `:app:assembleDebug`. Two compile blockers were fixed during validation:

- `BuildConfig` was unavailable under AGP 8 defaults, fixed by enabling `buildFeatures { buildConfig true }`.
- `NetProbe.openTlsSocket` used `SSLSocketFactory.getDefault()` through the base `SocketFactory` type, fixed by casting to `SSLSocketFactory` before calling `createSocket(Socket, host, port, autoClose)`.

## 6. APK Artifact

Artifact:

```text
prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk
```

Observed size:

```text
36052 bytes
```

The prototype now has `.gitignore` rules for `.gradle/`, `build/`, `app/build/`, `local.properties` and APK outputs.

## 7. Install Result

Install status: blocked.

Command:

```powershell
adb devices -l
```

Result:

```text
List of devices attached
```

No physical Android device was connected.

## 8. Launch Result

Launch status: passed by manual device test.

Initial local validation still had no Android device/emulator. The emulator package was partially installed, but there is no AVD and the attempted `system-images;android-35;google_apis;x86_64` installation timed out after 20 minutes, leaving the system image incomplete.

Manual device result:

- Device: HUAWEI CET-LX9.
- Android: 12 / SDK 31.
- APK installation: passed after resolving Huawei installer/source-permission flow.
- App launch: passed.
- Exported report: `docs/research/imapdiag_20260513_220755_mailru_unknown_operator_normal_mobile_foreground.json`.

## 9. UI Validation

Runtime UI validation is blocked by no device/emulator.

Not validated at runtime:

- app opens without crash;
- provider selector interaction;
- delivery mode selector interaction;
- secure password field rendering;
- debug override visibility;
- required field validation.

These remain manual QA items for the first device run.

## 10. Runtime Provider Test

Provider transport test status: partial smoke passed.

First runtime report:

- provider: Mail.ru;
- mode: `single_account_smoke`;
- network type: `mobile`;
- manual network mode: `normal_mobile`;
- IMAP DNS/TCP/TLS/greeting/login/SELECT/IDLE: passed;
- SMTP DNS/TCP/TLS/greeting/EHLO/AUTH: passed;
- send/receive correlation: not requested;
- result: `diagnostic_only`;
- field validity for whitelist conclusion: false.

No Wi-Fi control, two-account canonical delivery or whitelist/restricted mobile conclusion is made.

## 11. JSON Export Validation

Runtime export status: partially passed.

Static/runtime checks completed:

- all sample `*.json` reports under `sample-reports/` parse as JSON;
- export filename convention is implemented in source;
- JSON schema file exists at `schemas/diagnostic-report.schema.json`.
- runtime JSON report parses successfully;
- runtime report contains schemaVersion, runId, app/device/network/provider/checks/messageCorrelation/result/fieldValidity;
- runtime report does not include app password, raw AUTH command, raw protocol transcript or raw logcat.

Not validated:

- schema validation of a runtime-generated report.

Observed issue: the saved file name lacks the final result suffix required by the Blueprint convention. Expected shape would include `_diagnostic_only.json`. This may be either a picker/manual rename artifact or an APK/version mismatch and should be checked on the next export.

## 12. Logcat No-Secret Validation

Runtime logcat validation status: blocked by no device/emulator.

Static no-secret scan was performed. It found expected source-code variable names and documentation text around password/auth handling, but no real credentials, app passwords, tokens, raw logcat dumps or real provider email local-parts in committed project files.

Do not treat this as replacement for runtime logcat QA. The next device run must still execute:

```powershell
adb logcat -c
# run diagnostics
adb logcat -d
```

The raw logcat output must not be committed.

## 13. Manifest Permissions

Built APK permissions were checked with `aapt dump permissions`.

Observed permissions:

```text
android.permission.INTERNET
android.permission.ACCESS_NETWORK_STATE
```

No dangerous permissions, background service permissions, notification permissions or battery-optimization permissions were present.

## 14. Issues Found

- Java/JDK, Gradle, Android SDK and adb were initially unavailable.
- Android SDK package installation required JDK 8 for the legacy `sdkmanager` bundled by Chocolatey.
- Android Gradle Plugin rejected the non-ASCII workspace path on Windows until `android.overridePathCheck=true` was added.
- `BuildConfig` generation was disabled by default.
- `SSLSocketFactory` call needed explicit typing.
- No connected device.
- Emulator fallback incomplete due system image install timeout.

## 15. Fixes Applied

- Installed/configured JDK 17, Gradle, Android SDK, platform-tools/adb, platform `android-36` and build-tools.
- Added Gradle wrapper 8.13.
- Added `gradle.properties` path-check override for this Windows workspace.
- Enabled `BuildConfig` generation.
- Fixed TLS socket factory typing.
- Added prototype-local `.gitignore`.
- Updated README, implementation report, self-audit and manual QA checklist.

## 16. Remaining Blockers

- Need repeatable physical Android phone/tablet validation with captured adb/logcat.
- Need controlled test mailboxes and app passwords.
- Need Wi-Fi control preflight before any mobile/whitelist conclusion.
- Need two-account canonical delivery.
- Need runtime JSON schema validation.
- Need runtime logcat no-secret validation.
- Need real provider testing for Mail.ru, Yandex and VK Mail.

## 17. Next Steps

1. Connect a real Android device with SIM and USB debugging enabled.
2. Run `adb install -r app/build/outputs/apk/debug/app-debug.apk`.
3. Launch the app and complete UI validation from `docs/MANUAL_QA_CHECKLIST.md`.
4. Run Wi-Fi control provider preflight with controlled test accounts.
5. Export sanitized JSON and validate it against `schemas/diagnostic-report.schema.json`.
6. Run logcat no-secret check without committing raw logcat.
7. Only after Wi-Fi control pass, proceed to normal mobile and whitelist/restricted mobile tests.
