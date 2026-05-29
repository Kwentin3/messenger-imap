# Android Corporate Onboarding Entry Wording Slice Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Meta repo: https://github.com/Kwentin3/messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

Android branch: `feature/corporate-onboarding-entry-wording`

Android PR: https://github.com/Kwentin3/messenger-imap-android/pull/4

Status: implemented, build passed, runtime smoke pending

## 1. Executive Summary

Implemented the first safe Android onboarding slice in the Android fork. The welcome screen now exposes a visible corporate `Join organization` path first, while preserving the existing Delta Chat setup paths:

- `Create New Profile`
- `I Already Have a Profile`
- existing instant onboarding / QR / backup paths
- manual IMAP/SMTP path through existing setup

The change is app-layer only. It does not rename the app, package, launcher icon, application ID, signing config, notification channel identity, provider database, Delta Chat core, JNI/FFI, sync, encryption, MIME, or database migrations.

## 2. Branch Baseline Reconciliation Result

The Android baseline issue from the audit was handled by using the preferred route:

1. Created `feature/corporate-onboarding-entry-wording` from current Android `origin/main`.
2. Current `origin/main` includes the native build guard and internal smoke `0.1.1` build fixes.
3. Cherry-picked only the app-layer corporate onboarding foundation placeholder from the prior branch:
   - source commit: `3b9cf49e4 Add corporate onboarding foundation placeholder`
4. Added the wording/priority adjustment for this slice on top.

Resulting Android commit:

- `9285c45cc325a92ce908d8a1111481a6447256ca`

This route avoided pulling the later placeholder slices for directory, invite parser, external badges, and release metadata into this first onboarding-entry slice.

## 3. Source Docs Used

| Source | Status | Notes |
| --- | --- | --- |
| `docs/reports/ANDROID_BRANDING_AND_ONBOARDING_REFACTOR_AUDIT.report.md` | used from local docs branch / PR #16 | Not present in `origin/main` at report creation time. |
| `docs/blueprints/ANDROID_BRANDING_AND_ONBOARDING_REFACTOR_PLAN.md` | used from local docs branch / PR #16 | Not present in `origin/main` at report creation time. |
| `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` | used from main | Requires enrollment-oriented Android flow, provider-agnostic setup, no Mail.ru-only architecture, and no membership claim from APK possession. |
| `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md` | used from main | Invite flow must not imply membership by download or token possession. |
| `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md` | used from main | APK download, provider setup, diagnostics, and activation are distinct states. |
| `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md` | used from main | Control Plane owns invite resolution, verification, activation, and audit. |
| `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md` | used from main | Android is not membership/directory authority. |
| `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md` | used from main | Explicitly separates app installed, invite present, email verified, and active membership states. |
| `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md` | missing | Main contains `docs/blueprints/ANDROID_FORK_STRATEGY_DECISION.md` instead; that document was used as the available fork strategy source. |
| `docs/reports/ANDROID_INTERNAL_SMOKE_0_1_1_RUNTIME_CONFIRMATION.report.md` | used from local docs branch / PR #16 | Owner-reported install and launch success for 0.1.1; full smoke still pending. |

## 4. Files Changed

Android repo:

- `src/main/AndroidManifest.xml`
- `src/main/java/org/thoughtcrime/securesms/WelcomeActivity.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/CorporateOnboardingActivity.java`
- `src/main/res/layout/corporate_onboarding_activity.xml`
- `src/main/res/layout/welcome_activity.xml`
- `src/main/res/values/strings.xml`

Meta repo:

- `docs/reports/ANDROID_CORPORATE_ONBOARDING_ENTRY_WORDING_SLICE.report.md`

## 5. What Was Implemented

Implemented:

- added/restored `CorporateOnboardingActivity` as a minimal app-layer placeholder;
- registered the placeholder activity as non-exported in `AndroidManifest.xml`;
- wired `WelcomeActivity` to open `CorporateOnboardingActivity`;
- placed `Join organization` as the first visible welcome-screen action;
- demoted `Create New Profile` to a secondary action while keeping it visible;
- kept `I Already Have a Profile` visible;
- added welcome helper text explaining that organization invite/fallback code is the corporate path and existing Delta Chat setup remains available;
- updated corporate placeholder wording to say:
  - the screen is an internal MVP placeholder;
  - membership is not activated by the screen;
  - invite is required but not resolved by this placeholder;
  - provider setup is separate and uses existing account setup/manual IMAP/SMTP;
  - transport check is separate and does not prove email ownership or membership;
  - Control Plane integration is required for activation.

## 6. What Was Intentionally Not Changed

Not changed:

- app name / `app_name`;
- launcher icon;
- package ID / `applicationId`;
- signing config;
- notification channel identity;
- app store / Fastlane metadata;
- provider database or native provider lookup;
- Delta Chat core / chatmail/core;
- JNI/FFI;
- sync engine;
- encryption / SecureJoin / Autocrypt;
- MIME pipeline;
- database migrations;
- backend / Control Plane APIs;
- server / deployment / Traefik;
- release APK publication.

## 7. Old Account Setup Preservation Check

Preserved:

- `WelcomeActivity` still starts `InstantOnboardingActivity` from `signup_button`.
- `WelcomeActivity` still opens the existing sign-in dialog from `signin_button`.
- The sign-in dialog still exposes second-device and backup paths.
- Existing Delta Chat account setup was not removed.

Evidence:

- `src/main/java/org/thoughtcrime/securesms/WelcomeActivity.java`
- `src/main/res/layout/welcome_activity.xml`

## 8. Manual IMAP/SMTP Preservation Check

Manual IMAP/SMTP remains reachable through the existing Delta Chat setup path:

- `InstantOnboardingActivity` still imports and starts `EditRelayActivity`.
- `signup_options_view.xml` still contains `manual_account_setup_option`.
- `manual_account_setup_option` remains `Use Classic Email as Relay`.

No provider-db/native lookup logic was changed.

## 9. Build Command And Result

Initial run:

```text
.\gradlew.bat verifyNativeCoreLibraries --stacktrace
```

Result:

- failed before project tasks because the shell had `JAVA_HOME` set to Java 8.
- This was an environment issue, not a code failure.

Corrected build environment:

```text
JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot
```

Native guard:

```text
.\gradlew.bat verifyNativeCoreLibraries --stacktrace
```

Result:

- `BUILD SUCCESSFUL`

FOSS debug build:

```text
.\gradlew.bat assembleFossDebug -PABI_FILTER=arm64-v8a --stacktrace
```

Result:

- `BUILD SUCCESSFUL`
- produced local debug APK under `build/outputs/apk/foss/debug/`
- APK was not committed

APK native library inspection:

```text
tar -tf build/outputs/apk/foss/debug/messenger-imap-android-foss-debug-2.50.0.apk | Select-String libnative-utils.so
```

Result:

```text
lib/arm64-v8a/libnative-utils.so
```

## 10. Runtime Smoke Result

Runtime smoke was not executed locally because `adb devices` returned no connected device:

```text
List of devices attached
```

Runtime acceptance remains pending.

Minimum next smoke:

1. Install the locally built FOSS debug APK on an Android device.
2. Launch the app.
3. Confirm no crash on startup.
4. Confirm the welcome screen opens.
5. Confirm `Join organization` is visible.
6. Open corporate onboarding placeholder.
7. Return back.
8. Open `Create New Profile`.
9. Confirm existing setup path still opens.
10. Confirm manual IMAP/SMTP remains reachable from existing setup.
11. Confirm the screen does not show raw invite/token values.

## 11. Forbidden Areas Check

No changes were made to:

- `build.gradle`;
- package ID / `applicationId`;
- namespace;
- signing config;
- app label / `app_name`;
- launcher icon resources;
- notification channel code;
- `jni/`;
- `com.b44t.messenger` native wrapper classes;
- generated RPC layer;
- sync/encryption/MIME/database migration paths;
- provider-db/native lookup.

## 12. Secrets / Artifacts Check

Checks performed:

- `git diff --check`
- `git diff --name-status origin/main..HEAD`
- scan for committed APK/AAB/build output paths
- scan for obvious secret/key markers in changed files

Result:

- no APK/AAB/build outputs committed;
- no `.env`;
- no signing keys;
- no keystore/JKS files;
- no raw logs;
- no provider credentials;
- no app passwords added.

The scan matched existing generic password UI strings in `strings.xml`; those are pre-existing localization strings, not credentials.

## 13. Remaining Risks

- Runtime smoke is still pending on a physical device/emulator.
- The welcome screen is now corporate-first, but the app still carries Delta Chat app label/icon/branding by design.
- The corporate screen is a placeholder only; it does not resolve invites, verify email, activate membership, fetch directory, or call Control Plane.
- The broader previous corporate placeholder stack remains outside this first slice.

## 14. Recommended Next Slice

Next recommended slice:

`feature/corporate-onboarding-basic-entry-state`

Scope:

- add a fallback invite-code input on the corporate placeholder screen;
- keep the token local and redacted in UI/logs;
- add explicit internal/external placeholder state labels;
- do not call backend APIs yet;
- keep existing account setup/manual IMAP/SMTP paths unchanged.

