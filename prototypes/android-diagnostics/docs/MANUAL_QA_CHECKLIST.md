# Manual QA Checklist

## Build Check

- [x] Android SDK installed.
- [x] JDK 17+ installed.
- [x] `.\gradlew.bat clean assembleDebug` succeeds.
- [x] APK artifact exists under `app/build/outputs/apk/`.

## Install Check

- [ ] `adb install` succeeds on Android device with SIM. Blocked on 2026-05-13: no connected device/emulator.
- [ ] App opens without crash. Blocked on 2026-05-13: no connected device/emulator.

## UI Field Validation

- Provider selector has Mail.ru, VK Mail, Yandex.
- Delivery mode selector has single-account smoke and two-account canonical.
- Required email fields validate syntax.
- Empty password blocks run.
- Password fields use secure input type.
- Debug endpoint override is available only in debug build.

## No-Secret Checks

- App password does not appear in result screen.
- App password does not appear in copied summary.
- App password does not appear in exported JSON.
- App password does not appear in logcat during manual run.
- Password fields clear after run completion.
- Raw protocol transcript is not shown or exported.

## Wi-Fi Control Test

- Provider preflight can be completed on Wi-Fi.
- DNS/TCP/TLS stages run.
- IMAP login/select stages run.
- SMTP auth/send stages run.
- Two-account receive correlation works or failure is classified.

## Normal Mobile Test

- Manual network mode set to `normal_mobile`.
- Operator and region/city entered manually.
- Report exports with fieldValidity false for whitelist conclusion.

## Whitelist/Restricted Mobile Test

- Manual network mode set to `whitelist_restricted`.
- VPN inactive.
- Provider preflight checkbox reflects real Wi-Fi control result.
- Repeat count tracked externally by tester.

## Export JSON Validation

- Export uses system document picker.
- Filename follows `imapdiag_YYYYMMDD_HHMMSS_provider_operator_mode_scenario_result.json`.
- JSON contains `schemaVersion`.
- JSON contains app `buildNumber`.
- JSON contains `timeoutPolicy`.
- JSON contains `fieldValidity`.
- JSON validates against `schemas/diagnostic-report.schema.json`.

## Filename Validation

- Filename contains provider.
- Filename contains operator slug or `unknown_operator`.
- Filename contains mode.
- Filename contains `foreground`.
- Filename contains result.
- Filename does not contain email local-part or secrets.

## Sample Report Redaction Check

- Sample reports use synthetic domains.
- Sample reports contain no app passwords.
- Sample reports contain no raw auth payloads.
- Sample reports contain no raw protocol transcript.

## QA Run Log - 2026-05-13 Build/Runtime Validation

- Build check: passed.
- APK artifact: passed, `app/build/outputs/apk/debug/app-debug.apk`.
- Built APK permissions: passed, only `INTERNET` and `ACCESS_NETWORK_STATE`.
- Install check: blocked, `adb devices -l` returned no devices.
- Emulator fallback: blocked, emulator package partially installed but no complete system image/AVD is available.
- UI field validation: blocked, no runtime device/emulator.
- Runtime no-secret checks: blocked, no runtime device/emulator.
- Static no-secret scan: passed with expected source/docs terminology only; no real credentials found.
- Sample report JSON parse: passed for all `*.json` sample reports.
- Wi-Fi control provider test: blocked, no device/emulator and no test credentials supplied.
