# Corporate IMAP Messenger / messenger-imap

Android-first corporate IMAP/SMTP messenger based on a thin fork of Delta Chat Android, with a separate Corporate Control Plane, Corporate Directory, invite onboarding, APK distribution, provider profiles, diagnostics, and external contacts.

This repository is the main product, documentation, and coordination repository. Android source work lives in the separate Android fork repository.

## Download Android APK

The current internal Android smoke build is published in the Android fork repository:

- [Android internal smoke release 0.1.0](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0)
- Recommended APK: `messenger-imap-android-foss-debug-2.50.0.apk`
- SHA-256: `E50768D6DB2D1B26A76FB53A37D16ADD374E76DA544B9D2C8408B500AB320410`
- Status: internal debug build, runtime smoke pending

Warning: this APK is for internal smoke testing only. It is not a production release.

## Current Status

- MVP-0a Diagnostics accepted.
- Mail.ru / VK Mail transport baseline accepted.
- Product PRD package exists.
- Corporate Control Plane Blueprint exists.
- Corporate Directory Blueprint exists.
- Invite Onboarding & Distribution Blueprint exists.
- Android fork strategy accepted: thin fork of Delta Chat Android.
- Android internal smoke APK published as GitHub pre-release.
- Runtime smoke is still pending and must be performed on a device.

## Repositories

- [Main project / documentation repo](https://github.com/Kwentin3/messenger-imap)
- [Android fork repo](https://github.com/Kwentin3/messenger-imap-android)
- [Android internal smoke release](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0)

Repository roles:

- `messenger-imap`: product, meta, documentation, and Control Plane coordination repository.
- `messenger-imap-android`: thin fork of Delta Chat Android for Android client implementation.

## Key Documentation

Roadmaps:

- [Project Roadmap](docs/roadmap/PROJECT_ROADMAP.md)

Decisions:

- [Product Decisions Log](docs/product/decisions/PRODUCT_DECISIONS_LOG.md)
- [Android Fork Strategy Decision](docs/blueprints/ANDROID_FORK_STRATEGY_DECISION.md)

Blueprints:

- [Corporate Control Plane MVP Blueprint](docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md)
- [Corporate Directory MVP Blueprint](docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md)
- [Invite Onboarding & Distribution MVP Blueprint](docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md)

Infrastructure:

- [Infrastructure Assumptions](docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)
- [Server Audit Report](docs/infrastructure/SERVER_AUDIT_REPORT.md)

Product PRDs:

- [Root PRD](docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Android Messenger Client PRD](docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md)
- [Corporate Control Plane PRD](docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md)
- [Corporate Directory PRD](docs/product/domains/PRD_CORPORATE_DIRECTORY.md)
- [Invite Onboarding & Distribution PRD](docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md)
- [Provider Transport Profiles PRD](docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md)
- [Diagnostics & Transport Verification PRD](docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md)
- [External Contacts & Guest Access PRD](docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md)

## Runtime Smoke Checklist

- Install the recommended FOSS debug APK.
- Launch the app.
- Confirm there is no crash on startup.
- Confirm the standard Delta Chat setup path is reachable.
- Confirm the corporate onboarding entry is visible.
- Open the corporate onboarding placeholder.
- Open the fallback invite code entry.
- Enter a dummy code.
- Confirm the raw token is not displayed.
- Confirm back navigation works.

## Current Limitations

- Control Plane backend is not implemented.
- Directory/API integration is placeholder/fixture-level.
- Invite activation is placeholder-level.
- Runtime smoke is pending.
- This is not production software.
- iOS is out of scope.
- No production signing or release pipeline exists yet.

## Do Not Commit

- No APK/AAB files in git.
- No `.env` files.
- No secrets.
- No signing keys.
- No raw logs.
- APKs go to GitHub Releases, not repository files.

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
