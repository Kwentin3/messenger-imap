# Android Diagnostics APK Blueprint

## 1. Document Header

Дата: 2026-05-13  
Статус: Draft, refined for MVP-0a implementation  
Scope: MVP-0a foreground-only Android transport diagnostics APK  
Later scope: MVP-0b background reliability diagnostics after foreground transport pass  
Non-goal: corporate messenger  
Target platform: Android phone/tablet with SIM

## 2. Executive Summary

Минимальный Android diagnostics APK нужен, чтобы проверить transport hypothesis до fork/rebrand Delta Chat Android. Гипотеза: в режиме мобильных "белых списков" в РФ webmail-домены Mail.ru, VK Mail и Яндекс Почты могут оставаться доступными, но это не доказывает доступность IMAP/SMTP endpoints и портов.

Первая реализация должна быть MVP-0a: foreground-only диагностика транспорта на Android device with SIM. Она проверяет DNS, TCP, TLS, IMAP, SMTP, login, send, receive, Message-ID correlation, IDLE enter/exit, latency, Spam/Junk placement и sanitized JSON export.

MVP-0b начинается только после MVP-0a transport pass. В него входят background receive, locked-screen receive, Wi-Fi -> mobile transition, airplane mode recovery, Doze/battery optimization experiments и optional foreground service experiment.

Standalone APK выбран первым, потому что он быстрее и безопаснее полного fork Delta Chat Android. Desktop и Wi-Fi остаются только контрольной группой: успешный desktop/Wi-Fi тест не доказывает работу в whitelist/restricted mobile mode.

## 3. Problem Statement

Белые списки могут пропускать webmail, но не обязаны пропускать `imap.*:993` и `smtp.*:465`. Поэтому доступность `mail.ru`, `vk.com` или `yandex.ru` в браузере не является доказательством пригодности IMAP/SMTP как корпоративного transport.

Корпоративный Android-мессенджер поверх IMAP имеет смысл только если transport pass подтвержден на Android + SIM, в нескольких сетевых режимах и у нескольких операторов. До такого подтверждения нельзя начинать полноценный messenger UX, rebrand, fork Delta Chat Android или изменения chatmail/core.

Android добавляет отдельные риски: Doze, battery optimization, background restrictions, WorkManager latency, foreground service policy и устойчивость IMAP IDLE после смены сети. Эти риски относятся к MVP-0b и не должны раздувать MVP-0a.

## 4. MVP Split

### MVP-0a: Foreground Transport Diagnostics

Цель:

- проверить, проходят ли DNS/TCP/TLS/IMAP/SMTP/send/receive на Android-устройстве через текущую сеть;
- проверить provider/account setup;
- проверить two-account Message-ID correlation;
- проверить Spam/Junk placement;
- экспортировать sanitized JSON.

В MVP-0a не входит:

- background service;
- locked-screen delivery;
- notifications;
- foreground service;
- battery optimization workflow;
- persistent account storage;
- messenger UX.

### MVP-0b: Background Reliability Diagnostics

Запускается только после MVP-0a transport pass.

Цель:

- background receive;
- locked-screen receive;
- Wi-Fi -> mobile transition;
- airplane mode recovery;
- Doze/battery optimization experiments;
- optional foreground service experiment.

MVP-0b не должен менять chatmail/core, Delta Chat JNI, sync, encryption или database migrations.

## 5. Goals

### MVP-0a Goals

- Проверить endpoints Mail.ru, VK Mail и Yandex.
- Проверить IMAP/SMTP login через app password.
- Проверить SMTP test send.
- Проверить canonical two-account delivery: account A SMTP send -> account B IMAP receive.
- Проверить correlation by generated Message-ID.
- Проверить IMAP `SELECT INBOX`.
- Проверить вход/выход в IMAP IDLE.
- Измерить foreground delivery latency.
- Проверить Spam/Junk placement.
- Собрать sanitized JSON report.
- Поддержать repeatable field tests.
- Не сохранять секреты.
- Не менять Delta Chat Android, chatmail/core, JNI, sync или encryption.

### MVP-0b Goals

- Проверить background receive only after MVP-0a pass.
- Проверить locked-screen receive.
- Проверить network transition recovery.
- Проверить Doze/battery optimization behavior.
- Проверить optional foreground service strategy, если foreground transport уже доказан.

## 6. Non-Goals

