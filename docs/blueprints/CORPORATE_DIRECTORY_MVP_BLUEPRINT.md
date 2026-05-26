# Corporate Directory MVP Blueprint

Date: 2026-05-26

Status: Draft

Scope: MVP Blueprint

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Merge note: this Blueprint was originally drafted as a stacked follow-up from `blueprint/control-plane-mvp`. PR #3 and the Directory content were later fast-forwarded into `main`; the Blueprint now serves as the merged Directory baseline for the next Invite Onboarding Blueprint.

Source documents:

- [Project Roadmap](../roadmap/PROJECT_ROADMAP.md)
- [Corporate Control Plane MVP Blueprint](CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md)
- [Corporate Control Plane MVP Blueprint Report](CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT_REPORT.md)
- [Corporate Directory PRD](../product/domains/PRD_CORPORATE_DIRECTORY.md)
- [Corporate IMAP Messenger Root PRD](../product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Product PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Corporate Control Plane PRD](../product/domains/PRD_CORPORATE_CONTROL_PLANE.md)
- [Invite Onboarding & Distribution PRD](../product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md)
- [External Contacts & Guest Access PRD](../product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md)
- [Android Messenger Client PRD](../product/domains/PRD_ANDROID_MESSENGER_CLIENT.md)
- [Provider Transport Profiles PRD](../product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md)
- [Diagnostics & Transport Verification PRD](../product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md)
- [Product Decisions Log](../product/decisions/PRODUCT_DECISIONS_LOG.md)
- [Product Context Handoff](../product/handoff/PRODUCT_CONTEXT_HANDOFF.md)
- [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)
- [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md)
- [Delta Chat / Chatmail Capabilities Report](../research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md)
- [Delta Chat Corporate Feature Map](../hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md)

## 1. Executive Summary

Corporate Directory is a core B2B feature, not just a contact list. It is the authority model for corporate identity and visibility: who is an internal member, who is an external contact, who is active, who is suspended or revoked, which contacts a specific principal may see, which groups are managed, and how the Android client behaves when directory state becomes stale.

This Blueprint comes after the Corporate Control Plane MVP Blueprint because Control Plane is the source of truth for organization/workspace, membership, external relationships, directory publication, audit, and stale policy. Directory is the domain-specific representation and sync contract that clients consume.

Directory manages visible identity, not transport. Messages still flow through IMAP/SMTP providers. Directory state is fetched through the Control Plane when available, cached locally by the Android client, and treated as stale or expired according to policy when Control Plane is unavailable.

MVP Directory must support `DirectoryManifest`, `DirectorySnapshot`, canonical payload hashing, visible directory per principal, internal/external separation, member and external contact statuses, basic managed groups, revocation/suspension behavior, stale directory mode, and hash verification. It must not silently import arbitrary address books or expose internal directory data to external contacts.

## 2. Source Documents And Inherited Decisions

