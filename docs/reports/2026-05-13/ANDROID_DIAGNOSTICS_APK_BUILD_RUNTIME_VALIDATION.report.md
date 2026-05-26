# Android Diagnostics APK Build/Runtime Validation Report

Date: 2026-05-13

Scope: infrastructure validation for MVP-0a Android Diagnostics APK in `prototypes/android-diagnostics/`.

## Executive Summary

The Android build/runtime workspace was raised far enough to produce a debug APK. JDK 17, Android SDK platform/build-tools, adb and Gradle wrapper are now available/configured. `.\gradlew.bat clean assembleDebug --no-daemon` passes.

Runtime validation progressed after manual transfer to a Huawei phone. The APK installed/launched and produced the first exported JSON report. The first run is a Mail.ru `single_account_smoke` on normal mobile network, with all executed IMAP/SMTP connectivity/auth checks passing.

No canonical provider transport, Wi-Fi control or whitelist compatibility result is claimed yet, because the run did not perform two-account delivery and was not in whitelist/restricted mode.

## Build Result

- Gradle wrapper added: Gradle 8.13.
- Build command passed: `.\gradlew.bat clean assembleDebug --no-daemon`.
- APK artifact: `prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk`.
- Observed APK size: 36052 bytes.
- Built APK permissions: `INTERNET`, `ACCESS_NETWORK_STATE`.

## Environment

- JDK: Microsoft OpenJDK 17.0.19.
- Android SDK: `C:\Android\android-sdk`.
- Android SDK platform: `android-36`.
- Android Build Tools: `36.0.0` plus AGP-installed `35.0.0`.
- adb: Android Debug Bridge 37.0.0.
- Global Gradle used for wrapper generation: Gradle 9.5.0.

## Fixes Applied

- Added `prototypes/android-diagnostics/gradle.properties` with `android.overridePathCheck=true` for the non-ASCII Windows workspace path.
- Added Gradle wrapper files under `prototypes/android-diagnostics/`.
- Enabled AGP `BuildConfig` generation in `app/build.gradle`.
- Fixed `SSLSocketFactory` typing in `NetProbe.openTlsSocket`.
- Added prototype-local `.gitignore` for Gradle/build/APK artifacts.
- Updated project README, implementation report, self-audit, manual QA checklist and build/runtime validation report.

## Runtime Status

- Initial local `adb devices -l`: no devices attached.
- Emulator package: partially installed.
- Emulator AVD: none.
- Android system image: incomplete after timeout.
- Manual Huawei APK install: passed.
- App launch: passed.
- First JSON export: passed.
- First provider runtime smoke: Mail.ru normal mobile, passed for executed checks.
- Logcat no-secret runtime validation: still blocked because adb/logcat was not captured.
- Wi-Fi control provider test: still open.
- Two-account canonical delivery: still open.

First runtime JSON:

```text
docs/research/imapdiag_20260513_220755_mailru_unknown_operator_normal_mobile_foreground.json
```

First runtime report:

```text
docs/reports/2026-05-13/IMAPDIAG_MAILRU_NORMAL_MOBILE_SMOKE.report.md
```

## Security/Permissions Status

No dangerous permissions were added. Built APK declares only:

- `android.permission.INTERNET`
- `android.permission.ACCESS_NETWORK_STATE`

Static no-secret scan found expected password/auth terminology in source and docs, but no real app passwords, tokens, raw logcat dumps or real provider test credentials in project files. The first exported runtime JSON also does not contain app password, raw AUTH command, raw IMAP `LOGIN` command, base64 auth payload, OAuth/token material, raw protocol transcript, raw logcat or unmasked email local-part.

## Remaining Blockers

- Controlled two-account provider test accounts and app passwords are required.
- Wi-Fi control preflight is required before any mobile failure can be classified.
- Runtime logcat no-secret check remains open.
- Runtime JSON export validation is partially complete; schema validation of runtime report remains open.
- Provider Wi-Fi preflight remains open.
- Filename convention needs follow-up: the saved file did not include the final result suffix even though source code currently includes it.

## Recommendation

The APK passed the first basic device smoke path. Next step: run Wi-Fi control preflight with controlled two-account Mail.ru accounts, export sanitized JSON, capture adb/logcat no-secret evidence, then proceed to normal mobile and whitelist/restricted mobile tests only after Wi-Fi control passes.
