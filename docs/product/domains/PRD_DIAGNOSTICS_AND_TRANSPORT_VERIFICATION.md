# Diagnostics & Transport Verification PRD

Date: 2026-05-14

Status: high-level domain PRD.

Root PRD: [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)

## 1. Purpose

Define diagnostics and transport verification as a product capability for Corporate IMAP Messenger.

Diagnostics prove whether an IMAP/SMTP provider profile works in a specific network context. They support onboarding, provider trust, admin support, field testing, and evidence-based whitelist readiness.

## 2. Background: MVP-0a Diagnostics Accepted

MVP-0a diagnostics are accepted as successful for moving forward. A standalone Android Diagnostics APK was built, installed, launched, and used for foreground transport checks on a real Android device.

Accepted baseline:

- Mail.ru / VK Mail family as first transport baseline.
- IMAP: `imap.mail.ru:993`
- SMTP: `smtp.mail.ru:465`

Accepted limitations:

- full provider/operator matrix is deferred;
- background and locked-screen reliability are deferred;
- Yandex, Rambler, and custom profiles still require diagnostics;
- whitelist-ready status must remain evidence-based.

## 3. Goals

- Provide evidence that a provider profile works in a known context.
- Support onboarding with a clear transport check action.
- Help users and support teams understand failures.
- Attach diagnostic status to provider profiles.
- Support admin visibility into provider verification status.
- Export sanitized diagnostic reports.
- Preserve standalone diagnostics evidence until in-client diagnostics are designed.
- Avoid raw logs and secrets.

## 4. Non-goals

- No production-grade background guarantee from foreground diagnostics.
- No claim that all providers work.
- No claim that website reachability proves transport reachability.
- No raw logcat export as standard diagnostic evidence.
- No app password or raw AUTH export.
- No sensitive message payload export.
- No final in-client diagnostic UI design in this PRD.
- No detailed diagnostic API specification.

## 5. Core Checks

Diagnostics should cover the transport chain at product-relevant checkpoints:

- DNS;
- TCP;
- TLS;
- IMAP login;
- IMAP SELECT;
- IMAP IDLE;
- SMTP auth;
- SMTP send;
- receive by Message-ID;
- Spam/Junk placement.

The MVP can use the standalone diagnostics APK as accepted evidence while the messenger Blueprint defines which checks move into the client.

## 6. Where Diagnostics Appear

**Onboarding**  
The Android app should expose a product action such as **Check Transport** after provider setup and before or during membership activation, depending on policy.

**Provider Settings**  
Users or support staff should be able to re-run a connection check when provider settings change or messaging fails.

**Admin Support Flow**  
Admins should see provider diagnostic status and report references without secrets.

**Export Report**  
Field testers and support users should be able to export sanitized structured reports when required.

## 7. Diagnostic Report Requirements

Diagnostic reports must be sanitized JSON or equivalent structured format.

Reports may include:

- provider profile ID;
- provider display name;
- network context selected by tester;
- device/app build metadata where safe;
- diagnostic stage results;
- timestamps where safe;
- latency summary where safe;
- generated test Message-ID reference where safe;
- final status;
- failure stage and sanitized error class;
- report ID.

Reports must not include:

- app password;
- raw password;
- raw AUTH payload;
- raw protocol transcript with secrets;
- raw logcat;
- sensitive message payloads;
- unnecessary full personal data;
- real production message bodies.

## 8. Provider Diagnostic Status

Diagnostics produce or support provider status:

- `untested`
- `wifi_verified`
- `normal_mobile_verified`
- `whitelist_verified`
- `failed`
- `degraded`

Status must be attached to provider profile and network context. A provider can be verified in Wi-Fi and untested in whitelist mobile. A provider can be baseline for one environment but not proven in another.

Mail.ru / VK Mail has accepted baseline status based on MVP-0a evidence. Yandex, Rambler, and custom profiles require their own evidence before being marked verified.

## 9. Admin View Requirements

The control plane admin view should show:

- provider profile;
- diagnostic status;
- network context;
- last successful diagnostic report ID;
- last failed report ID where useful;
- date/time of last evidence;
- field tester or source if policy allows;
- high-level failed stage;
- warnings for stale, degraded, or untested profiles.

The admin view must not show secrets or raw logs.

## 10. MVP Scope

- Use standalone diagnostics evidence as accepted baseline.
- Represent diagnostic status in provider profiles.
- Provide a product-level **Check Transport** function in onboarding/settings, with exact implementation TBD after Blueprint.
- Export or reference sanitized reports.
- Show provider diagnostic status in admin/support context.
- Keep Mail.ru / VK Mail as accepted baseline.
- Keep Yandex, Rambler, and custom profiles unverified until evidence exists.

## 11. Later Scope

- Integrated diagnostic screen in Android client.
- Diagnostic history.
- Support export flow with redaction guarantees.
- Field campaign matrix by provider, operator, region, network mode, app version, and device family.
- Better failure explanations per stage.
- Background and locked-screen diagnostic modes.
- Automated report validation.
- Admin dashboard trends.

## 12. Acceptance Criteria

- PRD states that MVP-0a diagnostics are accepted.
- Core checks include DNS, TCP, TLS, IMAP login, IMAP SELECT, IMAP IDLE, SMTP auth, SMTP send, receive by Message-ID, and Spam/Junk placement.
- Diagnostics appear in onboarding, provider settings, admin support, and export report flows.
- Android client has a product-level **Check Transport** function or equivalent requirement.
- Diagnostic reports are sanitized JSON or equivalent structured reports.
- Reports exclude app passwords, raw AUTH, raw logcat, and sensitive payloads.
- Provider diagnostic statuses include untested, wifi_verified, normal_mobile_verified, whitelist_verified, failed, degraded.
- Whitelist-ready status is evidence-based.
- Background reliability remains later scope.

## 13. Open Questions

- Which MVP checks must be integrated into the messenger versus kept in standalone diagnostics?
- Is transport check required before activation or allowed after activation with warning?
- What exact network context labels are required for field testing?
- How long is diagnostic evidence considered fresh?
- Who can upload or approve diagnostic reports?
- Should failed diagnostics block enrollment or only warn admins?
- What report schema should be used by the Blueprint?
- How should background diagnostics be separated from foreground diagnostics?

## 14. MVP / Later / Non-goals Summary

MVP covers accepted standalone evidence, provider diagnostic status, a Check Transport product function, sanitized reports, and admin/support visibility.

Later covers integrated diagnostic UI, history, support export workflow, field campaign matrix, better failure explanations, and background/locked-screen diagnostics.

Non-goals exclude production background claims, raw log export, secrets in reports, all-provider proof, and diagnostic API specifications.
