# Android Client MVP Blueprint

Date: 2026-05-26

Status: Draft

Scope: MVP Blueprint

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Source documents:

- [Project Roadmap](../roadmap/PROJECT_ROADMAP.md)
- [Corporate Control Plane MVP Blueprint](CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md)
- [Corporate Directory MVP Blueprint](CORPORATE_DIRECTORY_MVP_BLUEPRINT.md)
- [Invite Onboarding & Distribution MVP Blueprint](INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md)
- [Android Messenger Client PRD](../product/domains/PRD_ANDROID_MESSENGER_CLIENT.md)
- [Corporate IMAP Messenger Root PRD](../product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Product PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Corporate Control Plane PRD](../product/domains/PRD_CORPORATE_CONTROL_PLANE.md)
- [Corporate Directory PRD](../product/domains/PRD_CORPORATE_DIRECTORY.md)
- [Invite Onboarding & Distribution PRD](../product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md)
- [External Contacts & Guest Access PRD](../product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md)
- [Provider Transport Profiles PRD](../product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md)
- [Diagnostics & Transport Verification PRD](../product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md)
- [Product Decisions Log](../product/decisions/PRODUCT_DECISIONS_LOG.md)
- [Product Context Handoff](../product/handoff/PRODUCT_CONTEXT_HANDOFF.md)
- [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)
- [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md)
- [Delta Chat / Chatmail Capabilities Report](../research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md)
- [Delta Chat Corporate Feature Map](../hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md)

## 1. Executive Summary

The Android client is the primary MVP product surface for employees and external contacts. It turns the accepted IMAP/SMTP transport baseline, Control Plane authority, Directory sync, Invite onboarding, provider profiles, diagnostics, and release metadata into a usable messenger experience.

The Android client is not the source of corporate truth. It consumes Control Plane state, verifies directory snapshots, maintains local cache, runs provider setup and transport checks, and sends messages through IMAP/SMTP using reused Delta Chat / chatmail capabilities where a safe and compliant implementation path is selected.

This Blueprint follows Control Plane, Directory, and Invite Blueprints because the Android client must implement their contracts rather than invent authority locally. It defines MVP client domains, local state, onboarding flow, directory sync, stale mode UX, provider setup, diagnostics, chat boundaries, external contact handling, release policy handling, security constraints, fork-vs-shell decision criteria, implementation slices, validation plan, and open questions.

This is not code. It does not modify Delta Chat Android, chatmail/core, Gradle, Android Diagnostics prototype, server, Traefik, Docker, or deployment files.

## 2. Source Documents And Inherited Decisions

| Source document | Decisions inherited |
| --- | --- |
| [Project Roadmap](../roadmap/PROJECT_ROADMAP.md) | Android Client Blueprint follows Control Plane, Directory, and Invite Blueprints; implementation is still blocked until Blueprint acceptance. |
| [Control Plane Blueprint](CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md) | Android consumes invite resolution, email verification, membership/external relationship activation, provider profiles, release metadata, diagnostics upload, audit-relevant state, and stale policies. |
| [Directory Blueprint](CORPORATE_DIRECTORY_MVP_BLUEPRINT.md) | Android stores local cache, verifies manifest/snapshot hash, separates internal/external contacts, respects visible directory and managed group roster authority. |
| [Invite Blueprint](INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md) | Android handles app link/deep link/fallback code, email verification code entry, provider setup, diagnostics gate, activation result, and first directory sync. |
| [Android Client PRD](../product/domains/PRD_ANDROID_MESSENGER_CLIENT.md) | Client is Android-first, invite-based, provider-agnostic, directory-backed, diagnostics-aware, external-contact aware, and does not choose fork/shell by PRD alone. |
| [Product PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md) | Stale Control Plane mode, email verification, multi-workspace scoping, managed group enforcement, trust states, and app release lifecycle are required. |
| [External Contacts PRD](../product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md) | External contacts are not employees, must be visibly marked, and must not receive internal directory. |
| [Provider Profiles PRD](../product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md) | Provider profiles are organization/workspace-scoped metadata and do not store credentials. |
| [Diagnostics PRD](../product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md) | Diagnostics support transport readiness and troubleshooting, but do not replace email ownership verification. |
| [Delta Chat / Chatmail Research](../research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md) | Delta Chat / chatmail already provide IMAP/SMTP transport, chat, contact, group, QR, attachment, and voice capabilities that should be reused where safe. |
| [Delta Chat Feature Map](../hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md) | Corporate product layer is missing: managed directory, provider policy, diagnostics status, corporate invites, external contact scope, and release policy. |