- Не чат и не messenger UI.
- Не Delta Chat fork.
- Не production messenger.
- Не E2EE implementation.
- Не corporate address book.
- Не group chats.
- Не production push architecture.
- Не постоянная синхронизация аккаунта.
- Не persistent account storage.
- Не серверная часть.
- Не cloud dashboard.
- Не MDM integration.
- Не собственный почтовый сервер.
- Не автоматический сбор персональных данных оператора через dangerous permissions.

## 7. Target Users

- Инженер или администратор, проводящий полевые тесты.
- Внутренний тестировщик.
- Доверенный сотрудник в конкретном регионе или на конкретном операторе.

UX не проектируется для массового конечного пользователя. Интерфейс должен быть понятным инженеру, но не обязан быть consumer-grade.

## 8. Provider Model

Provider presets должны быть data-driven: отдельная structure/config, которую использует UI и diagnostic runner. Сетевая логика не должна содержать scattered hardcode endpoints.

### Mail.ru

- IMAP: `imap.mail.ru:993` TLS.
- SMTP: `smtp.mail.ru:465` TLS.
- Auth: app password for external apps.
- Username: обычно full email address.

### VK Mail

- IMAP: `imap.mail.ru:993` TLS.
- SMTP: `smtp.mail.ru:465` TLS.
- Auth: app password.
- Notes: использует Mail.ru endpoints.

### Yandex

- IMAP: `imap.yandex.com:993` TLS.
- SMTP: `smtp.yandex.com:465` TLS.
- Auth: enabled IMAP plus app password or provider-approved auth path.
- Username: обычно full email address.

### Debug Overrides

Debug endpoint override допустим только в debug/internal mode:

- custom IMAP host/port/security;
- custom SMTP host/port/security;
- explicit STARTTLS/SSL mode if later needed;
- visible warning that result is not canonical provider preset result;
- `debugOverrideUsed` must be exported in JSON.

## 9. Provider Preflight Checklist

Перед mobile/whitelist field tests обязательно выполнить Wi-Fi control preflight для каждого provider/account pair:

- test mailbox created;
- IMAP enabled where required;
- app password created;
- login works;
- IMAP `SELECT INBOX` works;
- SMTP AUTH works;
- SMTP send works;
- receive works;
- Spam/Junk baseline checked;
- provider limitations recorded;
- app password не сохранен в документах/репозитории.

Rule: если auth/send/receive не работает на Wi-Fi control, field failure нельзя классифицировать как whitelist failure. Это `provider_fail`, `auth_fail` или `inconclusive`.

## 10. Functional Scope

### MVP-0a Screen A: Start / Test Setup

Required fields:

- provider selector: Mail.ru / VK Mail / Yandex;
- delivery mode:
  - single-account connectivity smoke;
  - two-account canonical delivery;
- sender account email;
- sender account app password;
- receiver account email for canonical delivery;
- receiver account app password, if receiver IMAP login is performed by the app;
- manual network mode:
  - Wi-Fi control;
  - normal mobile;
  - whitelist/restricted mobile;
- operator manual input;
- region/city manual input;
- optional notes;
- scenario fixed to `foreground` for MVP-0a;
- run diagnostics button.

Validation:

- email syntactic validation;
- provider/domain warning, not hard blocking unless configured;
- empty required password blocks run;
- no password persistence;
- VPN warning if detected best-effort;
- timeout policy visible if changed from defaults.

### MVP-0a Screen B: Running Diagnostics

Show checklist with per-step status:

- DNS IMAP;
- TCP IMAP;
- TLS IMAP;
- IMAP certificate info;
- IMAP greeting;
- IMAP login;
- `SELECT INBOX`;
- IDLE enter/exit;
- DNS SMTP;
- TCP SMTP;
- TLS SMTP;
- SMTP certificate info;
- SMTP greeting;
- EHLO;
- AUTH;
- test send;
- receive by Message-ID;
- Spam/Junk detection.

The screen must not display app password, raw AUTH commands, base64 auth strings or full protocol transcript.

### MVP-0a Screen C: Result

Show:

- overall result: `transport_pass`, `partial_pass`, `fail`, `diagnostic_only`, `inconclusive`;
- failed stage;
- latency;
- sanitized error category;
- redacted error message;
- VPN validity warning, if applicable;
- export JSON button;
- save JSON button;
- copy summary button;
- warning: "Export does not include app password or raw protocol transcript."

### MVP-0b Screens: Background / Locked / Transition Helpers

Not in MVP-0a.

Later MVP-0b may add:

- background scenario helper;
- locked-screen scenario helper;
- transition scenario helper;
- battery optimization state guidance;
- optional notification/foreground service experiment.

## 11. Diagnostic Checks

### IMAP Checks

