# Android Execution Phase 11 Invite Deep Link and Fallback Code Report

Date: 2026-05-29

Phase: 11 - Invite deep link / fallback code slice

Meta branch: `android/autonomous-execution`

Android repo: `Kwentin3/messenger-imap-android`

Android branch: `feature/invite-deeplink-fallback`

Android commit: `0876b4923`

Base slice: `feature/directory-readonly-sync` at `7aa8df9e9`

## Source Docs Used

- `docs/implementation/ANDROID_CLIENT_MVP_IMPLEMENTATION_PLAN.md`
- `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md`
- `docs/blueprints/ANDROID_FORK_SAFE_CUSTOMIZATION_BLUEPRINT.md`
- Phase 10 report and Android branch

## What Was Done

- Added a corporate invite parser separate from Delta Chat SecureJoin.
- Added `messenger-imap://invite/...` custom scheme handling through `CorporateOnboardingActivity`.
- Added internal/external/unknown invite kind representation.
- Added fallback invite code entry.
- Redacted invite codes before display; raw codes are not stored in route state.
- Built debug variants successfully.
- Pushed Android branch `feature/invite-deeplink-fallback`.

## What Was Not Done

- No Control Plane invite resolution or activation was implemented.
- No email verification was implemented.
- No production HTTPS app-link domain verification was implemented.
- No SecureJoin/core QR logic was changed.
- No raw invite token was logged or committed.

## Files Changed in Android Repo

- `src/main/AndroidManifest.xml`
- `src/main/java/org/thoughtcrime/securesms/corporate/CorporateOnboardingActivity.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/invite/CorporateInviteKind.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/invite/CorporateInviteParser.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/invite/CorporateInviteRoute.java`
- `src/main/res/layout/corporate_onboarding_activity.xml`
- `src/main/res/values/strings.xml`

## Commands / Checks Run

- `git checkout -B feature/invite-deeplink-fallback feature/directory-readonly-sync`
- `$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'; .\gradlew.bat assembleDebug --stacktrace`
- `git status --short`
- `git diff --name-only`
- `git diff -U0 | rg -n "^\+.*(AUTH|BEGIN PRIVATE KEY|BEGIN RSA PRIVATE KEY|\.env|app password|raw log|raw token|password\s*=)"`
- `git diff -U0 | rg -n "^\+.*token"`
- `git commit -m "Add corporate invite deep link fallback placeholder"`
- `git push -u origin feature/invite-deeplink-fallback`

## Tests Run

- `assembleDebug` passed.
- Intent handling path compiles for custom scheme placeholder.
- No device/emulator manual intent test was run in this phase.

## Acceptance Criteria Result

Accepted.

- Internal/external invite types represented: yes.
- Invalid/unresolved placeholder state supported: yes.
- Fallback code entry supported: yes.
- No token leakage in added code/reports: yes; only `tokenPresent` boolean is present.
- App builds: yes.
- Report created: yes.

## Gate Result

Gate to Phase 12 is open. The next slice can use the existing directory fixture to display external contact separation and warnings.

## Blockers

No Phase 11 blocker.

## Next Phase Decision

Proceed to Phase 12: External contact badge / warning slice.
