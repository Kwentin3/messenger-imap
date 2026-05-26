# Android IMAP Diagnostics APK

Standalone MVP-0a Android app for foreground IMAP/SMTP transport diagnostics.

## What This APK Is

This APK checks whether Mail.ru, VK Mail and Yandex IMAP/SMTP endpoints are reachable from the current Android device/network. It is intended for field testing on Android phones/tablets with SIM.

It checks:

- provider preset;
- runtime email/app password login;
- DNS/TCP/TLS;
- IMAP login, `SELECT INBOX`, IDLE enter/exit;
- SMTP AUTH and test send;
- Message-ID receive correlation;
- Spam/Junk placement;
- latency;
- sanitized JSON export.

## What This APK Is Not

- Not a messenger.
- Not a Delta Chat Android fork.
- Not a chatmail/core change.
- Not a background/locked-screen MVP.
- Not push/notification architecture.
- Not a corporate address book.
- Not a cloud dashboard.
- Not MDM/config import.

## Build

Prerequisites:

- JDK 17+.
- Android SDK with platform 36.
- Android Gradle Plugin compatible Gradle.

Build command:

```powershell
cd prototypes/android-diagnostics
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
$env:ANDROID_HOME='C:\Android\android-sdk'
$env:ANDROID_SDK_ROOT='C:\Android\android-sdk'
.\gradlew.bat clean assembleDebug
```

Expected debug APK:

```text
prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk
```

Current validation note: on 2026-05-13 the Windows build workspace was configured with JDK 17, Android SDK platform 36, Android Build Tools 36.0.0 and Gradle wrapper 8.13. `.\gradlew.bat clean assembleDebug` completed successfully. The local project path contains Cyrillic characters, so `gradle.properties` includes `android.overridePathCheck=true`.

## Install

After a successful build:

```powershell
adb install app/build/outputs/apk/debug/app-debug.apk
```

Use a real Android device with SIM for field tests. Emulator/Wi-Fi tests are only control checks.

If `adb devices -l` returns no device and no emulator is available, install/runtime/field checks remain blocked. Do not treat a local build-only result as provider or whitelist validation.

## Provider Setup Notes

Mail.ru:

- IMAP: `imap.mail.ru:993` TLS.
- SMTP: `smtp.mail.ru:465` TLS.
- Use app password for external apps.
- Username is usually full email address.

VK Mail:

- Uses Mail.ru endpoints.
- IMAP: `imap.mail.ru:993` TLS.
- SMTP: `smtp.mail.ru:465` TLS.
- Use app password.

Yandex:

- IMAP: `imap.yandex.com:993` TLS.
- SMTP: `smtp.yandex.com:465` TLS.
- IMAP must be enabled.
- Use app password or provider-approved auth path.

## Provider Preflight Checklist

Before mobile/whitelist tests, verify on Wi-Fi control:

1. Test mailbox created.
2. IMAP enabled where required.
3. App password created.
4. Login works.
5. IMAP `SELECT INBOX` works.
6. SMTP AUTH works.
7. SMTP send works.
8. Receive works.
9. Spam/Junk baseline checked.
10. Provider limitations recorded.
11. App password is not saved in documents/repository.

If auth/send/receive does not work on Wi-Fi control, do not classify field failure as whitelist failure.

## Running MVP-0a

1. Open the app.
2. Select provider.
3. Select delivery mode:
   - single-account smoke;
   - two-account canonical delivery.
4. Enter runtime credentials.
5. Select manual network mode.
6. Enter operator and region/city manually.
7. Confirm provider preflight if it was done.
8. Run foreground diagnostics.
9. Review result.
10. Export sanitized JSON.

## Interpreting Results

- `transport_pass`: canonical two-account send/receive succeeded.
- `network_whitelist_fail`: provider works on control network, but IMAP/SMTP network stage fails in restricted mobile mode.
- `auth_fail`: account/provider setup problem.
- `spam_junk_fail`: message landed in Spam/Junk.
- `diagnostic_only`: useful technical result but not whitelist proof, for example VPN active.
- `inconclusive`: metadata missing, unexpected network change, no preflight or ambiguous failure.

Self-send/single-account mode is smoke only and must not be treated as messenger-like transport proof.

## JSON Export

Export is available only after user action. Exported reports:

- include schemaVersion, runId, app build number, timeout policy and fieldValidity;
- do not include app passwords;
- do not include raw protocol transcript;
- do not include raw logcat;
- mask email local-parts by default.

Filename format:

```text
imapdiag_YYYYMMDD_HHMMSS_provider_operator_mode_scenario_result.json
```

## Security Warnings

- Do not share app passwords.
- Do not paste real credentials into notes.
- Do not attach raw logcat to field reports.
- Sample reports are synthetic/redacted only.

## Field Test Notes

- Run Wi-Fi control first.
- Run normal mobile.
- Run whitelist/restricted mobile.
- Repeat each scenario at least 3 times.
- Do not conclude from one successful run.
- If VPN is active, the run is not valid as whitelist proof.

## Known Limitations

- No background delivery MVP.
- No locked-screen delivery MVP.
- No notification/push path.
- No Delta Chat core behavior proof.
- No messenger UX.
- No cloud dashboard.
- Runtime install/launch was not validated in the current environment because no Android device was connected and the emulator system image installation did not complete within the validation window.
