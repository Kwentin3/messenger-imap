# Android Corporate Onboarding Basic Entry State Slice Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Meta repo: https://github.com/Kwentin3/messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

Android branch: `feature/corporate-onboarding-basic-entry-state`

Android PR: https://github.com/Kwentin3/messenger-imap-android/pull/5

Status: implemented, stacked PR open, build passed, runtime smoke pending

## 1. Executive Summary

Implemented the `corporate-onboarding-basic-entry-state` Android slice as a stacked app-layer change on top of `feature/corporate-onboarding-entry-wording`.

The corporate onboarding placeholder now supports local fallback invite-code entry and shows typed placeholder states:

- empty / waiting for code;
- internal invite placeholder;
- external invite placeholder;
- invalid placeholder.

The invite code is parsed locally only. It is not sent to a backend, not persisted, not logged, and cleared from the input after checking. The screen still states that invite possession does not activate membership and that Control Plane verification, email ownership confirmation, provider setup, and later directory sync remain separate future steps.

## 2. Source Documents Used

| Source | Status | Notes |
| --- | --- | --- |
| `docs/reports/ANDROID_BRANDING_AND_ONBOARDING_REFACTOR_AUDIT.report.md` | used from PR #16 / branch `docs/android-branding-onboarding-audit` | Not present in `origin/main` at report creation time. |
| `docs/blueprints/ANDROID_BRANDING_AND_ONBOARDING_REFACTOR_PLAN.md` | used from PR #16 / branch `docs/android-branding-onboarding-audit` | Defines this next slice as fallback invite-code input with redaction/local-only behavior. |
| `docs/reports/ANDROID_CORPORATE_ONBOARDING_ENTRY_WORDING_SLICE.report.md` | used from PR #17 / branch `docs/android-corporate-onboarding-entry-report` | Previous slice report; not present in `origin/main` at report creation time. |
| `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` | used from main | Requires invite/fallback code handling, provider-agnostic setup, and no membership claim from invite token or APK possession. |
| `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md` | used from main | Defines fallback code and invite activation as distinct from download/install. |
| `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md` | used from main | States that APK download, invite token possession, provider setup, diagnostics, and activation are separate states. |
| `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md` | used from main | Control Plane owns real invite resolution, email verification, activation, audit, and state authority. |
| `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md` | used from main | Directory sync and membership visibility are not Android-local authority. |
| `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md` | used from main | Explicit trust-state separation: app installed, invite present, email verified, active member, external contact. |
| `docs/reports/ANDROID_INTERNAL_SMOKE_0_1_1_RUNTIME_CONFIRMATION.report.md` | used from PR #16 / branch `docs/android-branding-onboarding-audit` | Owner reported 0.1.1 install and launch success; full checklist still pending. |
| `docs/reports/ANDROID_INTERNAL_SMOKE_APK_RELEASE_0_1_1.report.md` | used from main | Confirms native build flow, arm64 APK, and `libnative-utils.so`. |

## 3. Android Branch Baseline

This PR is stacked because the previous onboarding-entry slice is not merged to `main` yet.

| Item | Value |
| --- | --- |
| Base branch | `feature/corporate-onboarding-entry-wording` |
| Base commit | `9285c45cc325a92ce908d8a1111481a6447256ca` |
| Head branch | `feature/corporate-onboarding-basic-entry-state` |
| Head commit | `acbef3560b03252e21d78696e124999cf59af7c7` |
| Android PR | https://github.com/Kwentin3/messenger-imap-android/pull/5 |
| Dependency PR | https://github.com/Kwentin3/messenger-imap-android/pull/4 |
| Merge order | merge PR #4 first, then PR #5 |
| PR #5 mergeability | mergeable against its stacked base at creation time |

The diff against `feature/corporate-onboarding-entry-wording` contains only the basic entry-state slice.

## 4. Files Changed

Android repo, compared to `feature/corporate-onboarding-entry-wording`:

- `src/main/java/org/thoughtcrime/securesms/corporate/CorporateInviteCodeParser.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/CorporateInvitePlaceholderState.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/CorporateOnboardingActivity.java`
- `src/main/res/layout/corporate_onboarding_activity.xml`
- `src/main/res/values/strings.xml`
- `src/test/java/org/thoughtcrime/securesms/corporate/CorporateInviteCodeParserTest.java`

Meta repo:

- `docs/reports/ANDROID_CORPORATE_ONBOARDING_BASIC_ENTRY_STATE_SLICE.report.md`

## 5. Placeholder State Model

Implemented app-layer parser rules:

| Input | State | UI meaning |
| --- | --- | --- |
| empty / whitespace / null | `EMPTY` | Waiting for an organization invite code or invite link. |
| starts with `INT-` | `INTERNAL_PLACEHOLDER` | Internal organization invite detected locally. |
| starts with `ORG-` | `INTERNAL_PLACEHOLDER` | Internal organization invite detected locally. |
| starts with `EXT-` | `EXTERNAL_PLACEHOLDER` | External contact invite detected locally. |
| starts with `GUEST-` | `EXTERNAL_PLACEHOLDER` | External contact invite detected locally. |
| anything else | `INVALID_PLACEHOLDER` | Format not recognized in this placeholder build. |

The parser is implemented in `CorporateInviteCodeParser`. It returns a `CorporateInvitePlaceholderState` enum and does not perform I/O, logging, persistence, backend calls, or Android framework work.

