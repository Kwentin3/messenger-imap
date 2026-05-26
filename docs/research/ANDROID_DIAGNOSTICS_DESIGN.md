# Android diagnostics design

Дата: 2026-05-13

## Goal

Android diagnostics должны проверять транспорт именно с устройства и SIM, через текущую сеть. Desktop/RPC диагностика остается лабораторным инструментом, но не доказывает работоспособность в mobile whitelist/restricted mode.

## Recommended implementation path

MVP-0: отдельное минимальное Android diagnostics app или debug-only screen внутри `deltachat-android`.

Практичный порядок:

1. Separate diagnostics app для быстрого field test без риска сломать Delta Chat UX.
2. После подтверждения транспорта - debug screen в `deltachat-android`.
3. После стабилизации - интеграция в corporate onboarding.

Core reuse:

- provider endpoints/hints можно сверять с core/provider-db;
- полноценную отправку/получение сообщений лучше позже проводить через core account APIs;
- low-level DNS/TCP/TLS/Auth probes для diagnostics лучше держать отдельно, чтобы классифицировать сетевые ошибки.

## UI flow

1. Select provider: Mail.ru / VK Mail / Yandex.
2. Enter email.
3. Enter app password.
4. Select manual network label:
   - Wi-Fi control;
   - normal mobile;
   - whitelist/restricted mobile.
5. Optional manual fields:
   - operator;
   - region/city;
   - notes.
6. Run diagnostics.
7. Show checklist with latency/error class.
8. Export sanitized JSON.

## Permissions

Minimum:

- `INTERNET`;
- `ACCESS_NETWORK_STATE`.

Useful but avoid unless necessary:

- `POST_NOTIFICATIONS` только если диагностика уходит в фон;
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` только для background delivery test;
- `READ_PHONE_STATE` не просить в MVP-0. Carrier/region лучше собирать вручную или best-effort без dangerous permissions.

## Safe data

Можно собирать:

- provider;
- endpoint host/port;
- network type;
- carrier name, если доступен без dangerous permission;
- MCC/MNC, если доступен без dangerous permission;
- Android version;
- device model;
- app version/build;
- manual network label;
- timestamp;
- latency;
- error category;
- TLS certificate subject/issuer/validity;
- generated message id;
- email domain.

Нельзя собирать:

- app password;
- raw AUTH commands;
- full SMTP/IMAP protocol transcript;
- full email address by default;
- message body with sensitive text;
- OAuth/token material;
- raw crash/logcat chunks without redaction.

## Checks

For IMAP:

- DNS resolve;
- TCP connect `993`;
- TLS handshake;
- IMAP greeting;
- IMAP login;
- `SELECT INBOX`;
- `IDLE` enter/exit;
- search/fetch by generated Message-ID;
- Spam/Junk folder detection.

For SMTP:

- DNS resolve;
- TCP connect `465`;
- TLS handshake;
- SMTP greeting;
- EHLO;
- AUTH;
- test send;
- server response classification.

End-to-end:

- send message from account A to account B, or self-send if provider supports it;
- correlate by generated Message-ID;
- record delivery latency;
- classify folder placement.

## Error categories

- `dns_fail`
- `tcp_timeout`
- `tcp_refused`
- `tls_fail`
- `auth_fail`
- `provider_config_fail`
- `smtp_rejected`
- `imap_select_fail`
- `idle_unavailable`
- `message_not_received`
- `spam_or_junk_placement`
- `network_changed`
- `unknown`

## JSON schema draft

```json
{
  "schemaVersion": 1,
  "timestampUtc": "2026-05-13T00:00:00Z",
  "app": {
    "name": "imap-android-diagnostics",
    "version": "0.0.1",
    "buildType": "debug"
  },
  "device": {
    "manufacturer": "example",
    "model": "example",
    "androidVersion": "15",
    "sdk": 35
  },
  "network": {
    "type": "mobile",
    "manualMode": "whitelist_restricted",
    "carrierName": "manual-or-system",
    "mccMnc": null,
    "region": "manual",
    "batteryOptimizationIgnored": false,
    "foregroundState": "foreground"
  },
  "provider": {
    "name": "mailru",
    "emailDomain": "example.com",
    "imap": {"host": "imap.mail.ru", "port": 993, "security": "tls"},
    "smtp": {"host": "smtp.mail.ru", "port": 465, "security": "tls"}
  },
  "checks": [
    {
      "name": "imap_tls_handshake",
      "status": "ok",
      "latencyMs": 420,
      "errorCategory": null,
      "errorMessageRedacted": null
    }
  ],
  "messageCorrelation": {
    "messageId": "<generated-id@example.invalid>",
    "received": true,
    "folder": "INBOX",
    "latencyMs": 5300
  },
  "result": "pass"
}
```

## Secret handling

- Password fields must use password input type.
- Do not persist password.
- Do not put password into `Bundle`, saved instance state, logcat, exception message or JSON.
- Redact email local-part by default.
- Do not export raw logs together with diagnostics.
- Use generated non-sensitive test body.

## Where to integrate later

Inside `deltachat-android`:

- new `DiagnosticsActivity`;
- provider selector shared with corporate onboarding;
- use existing `LogViewFragment` only for inspiration, not for raw log export;
- add entry point from `EditRelayActivity` before `rpc.addOrUpdateTransport`.
