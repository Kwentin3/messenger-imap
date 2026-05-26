# Android Diagnostics APK Anamnesis

Дата: 2026-05-13  
Scope: MVP-0a standalone Android Diagnostics APK  
Статус: анамнез реализации, сборки и первых полевых запусков

## 1. Executive summary

Собран и частично проверен standalone Android Diagnostics APK для foreground-only диагностики IMAP/SMTP транспорта на Android-устройстве с SIM. Цель APK - проверить, могут ли Mail.ru / VK Mail / Yandex IMAP/SMTP endpoints быть пригодны как резервный transport для будущего messenger-like клиента в условиях мобильных whitelist/restricted сетей.

APK был реализован, debug APK был собран, вручную перенесен и установлен на телефон Huawei. Приложение запускалось на реальном Android-устройстве, экспортировало sanitized JSON, и пользователь выполнил минимум два реальных теста Mail.ru: один `single_account_smoke` в normal mobile сети и один `two_account_canonical` в Wi-Fi control.

Предварительный вывод: APK работоспособен как MVP-0a диагностика, Mail.ru IMAP/SMTP прошел DNS/TCP/TLS/auth на normal mobile, а Mail.ru two-account delivery прошел на Wi-Fi control. Whitelist/restricted mobile совместимость, стабильность по операторам и пригодность как messenger transport пока не подтверждены.

## 2. Исходная цель MVP-0a

Границы MVP-0a:

- standalone Android diagnostics APK;
- foreground-only transport diagnostics;
- проверка DNS/TCP/TLS/IMAP/SMTP/send/receive на Android device with SIM;
- не messenger;
- не Delta Chat fork;
- не изменение chatmail/core;
- не background/locked-screen MVP;
- не production UX;
- не push/notification/background service architecture;
- не постоянное хранение аккаунтов или credentials.

Canonical proof для MVP-0a - two-account delivery: account A отправляет через SMTP, account B получает через IMAP, сообщение коррелируется по generated Message-ID. Single-account/self-send - только smoke/diagnostic mode.

## 3. Что было реализовано

Фактическая реализация находится в `prototypes/android-diagnostics/`.

Реализовано:

- provider selector;
- presets для Mail.ru, VK Mail, Yandex;
- `single_account_smoke`;
- `two_account_canonical`;
- DNS/TCP/TLS checks;
- TLS metadata capture;
- IMAP greeting/login/`SELECT INBOX`/IDLE enter-exit;
- SMTP greeting/EHLO/AUTH/send;
- generated Message-ID correlation;
- Spam/Junk heuristic scan;
- timeout policy по Blueprint;
- `fieldValidity`;
- sanitized JSON export через Android system document picker;
- filename convention в коде;
- no-secret handling: password fields, redaction, no raw transcript export;
- JSON schema;
- redacted sample reports;
- manifest permissions only `INTERNET` and `ACCESS_NETWORK_STATE`.

Ключевые файлы:

- `prototypes/android-diagnostics/settings.gradle`
- `prototypes/android-diagnostics/build.gradle`
- `prototypes/android-diagnostics/app/build.gradle`
- `prototypes/android-diagnostics/app/src/main/AndroidManifest.xml`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/MainActivity.java`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/ProviderConfig.java`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/DiagnosticRunner.java`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/ImapSession.java`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/SmtpSession.java`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/NetProbe.java`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/DiagnosticReport.java`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/NetworkMetadata.java`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/TimeoutPolicy.java`
- `prototypes/android-diagnostics/app/src/main/java/com/example/imapdiag/Redactor.java`
- `prototypes/android-diagnostics/schemas/diagnostic-report.schema.json`
- `prototypes/android-diagnostics/sample-reports/*.redacted.json`

Связанные документы:

- `docs/blueprints/ANDROID_DIAGNOSTICS_APK_BLUEPRINT.md`
- `docs/blueprints/ANDROID_MAIL_LIBRARY_DECISION.md`
- `docs/research/WHITE_LIST_FIELD_TEST_PROTOCOL.md`
- `prototypes/android-diagnostics/docs/IMPLEMENTATION_REPORT.md`
- `prototypes/android-diagnostics/docs/SELF_AUDIT_REPORT.md`
- `prototypes/android-diagnostics/docs/BUILD_RUNTIME_VALIDATION_REPORT.md`
- `docs/reports/2026-05-13/ANDROID_DIAGNOSTICS_APK_BUILD_RUNTIME_VALIDATION.report.md`
- `docs/reports/2026-05-13/IMAPDIAG_MAILRU_NORMAL_MOBILE_SMOKE.report.md`

## 4. Build/runtime история

Сначала локальная Android build-среда отсутствовала: не были доступны `java`, `gradle`, Android SDK и `adb`. Затем окружение было поднято до успешной сборки.

Известные версии toolchain:

- JDK: Microsoft OpenJDK 17.0.19;
- Android SDK: `C:\Android\android-sdk`;
- Android SDK platform: `android-36`;
- Android Build Tools: `36.0.0`, также AGP установил/использовал `35.0.0`;
- adb: Android Debug Bridge 37.0.0;
- Gradle wrapper: Gradle 8.13;
- global Gradle для генерации wrapper: Gradle 9.5.0;
- Android Gradle Plugin: 8.11.1;
- app `versionName`: `0.0.1`;
- app `versionCode`: `1`;
- `minSdk`: 21;
- `targetSdk`: 36.

Gradle wrapper был добавлен в `prototypes/android-diagnostics/`.

Команда сборки:

```powershell
cd prototypes/android-diagnostics
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
$env:ANDROID_HOME='C:\Android\android-sdk'
$env:ANDROID_SDK_ROOT='C:\Android\android-sdk'
.\gradlew.bat clean assembleDebug --no-daemon
```

APK artifact:

```text
prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk
```

Observed size: `36052 bytes`.

Build errors/fixes:

- Android Gradle Plugin отклонял Windows path с кириллицей; добавлено `gradle.properties` с `android.overridePathCheck=true`.
- `BuildConfig` был недоступен под AGP 8 defaults; включено `buildFeatures { buildConfig true }`.
- `NetProbe.openTlsSocket` требовал корректного `SSLSocketFactory` typing; исправлено.
- Добавлен prototype-local `.gitignore` для Gradle/build/APK artifacts.

Runtime история:

- Локально `adb devices -l` не видел подключенных устройств.
- Emulator fallback не завершен: system image install timed out, AVD не создан.
- APK был вручную перенесен на Huawei phone.
- Установка APK на телефон прошла после прохождения Huawei installer/source-permission flow.
- App launch: passed.
- Runtime crashes: не зафиксированы в имеющихся артефактах.
- JSON export: passed, минимум два exported JSON сохранены в `docs/research/`.

## 5. Устройство и среда тестирования

Из exported JSON:

- device manufacturer/model: HUAWEI CET-LX9;
- Android: 12;
- SDK: 31;
- app: `android-imap-diagnostics`;
- buildType: debug;
- buildNumber: 1;
- VPN active best-effort: `false`;
- battery optimization ignored: `false`;
- foregroundState: `foreground`.

SIM/operator:

- устройство описывалось как телефон с SIM;
- `operatorManual`: `unknown_operator`;
- конкретный оператор, MCC/MNC, регион/город: неизвестно / требует уточнения.

Сети:

- normal mobile: был один Mail.ru `single_account_smoke`;
- Wi-Fi control: был один Mail.ru `two_account_canonical`;
- whitelist/restricted mobile: запусков в имеющихся артефактах нет.

Провайдеры:

- фактически протестирован Mail.ru;
- VK Mail и Yandex в реализации есть, но фактических runtime JSON по ним нет.

Аккаунты:

- использовались Mail.ru аккаунты;
- email local-part в отчетах masked as `***@mail.ru`;
- app passwords, токены, raw auth payloads и полные email в отчет не включаются.

## 6. Какие тестовые сценарии были выполнены пользователем

### Scenario A: Mail.ru normal mobile smoke

- provider: Mail.ru;
- delivery mode: `single_account_smoke`;
- network mode: `normal_mobile`;
- observed network type: `mobile`;
- app state: foreground;
- provider preflight: не подтвержден в fieldValidity;
- JSON export: да;
- logcat/no-secret check: не выполнен / не зафиксирован;
- repeats: 1 known run;
- source JSON: `docs/research/imapdiag_20260513_220755_mailru_unknown_operator_normal_mobile_foreground.json`.

### Scenario B: Mail.ru Wi-Fi control canonical

- provider: Mail.ru;
- delivery mode: `two_account_canonical`;
- network mode: `wifi_control`;
- observed network type: `wifi`;
- app state: foreground;
- provider preflight: не отмечен как подтвержденный в fieldValidity, несмотря на фактический Wi-Fi run;
- JSON export: да;
- logcat/no-secret check: не выполнен / не зафиксирован;
- repeats: 1 known run;
- source JSON: `docs/research/imapdiag_20260513_222425_mailru_unknown_operator_wifi_control_foreground.json`.

Неизвестно / требует уточнения:

- был ли отдельный provider preflight до запуска;
- был ли VPN независимо проверен пользователем, кроме best-effort report flag;
- был ли raw logcat снят локально, но не сохранен;
- были ли дополнительные неэкспортированные попытки;
- сколько аккаунтов реально использовалось в canonical run, кроме masked domains в JSON.

## 7. Фактические результаты тестов

### Run A: Mail.ru normal mobile `single_account_smoke`

Source:

```text
docs/research/imapdiag_20260513_220755_mailru_unknown_operator_normal_mobile_foreground.json
```

Metadata:

- timestampUtc: `2026-05-13T19:07:46Z`;
- runId: `ff8795f1-d50f-4a6a-81af-3911572d0e01`;
- provider: `mailru`;
- network type: `mobile`;
- manual mode: `normal_mobile`;
- operator: `unknown_operator`;
- scenario: `foreground`;
- delivery mode: `single_account_smoke`;
- result: `diagnostic_only`;
- validForWhitelistConclusion: `false`;
- invalidation reasons: `manual_mode_not_whitelist`, `provider_preflight_not_confirmed`.

Checks: 13/13 passed.

- `imap_dns`: ok, 74 ms, IPv4, 1 address (`217.69.139.90`).
- `imap_tcp`: ok, 60 ms.
- `imap_tls_handshake`: ok, 241 ms, TLSv1.2, `TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256`.
- `imap_greeting`: ok, 79 ms.
- `imap_login`: ok, 86 ms.
- `imap_select_inbox`: ok, 58 ms.
- `imap_idle_enter_exit`: ok, 96 ms.
- `smtp_dns`: ok, 79 ms, IPv6+IPv4, 3 addresses.
- `smtp_tcp`: ok, 58 ms.
- `smtp_tls_handshake`: ok, 178 ms, TLSv1.2, `TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256`.
- `smtp_greeting`: ok, 95 ms.
- `smtp_ehlo`: ok, 42 ms.
- `smtp_auth`: ok, 96 ms.

Message correlation:

- sendAccepted: `false`;
- received: `false`;
- folder: `not_requested`;
- finalStatus: `smoke_no_send`;
- latency: not applicable.

Interpretation: Mail.ru DNS/TCP/TLS/IMAP auth/SMTP auth worked on this device in normal mobile foreground mode. This is not a delivery proof and not a whitelist proof.

### Run B: Mail.ru Wi-Fi control `two_account_canonical`

Source:

```text
docs/research/imapdiag_20260513_222425_mailru_unknown_operator_wifi_control_foreground.json
```

Metadata:

- timestampUtc: `2026-05-13T19:24:07Z`;
- runId: `43cb2079-f25c-4675-ad03-d78e7be09b46`;
- provider: `mailru`;
- network type: `wifi`;
- manual mode: `wifi_control`;
- operator: `unknown_operator`;
- scenario: `foreground`;
- delivery mode: `two_account_canonical`;
- result: `transport_pass`;
- validForWhitelistConclusion: `false`;
- invalidation reasons: `manual_mode_not_whitelist`, `provider_preflight_not_confirmed`.

Checks: 15/15 passed.

- `imap_dns`: ok, 79 ms, IPv4, 1 address (`94.100.180.90`).
- `imap_tcp`: ok, 54 ms.
- `imap_tls_handshake`: ok, 145 ms, TLSv1.2, `TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256`.
- `imap_greeting`: ok, 80 ms.
- `imap_login`: ok, 90 ms.
- `imap_select_inbox`: ok, 64 ms.
- `imap_idle_enter_exit`: ok, 135 ms.
- `smtp_dns`: ok, 56 ms, IPv4, 2 addresses.
- `smtp_tcp`: ok, 41 ms.
- `smtp_tls_handshake`: ok, 203 ms, TLSv1.2, `TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256`.
- `smtp_greeting`: ok, 77 ms.
- `smtp_ehlo`: ok, 54 ms.
- `smtp_auth`: ok, 142 ms.
- `smtp_test_send`: ok, 668 ms.
- `receive_by_message_id`: ok, 5317 ms.

Message correlation:

- sendAccepted: `true`;
- sendLatencyMs: `0` in `messageCorrelation`, while `smtp_test_send` check latency is 668 ms;
- received: `true`;
- receiveLatencyMs: 5318 ms;
- folder: `INBOX`;
- spamOrJunk: `false`;
- pollAttempts: 2;
- finalStatus: `received`.

Interpretation: Mail.ru canonical two-account foreground delivery worked on Wi-Fi control on this device. This is a meaningful provider/account baseline, but it does not prove mobile whitelist/restricted compatibility.

## 8. Проблемы и баги, обнаруженные на телефоне

### Filename convention mismatch

- Description: оба exported JSON имеют имя без final result suffix. Blueprint/code convention ожидает `..._foreground_result.json`, например `_foreground_transport_pass.json` или `_foreground_diagnostic_only.json`.
- Reproducibility: observed in 2/2 exported runtime files.
- Probable cause: Android document picker/manual rename artifact, старый APK/version mismatch, либо фактический export path использует не тот filename builder.
- Severity: medium.
- Next step: на следующем export проверить suggested filename до сохранения и фактическое имя после сохранения; при необходимости исправить `DiagnosticReport.exportFileName()` или flow document picker.

### `fieldValidity` provider preflight not confirmed

- Description: Wi-Fi canonical run прошел send/receive, но `fieldValidity.invalidationReasons` все равно содержит `provider_preflight_not_confirmed`.
- Reproducibility: observed in both known runs.
- Probable cause: tester не отметил checkbox provider preflight, либо UI/logic не связывает successful Wi-Fi canonical run с preflight status.
- Severity: medium.
- Next step: уточнить intended semantics. Для строгих field tests оставить ручное подтверждение, но добавить в report более ясное поле: `providerPreflightConfirmedByTester` vs `thisRunIsWifiControlBaseline`.

### `messageCorrelation.sendLatencyMs` inconsistent with `smtp_test_send`

- Description: Wi-Fi canonical JSON содержит `messageCorrelation.sendLatencyMs: 0`, при этом check `smtp_test_send` имеет `latencyMs: 668`.
- Reproducibility: observed in Wi-Fi canonical run.
- Probable cause: send latency не прокинут в `MessageCorrelationResult`, либо считается другим способом.
- Severity: low/medium.
- Next step: синхронизировать latency fields или документировать различие.

### Runtime logcat no-secret check not captured

- Description: нет подтвержденного adb/logcat capture во время реального запуска.
- Reproducibility: always open until checked.
- Probable cause: телефон был установлен/проверен вручную, локально `adb devices` не видел device.
- Severity: high for security validation, not blocker for basic transport smoke.
- Next step: выполнить controlled run с USB debugging, `adb logcat -c`, diagnostics, `adb logcat -d`; raw logcat не коммитить, сохранить только sanitized conclusion.

### Huawei installer/source-permission friction

- Description: установка прошла после разрешения Huawei installer/source-permission flow.
- Reproducibility: device/vendor-specific, unknown.
- Probable cause: Android/Huawei sideload restrictions.
- Severity: low for diagnostics, medium for repeated field distribution.
- Next step: записать краткую install instruction для Huawei/Android sideload, если тестеров будет несколько.

### UI-проблемы

- Description: специфические UI-баги в артефактах не зафиксированы.
- Reproducibility: неизвестно.
- Probable cause: неизвестно.
- Severity: unknown.
- Next step: пройти `prototypes/android-diagnostics/docs/MANUAL_QA_CHECKLIST.md` на телефоне и зафиксировать screenshots только без секретов.

### Crashes

- Description: runtime crashes не зафиксированы.
- Reproducibility: no known crash.
- Probable cause: unknown.
- Severity: none known.
- Next step: продолжать фиксировать crash/ANR separately, без raw secrets.

### Permissions

- Description: проблем runtime permissions не зафиксировано; dangerous permissions отсутствуют.
- Reproducibility: not applicable.
- Probable cause: manifest minimal.
- Severity: none known.
- Next step: не добавлять dangerous permissions ради metadata.

### Export JSON/document picker

- Description: JSON export passed, но filename suffix mismatch остается open issue.
- Reproducibility: 2/2 known exports.
- Probable cause: см. filename issue.
- Severity: medium.
- Next step: проверить suggested filename and final saved filename.

### Provider auth

- Description: Mail.ru auth passed в известных runs. VK Mail/Yandex auth не проверены.
- Reproducibility: Mail.ru passed in 2 known runs.
- Probable cause of future issues: provider app password setup or unsupported auth variant.
- Severity: unknown for VK/Yandex.
- Next step: Wi-Fi preflight for VK Mail and Yandex.

### IMAP parser

- Description: в известных Mail.ru runs IMAP greeting/login/select/IDLE/search прошли; parser issues не зафиксированы.
- Reproducibility: Mail.ru passed in 2 known runs.
- Probable cause of future issues: folder names, modified UTF-7, provider-specific responses.
- Severity: unknown.
- Next step: test Spam/Junk folder detection and Yandex/VK Mail.

### SMTP parser

- Description: SMTP greeting/EHLO/AUTH/send прошли в известных Mail.ru runs.
- Reproducibility: Mail.ru passed.
- Probable cause of future issues: providers requiring non-PLAIN auth or STARTTLS 587 fallback.
- Severity: unknown.
- Next step: test VK Mail/Yandex and failure classification.

### Message-ID correlation

- Description: Wi-Fi canonical Mail.ru correlation passed; normal mobile smoke did not request correlation.
- Reproducibility: 1/1 known canonical run passed.
- Probable cause of future issues: server search behavior, indexing delay, Spam/Junk placement.
- Severity: unknown.
- Next step: repeat canonical Wi-Fi and normal mobile 3 times.

### Spam/Junk scan

- Description: Wi-Fi canonical message arrived in `INBOX`, `spamOrJunk=false`; explicit Spam/Junk positive-path not tested.
- Reproducibility: unknown.
- Probable cause of future issues: heuristic folder names.
- Severity: unknown/medium.
- Next step: verify provider-specific Spam/Junk folders or force a known spam-like test only if safe.

### FieldValidity

- Description: validity correctly rejects non-whitelist conclusions, but preflight semantics and export-success semantics need review.
- Reproducibility: observed in known JSON.
- Probable cause: conservative fieldValidity model.
- Severity: medium.
- Next step: revise fieldValidity fields after next controlled runs.

### Логирование/секреты

- Description: static/export review did not reveal secrets; runtime logcat remains unverified.
- Reproducibility: unknown.
- Probable cause: no adb/logcat device capture.
- Severity: high until checked.
- Next step: controlled no-secret logcat QA.

## 9. Безопасность и секреты

Confirmed by source/static/export review:

- app password не сохраняется намеренно;
- app password не найден в exported JSON;
- raw SMTP/IMAP transcript не экспортируется;
- raw AUTH commands/base64 auth payloads не найдены в exported JSON;
- full email local-part masked as `***@mail.ru`;
- sample reports redacted/synthetic;
- copied summary designed as sanitized;
- manifest permissions: only `INTERNET` and `ACCESS_NETWORK_STATE`;
- no `READ_PHONE_STATE`, location, contacts, SMS, call log, notifications, foreground service, battery optimization permission.

Known unknowns:

- app password не попадает в logcat: не подтверждено runtime logcat capture;
- app password не показывается на result screen: source/UI intent есть, но отдельный runtime QA result не зафиксирован;
- app password не попадает в copied summary: source/export intent есть, но runtime copy summary QA не зафиксирован;
- crash/exception paths на реальном телефоне не проверены через logcat.

В этот отчет не включены app passwords, токены, полные email, raw auth commands, raw protocol transcript или raw logcat.

## 10. Соответствие Blueprint

Соответствие:

- standalone APK first: да;
- MVP-0a foreground-only: да;
- no Delta Chat fork: да;
- no chatmail/core changes: да;
- no dangerous permissions: да;
- provider presets Mail.ru/VK Mail/Yandex: да;
- two-account canonical mode: реализован и один раз прошел на Mail.ru Wi-Fi;
- self-send/single-account smoke only: да;
- sanitized JSON export: да;
- timeout policy: реализован и экспортируется;
- fieldValidity rules: реализованы, но требуют уточнения по preflight/export semantics;
- raw protocol transcript rules: соблюдены в exported JSON.

Ограничения относительно Blueprint:

- whitelist/restricted mobile test не выполнен;
- repetition count не встроен в UI, отслеживается вручную;
- runtime logcat no-secret QA не выполнен;
- runtime schema validation against JSON schema не зафиксирована;
- filename convention observed mismatch in saved files;
- MVP-0b background/locked-screen intentionally not implemented.

## 11. Что можно считать подтвержденным

Только факты из известных артефактов:

- debug APK собирается командой `.\gradlew.bat clean assembleDebug --no-daemon`;
- APK artifact существует: `prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk`;
- APK был установлен на Huawei CET-LX9;
- APK запускался на Huawei CET-LX9 Android 12 / SDK 31;
- APK смог экспортировать sanitized JSON;
- manifest содержит только `INTERNET` и `ACCESS_NETWORK_STATE`;
- Mail.ru normal mobile foreground smoke прошел DNS/TCP/TLS/IMAP login/select/IDLE/SMTP auth;
- Mail.ru Wi-Fi control two-account canonical прошел SMTP send и IMAP receive by Message-ID;
- Wi-Fi canonical Mail.ru message arrived in `INBOX`, not Spam/Junk;
- exported JSON не содержит app password, raw AUTH command, raw protocol transcript или raw logcat по reviewed content.

Нельзя считать подтвержденным по этим фактам:

- работу в whitelist/restricted mobile mode;
- работу у конкретного оператора, потому что operator recorded as `unknown_operator`;
- работу VK Mail/Yandex;
- messenger-like reliability;
- background/locked-screen delivery.

## 12. Что пока не подтверждено

- whitelist/restricted mobile compatibility;
- работа у МТС/Билайн/МегаФон/T2 по отдельности;
- работа во всех регионах;
- работа VK Mail;
- работа Yandex;
- repeated Mail.ru normal mobile canonical delivery;
- repeated Mail.ru Wi-Fi canonical baseline;
- canonical delivery в normal mobile;
- canonical delivery в whitelist/restricted mobile;
- отсутствие секретов в runtime logcat;
- result screen/copy summary no-secret runtime QA;
- runtime JSON schema validation;
- стабильность IMAP IDLE beyond enter/exit;
- network transition Wi-Fi -> mobile;
- airplane mode recovery;
- background receive;
- locked-screen receive;
- применимость как messenger transport.

## 13. Предварительная интерпретация

Факты усиливают гипотезу, что standalone APK пригоден для следующего этапа MVP-0a field tests: он собирается, ставится на реальный телефон, запускается, выполняет IMAP/SMTP checks и экспортирует usable JSON.

Mail.ru выглядит работоспособным на уровне foreground DNS/TCP/TLS/auth в normal mobile и на уровне full canonical delivery в Wi-Fi control. Это хороший технический baseline, но не доказательство whitelist compatibility.

Главное ограничение: нет запусков `two_account_canonical` в `whitelist_restricted` и нет повторов по матрице operator/provider/network mode. Поэтому вывод о пригодности IMAP/SMTP как корпоративного резервного транспорта делать рано.

APK можно использовать для следующих field tests после небольшого QA/fix pass: filename convention, logcat no-secret validation, clearer preflight semantics, repeatable run notes.

## 14. Рекомендованные следующие шаги

1. Проверить и исправить filename suffix mismatch в JSON export.
2. Уточнить `fieldValidity`: разделить Wi-Fi baseline run и checkbox provider preflight.
3. Исправить или объяснить `messageCorrelation.sendLatencyMs: 0` при non-zero `smtp_test_send`.
4. Подключить телефон по adb и выполнить runtime logcat no-secret check; raw logcat не коммитить.
5. Повторить Mail.ru Wi-Fi `two_account_canonical` минимум 3 раза.
6. Повторить Mail.ru normal mobile `two_account_canonical` минимум 3 раза.
7. После успешного Wi-Fi baseline выполнить Mail.ru `whitelist_restricted` tests, если доступна такая сеть.
8. Выполнить Wi-Fi preflight для VK Mail и Yandex.
9. Затем выполнить normal mobile и whitelist/restricted mobile для VK Mail/Yandex.
10. Валидировать exported runtime JSON against `schemas/diagnostic-report.schema.json`.
11. В каждом запуске заполнять operator/region вручную; `unknown_operator` недостаточен для field conclusion.
12. Не переходить к MVP-0b background/locked-screen до достаточного MVP-0a foreground transport evidence.
13. Не начинать Delta Chat fork/chatmail/core changes до анализа MVP-0a field matrix.

Новая APK сборка желательна после фиксов filename/latency/fieldValidity/logcat QA. Если нужно только продолжить ручные Mail.ru тесты без этих фиксов, текущий debug APK уже способен выполнять базовые проверки, но результаты будут менее чистыми для анализа.

## 15. Приложения / ссылки на артефакты

APK:

```text
prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk
```

Runtime JSON reports:

```text
docs/research/imapdiag_20260513_220755_mailru_unknown_operator_normal_mobile_foreground.json
docs/research/imapdiag_20260513_222425_mailru_unknown_operator_wifi_control_foreground.json
```

Reports/docs:

```text
docs/reports/2026-05-13/ANDROID_DIAGNOSTICS_APK_BUILD_RUNTIME_VALIDATION.report.md
docs/reports/2026-05-13/ANDROID_DIAGNOSTICS_APK_IMPLEMENTATION_DETAILED.report.md
docs/reports/2026-05-13/IMAPDIAG_MAILRU_NORMAL_MOBILE_SMOKE.report.md
prototypes/android-diagnostics/docs/IMPLEMENTATION_REPORT.md
prototypes/android-diagnostics/docs/BUILD_RUNTIME_VALIDATION_REPORT.md
prototypes/android-diagnostics/docs/SELF_AUDIT_REPORT.md
prototypes/android-diagnostics/docs/MANUAL_QA_CHECKLIST.md
docs/blueprints/ANDROID_DIAGNOSTICS_APK_BLUEPRINT.md
docs/research/WHITE_LIST_FIELD_TEST_PROTOCOL.md
```

Important build commands:

```powershell
cd prototypes/android-diagnostics
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
$env:ANDROID_HOME='C:\Android\android-sdk'
$env:ANDROID_SDK_ROOT='C:\Android\android-sdk'
.\gradlew.bat clean assembleDebug --no-daemon
```

Git/branch:

- root `d:\Users\Roman\Desktop\Проекты\mesenger` is not a git repository;
- upstream git repositories exist under `imap-messenger-research/upstream/*`, but the diagnostics APK lives outside those upstream repos;
- commit hash for diagnostics APK: unknown / not applicable from root workspace.

