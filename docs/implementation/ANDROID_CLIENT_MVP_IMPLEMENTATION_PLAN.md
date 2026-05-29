# Android Client MVP Implementation Plan

Date: 2026-05-29

Status: Draft

Project: Corporate IMAP Messenger / messenger-imap

Android repository: `Kwentin3/messenger-imap-android`

Scope: Autonomous implementation plan for Android MVP slices on a thin Delta Chat Android fork.

Baseline: `intake/upstream-build-baseline` at `a3a8b3581f82456bb7fe3342485cef4593c31315`

Related decision: MVP uses a thin fork of Delta Chat Android.

## Execution Rules

- Build before modification.
- Keep corporate code isolated in app/product-layer packages where possible.
- Preserve existing Delta Chat account setup, messaging, provider setup and connectivity flows.
- Use fixtures/placeholders before backend integration.
- Do not modify chatmail/core, JNI, sync, encryption, MIME, or database migrations.
- Do not rename package/app, rebrand, sign release APKs or publish APKs.
- Do not commit APK/AAB/build outputs, `.env`, secrets, app passwords, raw AUTH, or raw logcat.
- Each slice must build, document changed files, and produce a report.

## Slice Overview

| Slice | Name | Purpose | Branch |
| --- | --- | --- | --- |
| 1 | Build baseline preserved | Keep clean upstream/fork build as control sample | `intake/upstream-build-baseline` |
| 2 | Corporate onboarding entry placeholder | Add safe corporate entry without activation | `feature/corporate-onboarding-foundation` |
| 3 | Invite deep link / fallback code handling | Add app-side corporate invite route and fallback code parser | `feature/invite-deeplink-fallback` |
| 4 | Provider profile policy integration placeholder | Represent provider profile policy without credential/server changes | `feature/provider-transport-check` |
| 5 | Check Transport minimal integration | Surface existing connectivity as corporate diagnostic evidence | `feature/provider-transport-check` |
| 6 | Directory manifest/snapshot read-only sync adapter | Add read-only directory models and fixture parser | `feature/directory-readonly-sync` |
| 7 | Stale directory UX | Represent stale/unavailable/hash mismatch states | `feature/directory-readonly-sync` |
| 8 | Internal/external contact separation and badges | Keep external contacts distinct in corporate metadata/UI | `feature/external-contact-badges` |
| 9 | Managed group roster warning layer | Warn without changing group protocol authority | `feature/external-contact-badges` |
| 10 | Release metadata / update warning | Add release metadata policy placeholder | `feature/release-metadata-warning` |
| 11 | Support-safe diagnostics export | Add redacted support summary placeholder | `feature/release-metadata-warning` |
| 12 | QA and regression tests | Rebuild and verify no basic-flow regression | current final Android branch |

## Slice 1 - Build Baseline Preserved

Purpose: preserve a reproducible clean build before corporate changes.

Inputs:

- Upstream Delta Chat Android fork.
- Phase 4 build report.

Files likely affected: none.

Non-goals:

- no code changes;
- no native rebuild requirement;
- no artifact commit.

Acceptance criteria:

- `assembleDebug` passes in ASCII checkout;
- Android branch is clean;
- APK artifacts are local only;
- upstream commit and submodule commit recorded.

Tests/checks:

- `.\gradlew.bat assembleDebug --stacktrace`
- `git status --short`
- no APK/AAB staged.

Rollback/containment:

- return to `intake/upstream-build-baseline`.

Dependencies: Android fork repository setup.

## Slice 2 - Corporate Onboarding Entry Placeholder

Purpose: introduce a low-risk corporate onboarding surface.

Inputs:

- Invite Onboarding Blueprint.
- Android Fork Safe Customization Blueprint.

Files likely affected:

- `src/main/AndroidManifest.xml`
- new files under `src/main/java/org/thoughtcrime/securesms/corporate/`
- new resources under `src/main/res/layout` and `src/main/res/values`
- optional narrow entry in `WelcomeActivity.java` or settings.

Non-goals:

- no membership activation;
- no Control Plane API;
- no email verification delivery;
- no provider credentials handling;
- no package/app rename.

Acceptance criteria:

- app builds;
- corporate entry opens a placeholder status flow;
- existing welcome/account setup remains available;
- no secrets logged or stored;
- report created.

Tests/checks:

- `.\gradlew.bat assembleDebug --stacktrace`
- inspect diff for docs/resources/app-layer-only changes.

Rollback/containment:

- remove manifest Activity, resources and corporate package;
- revert narrow entry point if added.

