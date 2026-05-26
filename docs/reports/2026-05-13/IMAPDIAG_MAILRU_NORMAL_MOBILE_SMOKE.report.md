# IMAP Diagnostics Runtime Report: Mail.ru Normal Mobile Smoke

Date: 2026-05-13

Source JSON: `docs/research/imapdiag_20260513_220755_mailru_unknown_operator_normal_mobile_foreground.json`

## Executive Summary

The first real Android runtime JSON was reviewed. The APK launched on a Huawei device and successfully completed Mail.ru foreground connectivity/auth diagnostics over a normal mobile network.

This is a useful positive signal for MVP-0a runtime viability, but it is not a `transport_pass`. The run used `single_account_smoke`, did not send a message, did not perform receive correlation and was not run in whitelist/restricted mode.

## Device And Network

- Device: HUAWEI CET-LX9.
- Android: 12 / SDK 31.
- Network type: `mobile`.
- Manual mode: `normal_mobile`.
- Operator: `unknown_operator`.
- VPN active: `false`.
- Battery optimization ignored: `false`.

## Provider

- Provider: Mail.ru.
- IMAP: `imap.mail.ru:993` TLS.
- SMTP: `smtp.mail.ru:465` TLS.
- Sender/receiver domain in report: `mail.ru`.
- Email local-part: masked.
- Debug override: `false`.

## Check Results

All 13 executed checks passed:

- `imap_dns`: ok, 74 ms, IPv4 `217.69.139.90`.
- `imap_tcp`: ok, 60 ms.
- `imap_tls_handshake`: ok, 241 ms, TLSv1.2.
- `imap_greeting`: ok, 79 ms.
- `imap_login`: ok, 86 ms.
- `imap_select_inbox`: ok, 58 ms.
- `imap_idle_enter_exit`: ok, 96 ms.
- `smtp_dns`: ok, 79 ms, IPv4/IPv6 addresses resolved.
- `smtp_tcp`: ok, 58 ms.
- `smtp_tls_handshake`: ok, 178 ms, TLSv1.2.
- `smtp_greeting`: ok, 95 ms.
- `smtp_ehlo`: ok, 42 ms.
- `smtp_auth`: ok, 96 ms.

## Message Correlation

- Delivery mode: `single_account_smoke`.
- Send accepted: `false`.
- Received: `false`.
- Folder: `not_requested`.
- Final status: `smoke_no_send`.

This is expected for smoke mode. It means the run checks endpoint reachability and authentication, not messenger-like delivery.

## Result Classification

- Report result: `diagnostic_only`.
- Valid for whitelist conclusion: `false`.
- Invalidation reasons:
  - `manual_mode_not_whitelist`;
  - `provider_preflight_not_confirmed`.

Interpretation: Mail.ru IMAP/SMTP foreground connectivity and auth worked on this Huawei phone over normal mobile network. The run cannot prove whitelist compatibility and cannot prove two-account transport.

## Security Review

The reviewed JSON does not contain:

- app password;
- raw AUTH command;
- raw IMAP `LOGIN` command;
- base64 auth payload;
- OAuth/token material;
- raw protocol transcript;
- raw logcat;
- unmasked email local-part.

The report includes provider endpoint IP addresses. This is acceptable endpoint metadata for diagnostics.

## Observed Issue

The saved filename was:

```text
imapdiag_20260513_220755_mailru_unknown_operator_normal_mobile_foreground.json
```

The Blueprint convention expects a result suffix:

```text
imapdiag_YYYYMMDD_HHMMSS_provider_operator_mode_scenario_result.json
```

For this run, the expected suffix would be `_diagnostic_only.json`. The source code currently includes result in `DiagnosticReport.exportFileName()`, so this should be checked on the next export: it may be a manual rename/system picker artifact or an older APK/version mismatch.

## Next Recommended Runs

1. Repeat Mail.ru `single_account_smoke` on Wi-Fi control and normal mobile, 3 times each.
2. Run Mail.ru `two_account_canonical` on Wi-Fi control with two controlled accounts.
3. If Wi-Fi control passes, run Mail.ru `two_account_canonical` on normal mobile.
4. Only after that, run whitelist/restricted mobile tests.
5. Capture adb logcat no-secret check during a device run, without committing raw logcat.
