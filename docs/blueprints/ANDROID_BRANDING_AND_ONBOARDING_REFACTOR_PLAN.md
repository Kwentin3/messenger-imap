# Android Branding And Onboarding Refactor Plan

Date: 2026-05-29

Status: Draft

Project: Corporate IMAP Messenger / messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

Scope: app-layer branding, onboarding, and provider-flow refactor plan for thin Delta Chat Android fork

Related audit: `docs/reports/ANDROID_BRANDING_AND_ONBOARDING_REFACTOR_AUDIT.report.md`

## 1. Executive Summary

The Android fork should move from a Delta Chat-first startup UX toward a corporate onboarding UX without breaking Delta Chat's working account setup, IMAP/SMTP transport, QR login, backups, second-device setup, or provider discovery.

The refactor must stay thin:

- no package ID change;
- no signing config change;
- no launcher icon/app name change in the first slice;
- no chatmail/core, JNI/FFI, sync, encryption, SecureJoin, MIME, or database migration changes;
- no provider-db rewrite;
- no Mail.ru-only flow;
- no removal of manual IMAP/SMTP setup.

## 2. Baseline Precondition

Before code work, reconcile Android branch state.

Current finding:

- `origin/main` contains the native build guard and 0.1.1 README/build docs.
- `origin/feature/release-metadata-warning` contains the corporate app-layer placeholder stack.

Implementation must choose one explicit route:

1. create a new branch from current `origin/main` and cherry-pick/reapply the corporate placeholder layer; or
2. rebase `origin/feature/release-metadata-warning` onto current `origin/main` and resolve conflicts intentionally.

Do not start UX refactor until the baseline branch contains both:

- native build guard / 0.1.1 build pipeline fixes;
- corporate app-layer placeholder entry.

## 3. Ownership Boundaries

| Domain | Owns | Does not own |
| --- | --- | --- |
| Android app layer | Welcome screen, corporate entry, placeholder invite UX, provider setup handoff, transport check handoff, visible wording. | Membership authority, provider credentials in Control Plane, directory authority. |
| Control Plane | Invite resolution, email verification, activation, release metadata, provider profile policy, audit. | Android UI implementation, IMAP/SMTP transport. |
| Directory | DirectoryManifest, DirectorySnapshot, internal/external visibility, stale/hash state. | Local Android contact authority or historical chat membership. |
| Delta Chat core / chatmail/core | IMAP/SMTP transport, account setup internals, sync, encryption, MIME, database. | Corporate product authority and UX policy. |

## 4. Global Non-Goals

- No app/package rename.
- No launcher icon change.
- No signing/release config change.
- No production APK release.
- No Control Plane backend implementation.
- No server/deployment/Traefik changes.
- No provider credentials in Control Plane.
- No Mail.ru-only implementation.
- No removal of existing account setup.
- No removal of manual IMAP/SMTP path.
- No provider-db/native lookup rewrite.
- No upstream license/credits removal.

## 5. Phase 1: Minimal Safe Product Wording

### Scope

Change only app-layer wording and visible entry labels required to make the corporate path understandable.

Likely files:

- `src/main/res/values/strings.xml`
- `src/main/res/layout/welcome_activity.xml`
- `src/main/java/org/thoughtcrime/securesms/WelcomeActivity.java`
- `src/main/java/org/thoughtcrime/securesms/corporate/CorporateOnboardingActivity.java`, if placeholder branch is included
- `src/main/res/layout/corporate_onboarding_activity.xml`, if placeholder branch is included

### Acceptance Criteria

- Welcome screen has a visible corporate entry such as "Join organization".
- Existing "Create New Profile" remains reachable.
- Existing "I Already Have a Profile" remains reachable.
- Manual IMAP/SMTP remains reachable.
- Screen copy states that APK install/app launch does not equal membership.
- App builds with native guard.
- No package/signing/icon/app label changes.

### Tests

- `./gradlew verifyNativeCoreLibraries`
- `./gradlew assembleFossDebug -PABI_FILTER=arm64-v8a`
- Device smoke:
  - launch app;
  - open corporate entry;
  - back navigation;
  - open standard account setup;
  - open manual IMAP/SMTP path.

### Non-Goals

- No backend integration.
- No invite activation.
- No provider policy enforcement.
- No app identity changes.

### Rollback

Revert the small app-layer commit. Existing Delta Chat account setup should remain intact.

## 6. Phase 2: Welcome / Onboarding UX Refactor

### Scope

Make corporate onboarding the intended first path while preserving Delta Chat setup as fallback/advanced path.

Likely files:

- `WelcomeActivity.java`
- `welcome_activity.xml`
- `CorporateOnboardingActivity.java`
- `corporate_onboarding_activity.xml`
- `strings.xml`

### Target Behavior

```text
Welcome
  -> Join organization
      -> invite/fallback code placeholder
      -> provider setup handoff
      -> transport check handoff
  -> Existing profile / backup / second device
  -> Advanced: create standalone profile / manual IMAP-SMTP
```

### Acceptance Criteria

- Corporate entry appears before standalone Delta Chat profile creation where appropriate.
- Existing account setup path is not removed.
- Backup and second-device setup still work.
- Corporate screen does not claim membership activation without Control Plane.
- Fallback invite code input redacts or avoids exposing raw token in status/logs.

### Tests