Inherited decisions:

- Android-first MVP.
- iOS is out of current scope.
- MVP-0a Diagnostics accepted.
- Mail.ru / VK Mail is the first accepted transport baseline.
- Product remains provider-agnostic.
- APK download does not equal membership.
- Invite token does not equal membership.
- Email ownership verification is required.
- Internal invite creates `Membership`.
- External invite creates `ExternalRelationship`.
- Control Plane may be unavailable in whitelist/restricted mode.
- Android must support cached/stale state.
- Corporate Directory is source of active identity and visibility.
- External contacts must not receive internal corporate directory.
- Managed groups use current Directory roster, not historical local chat membership.
- Background reliability is deferred.
- Delta Chat / chatmail upstream is not vendor-copied into this repo.

## 3. Goals

MVP Android Client must:

- support invite-based onboarding for employees and external contacts;
- handle app link/deep link and fallback invite code;
- support email verification code entry;
- support provider setup using Mail.ru / VK Mail baseline and manual/custom profiles;
- keep provider credentials local and outside Control Plane;
- expose a Check Transport or equivalent diagnostics gate;
- activate only through Control Plane result;
- perform first visible directory sync after activation;
- store and verify local directory cache;
- show stale/expired/hash-mismatch states;
- separate Internal Members and External Contacts;
- visibly mark external contacts in directory, picker, chat header, and chat info;
- support one-to-one chats through IMAP/SMTP transport;
- support basic groups or a documented MVP fallback;
- enforce managed group roster freshness according to Directory policy;
- support attachments and voice messages where safely reused;
- handle app release policy warnings;
- produce sanitized support/diagnostic exports where supported;
- define fork-vs-shell decision criteria for implementation planning.

## 4. Non-Goals

This Blueprint does not include:

- Android code changes;
- Gradle changes;
- UI mockups;
- final visual design;
- OpenAPI specification;
- SQL/database work;
- server deployment;
- Traefik changes;
- Docker Compose;
- APK signing pipeline implementation;
- APK binaries in git;
- iOS support;
- app store or TestFlight flow;
- production-grade background/locked-screen reliability guarantee;
- Delta Chat Android fork implementation;
- chatmail/core modifications;
- full CRM/helpdesk;
- external project rooms;
- signed IMAP/system-account directory updates in MVP;
- automatic silent address book import.

## 5. System Context

Conceptual context:

```text
Android Client
  -> Control Plane
     - invite resolution
     - email verification
     - activation result
     - directory manifest/snapshot
     - provider profile metadata
     - release policy
     - diagnostics upload/reference

Android Client
  -> IMAP/SMTP Provider
     - message send/receive
     - provider setup verification
     - transport diagnostics

Android Client
  -> Local device storage
     - account configuration
     - provider credentials
     - chat database / core state
     - directory cache
     - release policy cache
     - diagnostic local report cache where needed
```

Control Plane availability can be worse than provider IMAP/SMTP availability in mobile whitelist mode. The client must be honest about stale Control Plane state while still allowing IMAP/SMTP messaging with known contacts where policy permits.

## 6. Client Domain Ownership

| Client domain | Owns | Does not own |
| --- | --- | --- |
| Onboarding Shell | First launch, invite link/code entry, verification code entry, pending/failed/activated states. | Invite authority, membership authority, email sender. |
| Control Plane Sync Adapter | API calls, local sync metadata, stale/unavailable states. | Server state, RBAC, directory generation. |
| Directory Cache | Local applied snapshots, version/hash metadata, contact picker data. | Corporate authority, snapshot generation, visibility expansion. |
| Provider Setup | Local provider profile selection, credential entry, account configuration. | User credential storage in Control Plane, provider verification status authority. |
| Diagnostics Surface | Local Check Transport action, sanitized report preparation, delayed upload. | Email ownership proof, membership activation. |
| Messaging Surface | Chats, one-to-one send/receive, basic groups, attachments, voice messages via reused core. | Message server role, corporate membership authority. |
| External Contact UX | Badges, warnings, scoped directory display, guest mode copy. | External relationship authority, reassignment policy. |
| Release Policy UX | Warn/block/deprecated state from cached or fresh release metadata. | APK signing, release publishing, storage backend. |

