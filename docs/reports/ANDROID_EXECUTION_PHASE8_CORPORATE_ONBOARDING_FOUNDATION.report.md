# Android Execution Phase 8 Corporate Onboarding Foundation Report

Date: 2026-05-29

Phase: 8 - Corporate onboarding foundation slice

Meta branch: `android/autonomous-execution`

Android repo: `Kwentin3/messenger-imap-android`

Android branch: `feature/corporate-onboarding-foundation`

Android commit: `3b9cf49e4`

Upstream baseline commit: `a3a8b3581f82456bb7fe3342485cef4593c31315`

Core submodule: `jni/deltachat-core-rust` at `784a6abb3bae6d027062cb9dbc1bf9829905b013`

## Source Docs Used

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`
- `docs/implementation/ANDROID_CLIENT_MVP_IMPLEMENTATION_PLAN.md`
- `docs/blueprints/ANDROID_FORK_SAFE_CUSTOMIZATION_BLUEPRINT.md`
- `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md`
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`

## What Was Done

- Added an isolated corporate Android app-layer Activity: `CorporateOnboardingActivity`.
- Added a welcome-screen entry point labeled `Join organization`.
- Added a non-exported manifest entry for the placeholder Activity.
- Added placeholder onboarding status text for invite resolution, email verification, provider setup, diagnostics, activation and first directory sync.
- Built both debug variants through `assembleDebug`.
- Pushed Android branch `feature/corporate-onboarding-foundation`.

## What Was Not Done

- No membership activation was implemented.
- No Control Plane API/client was implemented.
- No email verification delivery was implemented.
- No provider credentials or app passwords were handled.
- No chatmail/core, JNI, sync, encryption, MIME or database migration code was changed.
- No package ID, app name, branding, signing or release flow was changed.

## Files Changed in Android Repo

- `src/main/AndroidManifest.xml`
- `src/main/java/org/thoughtcrime/securesms/WelcomeActivity.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/CorporateOnboardingActivity.java`
- `src/main/res/layout/corporate_onboarding_activity.xml`
- `src/main/res/layout/welcome_activity.xml`
- `src/main/res/values/strings.xml`

## Commands / Checks Run

- `git checkout -B feature/corporate-onboarding-foundation intake/upstream-build-baseline`
- `$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'; .\gradlew.bat assembleDebug --stacktrace`
- `git status --short`
- `git diff --name-only`
- `git diff -U0 | rg -n "^\+.*(password|AUTH|BEGIN PRIVATE KEY|BEGIN RSA PRIVATE KEY|\.env|token|app password|raw log)"`
- `Get-ChildItem -Path build\outputs\apk -Recurse -Filter *.apk`
- `git commit -m "Add corporate onboarding foundation placeholder"`
- `git push -u origin feature/corporate-onboarding-foundation`

## Tests Run

- `assembleDebug` passed.
- Debug APKs were produced locally under `C:\work\messenger-imap-android\build\outputs\apk\...`.
- APK artifacts were not committed.

## Acceptance Criteria Result

Accepted.

- App still builds: yes.
- Existing Delta Chat welcome/account setup remains available: yes.
- New entry point visible only on the welcome screen: yes.
- No actual membership activation: yes.
- No secrets added: yes.
- Report created: yes.

## Gate Result

Gate to Phase 9 is open. The next slice can add provider profile and transport check placeholders in the same app-layer corporate package while preserving existing provider setup.

## Blockers

No Phase 8 blocker.

## Next Phase Decision

Proceed to Phase 9: Provider profile and transport check slice.