1. DNS resolve IMAP host.
2. TCP connect to port `993`.
3. TLS handshake.
4. Certificate info collection:
   - subject;
   - issuer;
   - validFrom/validTo;
   - protocol;
   - cipher suite if available.
5. Read IMAP greeting.
6. Login with email/app password.
7. `SELECT INBOX`.
8. Enter and exit `IDLE`.
9. Search/fetch by generated Message-ID.
10. Search known Spam/Junk folders.

### SMTP Checks

1. DNS resolve SMTP host.
2. TCP connect to port `465`.
3. TLS handshake.
4. Certificate info collection.
5. Read SMTP greeting.
6. EHLO.
7. AUTH with email/app password.
8. Send generated test message.
9. Classify SMTP server response.

### End-to-End

Canonical mode: account A SMTP send -> account B IMAP receive.

Required:

- generated Message-ID;
- generated non-sensitive subject/body;
- correlation by Message-ID;
- delivery latency from SMTP accepted to IMAP observed;
- folder placement: INBOX vs Spam/Junk/other/not found.

## 12. Delivery Test Modes

### Mode 1: Single-Account Connectivity Smoke Test

Scope:

- DNS/TCP/TLS/IMAP/SMTP/auth;
- optional self-send smoke test only;
- provider/account setup validation.

Result interpretation:

- useful as smoke test;
- not a full messenger-like transport proof;
- cannot produce canonical `transport_pass` for field decision.

### Mode 2: Two-Account Canonical Delivery Test

Scope:

- account A SMTP send;
- account B IMAP receive;
- generated Message-ID correlation;
- Spam/Junk scan on receiver side.

Result interpretation:

- canonical result for `transport_pass`;
- should be run with two controlled accounts;
- prefer two accounts on the same provider for MVP-0a;
- later add cross-provider matrix if needed.

### Mode 3: Cross-Provider Delivery Test

Scope:

- account A on provider X -> account B on provider Y.

Result interpretation:

- optional later;
- useful for future corporate use case;
- not required for MVP-0a acceptance.

## 13. Default Timeouts

MVP-0a defaults:

- DNS resolve timeout: 5s.
- TCP connect timeout: 10s.
- TLS handshake timeout: 15s.
- IMAP greeting timeout: 10s.
- IMAP login timeout: 20s.
- IMAP `SELECT INBOX` timeout: 15s.
- IMAP IDLE enter/exit timeout: 20s.
- SMTP greeting timeout: 10s.
- SMTP AUTH timeout: 20s.
- SMTP DATA/send accepted timeout: 30s.
- Foreground receive correlation timeout: 120s.
- Polling interval during receive correlation: 5s.
- IDLE observe window: 60s.

Rules:

- timeout values are defaults and must be exported in report;
- implementation may allow debug override;
- changing timeout must be visible in JSON report;
- do not silently increase timeouts in field tests.

MVP-0b draft defaults:

- background receive window: 5-15 min;
- locked-screen receive window: 5-15 min;
- exact values to be finalized after MVP-0a.

## 14. Test Modes

### MVP-0a

Network modes:

- Wi-Fi control;
- normal mobile;
- whitelist/restricted mobile.

App/device state:

- foreground only.

### MVP-0b

App/device states:

- background;
- locked screen.

Network transitions:

- Wi-Fi -> mobile;
- airplane mode on/off recovery.

MVP-0a implementation must not include background service, notifications, foreground service or battery optimization workflow.

## 15. VPN Invalidation Rule

If VPN is active, the run is not valid as whitelist compatibility proof.

Classification:

- result may be `diagnostic_only` or `inconclusive_for_whitelist`;
- VPN run can be saved as technical log;
- VPN run cannot support a conclusion about IMAP/SMTP availability in mobile whitelist mode;
- UI must show an explicit warning if VPN is detected best-effort.

VPN detection is best-effort. Failure to detect VPN does not guarantee no VPN exists.

## 16. Field Test Validity Rules

A run may be used for whitelist compatibility conclusions only if:

- device is Android with SIM;
- network mode is marked by tester as mobile whitelist/restricted;
- VPN inactive;
- provider preflight passed on Wi-Fi;
- same account/config works on control network;
- DNS/TCP/TLS/auth/send/receive stages are recorded;
- report exported successfully;
- no `network_changed` during critical stage, unless testing transition scenario;
- repetition count is tracked.

A run is `inconclusive` if:

- metadata incomplete;
- VPN active;
- network changed unexpectedly;
- app killed;
- provider preflight not done;
- auth fails without Wi-Fi baseline;
- tester cannot confirm network mode;
- timeout policy modified but not recorded.