| Source document | Decisions inherited |
| --- | --- |
| [Project Roadmap](../roadmap/PROJECT_ROADMAP.md) | Directory Blueprint follows Control Plane Blueprint; Android and deployment remain later. |
| [Control Plane Blueprint](CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md) | Control Plane owns directory manifest/snapshot publication, current version/hash, visible directory rules, managed group roster source, stale policy, and audit. |
| [Control Plane Blueprint Report](CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT_REPORT.md) | Directory is the recommended next Blueprint because onboarding, Android, managed groups, and external contacts depend on it. |
| [Corporate Directory PRD](../product/domains/PRD_CORPORATE_DIRECTORY.md) | Directory is centrally managed, versioned, hash-verifiable, cacheable, and honest about revocation limits. |
| [Root PRD](../product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md) | Corporate Directory is the source of active members, statuses, managed groups, versions, hashes, and local client cache behavior. |
| [PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md) | Stale mode, multi-workspace scoping, managed roster enforcement, trust state separation, and canonical hash rules are required. |
| [Control Plane PRD](../product/domains/PRD_CORPORATE_CONTROL_PLANE.md) | Admin changes to members, external contacts, and groups must publish directory state and produce audit events. |
| [Invite PRD](../product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md) | First directory sync follows activation; internal invite creates membership; external invite creates external relationship. |
| [External Contacts PRD](../product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md) | External contacts are scoped guest relationships and must not receive internal directory. |
| [Android Client PRD](../product/domains/PRD_ANDROID_MESSENGER_CLIENT.md) | Android client stores local cache, verifies manifest/snapshot, shows stale warnings, and uses visible directory for contact picker and groups. |
| [Provider Profiles PRD](../product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md) | Provider profile state is organization/workspace-scoped context; provider diagnostics are not directory authority. |
| [Diagnostics PRD](../product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md) | Diagnostic evidence upload can be delayed; diagnostics do not replace membership or directory trust. |
| [Decisions Log](../product/decisions/PRODUCT_DECISIONS_LOG.md) | Version/hash, external contact separation, stale cache, current managed roster, and no silent unsafe import are accepted decisions. |
| [Product Context Handoff](../product/handoff/PRODUCT_CONTEXT_HANDOFF.md) | Future work must not use stale historical group roster as managed authority or expose internal directory to external contacts. |
| [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md) | Control Plane can be unavailable in whitelist mode; signed IMAP/system-account updates are later scope. |
| [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md) | Deployment is later and must not be bundled into this Directory Blueprint. |
| [Delta Chat / Chatmail Capabilities Report](../research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md) | Delta Chat has contact, vCard, group, and broadcast primitives, but no ready-made trusted corporate directory distribution channel. |
| [Delta Chat Feature Map](../hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md) | Corporate directory and managed provider profiles are product-layer models; core changes require later Blueprint justification. |

Inherited decisions:

- Control Plane is the source of truth.
- Directory is centrally managed.
- Android client has a local cache.
- Internal and external directory spaces are separate.
- Visible directory per user/principal is required.
- Directory version/hash is required.
- Stale directory mode is required.
- No silent unsafe address book import.
- Signed IMAP/system-account directory updates are later/fallback scope, not MVP.
- External contacts do not receive internal directory.
- Managed groups use the current active roster from Directory/Control Plane.
- Directory active status is not the same as cryptographic verification.

## 3. Goals

MVP Directory Blueprint must:

- define `DirectoryManifest`;
- define `DirectorySnapshot`;
- define canonical payload structure and hash rules;
- define `directoryVersion` and `directoryHash` semantics;
- define visible directory views per principal;
- define internal members versus external contacts;
- define member and external contact statuses;
- define managed group roster authority;
- define revocation and suspension effects;
- define stale and expired directory behavior;
- define sync behavior;
- define Android local cache expectations;
- define Control Plane responsibilities;
- define audit events;
- define security and privacy invariants;
- define MVP boundaries and later scope.

## 4. Non-Goals

This Blueprint does not include:

- SQL schema;
- OpenAPI specification;
- Android UI implementation;
- Delta Chat Android changes;
- chatmail/core changes;
- full HR/IdP integration;
- signed IMAP/system-account update implementation in MVP;
- automatic silent address book import;
- CRM/helpdesk workflows;
- guarantee of erasing already-seen information;
- deployment;
- Docker Compose;
- Traefik changes.

## 5. System Context

Conceptual context:

```text
Control Plane
  -> Directory Authority
  -> DirectoryManifest
  -> DirectorySnapshot
  -> Audit Log

Android Client
  -> fetch manifest/snapshot when Control Plane is available
  -> maintain local cached directory
  -> detect fresh/stale/expired/hash_mismatch states
  -> use visible directory for search, contact picker, and managed group roster

IMAP/SMTP
  -> message transport only
  -> not directory authority

Delta Chat / Chatmail
  -> contact, vCard, chat, group, and transport capabilities reused where safe
  -> not source of corporate directory truth
```

Directory is consumed by Android but owned by Control Plane. Android may create local contacts or use Delta Chat contact primitives, but those records are not corporate authority unless they match the published directory state.

