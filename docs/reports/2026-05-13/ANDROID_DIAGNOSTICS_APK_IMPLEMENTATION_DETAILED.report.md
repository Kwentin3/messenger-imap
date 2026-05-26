# Android Diagnostics APK Implementation Detailed Report

Дата: 2026-05-13  
Статус: implementation completed, build/runtime validation blocked by local environment  
Scope: standalone Android MVP-0a foreground transport diagnostics  
Рабочая зона: `prototypes/android-diagnostics`

## Executive Summary

Реализован standalone Android diagnostics project для MVP-0a: foreground-only проверки IMAP/SMTP-транспорта на Android-устройстве. Проект создан отдельно от Delta Chat Android и chatmail/core, без изменений upstream-кода, JNI, sync, encryption или database migrations.

Реализованы provider presets для Mail.ru, VK Mail и Yandex, runtime credential input, single-account smoke mode, two-account canonical delivery mode, DNS/TCP/TLS checks, IMAP login/select/IDLE, SMTP auth/send, Message-ID correlation, Spam/Junk heuristic detection, timeout policy export, sanitized JSON export, fieldValidity logic, schema, redacted sample reports и self-audit документация.

APK не был собран на текущей машине: отсутствуют `java`, `gradle`, Android SDK и `adb`. Это environment blocker, а не сознательно пропущенный шаг. Field tests на Android device with SIM не выполнялись и не имитировались.

## Implemented Artifacts

Основной прототип:

- `prototypes/android-diagnostics/settings.gradle`
- `prototypes/android-diagnostics/build.gradle`
- `prototypes/android-diagnostics/app/build.gradle`
- `prototypes/android-diagnostics/app/src/main/AndroidManifest.xml`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/*`
- `prototypes/android-diagnostics/app/src/main/res/values/*`

Документация прототипа:

- `prototypes/android-diagnostics/README.md`
- `prototypes/android-diagnostics/docs/IMPLEMENTATION_NOTES.md`
- `prototypes/android-diagnostics/docs/MANUAL_QA_CHECKLIST.md`
- `prototypes/android-diagnostics/docs/SELF_AUDIT_REPORT.md`
- `prototypes/android-diagnostics/docs/IMPLEMENTATION_REPORT.md`

JSON/report artifacts:

- `prototypes/android-diagnostics/schemas/diagnostic-report.schema.json`
- `prototypes/android-diagnostics/sample-reports/sample-transport-pass.redacted.json`
- `prototypes/android-diagnostics/sample-reports/sample-network-whitelist-fail.redacted.json`
- `prototypes/android-diagnostics/sample-reports/sample-auth-fail.redacted.json`
- `prototypes/android-diagnostics/sample-reports/sample-inconclusive-vpn.redacted.json`

Blueprint decision:

- `docs/blueprints/ANDROID_MAIL_LIBRARY_DECISION.md`

## Architecture Summary

Прототип сделан как отдельное Android-приложение на Java с простым native UI, без сторонних mail libraries. Выбран manual protocol approach over `SSLSocket`, потому что MVP-0a требует пошаговой диагностики и строгого контроля логирования:

- DNS resolve;
- TCP connect;
- TLS handshake;
- TLS metadata extraction;
- IMAP greeting/login/select/IDLE/search/list;
- SMTP greeting/EHLO/AUTH/send;
- Message-ID receive correlation.

Ключевые классы:

- `MainActivity.java` - UI, field validation, запуск foreground diagnostics, JSON export через Android document picker.
- `ProviderConfig.java` - data-driven provider presets.
- `DiagnosticRunner.java` - foreground pipeline.
- `ImapSession.java` - минимальные IMAP commands over TLS.
- `SmtpSession.java` - минимальные SMTP commands over TLS.
- `NetProbe.java` - DNS/TCP/TLS probes.
- `DiagnosticReport.java` - sanitized report model, fieldValidity, filename convention.
- `NetworkMetadata.java` - best-effort network/VPN/battery metadata without dangerous permissions.
- `TimeoutPolicy.java` - defaults from Blueprint.
- `Redactor.java` - email masking, slugging, redacted error handling.

## Mail Protocol Decision

Перед реализацией создан decision note: `docs/blueprints/ANDROID_MAIL_LIBRARY_DECISION.md`.

Сравнивались:

- Jakarta Mail / Angus Mail;
- Apache Commons Net;
- manual protocol over `SSLSocket`;
- K-9/Thunderbird Android mail stack as broader candidate.

Решение: manual protocol over `SSLSocket` for MVP-0a.

Причины:

- прямой контроль над диагностическими стадиями;
- отсутствие новых runtime dependencies;
- прямой доступ к `SSLSession` metadata;
- явный timeout control;
- меньше риск secret logging;
- narrow command set лучше соответствует diagnostics APK, а не mail client.

## Provider Model

Реализованы presets:

| Provider | ID | IMAP | SMTP | Notes |
|---|---|---|---|---|
| Mail.ru | `mailru` | `imap.mail.ru:993` TLS | `smtp.mail.ru:465` TLS | app password, full email username |
| VK Mail | `vkmail` | `imap.mail.ru:993` TLS | `smtp.mail.ru:465` TLS | Mail.ru endpoints, app password |
| Yandex | `yandex` | `imap.yandex.com:993` TLS | `smtp.yandex.com:465` TLS | IMAP enabled + app password/provider-approved auth |

Debug endpoint override реализован только для debug build через `BuildConfig.DEBUG`; в report выставляется `debugOverrideUsed`.

## UI Scope

Реализован один простой native Android screen с тремя логическими зонами.

Setup:

- provider selector;
- delivery mode selector;
- sender email/app password;
- receiver email/app password для canonical mode;
- optional self-send smoke checkbox;
- manual network mode: `wifi_control`, `normal_mobile`, `whitelist_restricted`;
- operator manual input;
- region/city manual input;
- notes;
- provider preflight checkbox;
- debug endpoint override in debug builds.

Running diagnostics:

- checklist status выводится текстом;
- app password, AUTH payload и protocol transcript не показываются.

Result:

- result;
- error summary;
- warning about sanitized export;
- export JSON;
- copy sanitized summary.

## Diagnostic Pipeline

Foreground-only pipeline:

1. Collect network metadata.
2. Resolve IMAP host.
3. TCP connect to IMAP.
4. TLS handshake to IMAP.
5. IMAP greeting.
6. IMAP login.
7. IMAP `SELECT INBOX`.
8. IMAP IDLE enter/exit.
9. Resolve SMTP host.
10. TCP connect to SMTP.
11. TLS handshake to SMTP.
12. SMTP greeting.
13. SMTP EHLO.
14. SMTP AUTH.
15. Optional SMTP send for single-account smoke only if explicit checkbox enabled.
16. Required SMTP send for two-account canonical mode.
17. IMAP receive correlation by generated Message-ID.
18. Spam/Junk folder scan if INBOX correlation fails.

Single-account mode is smoke-only and does not produce canonical `transport_pass`. Two-account canonical delivery is the intended proof mode for transport.

## Timeout Policy

Implemented in `TimeoutPolicy.java` and exported in JSON:

- DNS resolve: 5s.
- TCP connect: 10s.
- TLS handshake: 15s.
- IMAP greeting: 10s.
- IMAP login: 20s.
- IMAP SELECT: 15s.
- IMAP IDLE enter/exit: 20s.
- SMTP greeting: 10s.
- SMTP AUTH: 20s.
- SMTP DATA/send accepted: 30s.
- Foreground receive correlation: 120s.
- Polling interval: 5s.
- IDLE observe window: 60s.

No silent timeout increase is implemented.

## JSON Export

Schema exists at:

`prototypes/android-diagnostics/schemas/diagnostic-report.schema.json`

Export is user-initiated via Android system document picker. Filename convention:

```text
imapdiag_YYYYMMDD_HHMMSS_provider_operator_mode_foreground_result.json
```

Report includes:

- `schemaVersion`;
- `timestampUtc`;
- `runId`;
- app version/build;
- device metadata;
- network metadata;
- provider/endpoints;
- delivery mode;
- timeoutPolicy;
- checks array;
- messageCorrelation;
- result;
- errorSummary;
- fieldValidity;
- notes.

Forbidden content is not intentionally exported:

- app password;
- raw auth commands;
- base64 auth strings;
- full SMTP/IMAP transcript;
- OAuth tokens;
- raw logcat;
- sensitive body.

## Field Validity Logic

Implemented `fieldValidity` logic includes:

- whitelist mode check;
- VPN active invalidation;
- provider preflight checkbox;
- network changed invalidation;
- export action flag.

Current nuance: exported-success signal is modeled at export-generation time; after runtime testing it should be revisited to reflect confirmed document write completion.

## Security And Privacy

Implemented:

- password fields use secure input type;
- password fields are cleared after run completion and on activity destroy;
- app passwords are not written into JSON;
- no SharedPreferences credential storage;
- no raw logcat export;
- email local-parts masked by default;
- copied summary is sanitized;
- exceptions are redacted before UI/export;
- generated test body is synthetic.

Residual risk:

- runtime password strings exist in process memory while a run is active;
- logcat validation could not be performed without Android device/adb;
- manual protocol code needs real-device review to verify no platform/library logs leak sensitive data.

## Permissions

Manifest contains only:

- `android.permission.INTERNET`;
- `android.permission.ACCESS_NETWORK_STATE`.

No dangerous permissions were added:

- no `READ_PHONE_STATE`;
- no precise location;
- no contacts;
- no SMS;
- no call log.

No background/notification permissions were added:

- no `POST_NOTIFICATIONS`;
- no `FOREGROUND_SERVICE`;
- no `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`.

## Non-Goals Preserved

Not implemented:

- messenger UX;
- persistent accounts;
- background receive;
- locked-screen receive;
- notifications/push;
- foreground service;
- corporate address book;
- MDM/config import;
- cloud dashboard;
- Delta Chat Android fork;
- chatmail/core changes;
- Delta Chat JNI/sync/encryption/database migrations.

## Validation Performed

Static/local checks performed:

- file structure inspection;
- manifest permission inspection;
- search for dangerous/background permissions;
- JSON parse check for all sample reports;
- JSON parse check for schema;
- secret-pattern grep over prototype and mail decision doc;
- upstream repo status check.

Results:

- manifest contains only `INTERNET` and `ACCESS_NETWORK_STATE`;
- dangerous/background permissions not found;
- sample JSON files parse successfully;
- schema JSON parses successfully;
- secret-pattern grep found no real token/app-password patterns; only expected masked synthetic emails in sample reports;
- `deltachat-android` upstream status remained clean;
- `core` upstream status remained clean.

## Build And Runtime Status

Attempted:

```powershell
cd prototypes/android-diagnostics
gradle assembleDebug
java -version
adb version
```

Observed:

- `gradle`: command not found;
- `java`: command not found;
- `adb`: command not found;
- no `app/build/outputs/apk/` directory;
- no APK artifact generated.

Conclusion: build/install/runtime validation is blocked by missing local Android build environment.

## Known Limitations

- APK not built locally.
- No real Android device/SIM field result.
- No emulator result.
- No logcat no-secret runtime verification.
- No runtime JSON schema validation inside APK.
- Manual IMAP parser is intentionally narrow.
- IMAP folder parsing is basic.
- Spam/Junk detection is heuristic: folder names containing `spam`, `junk`, `bulk`, `спам`.
- SMTP uses `AUTH PLAIN`; some providers may require other auth variants.
- No STARTTLS 587 fallback.
- No OAuth/provider-specific auth.
- No IPv6-specific test mode, only DNS address-family summary.
- No background/locked-screen MVP-0b behavior.

## Risks

- Manual protocol implementation may need provider-specific fixes after first real-device run.
- IMAP folder names and modified UTF-7 may require refinement.
- Some providers may reject current SMTP auth flow.
- Provider anti-spam may affect test-send delivery.
- Android UI currently simple and should be reviewed on real device for ergonomics.
- FieldValidity logic depends on tester honesty for manual network mode and preflight checkbox.
- Build may reveal Android API or Gradle compatibility issues not visible without toolchain.

## Next Steps

1. Install JDK 17+, Android SDK/platform 36, Gradle or Gradle wrapper, and adb.
2. Build debug APK from `prototypes/android-diagnostics`.
3. Install on real Android device with SIM.
4. Run manual QA checklist on Wi-Fi control.
5. Validate exported JSON against schema.
6. Confirm no app passwords in logcat during a controlled run.
7. Run provider preflight for Mail.ru, VK Mail, Yandex.
8. Run normal mobile tests.
9. Run whitelist/restricted mobile tests only after Wi-Fi preflight passes.
10. Feed first real failures back into IMAP/SMTP parser and error taxonomy.

## Verdict

MVP-0a implementation is structurally complete as a standalone prototype and aligns with the refined Blueprint. It is not yet field-ready until the Android build toolchain is installed, APK is built, and at least Wi-Fi control tests pass on a real Android device.

The implementation preserved the main engineering boundary: prove foreground IMAP/SMTP transport first, without turning the prototype into a messenger or touching Delta Chat/chatmail core.