## 17. Data Model

### ProviderConfig

- `id`: `mailru`, `vkmail`, `yandex`.
- `displayName`.
- `domains`: expected provider domains.
- `imapHost`.
- `imapPort`.
- `imapSecurity`: `tls`.
- `smtpHost`.
- `smtpPort`.
- `smtpSecurity`: `tls`.
- `requiresAppPassword`: boolean.
- `setupHint`.
- `supportsDebugOverride`: debug/internal only.

### TestRun

- `id`: UUID.
- `startedAtUtc`.
- `finishedAtUtc`.
- `mvpStage`: `mvp_0a` or `mvp_0b`.
- `scenario`: `foreground`, `background`, `locked`, `transition`.
- `deliveryMode`: `single_account_smoke`, `two_account_canonical`, `cross_provider`.
- `providerConfig`.
- `senderEmailDomain`.
- `receiverEmailDomain`.
- `maskedSenderEmail`.
- `maskedReceiverEmail`.
- `networkMetadata`.
- `deviceMetadata`.
- `timeoutPolicy`.
- `checks`.
- `messageCorrelation`.
- `result`.
- `errorSummary`.
- `notes`.
- `repetitionIndex`.

### NetworkMetadata

- `type`: wifi/mobile/ethernet/vpn/unknown.
- `manualMode`: wifi_control/normal_mobile/whitelist_restricted.
- `operatorManual`.
- `carrierNameBestEffort`.
- `mccMncBestEffort`.
- `region`.
- `isRoamingBestEffort`.
- `vpnActiveBestEffort`.
- `vpnValidityWarningShown`.
- `batteryOptimizationIgnored`.
- `foregroundState`.
- `networkCapabilitiesSummary`.

If metadata is not available without dangerous permission, ask tester to enter it manually instead of requesting permission.

### DeviceMetadata

- `manufacturer`.
- `model`.
- `androidVersion`.
- `sdk`.
- `appVersion`.
- `buildType`.
- `buildNumber`.

### TimeoutPolicy

- `dnsResolveMs`.
- `tcpConnectMs`.
- `tlsHandshakeMs`.
- `imapGreetingMs`.
- `imapLoginMs`.
- `imapSelectMs`.
- `imapIdleEnterExitMs`.
- `smtpGreetingMs`.
- `smtpAuthMs`.
- `smtpSendAcceptedMs`.
- `receiveCorrelationMs`.
- `receivePollingIntervalMs`.
- `idleObserveWindowMs`.
- `modifiedFromDefault`: boolean.

### CheckResult

- `name`.
- `protocol`: imap/smtp/system/e2e.
- `endpoint`: host/port where applicable.
- `status`: pending/running/ok/fail/skipped.
- `startedAtUtc`.
- `finishedAtUtc`.
- `latencyMs`.
- `errorCategory`.
- `errorMessageRedacted`.
- `tlsInfo`.
- `dnsInfo`.

### EndpointResult

- `host`.
- `port`.
- `resolvedAddressCount`.
- `addressFamilies`: IPv4/IPv6.
- `resolvedAddresses`: optional debug build only.
- `dnsResolverInfoBestEffort`.
- `tcpConnected`.
- `tlsConnected`.
- `tlsInfo`.
- `latencyMs`.
- `errorCategory`.

### MessageCorrelationResult

- `messageId`.
- `sendAccepted`.
- `sendLatencyMs`.
- `received`.
- `receiveLatencyMs`.
- `folder`.
- `spamOrJunk`.
- `pollAttempts`.
- `finalStatus`.

### ExportReport

- `schemaVersion`.
- `timestampUtc`.
- `runId`.
- `app`.
- `device`.
- `network`.
- `provider`.
- `timeoutPolicy`.
- `checks`.
- `messageCorrelation`.
- `result`.
- `errorSummary`.
- `notes`.

### ErrorCategory

Use the taxonomy in section 19.

## 18. JSON Report Schema Draft