## 6. Directory Authority Model

Control Plane owns canonical directory state. Android cache, local contacts, imported vCards, historical chat participants, and provider address books are not sources of corporate authority.

Authority rules:

- Control Plane owns `directoryVersion` and `directoryHash`.
- Control Plane generates `DirectoryManifest`.
- Control Plane generates or references `DirectorySnapshot`.
- Control Plane decides visible directory per principal.
- Control Plane publishes stale/expired policy.
- Android only applies snapshots that match manifest hash.
- Android local cache enables continuity but does not create current membership claims.
- User-created contacts are local/non-authoritative unless matched to directory records.
- vCard import may help populate local contact records, but it cannot override directory authority or cryptographic trust.
- External contacts are organization-controlled visible directory records, not internal members.

Directory is not a single identical full address book for everyone. It has authority data and filtered views.

## 7. Core Entities

This section defines logical entities and fields. It is not a database schema.

### 7.1 DirectoryManifest

Fields:

- `organizationId`;
- `workspaceId` optional;
- `schemaVersion`;
- `directoryVersion`;
- `directoryHash`;
- `snapshotRef` or `snapshotUrl`;
- `updatedAt`;
- `stalePolicy`;
- `minClientVersion` optional;
- `visibilityScopeInfo` optional;
- `manifestGeneratedAt`, not included in hash.

Notes:

- Manifest is the lightweight sync entry point.
- `directoryHash` refers to the canonical snapshot payload, not volatile manifest metadata.
- Manifest may be principal-scoped if different principals receive different snapshots.
- Manifest must not expose hidden internal directory content to external contacts.

### 7.2 DirectorySnapshot

Fields:

- `organizationId`;
- `workspaceId` optional;
- `schemaVersion`;
- `directoryVersion`;
- `payloadType`;
- `internalMembers`;
- `externalContacts`;
- `managedGroups`;
- `providerProfileRefs` optional;
- `policyRefs` optional;
- `canonicalPayloadHash`.

Notes:

- `payloadType` identifies whether the snapshot is full admin authority data, employee-visible data, external-visible data, or another supported view.
- MVP can use full snapshots per authorized principal type; deltas are later scope.
- Provider/profile references are optional context and must not turn Directory into provider credential storage.

### 7.3 InternalMemberRecord

Fields:

- `memberId`;
- `userId`;
- `organizationId`;
- `workspaceId` optional;
- `email`;
- `displayName`;
- `role` or `displayRole`;
- `department` or `team`;
- `status`: `pending`, `active`, `suspended`, `revoked`;
- profile metadata;
- visibility flags;
- `cryptographicVerificationStatus` optional;
- `updatedAt` optional, not necessarily hashed unless semantic.

Notes:

- Active internal members may appear in employee-visible directory and managed groups according to policy.
- Pending, suspended, and revoked records may remain visible to admins but should not be treated as active recipients by ordinary users.

### 7.4 ExternalContactRecord

Fields:

- `externalContactId`;
- `relationshipId`;
- `organizationId`;
- `workspaceId` optional;
- `email`;
- `displayName`;
- `companyName`;
- `contactType`: `client`, `supplier`, `partner`, `contractor`, `other`;
- `status`: `pending`, `active`, `suspended`, `revoked`, `archived`, `reassigned`;
- `visibilityScope`;
- `assignedEmployeeId`;
- `assignedTeamId`;
- `allowedContactRefs`;
- badges or labels.

Notes:

- External contact record is not internal membership.
- External contacts must not appear in "All Employees".
- External contacts can appear in an employee-visible external section when visibility scope allows.
- External contacts receive only scoped allowed contact cards or visible directory data.

### 7.5 VisibleDirectoryView

Fields:

- `principalId`;
- `principalType`: `internal_member`, `external_contact`, `admin`, `support`, `auditor`;
- `organizationId`;
- `workspaceId` optional;
- `visibleInternalMembers`;
- `visibleExternalContacts`;
- `visibleManagedGroups`;
- `hiddenReason` metadata optional.

