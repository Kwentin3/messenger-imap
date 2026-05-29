# Corporate IMAP Messenger / messenger-imap

Android-first corporate IMAP/SMTP messenger based on a thin fork of Delta Chat Android, with Corporate Control Plane, Corporate Directory, invite onboarding, provider profiles, diagnostics, and external contacts.

## Download Android APK

Current internal smoke build:

- [Android Internal Smoke APK 0.1.2](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.2)
- Direct APK download: [messenger-imap-android-foss-debug-2.50.0.apk](https://github.com/Kwentin3/messenger-imap-android/releases/download/android-internal-smoke-0.1.2/messenger-imap-android-foss-debug-2.50.0.apk)
- Recommended APK: `messenger-imap-android-foss-debug-2.50.0.apk`
- SHA-256: `9510CEDBC9FA30099339A6B03DAAA6DBBFF1F8446406193B1BA1799F24A599EF`
- ABI coverage: `arm64-v8a`
- Status: internal debug build, runtime smoke pending
- Includes: corporate onboarding entry and local fallback invite-code placeholder states

Warning: this APK is for internal smoke testing only. It is not a production release.

Previous release [0.1.1](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.1) installed and launched successfully on the owner's Huawei device, but it does not include the current corporate onboarding closeout slices.

Previous release [0.1.0](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0) is broken and rejected for runtime smoke. It installs but crashes on launch on a Huawei device.

## Repositories

- [Main project repo](https://github.com/Kwentin3/messenger-imap)
- [Android fork repo](https://github.com/Kwentin3/messenger-imap-android)
- [Android internal smoke release 0.1.2](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.2)
- [Direct FOSS debug APK download](https://github.com/Kwentin3/messenger-imap-android/releases/download/android-internal-smoke-0.1.2/messenger-imap-android-foss-debug-2.50.0.apk)

Repository roles:

- `messenger-imap` is the product, meta, documentation, and Control Plane coordination repository.
- `messenger-imap-android` is the thin fork of Delta Chat Android for Android client implementation.

## Current Status

- MVP-0a diagnostics accepted.
- Mail.ru / VK Mail baseline accepted.
- Thin fork Delta Chat Android selected.
- Android fork repository exists.
- Current internal smoke APK `0.1.2` published through GitHub Releases.
- Android main contains corporate onboarding entry and fallback invite-code placeholder states.
- Corrected internal smoke APK `0.1.1` installed and launched successfully on the owner's Huawei device.
- Broken internal smoke APK `0.1.0` rejected after Huawei startup crash.
- Runtime smoke for `0.1.2` is pending.
- Control Plane, Corporate Directory, and Invite Onboarding Blueprints exist.
- Control Plane backend is not implemented.
- Directory/API integration is placeholder-level.
- APK files are not stored in git.
- Not production.

## Quick Documentation Links

- [Project Roadmap](docs/roadmap/PROJECT_ROADMAP.md)
- [Corporate Control Plane MVP Blueprint](docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md)
- [Corporate Directory MVP Blueprint](docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md)
- [Invite Onboarding & Distribution MVP Blueprint](docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md)
- [Infrastructure Assumptions](docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)
- [Server Audit Report](docs/infrastructure/SERVER_AUDIT_REPORT.md)
- [Product Decisions Log](docs/product/decisions/PRODUCT_DECISIONS_LOG.md)

## Runtime Smoke Checklist

- Download the FOSS debug APK.
- Install on an Android device.
- Launch the app.
- Confirm there is no crash.
- Confirm the standard Delta Chat setup path is reachable.
- Confirm the corporate onboarding entry is visible.
- Open the corporate onboarding placeholder.
- Press check with empty code.
- Enter `INT-TEST-001` and confirm internal placeholder state.
- Enter `EXT-TEST-001` and confirm external placeholder state.
- Enter `BADCODE` and confirm invalid placeholder state.
- Confirm the raw code is cleared and not repeated in status.
- Confirm back navigation works.
- Confirm `Create New Profile` opens the existing setup.
- Confirm `I Already Have a Profile` opens the existing flow.
- Confirm manual IMAP/SMTP remains reachable.

## Safety / Do Not Commit

- APKs are published in GitHub Releases, not committed to git.
- No `.env`.
- No secrets.
- No signing keys.
- No raw logs.
- No APK/AAB/build outputs.

## Repository Layout

```text
docs/
  blueprints/   Product and technical planning inputs.
  hand_off/     Handoff notes and prompt drafts.
  product/      Product PRDs, decisions, and product context.
  reports/      Research, implementation, validation, and bootstrap reports.
  research/     Transport, provider, Delta Chat / Chatmail research, and evidence JSON.
  roadmap/      Execution roadmap between PRD package and technical Blueprints.
  upstream/     Upstream project references and license notes.
prototypes/
  android-diagnostics/  Standalone Android Diagnostics APK source and docs.
releases/
  README.md     Policy for binary release artifacts.
```

## Import Boundaries

This repository intentionally does not include:

- Delta Chat / Chatmail upstream clones;
- local git worktrees;
- APK build outputs;
- `.gradle`, `build`, `app/build`;
- `node_modules`;
- local account databases;
- raw logcat or raw protocol logs;
- real credentials, app passwords, tokens, or raw AUTH payloads.