```json
{
  "schemaVersion": 1,
  "timestampUtc": "2026-05-13T00:00:00Z",
  "runId": "00000000-0000-4000-8000-000000000000",
  "mvpStage": "mvp_0a",
  "scenario": "foreground",
  "deliveryMode": "two_account_canonical",
  "app": {
    "name": "android-imap-diagnostics",
    "version": "0.0.1",
    "buildType": "debug",
    "buildNumber": "local"
  },
  "device": {
    "manufacturer": "Example",
    "model": "ExamplePhone",
    "androidVersion": "15",
    "sdk": 35
  },
  "network": {
    "type": "mobile",
    "manualMode": "whitelist_restricted",
    "operatorManual": "mts",
    "carrierNameBestEffort": "MTS RUS",
    "mccMncBestEffort": null,
    "region": "Moscow",
    "batteryOptimizationIgnored": false,
    "foregroundState": "foreground",
    "vpnActiveBestEffort": false,
    "vpnValidityWarningShown": false
  },
  "provider": {
    "name": "mailru",
    "senderEmailDomain": "example.com",
    "receiverEmailDomain": "example.com",
    "maskedSenderEmail": "***@example.com",
    "maskedReceiverEmail": "***@example.com",
    "imap": {
      "host": "imap.mail.ru",
      "port": 993,
      "security": "tls"
    },
    "smtp": {
      "host": "smtp.mail.ru",
      "port": 465,
      "security": "tls"
    },
    "debugOverrideUsed": false
  },
  "timeoutPolicy": {
    "dnsResolveMs": 5000,
    "tcpConnectMs": 10000,
    "tlsHandshakeMs": 15000,
    "imapGreetingMs": 10000,
    "imapLoginMs": 20000,
    "imapSelectMs": 15000,
    "imapIdleEnterExitMs": 20000,
    "smtpGreetingMs": 10000,
    "smtpAuthMs": 20000,
    "smtpSendAcceptedMs": 30000,
    "receiveCorrelationMs": 120000,
    "receivePollingIntervalMs": 5000,
    "idleObserveWindowMs": 60000,
    "modifiedFromDefault": false
  },
  "checks": [
    {
      "name": "imap_tls_handshake",
      "protocol": "imap",
      "endpoint": {"host": "imap.mail.ru", "port": 993},
      "status": "ok",
      "latencyMs": 420,
      "errorCategory": null,
      "errorMessageRedacted": null,
      "dnsInfo": {
        "resolvedAddressCount": 2,
        "addressFamilies": ["IPv4", "IPv6"],
        "resolvedAddresses": null
      },
      "tlsInfo": {
        "protocol": "TLSv1.3",
        "cipherSuite": "TLS_AES_256_GCM_SHA384",
        "subject": "CN=imap.mail.ru",
        "issuer": "CN=Example CA",
        "validTo": "2026-12-31T23:59:59Z"
      }
    }
  ],
  "messageCorrelation": {
    "messageId": "<diag-uuid@example.invalid>",
    "sendAccepted": true,
    "sendLatencyMs": 900,
    "received": true,
    "receiveLatencyMs": 5300,
    "folder": "INBOX",
    "spamOrJunk": false,
    "pollAttempts": 3,
    "finalStatus": "received"
  },
  "result": "transport_pass",
  "errorSummary": null,
  "notes": "manual tester notes"
}
```

Forbidden fields:

- app password;
- raw auth commands;
- full SMTP/IMAP protocol transcript;
- OAuth tokens;
- full email local-part by default;
- raw logcat;
- sensitive message body.

## 19. Error Taxonomy

- `dns_fail`: hostname did not resolve.
- `tcp_timeout`: TCP connect timed out.
- `tcp_refused`: TCP connection refused.
- `tls_fail`: TLS handshake failed.
- `cert_fail`: certificate validation failed or certificate is unexpected.
- `imap_greeting_fail`: IMAP greeting missing or invalid.
- `auth_fail`: generic auth failed.
- `imap_select_fail`: `SELECT INBOX` failed.
- `idle_unavailable`: IDLE unsupported or failed.
- `smtp_greeting_fail`: SMTP greeting missing or invalid.
- `smtp_auth_fail`: SMTP auth failed.
- `smtp_rejected`: SMTP accepted auth but rejected send.
- `message_not_received`: send accepted, receive not observed in timeout.
- `spam_or_junk_placement`: message found in Spam/Junk.
- `network_changed`: active network changed during test.
- `provider_config_fail`: preset or override invalid.
- `android_background_restricted`: Android background policy likely blocked run.
- `diagnostic_only`: run is useful technically but not proof for whitelist.
- `inconclusive_for_whitelist`: VPN or metadata prevents whitelist conclusion.
- `unknown`: redacted unknown error.

## 20. Security and Privacy Requirements

Hard requirements:

- App password is never persisted.
- App password is never written to logcat.
- App password is never included in JSON.
- Credentials are not stored in SharedPreferences.
- Credentials are not stored in Bundle/savedInstanceState.
- Password field uses secure input type.
- Password field is cleared after run completion, cancellation, screen close and process background where feasible.
- Full email is masked by default.
- Raw log export is not used.
- Exceptions are redacted before UI/export.
- Screenshots must not reveal password.
- Export is sanitized JSON only.
- Generated test body must be non-sensitive.