Notes:

- This is a derived view, not a separate authority source.
- MVP may materialize this as a principal-scoped snapshot or compute it at request time.
- External contacts must never receive the full internal member list through this view.

### 7.6 ManagedGroupRecord

Fields:

- `groupId`;
- `organizationId`;
- `workspaceId` optional;
- `displayName`;
- `groupType`;
- `rosterSource`;
- `activeMemberIds`;
- `externalParticipantIds` if allowed later;
- `status`;
- `lastRosterVersion`;
- `stalePolicy`.

Notes:

- Roster authority comes from Directory.
- Historical chat membership is not roster authority.
- MVP managed groups should use active internal members only unless a specific external room feature is later selected.

### 7.7 DirectoryPolicy

Fields:

- `staleAfter`;
- `expiredAfter`;
- `allowMessagingKnownContactsWhenStale`;
- `blockManagedGroupSendWhenExpired`;
- `externalDirectoryVisibilityDefaults`;
- `suspendedMemberVisibility`;
- `revokedMemberBehavior`.

Notes:

- Policy is organization/workspace-scoped.
- Exact default threshold values remain open.

### 7.8 LocalDirectoryCache

Fields:

- `organizationId`;
- `workspaceId` optional;
- `directoryVersion`;
- `directoryHash`;
- `lastSyncAt`;
- `staleState`;
- `cachedSnapshot`;
- `cacheAppliedAt`.

Notes:

- Local cache belongs to the Android client.
- It is usable continuity state, not authority.
- Cache state must be visible to users when stale or expired.

## 8. Directory Spaces And Views

Directory has authority spaces and visible views.

| Space or view | Purpose | Who receives it | Key rules |
| --- | --- | --- | --- |
| Internal Authority Directory | Full internal membership and status source. | Control Plane/admin authority only. | Contains pending/active/suspended/revoked records as needed for management. |
| External Contacts Directory | Organization-owned external contacts and relationships. | Admin authority; filtered employee views. | External contacts are separate from internal members. |
| Admin Full Directory View | Management view for admins/owners. | Owner/Admin. | Can show active, pending, suspended, revoked, archived, and reassigned records according to RBAC. |
| Employee Visible Directory | Employee search/contact/group view. | Active internal members. | Shows active internal members and allowed external contacts. |
| External Contact Visible Directory | Scoped guest view. | Active external contacts. | Shows only allowed employee/team/contact data; never full internal directory. |
| Support/Auditor View | Operational or read-only visibility. | Support/IT, Auditor. | Read-only or policy-limited metadata; no broad mutation. |

Rules:

- Not every user receives the full directory.
- External contacts receive only scoped allowed contacts.
- Employees see internal members plus allowed external contacts.
- Admins see management view.
- Support/auditor may see read-only metadata depending on RBAC.
- Visibility expansion is sensitive and must be auditable when policy changes.

## 9. Status Model

### 9.1 Internal Member Statuses

| Status | Meaning | Employee directory | Admin view | External contact view | Managed group eligible | One-to-one eligible | Cached client effect |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `pending` | Invite/enrollment not fully active. | No by default. | Yes. | No. | No. | No. | Do not expose as active. |
| `active` | Current organization member. | Yes according to visibility policy. | Yes. | Only if explicitly allowed. | Yes. | Yes. | Usable while fresh; stale warnings apply. |
| `suspended` | Temporarily disabled or restricted. | Conservative MVP default: hidden. | Yes. | No by default. | No by default. | Policy-restricted. | Hide/restrict after sync; warn if stale. |
| `revoked` | No longer active member. | No. | Yes for audit/history. | No. | No. | No managed send. | Remove/hide after sync; cannot erase already-seen info. |

### 9.2 External Contact Statuses

