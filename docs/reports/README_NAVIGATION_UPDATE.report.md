# README Navigation Update Report

Date: 2026-05-29

Status: Completed as docs-only README/navigation update

Project: Corporate IMAP Messenger / messenger-imap

## 1. What Was Changed

The root README in the meta repository was rewritten as the main project entry point. It now includes current status, an Android APK download section, repository roles, key documentation links, runtime smoke checklist, current limitations, and repository safety rules.

The `docs/README.md` file was updated with a `Quick Links` section for the most important project documents and Android smoke release.

The Android fork README was updated in a separate branch with a project-specific block above the upstream Delta Chat README. Upstream attribution and license sections were preserved.

## 2. Repositories Updated

- [Main project / documentation repo](https://github.com/Kwentin3/messenger-imap)
- [Android fork repo](https://github.com/Kwentin3/messenger-imap-android)

## 3. APK Release Link

- [Android internal smoke release 0.1.0](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0)
- Recommended APK: `messenger-imap-android-foss-debug-2.50.0.apk`
- SHA-256: `E50768D6DB2D1B26A76FB53A37D16ADD374E76DA544B9D2C8408B500AB320410`
- Runtime smoke status: pending

## 4. README Links Added

Meta root README:

- Android internal smoke release
- Main project / documentation repo
- Android fork repo
- Project Roadmap
- Product Decisions Log
- Android Fork Strategy Decision
- Corporate Control Plane MVP Blueprint
- Corporate Directory MVP Blueprint
- Invite Onboarding & Distribution MVP Blueprint
- Infrastructure Assumptions
- Server Audit Report
- Product PRDs

Meta `docs/README.md`:

- Android internal smoke release
- Android fork repository
- Project Roadmap
- Control Plane Blueprint
- Directory Blueprint
- Invite Blueprint
- Infrastructure Assumptions
- Server Audit Report
- Android Fork Strategy Decision
- Product Decisions Log

Android root README:

- Android internal smoke release
- Main project / documentation repo
- Android fork repo
- Recommended APK name, SHA-256, runtime status, and build commit

## 5. Links Verified

Relative markdown links were checked locally in:

- `README.md` in the meta repository branch `docs/root-readme-navigation`
- `docs/README.md` in the meta repository branch `docs/root-readme-navigation`
- `README.md` in the Android repository branch `docs/root-readme-apk-link`

All checked relative markdown links resolve to existing files in the branch where they are used.

## 6. Documents Not Linked Because Missing From Main

These documents were not linked from the main README as local files because they are not present in the current `origin/main` baseline of the meta repository:

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`
- `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md`
- `docs/blueprints/ANDROID_FORK_INTAKE_BLUEPRINT.md`
- `docs/blueprints/ANDROID_CLIENT_MVP_BLUEPRINT.md`
- `docs/reports/ANDROID_INTERNAL_SMOKE_APK_RELEASE.report.md`
- `docs/reports/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_AUDIT.report.md`
- `docs/reports/PROJECT_PRE_IMPLEMENTATION_ANAMNESIS_AND_READINESS_AUDIT.report.md`

Known related open PRs:

- [Android Client MVP Blueprint PR #6](https://github.com/Kwentin3/messenger-imap/pull/6)
- [Android Autonomous Execution PR #11](https://github.com/Kwentin3/messenger-imap/pull/11)
- [Android Internal Smoke APK Release Report PR #13](https://github.com/Kwentin3/messenger-imap/pull/13)

## 7. Remaining Actions

- Merge the meta README/navigation PR after review.
- Merge the Android README APK-link PR after review.
- Run runtime smoke on a real Android device using the recommended FOSS debug APK.
- Link the Android Autonomous Execution Roadmap and related reports from README after those documents land in `main`.

## 8. Confirmation: No APK Committed To Git

No APK, AAB, keystore, signing key, `.env`, secret, or build artifact was added to either repository. The APK remains published only as a GitHub Release asset.