Do not reuse Delta Chat Android `LogViewFragment` as report basis. Existing Delta Chat LogView intentionally gathers raw logcat and warns that logs may contain sensitive information; this is not acceptable for corporate diagnostic reports.

## 21. Raw Protocol Transcript Rules

Allowed:

- step names;
- status;
- latency;
- endpoint host/port;
- TLS metadata;
- DNS summary;
- redacted server error class;
- SMTP/IMAP response code without sensitive payload, if safe.

Forbidden:

- AUTH command payload;
- base64 auth strings;
- raw LOGIN command;
- full SMTP/IMAP transcript;
- message body containing tester text;
- app password;
- OAuth/token material;
- unredacted exception containing command payload.

## 22. DNS/IP Reporting

Provider endpoint IP addresses may be stored because they are endpoint metadata, not user secrets. The report must not include user's local/private IP unless explicitly needed and safe.

Include:

- `resolvedAddressCount`;
- `addressFamilies`: IPv4/IPv6;
- `resolvedAddresses` optional in debug build;
- DNS resolver info only if available without dangerous permissions.

If `resolvedAddresses` are omitted, keep enough summary for analysis.

## 23. Android Permissions

Minimum:

- `INTERNET`.
- `ACCESS_NETWORK_STATE`.

Do not use in MVP-0a:

- `READ_PHONE_STATE`.
- Precise location.
- Contacts.
- SMS.
- Call log.
- `POST_NOTIFICATIONS`.
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`.

Optional later for MVP-0b:

- `POST_NOTIFICATIONS` for controlled background notification test.
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` only for controlled background experiment.

No dangerous permission escalation rule:

- if metadata is not available without dangerous permission, ask tester to enter it manually;
- operator, region/city and mode are manual fields;
- carrierName/MCC/MNC are best-effort only without dangerous permissions;
- absence of carrier metadata must not block run;
- do not add dangerous permissions just to improve report quality.

## 24. Export Naming Convention

Format:

```text
imapdiag_YYYYMMDD_HHMMSS_provider_operator_mode_scenario_result.json
```

Example:

```text
imapdiag_20260513_143500_mailru_mts_whitelist_foreground_transport_pass.json
```

Rules:

- operator normalized into safe slug form;
- unknown operator -> `unknown_operator`;
- mode values: `wifi_control`, `normal_mobile`, `whitelist_restricted`;
- scenario values: `foreground`, `background`, `locked`, `transition`;
- result values: `transport_pass`, `partial_pass`, `fail`, `diagnostic_only`, `inconclusive`;
- filename must not contain email local-part or secrets.

## 25. Report Storage and Export Rules

- Report is created only after explicit Export or Save action.
- App password is not saved.
- JSON may be saved to Downloads/Documents through Android system picker/share sheet.
- Raw logcat is not attached.
- Screenshots are not required.
- Sample reports must be redacted/synthetic only.
- Every run must have UUID.
- Exported report must include `schemaVersion` and app `buildNumber`.
- Copy summary must be sanitized and shorter than full JSON.

## 26. No-Secret QA Checklist

Before field build, verify:

- app password does not appear in logcat;
- app password does not appear in JSON;
- app password is not saved in SharedPreferences;
- app password is not saved in Bundle/savedInstanceState;
- app password does not appear in crash/exception message;
- password field clears after run/cancel/close;
- exported report does not contain full email local-part unless explicitly enabled in debug;
- raw logcat export is absent;
- sample reports are synthetic/redacted;
- copy summary contains no secrets;
- screenshot of result screen does not show password;
- dependency logging disabled or redacted.

## 27. Architecture Options

### Option A. Standalone Android Diagnostics APK

Pros:

- fastest route to field test;
- smaller blast radius;
- no Delta Chat UX/core changes;
- easier no-secret persistence;
- can be installed on test devices immediately after build.

Cons:

- does not fully verify Delta Chat core behavior;
- later integration still required.

### Option B. Debug Screen Inside `deltachat-android`

Pros:

- closer to future product;
- can reuse some app infrastructure;
- can later reuse provider/onboarding UI.

Cons:

- requires heavier Delta Chat Android build;
- higher risk of touching unrelated UX/core paths;
- wider scope.

### Option C. Immediate `deltachat-android` Fork

Pros:

- faster to product UX after transport is proven.

Cons:

- premature;
- GPL/client distribution implications need review;
- high scope creep risk;
- easy to confuse diagnostics with messenger work.

