# Android MVP Handoff Report

Date: 2026-05-29

Phase: 15 - MVP handoff and next-stage planning

Meta branch: `android/autonomous-execution`

Android repo: `Kwentin3/messenger-imap-android`

Final Android branch: `feature/release-metadata-warning`

Final Android commit: `8a51805d4`

Baseline: `intake/upstream-build-baseline` at `a3a8b3581f82456bb7fe3342485cef4593c31315`

## Source Docs Used

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`
- `docs/implementation/ANDROID_CLIENT_MVP_IMPLEMENTATION_PLAN.md`
- `docs/blueprints/ANDROID_FORK_SAFE_CUSTOMIZATION_BLUEPRINT.md`
- Phase 0-14 reports
- Android branch history in `Kwentin3/messenger-imap-android`

## Implemented Slices

- Phase 8: Corporate onboarding foundation placeholder.
- Phase 9: Provider profile and transport check placeholder.
- Phase 10: Corporate directory read-only fixture model with hash check and stale states.
- Phase 11: Corporate invite custom scheme and fallback code placeholder.
- Phase 12: External contact badge/warning placeholder and scoped external view helper.
- Phase 13: Release metadata/update warning placeholder and redacted support diagnostics summary.
- Phase 14: Build/static QA.

## Android Branch / Commit Index

- `intake/upstream-build-baseline` -> `a3a8b3581`
- `feature/corporate-onboarding-foundation` -> `3b9cf49e4`
- `feature/provider-transport-check` -> `db76d9bfa`
- `feature/directory-readonly-sync` -> `7aa8df9e9`
- `feature/invite-deeplink-fallback` -> `0876b4923`
- `feature/external-contact-badges` -> `9979d8e90`
- `feature/release-metadata-warning` -> `8a51805d4`

## Tests and Checks

- Clean upstream/fork debug build passed in Phase 4.
- Each implementation branch passed `.\gradlew.bat assembleDebug --stacktrace`.
- Final branch passed `assembleDebug`.
- Final branch working tree was clean.
- No APK/AAB/signing key/`.env` was committed.
- Secret-pattern scans on added diffs did not find raw secrets.
- `adb devices` showed no connected device; runtime smoke remains pending.

## Upstream Divergence Summary

The Android fork currently diverges from upstream by six small app-layer commits on top of `a3a8b3581`. The diff is concentrated in:

- `WelcomeActivity.java`
- `AndroidManifest.xml`
- `src/main/java/org/thoughtcrime/securesms/corporate/**`
- `corporate_onboarding_activity.xml`
- `welcome_activity.xml`
- `strings.xml`

No chatmail/core, JNI, sync, encryption, MIME, database migration, notification service, provider-db, package ID, app name, signing or release pipeline changes were made.

## Compliance Notes

- Delta Chat Android is GPLv3+ lineage.
- Modified APK distribution still requires corresponding source distribution and notice preservation.
- Legal/compliance review remains required before public/commercial APK distribution.
- This work produced local debug APKs only; no APK was published or committed.

## Release Blockers

- GPL/source publication workflow not finalized.
- Signing key custody not defined.
- Release storage/download channel not defined.
- Package ID/app name/branding decision not implemented.
- Production APK SHA-256 metadata cannot be final until release build/signing flow exists.

## Integration Blockers

- Control Plane backend/API is not implemented in this roadmap.
- Email verification provider and invite activation API are not implemented.
- Directory API and canonical production payload are not implemented.
- Provider profile source of truth is not implemented.
- Diagnostics evidence upload is not implemented.
- Device/emulator runtime smoke remains pending.

## Ready for Control Plane Integration Test?

Not yet for a real end-to-end integration test. The Android app-layer placeholders are ready for review and for the next implementation slice, but real integration needs Control Plane API contracts, test server endpoints, invite verification, directory snapshot endpoint and diagnostics policy.

## Recommended Next Slice

Run Android runtime smoke on an emulator/device, then create the first real integration Blueprint for Android-to-Control-Plane contracts:

- invite resolution contract;
- email verification challenge contract;
- provider profile handoff contract;
- diagnostics evidence contract;
- directory manifest/snapshot fetch contract.

## Acceptance Criteria Result

Accepted with documented runtime and backend limitations.

All roadmap phases have either completed or recorded their limitations. No stop-condition blocker was encountered.