Dependencies: Slice 1.

## Slice 3 - Invite Deep Link / Fallback Code Handling

Purpose: add app-side corporate invite intake without backend activation.

Inputs:

- Invite Onboarding Blueprint.
- Corporate onboarding placeholder.

Files likely affected:

- `AndroidManifest.xml`
- corporate invite parser/model classes;
- corporate onboarding Activity.

Non-goals:

- no Control Plane activation;
- no SecureJoin/core QR changes;
- no app link domain deployment;
- no raw invite token logging.

Acceptance criteria:

- internal/external invite kinds represented;
- fallback code entry represented;
- invalid/expired/unavailable placeholder states represented;
- raw token is redacted from UI logs/reports;
- app builds;
- report created.

Tests/checks:

- build;
- manual intent command documented if emulator/device is available;
- no-token grep in changed files/reports.

Rollback/containment:

- remove corporate intent-filter and parser;
- corporate Activity still usable as placeholder.

Dependencies: Slice 2.

## Slice 4 - Provider Profile Policy Integration Placeholder

Purpose: represent provider profile handoff while preserving existing manual IMAP/SMTP setup.

Inputs:

- Provider Transport Profiles PRD.
- Diagnostics PRD.
- Existing `EditRelayActivity`/`RelayListActivity`.

Files likely affected:

- corporate provider model package;
- optional settings/onboarding text or status row;
- no required edits to existing provider setup in first pass.

Non-goals:

- no Mail.ru-only architecture;
- no Control Plane credential storage;
- no provider-db/core changes;
- no forced provider selection.

Acceptance criteria:

- provider-agnostic path remains explicit;
- custom provider/manual setup remains possible;
- app builds;
- report created.

Tests/checks:

- build;
- inspect that existing `EditRelayActivity` remains usable.

Rollback/containment:

- remove corporate provider model/status row.

Dependencies: Slice 2.

## Slice 5 - Check Transport Minimal Integration

Purpose: expose existing connectivity as corporate transport evidence placeholder.

Inputs:

- Diagnostics PRD.
- Existing `ConnectivityActivity` and `DcContext.getConnectivityHtml()`.

Files likely affected:

- corporate diagnostics wrapper;
- corporate onboarding/status Activity;
- optional route to `ConnectivityActivity`.

Non-goals:

- no raw log upload;
- no server-side diagnostics evidence;
- no credential export;
- no background reliability promise.

Acceptance criteria:

- check transport entry exists or is linked from corporate status;
- existing connectivity page remains available;
- failed/pending/passed placeholder states are represented;
- app builds;
- report created.

Tests/checks:

- build;
- no raw logcat/AUTH/password strings in new code.

Rollback/containment:

- remove corporate diagnostics wrapper/status row.

Dependencies: Slice 2 and Slice 4.

## Slice 6 - Directory Manifest/Snapshot Read-Only Sync Adapter

Purpose: add client-side read-only directory data model before backend sync.

Inputs:

- Corporate Directory Blueprint.
- Control Plane Blueprint.

Files likely affected:

- corporate directory model package;
- local fixture/sample parser;
- optional test-friendly canonical/hash helper.

Non-goals:

- no Android directory authority;
- no writes to Control Plane;
- no writes to Delta Chat core DB;
- no system contacts import.

Acceptance criteria:

- `DirectoryManifest` and `DirectorySnapshot` represented;
- `directoryVersion` and `directoryHash` represented;
- internal members and external contacts are distinct;
- app builds;
- parser/hash check has at least a fixture or deterministic sample check;
- report created.

Tests/checks:

- build;
- local unit-style parser check if available, otherwise documented manual fixture check.

Rollback/containment:

- remove corporate directory package and fixtures.

Dependencies: Slice 2.

## Slice 7 - Stale Directory UX

Purpose: represent stale/unavailable/expired/hash mismatch states in Android.

Inputs:

- Directory Blueprint.
- Directory read-only adapter.

Files likely affected:

- corporate directory state model;
- corporate onboarding/status Activity or directory placeholder.

Non-goals:

- no background sync engine;
- no push/signed IMAP update mechanism;
- no authoritative roster enforcement.

Acceptance criteria:

- states include fresh, stale, expired, unavailable and hash_mismatch;
- UX/status text does not claim authority beyond local state;
- existing messaging remains possible when directory is stale;
- app builds;
- report created.

Tests/checks:

- build;
- fixture/state smoke notes.

Rollback/containment:

- remove stale state UI/model additions.

Dependencies: Slice 6.

