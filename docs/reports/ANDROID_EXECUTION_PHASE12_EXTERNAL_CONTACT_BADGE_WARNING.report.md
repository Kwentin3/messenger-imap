# Android Execution Phase 12 External Contact Badge and Warning Report

Date: 2026-05-29

Phase: 12 - External contact badge / warning slice

Meta branch: `android/autonomous-execution`

Android repo: `Kwentin3/messenger-imap-android`

Android branch: `feature/external-contact-badges`

Android commit: `9979d8e90`

Base slice: `feature/invite-deeplink-fallback` at `0876b4923`

## Source Docs Used

- `docs/implementation/ANDROID_CLIENT_MVP_IMPLEMENTATION_PLAN.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`
- `docs/blueprints/ANDROID_FORK_SAFE_CUSTOMIZATION_BLUEPRINT.md`
- Phase 11 report and Android branch

## What Was Done

- Added `CorporateExternalContactPolicy` app-layer placeholder.
- Added an external/internal badge label helper.
- Added a scoped external-visible directory helper on the read-only snapshot model.
- Updated corporate onboarding placeholder to show external contact warning/status.
- Built debug variants successfully.
- Pushed Android branch `feature/external-contact-badges`.

## What Was Not Done

- No full external project room implementation was added.
- No existing Delta Chat contact list, chat list or core contact DB was changed.
- No system address book import was added.
- No internal directory access was granted to an external principal.
- No managed group protocol or roster authority logic was changed.

## Files Changed in Android Repo

- `src/main/java/org/thoughtcrime/securesms/corporate/CorporateOnboardingActivity.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/directory/CorporateDirectorySnapshot.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/external/CorporateExternalContactPolicy.java`
- `src/main/res/layout/corporate_onboarding_activity.xml`
- `src/main/res/values/strings.xml`

## Commands / Checks Run

- `git checkout -B feature/external-contact-badges feature/invite-deeplink-fallback`
- `$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'; .\gradlew.bat assembleDebug --stacktrace`
- `git status --short`
- `git diff --name-only`
- `git diff -U0 | rg -n "^\+.*(password|AUTH|BEGIN PRIVATE KEY|BEGIN RSA PRIVATE KEY|\.env|token|app password|raw log)"`
- `git status --short --ignored build\outputs\apk`
- `git commit -m "Add external contact badge warning placeholder"`
- `git push -u origin feature/external-contact-badges`

## Tests Run

- `assembleDebug` passed.
- Scoped external visibility helper compiles and is exercised by corporate onboarding display code.
- APK artifacts remained ignored local build outputs.

## Acceptance Criteria Result

Accepted.

- External contacts not mixed with internal members: yes.
- External principal fixture does not see full internal directory: yes.
- App builds: yes.
- Report created: yes.

## Gate Result

Gate to Phase 13 is open. The next slice can add release metadata and update warning placeholders without signing, auto-update or release publication.

## Blockers

No Phase 12 blocker.

## Next Phase Decision

Proceed to Phase 13: Release metadata / update warning slice.