## 7. Implementation Path Decision

The Android MVP has two plausible implementation paths:

1. Thin Delta Chat Android fork.
2. Custom Android shell over chatmail/core.

This Blueprint does not make the final implementation choice, but it defines decision criteria and a conservative planning assumption.

### 7.1 Thin Delta Chat Android Fork

Potential advantages:

- existing IMAP/SMTP account setup and message transport;
- existing chat list, one-to-one chats, groups, attachments, voice messages;
- existing QR/contact/group primitives;
- faster end-to-end user-visible prototype;
- lower risk of rebuilding messenger primitives from scratch.

Risks:

- GPL distribution obligations for Android app modifications;
- product-layer changes may be entangled with existing UI;
- package identity, branding, release signing, and update channel require care;
- background behavior may look more mature than the MVP can honestly promise;
- upstream sync burden.

### 7.2 Custom Android Shell Over Chatmail/Core

Potential advantages:

- clearer product-specific UX from first screen;
- tighter control over onboarding, directory, stale mode, and external contact UX;
- potentially cleaner separation between product layer and transport core.

Risks:

- more implementation work before usable messaging;
- must build or integrate chat UI, account lifecycle, notifications, attachments, voice;
- FFI/core integration and packaging complexity;
- greater risk of delaying MVP.

### 7.3 Recommended Decision Slice

Before full Android implementation, run a focused decision spike:

- confirm licensing and distribution obligations for a modified Delta Chat Android fork;
- confirm feasibility of adding product-layer onboarding before generic account setup;
- confirm directory cache and contact picker override approach;
- confirm external badge/warning integration points;
- confirm provider profile injection without hardcoding Mail.ru-only flows;
- confirm sanitized diagnostics/export integration path;
- estimate effort for fork vs shell.

Default planning assumption until the spike completes:

- reuse Delta Chat / chatmail capabilities where safe;
- avoid chatmail/core protocol changes for MVP;
- implement corporate authority as a product-layer wrapper/adaptation;
- avoid device contacts permission and silent imports in MVP unless explicitly approved.

## 8. Core Client State Model

Logical local state, not an Android database schema:

### 8.1 AppInstallState

Fields:

- app version;
- version code;
- release channel;
- signing info display reference;
- last release policy sync;
- release policy state: `ok`, `deprecated`, `force_upgrade`, `blocked`, `unknown`.

Rules:

- installed app is not membership;
- APK-by-email is emergency fallback only;
- release policy can be stale when Control Plane is unavailable.

### 8.2 EnrollmentLocalState

Fields:

- invite reference;
- invite kind: `internal` or `external`;
- enrollment session ID;
- organization/workspace reference;
- email verification status;
- provider setup status;
- diagnostic gate status;
- activation status;
- first directory sync status.

Rules:

- invite present is not membership;
- email verified is not membership;
- activation must come from Control Plane;
- external activation creates external relationship, not membership.

### 8.3 AccountTransportState

Fields:

- provider profile ID;
- provider family;
- IMAP/SMTP settings applied;
- username/email;
- local credential reference;
- last transport check status;
- last successful send/receive diagnostic;
- network context if known.

Rules:

- credentials are local only;
- provider profile metadata does not contain credentials;
- diagnostics do not replace email verification.

### 8.4 DirectoryCacheState

Fields:

- organizationId;
- workspaceId optional;
- directoryVersion;
- directoryHash;
- cacheAppliedAt;
- lastSyncAt;
- stale state: `fresh`, `stale`, `expired`, `unavailable`, `hash_mismatch`;
- visible internal members;
- visible external contacts;
- visible managed groups.

Rules:

- local cache is not authority;
- snapshot must be hash-verified before apply;
- external users must never receive full internal directory;
- stale/expired state must be visible in relevant actions.

### 8.5 TrustIdentityState

Fields:

- app installed;
- invite present;
- email ownership verified;
- membership status;
- external relationship status;
- directory trust status;
- cryptographic verification status optional;
- imported/local contact status.

Rules:

- imported contact is not directory active;
- directory active is not cryptographic verification;
- external active is not internal membership;
- SecureJoin/equivalent indicators remain separate.

## 9. Main Client Flows

### 9.1 First Launch

1. App opens to onboarding-oriented entry.
2. User can open an app link/deep link, scan/enter fallback code, or use support/test mode if approved.
3. App does not present itself as a generic email client before enrollment.
4. App does not grant directory or membership without Control Plane activation.

