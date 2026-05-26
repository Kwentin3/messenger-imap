# Android Diagnostics MVP-0a Closure Report

Date: 2026-05-13

Scope: closure of the MVP-0a Android IMAP/SMTP diagnostics track.

## 1. Executive summary

The project implemented a standalone Android Diagnostics APK for foreground IMAP/SMTP transport checks on a real Android phone.

The debug APK was built, installed and launched on a real Huawei Android device. It exported sanitized JSON reports. Mail.ru IMAP/SMTP transport passed real foreground diagnostics, including canonical two-account SMTP send plus IMAP receive correlation by Message-ID.

The MVP-0a diagnostic track is accepted as successful for moving the project forward. This acceptance is made with known limitations and does not claim full provider, operator, background or locked-screen coverage.

## 2. Initial goal

MVP-0a was scoped as a diagnostic track, not a messenger product.

The goal was:

- build a standalone Android diagnostics APK;
- run foreground-only transport diagnostics;
- test whether IMAP/SMTP can work as a transport on Android under real mobile conditions;
- avoid starting a Delta Chat fork;
- avoid changing chatmail/core;
- avoid proving background or locked-screen reliability.

The intended proof was transport-level evidence: DNS, TCP, TLS, IMAP, SMTP, send and receive correlation. It was not a production UX, sync engine, encryption architecture or background delivery milestone.

## 3. Implemented scope

The implemented diagnostics APK includes:

- provider selector;
- Mail.ru, VK Mail and Yandex presets;
- custom/debug endpoint override in debug builds;
- `single_account_smoke` mode;
- `two_account_canonical` mode;
- DNS/TCP/TLS checks;
- IMAP login, `SELECT INBOX` and IDLE enter/exit;
- SMTP auth and send;
- generated Message-ID correlation;
- Spam/Junk detection heuristic;
- sanitized JSON export;
- explicit timeout policy exported in reports;
- `fieldValidity` classification;
- no dangerous Android permissions.

The APK intentionally stayed narrow and diagnostic-only.

## 4. Build/runtime result

Build/runtime status:

- Gradle wrapper added under `prototypes/android-diagnostics/`.
- Debug APK build passed with `.\gradlew.bat clean assembleDebug --no-daemon`.
- APK artifact: `prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk`.
- Device used for known runtime reports: HUAWEI CET-LX9, Android 12 / SDK 31.
- Manual APK install passed after the device installer/source-permission flow.
- App launch passed.
- Sanitized JSON export passed.

The manifest remained limited to `INTERNET` and `ACCESS_NETWORK_STATE`.

## 5. Test evidence summary

Known accepted Mail.ru evidence:

- Mail.ru Wi-Fi control `two_account_canonical`: `transport_pass`.
- Mail.ru mobile MTS Krasnodar kray `two_account_canonical`: `transport_pass`.
- Mail.ru mobile smoke: IMAP/SMTP DNS, TCP, TLS, login/auth and connectivity checks passed.
- Message delivery passed: SMTP accepted the generated test message and IMAP received it by generated Message-ID.
- The canonical messages arrived in `INBOX`.
- Spam/Junk flag was `false`.
- VPN best-effort flag was `false`.

Relevant sanitized JSON artifacts:

- `docs/research/imapdiag_20260513_222425_mailru_unknown_operator_wifi_control_foreground.json`
- `docs/research/imapdiag_20260513_225420_mailru_mts_normal_mobile_foreground_transport.json`
- `docs/research/imapdiag_20260513_220755_mailru_unknown_operator_normal_mobile_foreground.json`

This report intentionally does not include app passwords, tokens, raw AUTH payloads, raw protocol transcript, raw logcat or full email addresses.

## 6. Whitelist context clarification

One or more JSON reports may be formally marked as `normal_mobile`.

The tester later confirmed that the actual phone network context was whitelist/restricted mobile, despite the JSON manual mode value. The practical signs were:

- resources from the whitelist worked;
- Mail.ru and similar allowed services worked;
- Gosuslugi and other allowed services worked;
- Google and YouTube did not open.

This is recorded as a tester-confirmed whitelist context / manual context override. The original JSON files must not be edited retroactively.

Management interpretation: the Mail.ru mobile `transport_pass` is accepted as sufficient evidence that Mail.ru IMAP/SMTP can work in the factual whitelist/restricted mobile context observed by the tester.

For future tests, the tester must select `manualMode = whitelist_restricted` when the phone is in that restricted network mode.

## 7. Accepted decision

- MVP-0a Diagnostic Track: accepted.
- Mail.ru transport baseline: accepted.
- Continue to next-stage architecture: yes.
- Full provider/operator matrix: deferred; not required before the next architecture step.
- MVP-0b background/locked-screen: deferred.
- Delta Chat fork: not started.

## 8. Known limitations

Known limitations at closure:

- not all mobile operators were tested;
- VK Mail runtime was not verified;
- Yandex runtime was not verified;
- a full repeat matrix was not completed;
- background receive was not tested;
- locked-screen receive was not tested;
- runtime logcat no-secret check may remain open;
- exported filename suffix mismatch was observed;
- `fieldValidity` and preflight semantics need refinement;
- `messageCorrelation.sendLatencyMs` can be wrong or inconsistent with the `smtp_test_send` check latency;
- part of the field evidence has a formal JSON `normal_mobile` label despite tester-confirmed whitelist/restricted context.

These limitations are accepted as non-blocking for the architecture transition.

## 9. Non-blocking bugs/debts

Must fix before a large field campaign:

- make the manual network mode explicit and hard to mislabel;
- improve `fieldValidity` and preflight semantics;
- fix or document exported filename suffix behavior;
- fix `messageCorrelation.sendLatencyMs`;
- perform controlled runtime logcat no-secret validation;
- add clearer tester instructions for operator, region and whitelist context capture.

Nice to fix:

- add repeat-count tracking or run grouping;
- improve provider-specific Spam/Junk folder handling;
- improve actionable error text per failed diagnostic stage;
- validate exported runtime JSON against the schema as part of QA;
- document Huawei/Android sideload friction for field testers.

Not blocking the next architecture stage:

- full operator/provider/repeat matrix;
- VK Mail/Yandex field validation;
- background and locked-screen reliability;
- messenger UI implementation;
- Delta Chat fork or chatmail/core changes.

## 10. Final verdict

MVP-0a fulfilled its purpose: it produced enough evidence to continue designing an Android IMAP/SMTP-backed messenger.

Mail.ru is the first accepted transport baseline. The Diagnostic APK remains available for validating additional providers and networks.