| Status | Meaning | Employee directory | Admin view | External contact view | Managed group eligible | One-to-one eligible | Cached client effect |
| --- | --- | --- | --- | --- | --- | --- | --- |
| `pending` | External invite not fully activated. | No by default. | Yes. | Limited onboarding only. | No. | No. | Do not expose as active. |
| `active` | External relationship active. | Yes if visibility scope allows. | Yes. | Scoped allowed contacts only. | No internal groups by default. | Yes within allowed scope. | Usable while fresh; stale warnings apply. |
| `suspended` | Temporarily restricted external access. | Hidden or warning state by policy. | Yes. | Block/restrict after sync. | No. | Policy-restricted. | Restrict after sync; stale cache must not expand access. |
| `revoked` | External relationship terminated. | No active external section. | Yes for audit/history. | Block after sync. | No. | No. | Hide/disable after sync. |
| `archived` | Retained for history, not active. | No active external section. | Yes. | No active access. | No. | No. | Hide from active views. |
| `reassigned` | Assigned employee/team changed. | Visible to new assignee/scope. | Yes. | Updated allowed contact/team where policy allows. | No internal groups by default. | Yes within new scope. | Old handler loses access after sync. |

## 10. Version And Hash Model

`directoryVersion` is monotonic per organization/workspace. It changes when directory semantics change: member status, external relationship visibility, managed group roster, visible profile fields, or stale policy references that affect client behavior.

`directoryHash` is:

```text
SHA-256(canonical directory snapshot payload)
```

Hash semantics:

- verifies content integrity and change detection;
- does not prove issuer authenticity;
- does not replace signatures;
- does not prove cryptographic identity of contacts;
- signed snapshots and replay protection are later scope.

### 10.1 Included In Hash

Include fields that affect directory semantics:

- `organizationId`;
- `workspaceId`;
- `schemaVersion`;
- `directoryVersion` if part of canonical snapshot payload;
- internal member IDs and directory-visible fields;
- internal member statuses;
- external contact IDs or relationship IDs;
- external contact statuses;
- visibility scopes;
- assigned employee/team fields;
- allowed contact references;
- managed group IDs and active roster member IDs;
- group status/type when semantically relevant;
- policy values that affect directory behavior if embedded in snapshot.

### 10.2 Excluded From Hash

Exclude volatile or transport metadata:

- `generatedAt`;
- `manifestGeneratedAt`;
- `serverTime`;
- `requestId`;
- pagination cursors;
- retry counters;
- transient sync metadata;
- transport headers;
- logging IDs;
- diagnostic upload transient state.

### 10.3 Canonical Payload Rules

Canonical payload rules:

- stable JSON canonicalization;
- UTF-8 encoding;
- stable object field ordering;
- sort `internalMembers` by `memberId`;
- sort `externalContacts` by `externalContactId` or `relationshipId`;
- sort `managedGroups` by `groupId`;
- sort member IDs inside group rosters;
- sort allowed contact references;
- lowercase and normalize email fields;
- normalize explicit nulls, empty strings, and empty arrays according to a documented rule;
- include organization/workspace scope;
- no volatile fields in hashed payload.

### 10.4 Client Verification

Client verification flow:

1. Receive `DirectoryManifest`.
2. Download referenced `DirectorySnapshot` if version/hash differs from cache.
3. Canonicalize snapshot payload using the defined rules.
4. Compute SHA-256.
5. Compare computed hash with manifest `directoryHash`.
6. If equal, apply snapshot.
7. If mismatch, reject snapshot, keep previous accepted cache where policy allows, mark state `hash_mismatch`, and report error when Control Plane is reachable.

## 11. Sync Model

MVP sync is full manifest/snapshot sync. Deltas are later scope.

Flow:

1. Client has local `directoryVersion` and `directoryHash`.
2. Client requests `DirectoryManifest`.
3. If version/hash unchanged, keep cache.
4. If changed, download `DirectorySnapshot`.
5. Compute canonical hash.
6. Compare with manifest hash.
7. If match, apply snapshot.
8. If mismatch, reject/quarantine and show error/report.
9. Update local cache metadata.
10. Refresh visible directory, search, contact picker, and managed group roster.

Sync moments:

- first sync after internal membership activation;
- first sync after external relationship activation;
- app start/resume when Control Plane is reachable;
- periodic/background-eligible sync later, if Android implementation supports it;
- manual refresh by user/support;
- after stale or unavailable period once Control Plane returns;
- after admin publish/revoke/reassign event, when the client next sees changed manifest.

Revoked user behavior:

- A revoked user's client may not learn revocation until next Control Plane sync.
- After sync, app should clear/hide corporate directory data for that organization/workspace according to policy.
- Previously seen information cannot be guaranteed erased.

## 12. Stale And Expired Directory Behavior

Directory cache state:

- `fresh`;
- `stale`;
- `expired`;
- `unavailable`;
- `hash_mismatch`.

Policy fields:

- `staleAfter`;
- `expiredAfter`;
- `allowMessagingKnownContactsWhenStale`;
- `blockManagedGroupSendWhenExpired`;
- managed group stale-send behavior;
- external visible directory stale behavior.

### 12.1 Fresh

Behavior:

- normal directory search;
- normal contact picker;
- managed group roster may be used;
- visible directory reflects latest accepted snapshot.

### 12.2 Stale

Behavior:

- show stale directory warning where relevant;
- messaging known active contacts may continue if policy allows;
- contact picker must indicate state is not current;
- creating a new managed group should be restricted or require fresh state;
- sending to an existing managed group must use current active roster if available or warn/block according to policy;
- invite activation remains impossible without Control Plane.

### 12.3 Expired

Behavior:

- stronger warning;
- managed group sends blocked or require explicit policy override;
- creating new chats from directory may be blocked;
- external visible directory may be hidden or warning-heavy;
- known one-to-one contact messaging may be allowed or blocked by policy;
- app must not claim current membership state.

### 12.4 Control Plane Unavailable

Behavior:

- use cached directory if policy allows;
- do not claim current membership;
- no invite activation;
- no new directory publish;
- no release policy update except cached policy;
- no external visibility expansion;
- diagnostics may run locally and upload later.

### 12.5 Hash Mismatch

Behavior:

- reject new snapshot;
- keep previous accepted cache if policy allows;
- mark state `hash_mismatch`;
- show safe error;
- report to Control Plane when available;
- do not auto-apply mismatched payload.

## 13. Revocation And Suspension Behavior

### 13.1 Internal Revocation

Rules:

- revoked member is hidden from active employee directory;
- revoked member is removed from managed groups;
- dynamic "All Employees" excludes revoked member;
- revoked member cannot be selected for new managed sends;
- revoked member remains visible to admins/auditors where policy requires history;
- revoked user's client clears/hides corporate directory after sync;
- Control Plane cannot erase messages or information already delivered through IMAP/SMTP or already seen by people.

### 13.2 Internal Suspension

Conservative MVP default:

- suspended members are hidden from ordinary employee directory;
- suspended members are excluded from managed groups;
- suspended members remain visible to admins/support/auditors;
- one-to-one behavior is policy-restricted;
- reactivation requires Control Plane publish/sync.

### 13.3 External Contact Revoke / Archive

Rules:

- revoked/archived external contact is removed from active external section;
- external relationship is disabled;
- assigned employees lose active external relationship after sync;
- external contact cannot receive internal directory;
- admin/audit history remains.

### 13.4 External Reassignment

Rules:

- relationship remains organization-owned;
- old assigned employee/team loses active visibility after sync;
- new assigned employee/team gains visibility after sync;
- external contact sees new allowed contact/team where policy allows;
- no internal HR reason is exposed;
- directory version/hash changes when visible scope changes.

## 14. Managed Groups And Roster Enforcement

Managed group roster comes from Directory, not local chat history.

MVP managed group types:

- dynamic "All Employees";
- department/team groups;
- starter groups.

Later managed group types:

- external project rooms;
- groups with controlled external participants;
- richer announcement/broadcast policy.

Rules:

- active roster is generated from active members.
- revoked members are excluded.
- suspended members are excluded by conservative MVP default.
- external participants are not part of internal managed groups by default.
- historical local chat participants are not roster authority.
- sending to a managed group must use the current active roster.
- if roster is stale, Android must warn or block according to policy.
- if roster is expired, default behavior should block managed group sends unless policy explicitly allows override.
- if a historical chat includes a revoked member, Android should show a warning.

Integration contract for Android later:

- Android receives managed group records with `lastRosterVersion`.
- Android checks cache state before managed sends.
- Android uses Directory roster for new managed sends.
- Android distinguishes managed group from ordinary/user-created chat.
- Ordinary historical chats can remain visible but must not imply current managed membership.

## 15. Trust And Verification Semantics

Directory state is only one trust layer.

Rules:

- directory active does not mean cryptographically verified;
- email verified does not mean active membership;
- invite present does not mean active membership;
- imported contact does not mean directory active;
- external contact active does not mean internal member;
- SecureJoin or equivalent cryptographic verification remains separate.

Expected fields:

- `directoryTrustStatus`;
- `emailVerified`;
- `membershipStatus`;
- `externalRelationshipStatus` where applicable;
- `cryptographicVerificationStatus` optional.

The client must not present imported or directory-created contacts as cryptographically verified unless the underlying cryptographic trust mechanism actually verifies them.

## 16. Manual Import / vCard

MVP rules:

- no silent arbitrary address book import;
- no automatic import of vCards from messages;
- manual import only with preview/confirmation if included;
- imported contacts are not automatically verified;
- vCard import cannot override corporate directory authority;
- imported contacts may be local/non-corporate unless matched by directory;
- imported contact data must not change SecureJoin/Autocrypt trust state.

Delta Chat / Chatmail already has contact and vCard primitives. They are useful implementation tools, not corporate authority. Any automatic signed directory file distribution is later scope and must include signature, version, replay protection, issuer, audit, and preview/rollback design.

## 17. Control Plane Responsibilities

Control Plane must:

- generate manifest;
- generate or reference snapshot;
- canonicalize payload;
- compute hash;
- publish directory version;
- handle member status changes;
- handle external contact and relationship status changes;
- handle managed groups;
- expose visible directory per principal;
- define stale policies;
- audit publish/revoke/reassign/suspend/group changes;
- reject unauthorized visible directory access;
- avoid exposing internal directory data to external contacts.

Control Plane owns authority. It may delegate storage or caching implementation later, but ownership remains here.

## 18. Android Client Responsibilities

Android client must:

- store local directory cache;
- fetch manifest and snapshot when Control Plane is available;
- verify hash before applying snapshot;
- apply accepted snapshot to local directory state;
- show stale/expired/hash mismatch warnings;
- use visible directory for search and contact picker;
- distinguish internal and external contacts;
- display external badges;
- never expose full internal directory to external users;
- respect managed group roster freshness;
- avoid presenting local/imported contacts as corporate authority;
- clear/hide corporate directory after revoked state is synced;
- report hash mismatch or sync errors when Control Plane is available.

Android client must not:

- invent current membership state from local contacts;
- treat historical group membership as current managed roster;
- silently import arbitrary address books;
- use provider diagnostics as directory trust.

## 19. Audit Events

Directory-related audit events:

- directory published;
- directory snapshot hash generated;
- member added;
- member updated;
- member suspended;
- member revoked;
- external contact added;
- external contact updated;
- external contact suspended;
- external contact revoked;
- external contact archived;
- external contact reassigned;
- managed group created;
- managed group updated;
- managed group archived/disabled;
- stale/expired policy changed;
- hash mismatch reported;
- manual import performed if supported;
- visible directory access/security anomaly, later if needed.

Audit metadata must be redacted and must not include secrets, raw tokens, credentials, raw logs, or private keys.

## 20. Security And Privacy

Requirements:

- external contacts do not receive internal directory;
- no secrets in directory payload;
- no app passwords in directory payload;
- no raw AUTH;
- no raw diagnostic logs;
- avoid unnecessary full email exposure where not needed;
- directory snapshots must be authorized per principal;
- support/auditor views limited by RBAC;
- hash mismatch must not auto-apply;
- revoked member data must not leak to unauthorized users;
- external contacts must not leak to unauthorized employees;
- external visible directory cache must not expand visibility while stale;
- admin/support access to directory metadata must be auditable.

Directory data is sensitive business data. Even without passwords, it can reveal organization structure, client relationships, departments, and counterparties.

## 21. MVP Scope

MVP includes:

- `DirectoryManifest`;
- `DirectorySnapshot`;
- `directoryVersion`;
- `directoryHash`;
- canonical payload rules;
- hash verification;
- visible directory per principal;
- internal/external separation;
- internal member statuses;
- external contact statuses;
- local Android cache expectations;
- stale/expired state;
- basic managed group roster authority;
- dynamic "All Employees";
- revocation behavior;
- suspension behavior;
- external contact reassignment visibility effects;
- no silent arbitrary import.

## 22. Later Scope

Later scope:

- signed snapshots;
- signed IMAP/system-account updates;
- replay protection;
- directory deltas;
- rollback;
- HR/IdP integration;
- CSV/admin import workflow;
- advanced conflict handling;
- multi-workspace UI;
- external project rooms;
- managed external project group rosters;
- CRM/helpdesk integration;
- cryptographic trust policy hardening;
- advanced audit/export controls.

## 23. Boundaries With Other Blueprints

| Blueprint | Owns |
| --- | --- |
| Corporate Control Plane MVP Blueprint | Organization, membership, invite, email verification, release metadata, provider profile authority, audit module, stale policy publication. |
| Corporate Directory MVP Blueprint | Directory representation, manifest/snapshot, canonical hash, visible views, roster source, stale directory behavior, directory cache contract. |
| Invite Onboarding & Distribution Blueprint | Join flow, internal/external invite UX, landing page, APK download handoff, verification flow orchestration. |
| Android Client MVP Blueprint | App screens, local storage implementation, user-facing warnings, search/contact picker UI, group send UX. |
| External Contacts & Guest Access Blueprint | External relationship UX, policies, broader visibility scopes, reassignment details, external project rooms. |
| Provider / Diagnostics Blueprint Slice | Provider diagnostic evidence model, in-client check scope, standalone diagnostics relationship. |
| Deployment Blueprint | Server deployment, Traefik, database/storage/secrets, backup, rollback. |

## 24. Open Questions

- Exact `staleAfter` default value.
- Exact `expiredAfter` default value.
- Exact canonical JSON library/standard.
- Whether MVP snapshots are full only or include simple deltas.
- Whether visible directory is precomputed or computed per request.
- Whether external contacts receive minimal snapshot or only allowed contact cards.
- Whether suspended members are visible to employees.
- Whether user local nicknames are allowed for corporate contacts.
- Whether manual vCard import is included in MVP.
- Exact managed group send behavior when stale.
- Whether `DirectorySnapshot` includes provider profile references.
- Whether `DirectorySnapshot` includes release policy references or leaves that to a separate API.
- Audit retention for directory events.
- Whether admin publishes snapshots explicitly or every accepted change publishes automatically.
- How to represent multi-workspace UI if deferred but data model supports it.

## 25. Acceptance Criteria

This Blueprint is accepted when:

- it defines `DirectoryManifest`;
- it defines `DirectorySnapshot`;
- it defines version/hash semantics;
- it defines canonical payload rules;
- it defines hash verification behavior;
- it defines visible directory per principal;
- it separates Internal Members and External Contacts;
- it defines internal member statuses;
- it defines external contact statuses;
- it defines stale/expired behavior;
- it defines revocation behavior;
- it defines suspension behavior;
- it defines managed group roster authority;
- it defines Control Plane responsibilities;
- it defines Android client responsibilities;
- it excludes silent arbitrary import;
- it excludes signed IMAP/system-account updates from MVP;
- it lists open questions;
- it does not write code, SQL, OpenAPI, deployment files, Android UI, or upstream Delta Chat / Chatmail changes.