- Launch on clean install.
- Navigate each top-level path.
- Rotate/background/resume where practical.
- Verify no crash and no token in logs from normal dummy-code path.

### Non-Goals

- No verified app link.
- No real invite resolution API.
- No email verification backend.

### Rollback

Revert UX routing changes; keep Phase 1 wording if safe.

## 7. Phase 3: Provider Flow Refactor

### Scope

Introduce organization-recommended provider concept at app-layer while keeping custom IMAP/SMTP.

Likely files:

- `CorporateOnboardingActivity.java`
- `CorporateProviderPolicy.java`
- `EditRelayActivity.java` only for safe labels/handoff
- `activity_edittransport.xml` only for safe helper text, if needed
- `strings.xml`

### Target Behavior

- If organization provider profile is known later, show it as recommended.
- Mail.ru / VK Mail can be shown as accepted baseline only when evidence supports it.
- Custom IMAP/SMTP remains available where policy allows.
- Other providers are not presented as corporate-verified unless diagnostics evidence exists.
- Check Transport handoff is explicit.

### Acceptance Criteria

- No Mail.ru-only hardcode.
- `EditRelayActivity` still supports manual IMAP/SMTP fields.
- Provider-db hints are still displayed or safely wrapped.
- Credentials remain local to Delta Chat/Core setup, not sent to Control Plane.
- Diagnostics/check transport language does not imply membership activation.

### Tests

- Enter custom email.
- Provider hint appears/disappears as before.
- Advanced IMAP/SMTP opens and accepts input.
- Failed login still displays existing error path.
- Connectivity screen opens after configured account.

### Non-Goals

- No provider-db changes.
- No Control Plane provider API implementation.
- No app password storage outside existing setup.

### Rollback

Revert app-layer provider wrapper/wording commit; leave core provider flow unchanged.

## 8. Phase 4: User-Facing Delta Chat References Cleanup

### Scope

Suppress or replace inappropriate user-facing Delta Chat links while preserving upstream attribution, license, and developer documentation.

Likely files:

- `strings.xml`
- `LocalHelpActivity.java`
- settings/preferences XML
- optional corporate help/support screen later

### Replace Or Suppress Later

- update reminder pointing to `get.delta.chat`;
- donation prompts;
- "Send statistics to Delta Chat's developers";
- user-facing Help/About entries if they imply official Delta Chat support for this fork;
- notification permission text naming Delta Chat.

### Preserve

- `LICENSE`;
- upstream copyright/credits;
- developer README attribution;
- source comments where not user-facing;
- GPL/source distribution notes.

### Acceptance Criteria

- User-facing support links point to corporate support placeholder or are clearly deferred.
- License/credits remain intact.
- Build passes.
- No removal of required GPL notices.

### Tests

- Open Help/About/settings.
- Check notification permission denial text where feasible.
- Build and lint/smoke.

### Non-Goals

- No store metadata update.
- No app name/icon/package rename.

### Rollback

Revert text/link changes only.

## 9. Phase 5: App Identity And Branding Later

### Scope

Handle app name, launcher icon, package ID, notification channels, signing, release identity, and store/distribution metadata after a separate identity/release decision.

Likely files:

- `build.gradle`
- `src/main/AndroidManifest.xml`
- `src/main/res/mipmap*`
- `src/main/res/drawable*`
- `src/main/res/values/strings.xml`
- notification channel code
- fastlane metadata
- release docs

### Acceptance Criteria

- Package ID decision exists.
- Signing key custody decision exists.
- GPL/source distribution workflow exists.
- Migration/authority impact reviewed.
- Notification channel migration plan exists.
- APK release metadata updated.

### Tests

- Clean install.
- Upgrade from previous internal build.
- Share/file provider flows.
- Notification channels.
- Deep links/app links.
- Backup/restore.

### Non-Goals

- No identity work before decisions.
- No signing keys committed.

### Rollback

Identity changes are high-risk and may not be trivially rollbackable for installed users. Use a dedicated release branch and test upgrade/downgrade behavior before distribution.

## 10. High-Risk Areas

Do not change without separate Blueprint/approval:

- `jni/`
- chatmail/core and generated RPC contracts
- JNI/FFI wrappers
- sync engine
- encryption, SecureJoin, Autocrypt
- MIME pipeline
- database/account migrations
- provider-db internals
- package ID/applicationId/signing config
- file provider authorities
- notification channel IDs for already-installed users

## 11. First Implementation Slice

Name: `feature/corporate-onboarding-entry-wording`

Prerequisite: branch baseline contains current native build guard and corporate placeholder layer.

Scope:

- restore or keep `CorporateOnboardingActivity`;
- make "Join organization" visible on the welcome screen;
- update corporate placeholder copy;
- keep existing Delta Chat setup buttons;
- keep manual IMAP/SMTP path;
- no identity changes.

Definition of Done:

- docs reference this plan;
- build passes;
- install/launch smoke passes;
- corporate entry opens;
- old account setup opens;
- manual IMAP/SMTP opens;
- no APK/build artifacts committed;
- no secrets;
- package/signing unchanged.

## 12. Deferred Decisions

- final app name;
- package ID/applicationId;
- launcher icon/branding;
- signing key custody;
- verified app link domain;
- Control Plane provider-profile API contract;
- email verification delivery provider;
- release storage and source distribution workflow;
- notification channel migration policy.
