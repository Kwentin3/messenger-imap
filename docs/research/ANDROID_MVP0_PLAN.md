# Android MVP-0 plan

Дата: 2026-05-13

## Goal

MVP-0 проверяет транспорт и мобильную применимость, а не строит красивый корпоративный мессенджер.

Минимальная цель:

- Android debug APK или отдельный diagnostics APK;
- provider selector Mail.ru / VK Mail / Yandex;
- email + app password input;
- IMAP/SMTP diagnostics;
- test send/receive;
- IMAP IDLE latency check;
- JSON report export without secrets;
- manual network mode label;
- repeatable Android field protocol;
- basic corporate domain allowlist check.

## Recommended route

Сначала сделать отдельное minimal Android diagnostics app. Параллельно поднять сборку `deltachat-android`. После подтверждения транспорта перенести diagnostics screen в Android fork/wrapper.

Причина: текущий главный риск - не UI Delta Chat, а фактическая доступность IMAP/SMTP и background delivery в mobile whitelist. Это быстрее проверить отдельным APK.

## Milestones

### M0. Build environment

- Установить JDK 17+.
- Установить Android SDK/Platform Tools/NDK `27.0.12077973`.
- Установить Rust.
- Собрать `deltachat-android` `assembleFossDebug`.
- Установить APK на Android device.

Complexity: medium.

### M1. Standalone diagnostics APK

- Provider selector.
- Email/app password input.
- Manual network labels.
- DNS/TCP/TLS probes.
- IMAP login/select/idle.
- SMTP login/send.
- Receive correlation.
- JSON export.

Complexity: medium.

### M2. Field test

- 3 repetitions per combination.
- Operators: MTS, Beeline, MegaFon, T2.
- Providers: Mail.ru, VK Mail, Yandex.
- States: foreground, background, locked screen.
- Networks: Wi-Fi control, normal mobile, whitelist/restricted mobile.

Complexity: high due coordination, not code.

### M3. Delta Chat Android integration spike

- Add debug diagnostics entry point.
- Add corporate provider selector in onboarding.
- Add domain allowlist validation.
- Keep core untouched.

Complexity: medium.

## Likely files/modules to change later

Inside `deltachat-android`:

- `src/main/java/org/thoughtcrime/securesms/relay/EditRelayActivity.java`
- `src/main/res/layout/activity_edittransport.xml`
- new `src/main/java/org/thoughtcrime/securesms/diagnostics/*`
- new diagnostic layout resources
- `src/main/AndroidManifest.xml` for new Activity
- `src/main/java/org/thoughtcrime/securesms/util/Prefs.java` for managed-mode flag
- optional strings under `src/main/res/values/strings.xml`

Avoid changing:

- `jni/deltachat-core-rust`;
- `jni/dc_wrapper.c`, unless a necessary core API is missing;
- database migrations;
- Autocrypt/SecureJoin/group protocol;
- core sync scheduler.

## Backlog

1. Prepare Android build host.
2. Build `assembleFossDebug`.
3. Install upstream debug APK on test device.
4. Verify classic email login screen for Mail.ru/Yandex/VK test accounts.
5. Create standalone diagnostics APK.
6. Implement provider presets.
7. Implement DNS/TCP/TLS checks.
8. Implement IMAP login/select/idle.
9. Implement SMTP login/send.
10. Implement receive correlation.
11. Implement sanitized JSON export.
12. Implement manual field-test metadata.
13. Run Wi-Fi control tests.
14. Run normal mobile tests.
15. Run whitelist/restricted tests.
16. Analyze provider/operator matrix.
17. Decide fork vs wrapper vs separate app.

## Definition of Done

- APK runs on real Android phone with SIM.
- No real secrets in code/repo/log/export.
- At least two controlled test mailboxes per provider where possible.
- Each test captures network type, operator, provider, app build, battery state.
- Report classifies failure stage.
- Results repeated at least 3 times per scenario.
- Decision made whether to invest in `deltachat-android` fork.

## Risks

- IMAP/SMTP ports may not be whitelisted even if webmail is.
- Android Doze may break background IDLE.
- Provider may block SMTP auth or classify test mail as spam.
- App passwords may require provider-specific setup.
- FCM path is not applicable to ordinary IMAP providers.
- Full Delta Chat Android build setup is non-trivial on Windows.

## Verdict

`deltachat-android` remains a strong future base for messenger UX. But MVP-0 should first prove Android transport behavior with a minimal diagnostics APK and real SIM field tests.
