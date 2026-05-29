# Android Fork Safe Customization Blueprint

Date: 2026-05-29

Status: Draft

Project: Corporate IMAP Messenger / messenger-imap

Meta repository: `Kwentin3/messenger-imap`

Android repository: `Kwentin3/messenger-imap-android`

Scope: Safe customization map and corporate extension points for the thin Delta Chat Android fork.

Related decision: MVP uses a thin fork of Delta Chat Android.

## Executive Summary

Corporate Android MVP work must stay above Delta Chat core and preserve the upstream Android app relationship. The first safe path is an isolated app/product-layer corporate package with small entry points into onboarding, provider setup, connectivity checks, directory rendering, invite handling, external-contact warnings and release metadata warnings.

This Blueprint does not authorize chatmail/core, JNI, sync, encryption, MIME, database migration, package rename, signing, release APK publication, server deployment, Control Plane backend or Android deep upstream rewrites.

## Source Inputs

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`
- `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md`
- `docs/hand_off/ANDROID_FORK_SAFE_EXTENSION_MAP.md`
- `docs/reports/ANDROID_FORK_ARCHITECTURE_AUDIT.report.md`
- `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md`
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`
- `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md`
- `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md`
- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`

## Goals

- Define safe app-layer extension points for corporate Android MVP slices.
- Keep corporate features isolated where possible.
- Preserve clean upstream build and fork relationship.
- Preserve provider-agnostic IMAP/SMTP behavior.
- Avoid early package rename, branding, signing and release changes.
- Avoid core/JNI/protocol/database changes.
- Give each planned corporate slice a likely implementation boundary.

## Non-Goals

- No Control Plane backend or API implementation.
- No server deployment, Traefik or Docker changes.
- No chatmail/core modifications.
- No JNI/FFI contract modifications.
- No sync, encryption, MIME or database migration changes.
- No app store, MDM, release signing or APK publication.
- No iOS.
- No Mail.ru-only implementation.
- No silent arbitrary system address book import.

## Safe Package Strategy

Corporate code should start in a new Android app-layer namespace, for example:

`org.thoughtcrime.securesms.corporate`

This package can contain:

- onboarding placeholder Activity;
- invite route parser;
- provider policy models;
- transport check summary wrapper;
- directory manifest/snapshot models;
- directory scope/state enums;
- external contact badge metadata;
- release metadata models;
- support-safe diagnostic summary builder.

Existing Delta Chat files should only receive narrow integration points when a feature must become visible from existing UI.

## Safe Extension Points

| Extension point | Purpose | Likely files/modules | First allowed change |
| --- | --- | --- | --- |
| Corporate onboarding entry | Introduce corporate flow without activation | `WelcomeActivity.java`, `ApplicationPreferencesActivity.java`, `AndroidManifest.xml`, new corporate Activity | Launch placeholder screen only |
| Invite deep link handler | Receive corporate join links / fallback codes | `AndroidManifest.xml`, new corporate invite Activity/parser | Parse route kind and token presence without logging raw token |
| Provider profile wrapper | Represent policy/profile guidance around existing setup | `relay/EditRelayActivity.java`, `relay/RelayListActivity.java`, new corporate provider model | Show/surface provider-agnostic hint; preserve manual setup |
| Check Transport entry | Reuse existing connectivity status safely | `ConnectivityActivity.java`, `connect/DcHelper.java`, new corporate diagnostics wrapper | Show connectivity summary; no raw log upload |
| Directory adapter | Consume DirectoryManifest/Snapshot read-only | new corporate directory package; later contact list adapter | Fixture-backed parse/hash/stale states |
| External badge/warning | Separate external contacts from internal members | `contacts/ContactSelectionListItem.java`, `ContactSelectionListAdapter.java`, `ConversationActivity.java` | Fixture-backed badge/warning only |
| Managed group roster warning | Warn that Control Plane owns managed rosters | group/chat surfaces | Warning only; no group protocol changes |
| Release metadata warning | Show version/channel/update policy | new corporate release package; settings/welcome entry | Mocked metadata; no auto-update |
| Support diagnostics summary | Export redacted support-safe state | `ConnectivityActivity.java`, `LogViewActivity.java`, new corporate diagnostics package | Redacted summary only |

## Forbidden / High-Risk Areas

| Area | Rule |
| --- | --- |
| `jni/deltachat-core-rust` | No changes without separate Blueprint |
| `com.b44t.messenger` JNI/native wrappers | No changes in MVP app-layer slices |
| `chat.delta.rpc` generated RPC wrappers | Avoid changes unless generated from accepted upstream/core contract |
| Sync engine | No changes |
| Encryption and SecureJoin | No changes; corporate invite is separate from SecureJoin |
| MIME/message pipeline | No changes |
| Core database migrations | No changes |
| Background services/notifications | Audit-only until later reliability Blueprint |
| Package ID/app name/branding | Later slice after build baseline and compliance path |
| Release signing/update pipeline | Later release Blueprint only |
| Raw log export | Do not use for corporate support without redaction |

## Corporate Feature Mapping

### Corporate Onboarding

Safe start:

- new Activity with status cards/rows for invite, email verification, provider setup, diagnostics, activation and directory sync;
- no real membership activation;
- no Control Plane dependency;
- optional launch from welcome or settings only.

Stop if:

- activation requires backend API implementation;
- credentials need to leave the Android client;
- package rename or release signing is required.

### Invite Deep Link / Fallback Code

Safe start:

- new app link/custom route handler for corporate invite links;
- sanitize token display and logs;
- represent internal/external invite kinds;
- support fallback code entry.

Stop if:

- raw token would be logged;
- handler would collide with Delta Chat SecureJoin semantics;
- app link domain verification requires deployment changes.

### Provider Profiles / Transport Check

Safe start:

- wrapper model for provider profile hints and policy;
- route to existing transport setup and connectivity status;
- preserve manual/custom provider path.

Stop if:

- Control Plane app password storage is introduced;
- Mail.ru-only assumptions appear in architecture;
- core provider-db or DNS lookup changes are required.

### Corporate Directory Read-Only Sync

Safe start:

- local model for `DirectoryManifest`;
- local model for `DirectorySnapshot`;
- `directoryVersion`, `directoryHash`, stale and hash mismatch states;
- fixture-backed parser and verifier.

Stop if:

- Android becomes directory authority;
- writes to Control Plane are required;
- core contact DB writes or migrations are required.

### External Contact Badges / Warnings

Safe start:

- metadata model distinguishing internal member and external contact;
- badges/warnings in a controlled surface;
- no external principal access to full internal directory.

Stop if:

- external contacts are mixed into internal member authority;
- silent system address book import is required;
- managed group protocol changes are required.

### Release Metadata / Update Warning

Safe start:

- local release metadata model;
- version/channel/deprecated/min-supported flags;
- manual warning only.

Stop if:

- auto-update/download pipeline is needed;
- signing key is needed;
- release APK publication is needed.

## Implementation Order

1. Preserve build baseline.
2. Add corporate onboarding placeholder.
3. Add provider profile and transport check wrapper.
4. Add directory read-only models and fixture parser.
5. Add invite deep link and fallback code route.
6. Add external contact badge/warning metadata and UI placeholder.
7. Add release metadata/update warning placeholder.
8. Run integration QA and no-secret/artifact checks.

## Acceptance Criteria

This Blueprint is accepted if:

- corporate features are mapped to app-layer extension points;
- forbidden areas are explicit;
- likely files/modules are listed;
- the path preserves thin fork and upstream relationship;
- provider-agnostic behavior is preserved;
- early rebrand/package rename/signing/deployment are excluded;
- no code, backend, server or deployment changes are required by this Blueprint.

## Open Questions

- Exact package name for corporate app-layer modules.
- Exact corporate invite app link domain and verification flow.
- Whether first visible entry should be welcome screen, settings, or debug-only.
- How to isolate corporate UI from upstream churn with minimal diffs.
- Whether later release identity requires package rename before private pilot.
- Which Android test strategy is viable in this upstream project.

## Gate to Phase 7

Phase 7 may proceed because the safe customization map supports an Android MVP implementation plan with low-risk, independently testable slices.
