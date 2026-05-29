# Android Execution Phase 10 Directory Read-Only Sync Report

Date: 2026-05-29

Phase: 10 - Corporate directory read-only sync slice

Meta branch: `android/autonomous-execution`

Android repo: `Kwentin3/messenger-imap-android`

Android branch: `feature/directory-readonly-sync`

Android commit: `7aa8df9e9`

Base slice: `feature/provider-transport-check` at `db76d9bfa`

## Source Docs Used

- `docs/implementation/ANDROID_CLIENT_MVP_IMPLEMENTATION_PLAN.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md`
- `docs/blueprints/ANDROID_FORK_SAFE_CUSTOMIZATION_BLUEPRINT.md`
- Phase 9 report and Android branch

## What Was Done

- Added read-only corporate directory model classes in the app layer.
- Represented `DirectoryManifest`, `DirectorySnapshot`, `directoryVersion`, `directoryHash`, internal members, external contacts and directory states.
- Added deterministic fixture hashing using SHA-256 over a stable canonical string.
- Added a sample fixture with one internal member and one external contact.
- Updated the corporate onboarding placeholder to show a fixture hash verification result.
- Built debug variants successfully.
- Pushed Android branch `feature/directory-readonly-sync`.

## What Was Not Done

- No Control Plane directory API integration was implemented.
- No Android authority or write path was introduced.
- No writes to Delta Chat core contacts or database were made.
- No system address book import was added.
- No signed IMAP/system-account update path was added.
- No chatmail/core, JNI, sync, encryption, MIME or database migration changes were made.

## Files Changed in Android Repo

- `src/main/java/org/thoughtcrime/securesms/corporate/CorporateOnboardingActivity.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/directory/CorporateDirectoryEntry.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/directory/CorporateDirectoryFixtures.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/directory/CorporateDirectoryHasher.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/directory/CorporateDirectoryManifest.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/directory/CorporateDirectoryPrincipalType.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/directory/CorporateDirectorySnapshot.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/directory/CorporateDirectoryState.java`
- `src/main/res/layout/corporate_onboarding_activity.xml`
- `src/main/res/values/strings.xml`

## Commands / Checks Run

- `git checkout -B feature/directory-readonly-sync feature/provider-transport-check`
- `$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'; .\gradlew.bat assembleDebug --stacktrace`
- `git status --short`
- `git diff --name-only`
- `git diff -U0 | rg -n "^\+.*(password|AUTH|BEGIN PRIVATE KEY|BEGIN RSA PRIVATE KEY|\.env|token|app password|raw log)"`
- `git status --short --ignored build\outputs\apk`
- `git commit -m "Add corporate directory read-only fixture model"`
- `git push -u origin feature/directory-readonly-sync`

## Tests Run

- `assembleDebug` passed.
- Fixture hash verification is executed by app-layer code when the corporate onboarding screen is created.
- APK artifacts remained ignored local build outputs.

## Acceptance Criteria Result

Accepted.

- Directory data scoped by organization/workspace: yes.
- Internal/external separation represented: yes.
- Hash verification or test placeholder present: yes.
- Stale/unavailable/hash mismatch states represented: yes.
- App builds: yes.
- Report created: yes.

## Gate Result

Gate to Phase 11 is open. The next slice can add corporate invite deep link and fallback code handling without touching SecureJoin/core.

## Blockers

No Phase 10 blocker.

## Next Phase Decision

Proceed to Phase 11: Invite deep link / fallback code slice.