### 9.2 Internal Employee Enrollment

1. App receives internal invite reference.
2. App resolves invite through Control Plane.
3. App displays employee enrollment context.
4. User confirms email.
5. User enters email verification code.
6. App applies provider profile recommendation or lets user choose manual/custom profile.
7. User enters provider credentials locally.
8. App runs required Check Transport / diagnostic gate.
9. App requests activation.
10. App receives active membership or blocked/pending state.
11. App performs first visible directory sync.
12. App enters employee mode.

### 9.3 External Contact Enrollment

1. App receives external invite reference.
2. App resolves external invite through Control Plane.
3. App displays guest/external access wording.
4. User verifies email ownership.
5. User configures provider transport locally.
6. App runs required diagnostics where policy requires.
7. App requests activation.
8. App receives active external relationship or blocked/pending state.
9. App performs first scoped visible directory sync.
10. App enters external contact mode with no internal directory exposure.

### 9.4 Control Plane Unavailable During Enrollment

Allowed:

- APK installation;
- local provider setup if enough profile context exists;
- local diagnostics if implemented;
- pending state display.

Blocked or delayed:

- invite resolution if not already resolved;
- email verification;
- activation;
- first directory sync;
- fresh release policy update.

The app must not imply active membership while pending.

### 9.5 Directory Sync

1. App requests `DirectoryManifest`.
2. If version/hash unchanged, keep cache.
3. If changed, download snapshot.
4. Canonicalize snapshot payload.
5. Compute SHA-256.
6. Compare with manifest hash.
7. Apply only on match.
8. Reject and mark `hash_mismatch` on mismatch.
9. Refresh search, contact picker, visible external section, and managed group roster.

### 9.6 One-To-One Chat

1. User searches visible directory.
2. User selects active internal member or allowed external contact.
3. App opens or creates one-to-one chat via reused messaging capabilities.
4. App sends/receives messages through IMAP/SMTP.
5. App shows external warning when the counterpart is external.
6. App avoids marking imported contacts as verified unless actually verified.

### 9.7 Managed Group Send

1. User opens managed group.
2. App checks directory cache freshness and roster version.
3. If fresh, app uses current active roster.
4. If stale, app warns or blocks according to policy.
5. If expired, MVP default should block managed group sends unless policy explicitly allows override.
6. Historical local participants do not define current managed roster.

### 9.8 Diagnostics And Support Export

1. User or support runs Check Transport.
2. App reports stage-level result where available.
3. App stores or prepares sanitized report.
4. If Control Plane unavailable, app queues upload/reference until later if policy allows.
5. Export must not include raw AUTH, app passwords, raw logcat, raw message payloads, or unnecessary personal data.

### 9.9 Release Policy Check

1. App reads cached release policy on startup.
2. When Control Plane is reachable, app refreshes release metadata.
3. App warns for deprecated version.
4. App blocks or requires upgrade below force/blocked thresholds.
5. If policy is stale, app shows conservative warning but does not invent fresh release state.

## 10. User Modes

| Mode | Meaning | Allowed surfaces |
| --- | --- | --- |
| `not_enrolled` | App installed, no active organization state. | Invite entry, app link handling, help/support, release info. |
| `enrollment_pending` | Invite/session exists but activation incomplete. | Verification, provider setup, diagnostics, pending state. |
| `employee_active` | Active internal membership. | Employee visible directory, chats, allowed groups, diagnostics/settings. |
| `employee_suspended` | Membership suspended after sync. | Restricted mode, support, no active managed sends by default. |
| `employee_revoked` | Membership revoked after sync. | Hide/clear corporate directory, support/account removal path. |
| `external_active` | Active external relationship. | Scoped visible directory, allowed chats only, external identity wording. |
| `external_suspended_or_revoked` | External relationship restricted or ended. | Restricted mode, support, no active internal directory. |

## 11. Directory UX Contract

MVP directory surfaces:

- Internal Members section;
- External Contacts section;
- search/contact picker;
- contact card;
- chat header;
- chat info;
- managed group roster view;
- stale/expired warning entry points.

Rules:

- employees see active internal members and allowed external contacts;
- external contacts see only allowed organization contacts/team;
- external contacts do not see "All Employees";
- external contacts do not receive internal managed groups by default;
- revoked/suspended records are hidden from ordinary active views after sync;
- admin/support management views are Control Plane scope, not Android MVP unless explicitly selected;
- manual local contacts must be visually distinct from directory contacts.