Blueprint recommendation: Option A first.

## 28. Mail Protocol Implementation Decision Gate

Before implementing IMAP/SMTP code, create a short technical decision note:

- `docs/blueprints/ANDROID_MAIL_LIBRARY_DECISION.md`; or
- `docs/research/ANDROID_MAIL_LIBRARY_DECISION.md`.

Compare at minimum:

- Jakarta Mail / Angus Mail;
- Apache Commons Net;
- minimal manual protocol over `SSLSocket`;
- any other Android-compatible candidate if relevant.

Criteria:

- Android compatibility;
- license;
- maintained status;
- supports IMAP over TLS 993;
- supports SMTP over TLS 465;
- supports IMAP IDLE;
- supports Message-ID search/fetch;
- timeout control;
- TLS/certificate metadata access;
- no secret logging by default;
- dependency size;
- implementation complexity.

Rule: do not start primary IMAP/SMTP implementation before selecting approach and recording the decision.

## 29. Suggested Implementation Stack

- Kotlin or Java Android app.
- minSdk: align with Delta Chat Android baseline where practical; minSdk 21 is acceptable.
- Simple native Android UI.
- Diagnostic runner separated from UI state.
- Provider config as structured data.
- Low-level DNS/TCP/TLS checks via Java/Kotlin networking APIs.

Mail library choice is blocked by the decision gate in section 28.

## 30. Build and Project Structure

Suggested workspace:

```text
prototypes/android-diagnostics/
  README.md
  app/
    build.gradle
    src/main/
  docs/
    IMPLEMENTATION_NOTES.md
  schemas/
    diagnostic-report.schema.json
  sample-reports/
    README.md
```

Use `config.example` only for non-secret provider defaults if needed. Do not use `.env` for real credentials on Android. Test credentials must be entered manually at runtime or supplied by a safe debug-only mechanism that does not enter git.

## 31. Field Test Workflow

1. Install APK on Android device with SIM.
2. Prepare controlled mailboxes and app passwords.
3. Run provider preflight on Wi-Fi control.
4. Select provider.
5. Select delivery mode.
6. Enter sender credentials.
7. Enter receiver credentials for canonical delivery if needed.
8. Select network mode.
9. Enter operator and region/city manually.
10. Run foreground test.
11. Export sanitized JSON.
12. Repeat at least 3 times.
13. Repeat on another operator/network mode/provider.
14. Do not run MVP-0b background/locked scenarios until MVP-0a foreground transport pass exists.
15. Do not make conclusions from one successful run.

This workflow must remain compatible with `docs/research/WHITE_LIST_FIELD_TEST_PROTOCOL.md`.

## 32. Pass/Fail Rules

- `transport_pass`: two-account canonical DNS/TCP/TLS/auth/send/receive succeeds and message arrives in INBOX within timeout.
- `partial_pass`: foreground transport works but IDLE or folder placement is degraded.
- `foreground_pass`: foreground send/receive succeeds; use as scenario status, not final product verdict.
- `background_pass`: MVP-0b only, background receive succeeds within defined window.
- `locked_screen_pass`: MVP-0b only, locked-screen receive/notification succeeds within defined window.
- `idle_pass`: IMAP IDLE enter/exit works and observe window completes.
- `fallback_polling_pass`: IDLE fails but polling observes message.
- `provider_fail`: fails on Wi-Fi and mobile due provider/auth/config behavior.
- `network_whitelist_fail`: webmail may work, but DNS/TCP/TLS to IMAP/SMTP fails in restricted mobile mode.
- `auth_fail`: credentials/provider account setup fail.
- `spam_junk_fail`: sent message arrives only in Spam/Junk.
- `diagnostic_only`: run is technically useful but not valid proof, for example VPN active.
- `inconclusive`: test interrupted, network changed, timeout ambiguous, metadata incomplete or preflight absent.

Important interpretations:

- If webmail works but TCP/TLS to IMAP/SMTP fails, classify as `network_whitelist_fail`.
- If auth fails on Wi-Fi too, classify as provider/account setup failure, not whitelist failure.
- If foreground works but background fails later in MVP-0b, transport is partially usable, but messenger reliability is not confirmed.
- Self-send can never be the only evidence for `transport_pass`.

## 33. Risks

- IMAP/SMTP ports may not be included in whitelist.
- Operator/region/time-of-day can change results.
- Android Doze can break background IDLE in MVP-0b.
- Provider anti-spam can reject or quarantine test messages.
- App password setup friction can dominate field testing.
- SMTP throttling or rate limits can distort repeat tests.
- Self-send behavior can differ by provider.
- TLS/SNI/cert behavior can differ between endpoints.
- Bad UX can cause accidental secret export.
- Debug endpoint override can produce non-representative results.