## Slice 8 - Internal/External Contact Separation and Badges

Purpose: keep external contacts visually and semantically separate from internal members.

Inputs:

- Directory Blueprint.
- External Contacts PRD.

Files likely affected:

- corporate directory/contact metadata;
- optional contact list or corporate placeholder UI;
- later `ContactSelectionListItem` integration only if low-risk.

Non-goals:

- no external access to full internal directory;
- no full external project room implementation;
- no silent address book import;
- no core contact DB migration.

Acceptance criteria:

- metadata distinguishes internal member vs external contact;
- external badge/warning represented in at least one safe surface;
- external principal fixture cannot see full internal directory;
- app builds;
- report created.

Tests/checks:

- build;
- fixture/state review.

Rollback/containment:

- remove badge/status surface and metadata.

Dependencies: Slice 6.

## Slice 9 - Managed Group Roster Warning Layer

Purpose: warn users that managed group roster authority belongs to Control Plane/Directory, not historical local chat membership.

Inputs:

- Directory Blueprint.
- External Contacts PRD.

Files likely affected:

- corporate warning model;
- safe Activity/status surface first;
- existing group/chat UI only after explicit review.

Non-goals:

- no group protocol changes;
- no membership enforcement in Delta Chat core;
- no server roster API.

Acceptance criteria:

- warning copy/state exists;
- no group membership mutation occurs;
- app builds;
- report created.

Tests/checks:

- build;
- inspect diff for no group/core mutation.

Rollback/containment:

- remove warning model/surface.

Dependencies: Slice 8.

## Slice 10 - Release Metadata / Update Warning

Purpose: represent release metadata policy without update automation.

Inputs:

- Invite Onboarding & Distribution Blueprint.
- Control Plane Blueprint.

Files likely affected:

- corporate release metadata model;
- corporate status Activity/settings row.

Non-goals:

- no auto-update;
- no APK download backend;
- no signing pipeline;
- no release APK publication.

Acceptance criteria:

- version, channel, sha256, minSupported, deprecated/blocked states represented;
- warnings are manual/user-visible only;
- app builds;
- report created.

Tests/checks:

- build;
- no APK/artifact committed.

Rollback/containment:

- remove release metadata package/status row.

Dependencies: Slice 2.

## Slice 11 - Support-Safe Diagnostics Export

Purpose: add redacted corporate support summary placeholder.

Inputs:

- Diagnostics PRD.
- Existing `LogViewActivity` risk assessment.

Files likely affected:

- corporate diagnostics summary builder;
- corporate status Activity/settings row.

Non-goals:

- no raw logcat;
- no app passwords;
- no AUTH strings;
- no automatic upload;
- no full support portal.

Acceptance criteria:

- exported/shown summary is redacted by construction;
- user action is required;
- app builds;
- no secrets in report;
- report created.

Tests/checks:

- build;
- grep changed files for obvious secret patterns.

Rollback/containment:

- remove diagnostics summary package/status row.

Dependencies: Slice 5.

## Slice 12 - QA and Regression Tests

Purpose: validate that corporate additions did not break basic Delta Chat behavior.

Inputs:

- All implemented Android slices.
- Phase reports.

Files likely affected:

- no product files required;
- QA report in meta repo or Android repo.

Non-goals:

- no release signing;
- no APK publication;
- no server/deployment validation.

Acceptance criteria:

- app builds;
- basic launch/account setup path is not blocked by corporate additions;
- existing provider setup path remains reachable;
- corporate invite/onboarding placeholders are reachable;
- no secret/artifact commit;
- final QA report created.

Tests/checks:

- `.\gradlew.bat assembleDebug --stacktrace`
- `git status --short`
- changed-file scan for secrets/artifacts.

Rollback/containment:

- revert the most recent feature branch/slice;
- preserve intake build baseline.

Dependencies: all implemented slices.

## Global Stop Conditions

Stop and create a blocker report if any slice requires:

- chatmail/core, JNI, sync, encryption, MIME or database migration changes;
- server deployment, Traefik edits, Docker/server changes or Control Plane backend implementation;
- signing keys, release APK publication, APK/AAB committed to git;
- `.env`, provider app passwords, raw AUTH or raw logcat;
- package/app rename before the dedicated release identity slice;
- breaking existing Delta Chat account setup or basic messaging path.

## Gate to Phase 8

Phase 8 may proceed if Slice 2 remains app-layer-only and can be implemented as a placeholder without Control Plane, core, JNI, package rename, signing, deployment, or secrets.

Gate result: open.