## 12. Provider Setup Contract

MVP provider setup must support:

- Mail.ru / VK Mail baseline;
- manual/custom IMAP/SMTP;
- provider display name and guidance from Control Plane/provider profile;
- IMAP host/port/security;
- SMTP host/port/security;
- username/email mode;
- auth method guidance;
- app-password hint where relevant;
- local credential entry;
- Check Transport action.

Rules:

- no Mail.ru-only architecture;
- Yandex/Rambler/custom profiles are not whitelist-ready without evidence;
- provider profile metadata must not include user credentials;
- credential storage must use Android-appropriate secure storage selected during implementation planning;
- never export provider credentials in reports.

## 13. Diagnostics Contract

MVP client diagnostics should be minimal and support-oriented.

Required product behavior:

- expose Check Transport or equivalent action;
- show pass/fail and failing stage where available;
- connect diagnostic result to provider profile and network context;
- allow delayed upload/reference when Control Plane was unavailable;
- keep standalone diagnostics APK valid as field evidence;
- avoid production-grade background claims.

Minimum possible in-client checks:

- DNS reachability;
- TCP connection;
- TLS handshake metadata;
- IMAP login/select minimal check;
- SMTP login/EHLO minimal check;
- optional send/receive correlation if policy requires and implementation supports it safely.

Open decision:

- whether diagnostics block activation or warn only.

## 14. Messaging Contract

Messaging uses IMAP/SMTP through the selected transport implementation.

MVP should support:

- one-to-one text messages;
- basic groups or a clearly documented fallback;
- attachments if available through reused implementation path;
- voice/audio messages if available through reused implementation path;
- delivery/connectivity indicators where already provided safely.

MVP must not claim:

- production-grade background delivery;
- locked-screen receive guarantee;
- all-provider compatibility;
- real-time voice calls;
- video calls.

## 15. External Contact UX

Client requirements:

- separate External Contacts section;
- badge in contact card, picker, chat header, and chat info;
- warning before or during conversations involving external contacts;
- external enrollment wording distinct from employee enrollment;
- no "All Employees" membership for external contacts;
- no internal directory exposure to external users;
- reassignment updates visible allowed contact/team after sync;
- old manager loses active visibility after sync when reassigned/revoked.

MVP default:

- one-to-one external contact relationship;
- default visibility `inviter_only` or assigned employee, as policy decides;
- external project rooms later.

## 16. Stale / Offline Behavior

States:

- Control Plane reachable;
- Control Plane unavailable;
- directory fresh;
- directory stale;
- directory expired;
- hash mismatch;
- release policy stale;
- diagnostics upload pending.

Allowed while stale:

- IMAP/SMTP messaging with known contacts if local policy allows;
- read existing chats;
- run local diagnostics;
- use cached provider profile;
- show cached directory with warning.

Restricted while stale/expired:

- invite activation;
- email verification;
- first directory sync;
- visibility expansion;
- managed group creation;
- managed group sends when roster expired;
- release policy refresh.

The app must show stale state plainly and avoid implying current membership when Control Plane sync is unavailable.

## 17. App Release Policy UX

The app must understand cached/fresh release metadata:

- version name;
- version code;
- channel;
- release date;
- APK SHA-256;
- signing info;
- `minSupportedVersion`;
- `forceUpgradeBelowVersion`;
- `deprecatedVersion`;
- `blockedVersion`.

Behavior:

- `ok`: normal use;
- `deprecated`: warn and recommend update;
- `force_upgrade`: block non-essential product use except help/update path;
- `blocked`: block according to policy;
- `unknown/stale`: warn conservatively where relevant.

APK-by-email remains emergency Android fallback only and does not grant membership or trust.

## 18. Security And Privacy

Requirements:

- no app passwords in Control Plane;
- no credentials in invite links;
- no raw invite tokens in persistent logs;
- no raw AUTH in reports;
- no raw logcat export as standard evidence;
- no APK signing keys in repo or docs;
- no silent Android contacts import;
- avoid Android contacts permission in MVP unless explicitly justified;
- external contacts never receive internal directory;
- local diagnostic reports are sanitized before upload/export;
- directory snapshots are applied only after hash verification;
- hash mismatch must not auto-apply;
- revoked/suspended state must restrict active views after sync;
- support flow must not ask users to paste secrets into reports.

## 19. Local Storage Boundaries

Local Android storage categories:

- transport account/core state;
- provider credentials;
- directory cache;
- release policy cache;
- enrollment session state;
- diagnostics local reports pending upload;
- user preferences.

Rules:

- credentials require secure storage design during implementation planning;
- directory cache is sensitive business data;
- external visible directory cache must be scoped separately from employee-visible cache;
- revocation cannot erase information already seen, but app must hide/clear active corporate directory after sync where policy requires;
- diagnostic report cache must exclude secrets.

## 20. Boundaries With Other Blueprints

| Blueprint | Owns |
| --- | --- |
| Control Plane MVP Blueprint | Server authority for org, membership, invites, verification, directory metadata, release metadata, provider profile metadata, audit. |
| Directory MVP Blueprint | Manifest/snapshot shape, hash rules, visible views, stale policy, managed roster authority. |
| Invite Onboarding & Distribution MVP Blueprint | End-to-end invite/enrollment/distribution flow contract. |
| Android Client MVP Blueprint | Client modes, local state, UX-state contracts, sync adapters, app handoff, provider setup UI contract, diagnostics surface, messaging boundary. |
| External Contacts & Guest Access Blueprint | Rich external relationship policies, broader scopes, reassignment details, external project rooms. |
| Provider / Diagnostics Blueprint Slice | Exact diagnostic implementation, evidence retention, report schema, provider verification policy. |
| Deployment Blueprint | Server routes, Traefik, storage, secrets, release storage, rollback. |

## 21. MVP Implementation Slices

These are planning slices for a later implementation plan, not code changes in this Blueprint.

1. Fork-vs-shell decision spike.
2. Android project/package/release identity decision.
3. Onboarding shell and invite handoff.
4. Control Plane sync adapter skeleton.
5. Email verification entry and pending states.
6. Provider profile setup and local credential handling.
7. Check Transport minimal diagnostic surface.
8. Activation result handling.
9. Directory manifest/snapshot sync and hash verification.
10. Visible directory, picker, and external contact badges.
11. One-to-one chat integration with reused messaging core.
12. Basic group/managed roster behavior.
13. Stale/expired state warnings and managed-send policy.
14. Release policy warning/blocking.
15. Sanitized support export/delayed upload.

## 22. Validation Plan

Blueprint acceptance should be followed by implementation-level validation planning:

- invite link opens landing/app handoff;
- fallback invite code works;
- email verification code flow works;
- wrong/expired/revoked invite states are safe;
- Mail.ru / VK Mail baseline provider setup works;
- manual/custom provider setup is possible;
- diagnostics export contains no secrets;
- first directory sync verifies hash;
- hash mismatch is rejected;
- stale directory warning appears;
- external contact cannot see internal directory;
- external badge appears in directory and chat;
- managed group send checks roster freshness;
- revoked member disappears from active directory after sync;
- release policy warning/blocking works from cached/fresh metadata.

## 23. Open Questions

- Thin Delta Chat Android fork or custom shell over chatmail/core?
- GPL/MPL compliance and distribution plan.
- Exact Android package ID and internal app name.
- Which Android minimum SDK/device class is targeted for MVP?
- Credential storage approach.
- Whether Android contacts permission is avoided entirely in MVP.
- Exact app link/deep link verification approach.
- Whether QR scanning is in Android MVP or fallback code is enough.
- Whether diagnostics block activation or warn only.
- Exact in-client diagnostics scope.
- Exact managed group send behavior when directory is stale.
- Whether MVP supports one active workspace UI only.
- Whether external contacts use the same APK mode or a constrained guest mode.
- Whether basic groups are user-created, admin-managed, or both.
- Background receive expectation for first field trial.
- Release signing and update channel policy.

## 24. Acceptance Criteria

This Blueprint is accepted when:

- it uses current Roadmap, Control Plane, Directory, Invite, PRD, and research docs;
- it defines Android client MVP scope;
- it excludes code, deployment, and upstream modifications;
- it defines fork-vs-shell decision criteria;
- it defines client local state boundaries;
- it defines onboarding and activation behavior;
- it includes email verification code entry;
- it includes provider setup and diagnostics boundaries;
- it includes first directory sync and hash verification;
- it includes stale/offline behavior;
- it includes internal/external directory separation;
- it includes external contact UI obligations;
- it includes managed group roster enforcement expectations;
- it includes release policy UX;
- it includes security/privacy requirements;
- it lists implementation slices and open questions.
