# Corporate IMAP Messenger / messenger-imap

Android-first corporate IMAP/SMTP messenger based on a thin fork of Delta Chat Android, with Corporate Control Plane, Corporate Directory, invite onboarding, provider profiles, diagnostics, and external contacts.

## Download Android APK

Current internal smoke build:

- [Android Internal Smoke APK 0.1.0](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0)
- Direct APK download: [messenger-imap-android-foss-debug-2.50.0.apk](https://github.com/Kwentin3/messenger-imap-android/releases/download/android-internal-smoke-0.1.0/messenger-imap-android-foss-debug-2.50.0.apk)
- Recommended APK: `messenger-imap-android-foss-debug-2.50.0.apk`
- SHA-256: `E50768D6DB2D1B26A76FB53A37D16ADD374E76DA544B9D2C8408B500AB320410`
- Status: broken internal debug build, startup crash reported on Huawei during runtime smoke

Warning: this APK is not accepted for smoke testing anymore. It installs but crashes on launch on a Huawei device. Do not treat it as a working build or production release.

## Repositories

- [Main project repo](https://github.com/Kwentin3/messenger-imap)
- [Android fork repo](https://github.com/Kwentin3/messenger-imap-android)
- [Android internal smoke release 0.1.0](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0)
- [Direct FOSS debug APK download](https://github.com/Kwentin3/messenger-imap-android/releases/download/android-internal-smoke-0.1.0/messenger-imap-android-foss-debug-2.50.0.apk)

Repository roles:

- `messenger-imap` is the product, meta, documentation, and Control Plane coordination repository.
- `messenger-imap-android` is the thin fork of Delta Chat Android for Android client implementation.

## Current Status

- MVP-0a diagnostics accepted.
- Mail.ru / VK Mail baseline accepted.
- Thin fork Delta Chat Android selected.
- Android fork repository exists.
- Internal smoke APK published through GitHub Releases.
- Runtime smoke failed on Huawei: both FOSS and GPlay debug APKs installed and crashed on launch.
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
- Open the fallback invite code entry.
- Enter a dummy code.
- Confirm the raw token is not displayed.
- Confirm back navigation works.

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