## 34. Open Questions

- Should port `587` STARTTLS be tested as fallback or is MVP-0a strictly `465`?
- Should IPv6 be tested separately or only recorded as address family?
- Is debug endpoint override required in first build?
- What minimum Android version should field matrix cover?
- What background service strategy should MVP-0b use?
- What is the priority of cross-provider tests after same-provider canonical delivery?

Closed by this refine:

- Canonical delivery uses two controlled accounts; self-send is smoke only.
- MVP-0a timeout defaults are defined in section 13.

## 35. MVP Implementation Backlog

### MVP-0a Foreground Transport Backlog

1. Create Android project under `prototypes/android-diagnostics/`.
2. Add README with build/run instructions.
3. Create mail protocol implementation decision note.
4. Define `ProviderConfig`.
5. Implement provider selector.
6. Implement delivery mode selector.
7. Implement setup screen.
8. Implement provider preflight support.
9. Implement secure password input handling.
10. Implement email masking utility.
11. Implement DNS probe.
12. Implement TCP probe.
13. Implement TLS probe and certificate info collection.
14. Implement IMAP greeting/login.
15. Implement `SELECT INBOX`.
16. Implement IMAP IDLE enter/exit.
17. Implement SMTP greeting/EHLO/auth.
18. Implement SMTP test send.
19. Implement Message-ID generation.
20. Implement two-account delivery mode.
21. Implement receive correlation by Message-ID.
22. Implement Spam/Junk folder scan.
23. Implement timeout defaults.
24. Implement error taxonomy mapping.
25. Implement field metadata form.
26. Implement VPN warning and invalidation.
27. Implement result screen.
28. Implement sanitized JSON export.
29. Implement export filename convention.
30. Add JSON schema under `schemas/`.
31. Add synthetic/redacted sample reports.
32. Add no-secret QA tests.
33. Add manual QA checklist.
34. Verify no credentials in logcat during a test run.

### MVP-0b Background Reliability Backlog

1. Background scenario helper.
2. Locked-screen scenario helper.
3. Notification permission experiment.
4. Battery optimization state capture.
5. Foreground service experiment if needed.
6. Wi-Fi -> mobile transition tests.
7. Airplane mode recovery tests.
8. Longer receive windows.
9. MVP-0b timeout policy.

### Future / Not MVP-0

1. Delta Chat Android integration.
2. Corporate onboarding.
3. Local offline report aggregator.
4. Managed config import.
5. UI polish.
6. Fork decision.

## 36. Later: Offline Report Aggregator

Not part of MVP-0a APK.

Later local tool/script may aggregate a folder of JSON reports:

- group by operator/provider/network mode/scenario/result;
- summarize pass/fail/inconclusive counts;
- preserve no-secret assumptions;
- no cloud dashboard;
- no personal data collection.

This must not be added to MVP-0a implementation backlog as APK requirement.

## 37. Definition of Done for Blueprint

This Blueprint is ready when:

- document is self-contained;
- standalone APK first is explicit;
- MVP-0a and MVP-0b are separated;
- MVP-0a is foreground-only;
- two-account delivery is canonical;
- self-send is smoke only;
- provider preflight exists;
- timeout defaults exist;
- VPN invalidation rule exists;
- no dangerous permission escalation rule exists;
- export filename convention exists;
- report storage/export rules exist;
- raw protocol transcript rules exist;
- DNS/IP reporting is specified;
- mail protocol implementation decision gate exists;
- no-secret QA checklist exists;
- field test validity rules exist;
- open questions are current;
- backlog is split into MVP-0a, MVP-0b and future;
- there is no proposal to start with full messenger or deep Delta Chat fork.

## 38. Final Recommendation

Implement a standalone Android diagnostics APK.

First implementation is MVP-0a foreground-only transport diagnostics. Canonical delivery test is two controlled accounts: account A SMTP send -> account B IMAP receive with generated Message-ID correlation. Self-send is smoke test only.

Do not fork Delta Chat Android until field evidence confirms transport viability on Android + SIM. Do not change chatmail/core, Delta Chat JNI, sync, database migrations or encryption for this phase.

After MVP-0a field results, decide whether to build MVP-0b background reliability diagnostics. Only after MVP-0b evidence should the team decide on thin Android fork, wrapper/custom shell or rejection of the IMAP transport hypothesis for whitelist scenarios.