## 6. UI Behavior Implemented

The corporate onboarding placeholder now shows:

- fallback invite-code input;
- `Check invite code` action;
- visible status text;
- placeholder/local-only explanation;
- explicit statement that code checking does not activate membership;
- existing provider setup and transport check status rows remain visible as separate next steps.

User-visible states:

- empty state: user is asked to enter an organization invite code or open an invite link;
- internal state: internal organization invite detected, but membership still requires Control Plane verification, email ownership confirmation, provider setup, and policy activation;
- external state: external contact invite detected, which would continue as scoped external relationship, not employee membership;
- invalid state: invite code format is not recognized in this placeholder build.

The raw code is cleared from the input after checking, and the status text never echoes the raw code.

## 7. Security / Privacy Behavior

Implemented:

- no backend call;
- no Control Plane call;
- no persistence;
- no log statement;
- no diagnostics export;
- no clipboard access;
- no QR/camera permission use;
- no contacts permission use;
- raw code is cleared after local parsing;
- raw code is not displayed in the status.

This is intentionally a local placeholder only.

## 8. What Was Intentionally Not Implemented

Not implemented:

- real invite resolution;
- real email verification;
- real membership activation;
- real external relationship activation;
- real directory fetch or sync;
- provider profile enforcement;
- Mail.ru-only onboarding;
- backend/API integration;
- server/deployment/Traefik changes;
- APK publication;
- app identity changes.

## 9. Build Command And Result

Environment:

```text
JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot
```

Parser unit test:

```text
.\gradlew.bat testFossDebugUnitTest --tests org.thoughtcrime.securesms.corporate.CorporateInviteCodeParserTest --stacktrace
```

Result:

- `BUILD SUCCESSFUL`

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
- local APK produced under `build/outputs/apk/foss/debug/`
- APK was not committed

APK inspection:

```text
tar -tf build/outputs/apk/foss/debug/messenger-imap-android-foss-debug-2.50.0.apk | Select-String libnative-utils.so
```

Result:

```text
lib/arm64-v8a/libnative-utils.so
```

## 10. Runtime Smoke Result

Runtime smoke was not executed locally because no Android device/emulator was connected:

```text
List of devices attached
```

Runtime acceptance is pending.

Minimum future smoke:

1. Launch app.
2. Confirm welcome screen opens.
3. Open `Join organization`.
4. Press `Check invite code` with empty input.
5. Enter `INT-TEST-001`; confirm internal placeholder state.
6. Enter `EXT-TEST-001`; confirm external placeholder state.
7. Enter `BADCODE`; confirm invalid placeholder state.
8. Confirm raw code is cleared and not repeated in status.
9. Confirm back navigation works.
10. Open `Create New Profile`.
11. Confirm `I Already Have a Profile` still opens old flow.
12. Confirm manual IMAP/SMTP remains reachable through existing setup.

## 11. Old Account Setup Preservation Check

No files from the old account setup path were changed in this slice.

Preserved by dependency branch and unchanged here:

- `WelcomeActivity` still routes `Create New Profile` to `InstantOnboardingActivity`;
- `I Already Have a Profile` still opens the existing second-device / backup dialog;
- old Delta Chat setup remains available.

## 12. Manual IMAP/SMTP Preservation Check

Manual IMAP/SMTP remains reachable through existing setup:

- `InstantOnboardingActivity` still owns the old setup flow;
- `EditRelayActivity` remains untouched;
- `signup_options_view.xml` remains untouched;
- provider-db/native lookup remains untouched.

## 13. Forbidden Areas Check

No changes were made to:

- `build.gradle`;
- `applicationId`;
- package namespace;
- app label / `app_name`;
- launcher icon;
- signing config;
- notification channel identity;
- app store metadata;
- chatmail/core;
- JNI/FFI;
- sync engine;
- encryption / SecureJoin / Autocrypt;
- MIME pipeline;
- database migrations;
- provider-db/native lookup;
- backend/server/deployment.

## 14. Secrets / Artifacts Check

Checks performed:

- `git diff --check`;
- `git diff --name-status feature/corporate-onboarding-entry-wording..HEAD`;
- committed artifact path scan for APK/AAB/build output/key files;
- search for obvious secret/log/persistence markers in the new corporate files.

Result:

- no APK/AAB/build outputs committed;
- no `.env`;
- no keystore/JKS/signing key;
- no provider credentials;
- no raw logs;
- no raw invite code logging or persistence.

The broad string scan matched pre-existing upstream strings for clipboard, help URLs, and password UI labels in `strings.xml`; these were not introduced by this slice.

## 15. Remaining Risks

- Runtime smoke remains pending on a real device/emulator.
- This is still a placeholder: it detects local prefix shape only, not real invite validity.
- The branch is stacked and depends on PR #4.
- The app still uses existing Delta Chat identity by design.

## 16. Recommended Next Slice

Recommended next slice:

`feature/corporate-onboarding-provider-setup-handoff`

Scope:

- add a clear button or handoff from corporate onboarding to existing provider/account setup;
- keep manual IMAP/SMTP reachable;
- show provider setup as separate from invite detection and membership activation;
- no provider-db rewrite;
- no backend/API calls;
- no Mail.ru-only flow.

