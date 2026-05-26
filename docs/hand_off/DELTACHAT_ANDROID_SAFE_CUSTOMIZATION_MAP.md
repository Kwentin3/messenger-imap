# Delta Chat Android Safe Customization Map

Date: 2026-05-13

Scope: handoff map for safe and high-risk customization areas before the Android IMAP/SMTP messenger MVP Blueprint.

## Baseline principle

The project must not become Mail.ru-only.

Mail.ru is the first accepted transport baseline. Architecture must support provider presets, manual provider profiles and diagnostic status per profile.

The diagnostics APK remains separate for now. Do not move it into Delta Chat Android before the Blueprint decides how diagnostics should integrate.

## Safe areas

Safe or lower-risk areas for a thin Android fork, after Blueprint approval:

- onboarding flow;
- provider selection;
- provider hints and help text;
- managed provider profiles;
- corporate domain allowlist;
- diagnostics entry point;
- address book import through existing APIs;
- UI-level restrictions;
- product documentation;
- build/CI metadata for the product fork;
- feature flags around corporate onboarding behavior.

These areas still need tests and licensing review, but they are less likely to disturb core mail synchronization, encryption or storage contracts.

## High-risk areas

High-risk areas to avoid until specifically justified:

- `chatmail/core`;
- JNI bridge;
- IMAP sync;
- SMTP queue and retry behavior;
- database migrations;
- Autocrypt, SecureJoin and encryption;
- group protocol;
- notification internals;
- background service changes before a separate background Blueprint;
- message threading/storage semantics;
- MIME generation/parsing internals;
- account migration and backup/restore.

Changing these areas can silently break compatibility, reliability or security. They should require a separate design note and targeted verification.

## Provider-agnostic profile model

The next stage should model providers explicitly instead of hardcoding Mail.ru.

Provider presets:

- Mail.ru;
- VK Mail;
- Yandex;
- later: Rambler and other providers.

Manual/custom provider fields:

- IMAP host;
- IMAP port;
- IMAP encryption: SSL/TLS or STARTTLS;
- IMAP username;
- SMTP host;
- SMTP port;
- SMTP encryption: SSL/TLS or STARTTLS;
- SMTP username;
- auth mode;
- app password.

Diagnostic status per profile:

- `untested`;
- `wifi_verified`;
- `normal_mobile_verified`;
- `whitelist_verified`;
- `failed`;
- `degraded`.

Whitelist-ready status must be based on field evidence, not provider assumption.

## Diagnostics boundary

Current rule:

- diagnostics APK stays in `prototypes/android-diagnostics/`;
- it remains the external tool for provider/network validation;
- future Android-client diagnostics screen is allowed only after the Blueprint;
- diagnostics MVP and messenger MVP must not be merged casually.

## First safe MVP fork changes

Recommended first changes, only after Blueprint:

- add product-specific provider profile layer;
- add Mail.ru/VK Mail/Yandex presets through that layer;
- add manual provider onboarding path;
- add diagnostics status fields and UI entry points;
- keep transport behavior delegated to existing Delta Chat / Chatmail mechanisms unless the Blueprint chooses another route.
