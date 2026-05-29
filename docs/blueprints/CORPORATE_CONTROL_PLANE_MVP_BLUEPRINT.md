# Corporate Control Plane MVP Blueprint

Date: 2026-05-26

Status: Draft

Scope: MVP Blueprint

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Source documents:

- [Project Roadmap](../roadmap/PROJECT_ROADMAP.md)
- [Corporate IMAP Messenger Root PRD](../product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Corporate Control Plane PRD](../product/domains/PRD_CORPORATE_CONTROL_PLANE.md)
- [Corporate Directory PRD](../product/domains/PRD_CORPORATE_DIRECTORY.md)
- [Invite Onboarding & Distribution PRD](../product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md)
- [External Contacts & Guest Access PRD](../product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md)
- [Provider Transport Profiles PRD](../product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md)
- [Diagnostics & Transport Verification PRD](../product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md)
- [Product PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Product Decisions Log](../product/decisions/PRODUCT_DECISIONS_LOG.md)
- [Product Context Handoff](../product/handoff/PRODUCT_CONTEXT_HANDOFF.md)
- [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)
- [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md)
- [Delta Chat / Chatmail Capabilities Report](../research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md)
- [Delta Chat Corporate Feature Map](../hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md)

## 1. Executive Summary

Corporate Control Plane is the MVP source of truth for organization state. It manages organizations/workspaces, memberships, roles, internal invites, external invites, email verification, directory authority, provider profiles, APK release metadata, diagnostic evidence references, audit, and stale-mode policy.

It is the first technical Blueprint because every later product surface depends on it. The Android client needs Control Plane state to know who the active organization members are, which external contacts are allowed, which provider profiles are recommended, which app release policy applies, and whether cached directory state is stale. Invite onboarding and distribution also depend on Control Plane because APK download does not equal organization membership.

The Control Plane is not a message server. IMAP/SMTP messages continue to flow through configured mail providers such as the Mail.ru / VK Mail transport family. Control Plane availability may be worse than provider availability in mobile whitelist or restricted-network mode, so the Android client must support cached/stale directory and policy state.

This Blueprint defines MVP architecture boundaries, domain entities, state machines, RBAC, workflows, security/privacy constraints, and infrastructure assumptions. It does not define OpenAPI, SQL migrations, Docker Compose, deployment runbooks, UI mockups, or code.

## 2. Source Documents And Inherited Decisions

| Source document | Decisions inherited |
| --- | --- |
| [Project Roadmap](../roadmap/PROJECT_ROADMAP.md) | Control Plane MVP Blueprint is the next required artifact; Control Plane precedes Android product work, Directory Blueprint, Invite Blueprint, and deployment. |
| [Root PRD](../product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md) | Product is Android-first, provider-agnostic, invite-based, and uses IMAP/SMTP for messages with a separate Control Plane for organization state. |
| [Corporate Control Plane PRD](../product/domains/PRD_CORPORATE_CONTROL_PLANE.md) | Control Plane owns organization, members, invites, external contacts, directory, provider profiles, app releases, diagnostics status, policies, and audit. |
| [Corporate Directory PRD](../product/domains/PRD_CORPORATE_DIRECTORY.md) | Directory uses version/hash manifests, canonical snapshots, internal/external separation, managed groups, and stale directory mode. |
| [Invite Onboarding & Distribution PRD](../product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md) | Internal invites create memberships; external invites create external relationships; activation requires Control Plane and email ownership verification. |
| [External Contacts PRD](../product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md) | External contacts are non-employees, must not receive internal directory, and belong to the organization rather than a personal manager. |
| [Provider Transport Profiles PRD](../product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md) | Provider profiles are organization/workspace-scoped, provider-agnostic, and carry diagnostic status without storing credentials. |
| [Diagnostics PRD](../product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md) | Diagnostic evidence can be collected locally and uploaded later; diagnostics do not replace email ownership proof. |
| [PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md) | Stale Control Plane mode, email verification, multi-workspace scoping, managed group enforcement, trust states, RBAC, canonical directory hash, invite abuse controls, and app release lifecycle are required. |
| [Decisions Log](../product/decisions/PRODUCT_DECISIONS_LOG.md) | Accepted product decisions must be preserved, especially provider-agnostic architecture and separation of internal membership from external relationship. |
| [Product Context Handoff](../product/handoff/PRODUCT_CONTEXT_HANDOFF.md) | Future Blueprints must not conflate invite token, email verification, active membership, external contact, or cryptographic verification. |
| [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md) | Domain, Traefik, server constraints, secrets policy, APK distribution assumptions, own database default, and no direct host ports by default. |
| [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md) | Existing Traefik container and `traefik-net` were observed, but path/network are candidate assumptions only and not deployment approval. |
| [Delta Chat / Chatmail Research](../research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md) | Reuse transport capabilities where possible; do not hardcode Mail.ru-only architecture; keep diagnostics evidence-based. |
| [Delta Chat Corporate Feature Map](../hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md) | Provider presets and diagnostics can be product-layer constructs; core/chatmail changes require later Blueprint justification. |

Inherited decisions:

- MVP-0a diagnostics accepted.
- Mail.ru / VK Mail transport family accepted as first baseline.
- Architecture remains provider-agnostic.
- APK download does not equal organization membership.
- Invite/enrollment is required.
- Email ownership verification is required.
- Internal invite creates `Membership`.
- External invite creates `ExternalRelationship`.
- Corporate Directory is a core B2B feature.
- Control Plane may be unavailable in whitelist/restricted mode.
- Stale directory and stale policy mode are required.
- iOS is out of current scope.
- APK-by-email is an Android emergency fallback only.
- Control Plane does not host messages.
- No Delta Chat / Chatmail vendor-copy or upstream modification is part of this Blueprint.

## 3. Goals

MVP Control Plane must:

- manage organizations/workspaces;
- manage users and memberships;
- manage roles and RBAC;
- manage internal invites;
- manage external invites;
- verify email ownership before internal membership or external relationship activation;
- own directory manifest and snapshot publication;
- manage member lifecycle state;
- manage external contact and external relationship lifecycle state;
- manage provider profiles and diagnostic statuses;
- manage APK release metadata and download references;
- expose join landing page data;
- provide the backend for an admin portal;
- provide audit log records for sensitive actions;
- publish stale directory and stale policy thresholds;
- support delayed diagnostic evidence upload when Control Plane was previously unreachable;
- keep messaging transport outside Control Plane.

## 4. Non-Goals

MVP Control Plane must not include:

- IMAP/SMTP message hosting;
- a real-time chat server;
- Android client implementation;
- Delta Chat Android fork work;
- chatmail/core changes;
- server deployment;
- Traefik changes;
- production hardening or a complete compliance program;
- full CRM or helpdesk;
- full MDM;
- iOS support;
- automatic signed IMAP/system-account directory updates;
- APK signing pipeline implementation;
- storage of user app passwords;
- storage of raw AUTH payloads;
- storage of raw diagnostic logs;
- OpenAPI-level endpoint specification;
- SQL migrations or database physical schema;
- UI mockups.

## 5. System Context

Conceptual context:

```text
Android Client
  <-> IMAP/SMTP Provider
      - messages
      - mail transport readiness
      - provider-specific connectivity

Android Client
  <-> Corporate Control Plane
      - organization/workspace state
      - membership and external relationship state
      - invites and email verification
      - directory manifest/snapshot
      - provider profile policy
      - APK release metadata
      - diagnostics evidence upload/reference
      - audit-relevant state changes

Corporate Control Plane
  -> Database
  -> APK release storage or GitHub Releases metadata
  -> Email verification sender
  -> Diagnostics evidence storage
  -> Admin Web UI
  -> Join Landing Page
```

Control Plane can be unavailable in mobile whitelist mode even while IMAP/SMTP provider transport works. In that case, messages may continue through the provider, but organization state changes, invite activation, revoke application, release checks, diagnostic upload, and directory refresh are delayed until Control Plane sync resumes.

## 6. Logical Components

| Component | Purpose | MVP responsibilities | Not responsible for | Dependencies |
| --- | --- | --- | --- | --- |
| Admin Web UI | Human admin surface for organization operations. | Manage organizations, members, invites, external contacts, directory publish, provider profiles, app releases, diagnostics status, and audit views. | Chat UI, Android client UX, raw message transport. | Control Plane API, Auth/Session, RBAC, Audit Log. |
| Public Join Landing Page | Browser entry point for invite links. | Resolve invite state, explain internal vs external join, show APK download/reference, expose fallback invite code/QR when policy allows. | Activating membership without Control Plane checks, exposing raw invite secrets unnecessarily. | Invite Module, APK Release Metadata, Email Verification flow, public routes. |
| Control Plane API | Boundary used by Android client, admin UI, landing page, and diagnostics upload. | Provide organization state, invite resolution, verification challenge flow, directory manifest/snapshot references, provider profile policy, release metadata, diagnostics upload endpoints, and admin operations. | Open IMAP/SMTP transport, full OpenAPI definition in this document. | Auth/Session, RBAC, domain modules, database. |
| Organization/Workspace Module | Tenant boundary and product scope. | Create and manage organization/workspace records, policies, domain hints, current directory version/hash, and status. | Legal entity management, billing, enterprise SSO in MVP. | Membership, Directory Authority, Provider Profiles, App Releases. |
| Membership Module | Internal employee/member relationship authority. | Track pending, active, suspended, revoked memberships and role assignment. | External contacts, CRM identities, mail provider accounts. | User, Organization, Invite, Email Verification, Directory. |
| RBAC Module | Product-level permission enforcement. | Enforce Owner/Admin/Manager/Support/IT/Auditor permissions and policy gates for sensitive actions. | Fine-grained enterprise policy engine in MVP. | Auth/Session, Membership, Audit. |
| Invite Module | Internal and external invite lifecycle. | Create, resolve, revoke, expire, exhaust, and audit invite tokens and constraints. | Sending arbitrary marketing emails, storing app passwords, treating all invites as employee invites. | RBAC, Email Verification, Membership, External Contacts, Audit. |
| Email Verification Module | Mailbox ownership proof. | Send challenge/code, validate attempts, rate limit failures, bind verification to invite and organization, store code hashes. | IMAP/SMTP transport diagnostics, automatic IMAP challenge reading in MVP. | Invite Module, User, Membership, ExternalRelationship, outbound email configuration. |
| Directory Authority Module | Source of truth for directory manifest and snapshots. | Generate directory version/hash, publish snapshots, own internal/external separation, managed group roster source, stale policy values. | Full signed IMAP/system-account fallback in MVP, HR/IdP import. | Organization, Membership, External Contacts, Managed Groups, Audit. |
| External Contacts Module | Guest/counterparty lifecycle. | Manage external contacts, relationships, one-to-one external invites, visibility scopes, reassignment, suspension, revoke/archive. | CRM/helpdesk, external organization admin portal, broad project-room model. | Invite Module, Email Verification, Directory Authority, Audit. |
| Provider Profiles Module | IMAP/SMTP provider policy and evidence summary. | Manage Mail.ru/VK Mail baseline, manual/custom profiles, organization-scoped provider settings, diagnostic status references. | Storing user provider credentials, implementing IMAP/SMTP. | Diagnostics Evidence, Organization, Directory/Policy sync. |
| APK Release Metadata Module | Android release policy and download references. | Store version/channel/SHA/size/signing info/release status and version-policy thresholds. | APK binary storage in git, APK signing pipeline, iOS release policy. | Join Landing Page, Android client release checks, storage/GitHub Releases reference. |
| Diagnostics Evidence Module | Sanitized diagnostics evidence references. | Accept or reference sanitized reports, link reports to provider profile, network context, organization/workspace, and result. | Raw logs, raw AUTH payloads, local troubleshooting UI. | Provider Profiles, Audit, storage policy. |
| Audit Log Module | Accountability for sensitive state changes. | Record actor, action, target, timestamp, severity, and redacted metadata for admin/invite/verification/directory/release events. | SIEM integration, full compliance suite in MVP. | All state-changing modules, RBAC. |
| Auth/Session Module | Admin and client identity/session boundary. | Authenticate admins/clients, bind sessions to user and organization context, support role checks. | Enterprise SSO, MDM identity, passkey strategy in MVP. | User, Membership, RBAC, configuration. |
| Configuration/Environment Module | Runtime configuration boundary. | Load public base URL, database URL, email sender settings, session/JWT secret references, storage references, logging level, and feature flags from environment. | Storing real secrets in git, deployment specification. | Infrastructure assumptions, secret storage policy. |

### 6.1 API Boundary Groups

This Blueprint defines API boundary groups, not full endpoint contracts:

- public invite/landing boundary: resolve invite safely, show install/join state, no raw secret disclosure;
- Android client boundary: enrollment, verification, directory manifest/snapshot, provider profiles, release policy, diagnostics upload;
- admin boundary: organization, members, invites, external contacts, directory, provider profiles, app releases, diagnostics, audit;
- internal service boundary: email sender, storage adapter, database, audit writer.

Exact URLs, request/response schemas, authentication mechanism, pagination, idempotency, and error codes belong to the implementation plan or API Blueprint.

### 6.2 Data Ownership Boundaries

Control Plane stores:

- organization/workspace records and policies;
- user identities and verified email state;
- memberships and roles;
- internal/external invites and hashed token references;
- email verification challenge hashes and attempt counters;
- external contacts and external relationships;
- directory manifest/snapshot metadata and snapshot payload references;
- managed group roster source data;
- provider profile metadata and diagnostic status references;
- sanitized diagnostic evidence references;
- APK release metadata and download/storage references;
- audit events with redacted metadata.

Control Plane must not store:

- user IMAP/SMTP app passwords;
- raw AUTH payloads;
- message content as a message store;
- raw logcat or raw diagnostic logs;
- APK signing keys;
- private SSH keys;
- database passwords in repository files;
- APK binaries in git.

## 7. Core Domain Model

This section defines logical entities and key fields only. It is not a SQL schema.

### 7.1 Organization / Workspace

Fields:

- `organizationId`;
- `workspaceId` optional for future multi-workspace support;
- `displayName`;
- `status`: `active`, `suspended`, `archived`;
- `domainHints`;
- `createdAt`;
- `policies`;
- `currentDirectoryVersion`;
- `currentDirectoryHash`.

Notes:

- All directory, invite, membership, provider profile, diagnostic, external relationship, policy, and release state is scoped by `organizationId` and, where applicable, `workspaceId`.
- MVP may expose one active workspace in UI, but the data model must not block multi-workspace later.

### 7.2 User

Fields:

- `userId`;
- `email`;
- `emailVerifiedAt`;
- `displayName`;
- `createdAt`.

Notes:

- A user identity can be an internal member in one organization and an external contact relationship participant in another organization later.
- Email verification proves mailbox ownership for a given invite/organization flow; it does not automatically create membership.

### 7.3 Membership

Fields:

- `membershipId`;
- `organizationId`;
- `workspaceId` optional;
- `userId`;
- `status`: `pending`, `active`, `suspended`, `revoked`;
- `role`;
- `joinedAt`;
- `revokedAt`;
- `revokedReason` optional;
- `lastDirectorySyncAt` optional.

Notes:

- Internal invite can create pending membership, but activation requires invite validity, policy checks, and email ownership verification.
- Revoked membership is removed from active directory snapshots and managed group rosters after publish/sync.

### 7.4 Role

Allowed MVP roles:

- `owner`;
- `admin`;
- `manager`;
- `support_it`;
- `auditor`;
- `employee`.

Notes:

- Exact permission keys are TBD in implementation plan.
- Manager must not become broad admin by default.
- Support/IT access must be limited and auditable.

### 7.5 Internal Invite

Fields:

- `inviteId`;
- `organizationId`;
- `workspaceId` optional;
- `inviteType`;
- `allowedEmail`;
- `allowedDomain`;
- `tokenHash`;
- `expiresAt`;
- `maxUses`;
- `usedCount`;
- `status`;
- `createdBy`;
- `revokedAt`.

Notes:

- Raw invite token should be shown only at creation or through controlled delivery.
- Internal invite can activate `Membership`; it must not activate `ExternalRelationship`.

### 7.6 External Invite

Fields:

- `inviteId`;
- `organizationId`;
- `workspaceId` optional;
- `externalContactType`;
- `visibilityScope`;
- `assignedEmployeeId`;
- `assignedTeamId`;
- `tokenHash`;
- `allowedEmail` optional;
- `expiresAt`;
- `maxUses`;
- `status`;
- `createdBy`.

Notes:

- External invite creates or activates `ExternalRelationship`, not `Membership`.
- MVP default is one-to-one external invite.

### 7.7 EmailVerificationChallenge

Fields:

- `challengeId`;
- `organizationId`;
- `workspaceId` optional;
- `inviteId` optional;
- `userId` optional;
- `email`;
- `codeHash`;
- `expiresAt`;
- `attemptsCount`;
- `status`.

Notes:

- Store challenge code hashed where feasible.
- Verification must check `allowedEmail` or `allowedDomain` against the verified email.
- IMAP/SMTP transport diagnostics are not a substitute for this challenge.

### 7.8 ExternalContact

Fields:

- `externalContactId`;
- `organizationId`;
- `workspaceId` optional;
- `email`;
- `displayName`;
- `companyName`;
- `type`: `client`, `supplier`, `partner`, `contractor`, `other`;
- `status`;
- `createdAt`.

Notes:

- External contact is not an employee and does not appear in "All employees".
- External contact must never receive the internal corporate directory.

### 7.9 ExternalRelationship

Fields:

- `relationshipId`;
- `organizationId`;
- `workspaceId` optional;
- `externalContactId`;
- `invitedBy`;
- `assignedEmployeeId`;
- `assignedTeamId`;
- `visibilityScope`;
- `status`: `pending`, `active`, `suspended`, `revoked`, `archived`, `reassigned`;
- `activatedAt`;
- `revokedAt`;
- `reassignedAt`.

Notes:

- Relationship belongs to the organization, not personally to the inviting manager.
- Reassignment must be audited.

### 7.10 DirectoryManifest

Fields:

- `organizationId`;
- `workspaceId` optional;
- `directoryVersion`;
- `directoryHash`;
- `schemaVersion`;
- `updatedAt`;
- `stalePolicy`;
- `snapshotUrl` or `snapshotReference`.

Notes:

- Manifest is the lightweight client sync entry point.
- It must be scoped by organization/workspace.

### 7.11 DirectorySnapshot

Fields:

- `organizationId`;
- `workspaceId` optional;
- `directoryVersion`;
- `canonicalPayloadHash`;
- `internalMembers`;
- `externalContacts`;
- `managedGroups`;
- `visibleDirectoryRules`.

Notes:

- Snapshot payload must separate internal members from external contacts.
- Snapshot canonical payload rules are defined in the Directory PRD and PRD Review Addendum; exact schema is for Directory Blueprint.

### 7.12 ManagedGroup

Fields:

- `groupId`;
- `organizationId`;
- `workspaceId` optional;
- `displayName`;
- `groupType`;
- `memberIds`;
- `managedBy`;
- `status`.

Notes:

- Managed group roster authority is the current directory/control-plane snapshot.
- Historical local chat membership is not roster authority.

### 7.13 ProviderProfile

Fields:

- `providerProfileId`;
- `organizationId` optional;
- `workspaceId` optional;
- `providerFamily`;
- `displayName`;
- IMAP settings;
- SMTP settings;
- auth method;
- app password hints;
- diagnostic status;
- `lastDiagnosticReportId`.

Notes:

- Provider profiles do not store user credentials.
- Mail.ru / VK Mail is the first baseline, not a hardcoded architecture.
- Manual/custom profiles must also be organization/workspace-scoped.

### 7.14 DiagnosticEvidence

Fields:

- `reportId`;
- `organizationId`;
- `workspaceId` optional;
- `providerProfileId`;
- `networkContext`;
- `result`;
- `createdAt`;
- `sanitizedReportLocation`;
- `uploadedBy` or `source`.

Notes:

- Evidence may come from standalone diagnostics APK or later in-client diagnostics.
- Reports must be sanitized before storage or publication.

### 7.15 AppRelease

Fields:

- `releaseId`;
- `versionName`;
- `versionCode`;
- `channel`;
- `apkUrl` or `storageRef`;
- `apkSha256`;
- `sizeBytes`;
- `releaseDate`;
- `signingInfo`;
- `minSupportedVersion`;
- `forceUpgradeBelowVersion`;
- `deprecatedVersion`;
- `blockedVersion`;
- `status`.

Notes:

- APK binary is not committed to git.
- Control Plane stores release metadata and can point to GitHub Releases, backend storage, object storage, or a redirect/proxy endpoint later.

### 7.16 AuditEvent

Fields:

- `eventId`;
- `organizationId`;
- `workspaceId` optional;
- `actorId`;
- `action`;
- `targetType`;
- `targetId`;
- `timestamp`;
- `metadata` redacted/safe;
- `severity`.

Notes:

- Audit metadata must not include passwords, raw tokens, raw AUTH, private keys, or full sensitive payloads.
- Audit retention is an open decision.

## 8. State Machines

### 8.1 Membership Status

| Transition | Meaning | Required authority |
| --- | --- | --- |
| `pending -> active` | Invite, policy, and email verification passed. | Control Plane |
| `pending -> revoked` | Pending membership was canceled or invite became invalid. | Admin/Owner or policy |
| `active -> suspended` | Temporary access restriction. | Admin/Owner or policy |
| `suspended -> active` | Reactivation after review. | Admin/Owner or policy |
| `active -> revoked` | Member removed from active organization state. | Admin/Owner |
| `suspended -> revoked` | Suspended member permanently removed. | Admin/Owner |

### 8.2 ExternalRelationship Status

| Transition | Meaning | Required authority |
| --- | --- | --- |
| `pending -> active` | External invite, policy, and email verification passed. | Control Plane |
| `pending -> revoked` | External onboarding canceled or invite invalid. | Admin/Owner or policy |
| `active -> suspended` | Temporary external access restriction. | Admin/Owner or policy |
| `suspended -> active` | External access restored. | Admin/Owner or policy |
| `active -> revoked` | External relationship terminated. | Admin/Owner |
| `suspended -> revoked` | Suspended external relationship terminated. | Admin/Owner |
| `active -> archived` | Relationship no longer active but retained for history/audit. | Admin/Owner or policy |
| `active -> reassigned` | Assigned employee/team changed. | Admin/Owner or policy |

After `reassigned`, the relationship should normally continue as active with new assignment metadata. Exact persisted representation is implementation-specific.

### 8.3 Invite Status

| Status | Meaning |
| --- | --- |
| `created` | Invite record exists but token delivery may not be complete. |
| `active` | Invite can be resolved and used if constraints pass. |
| `used` | Invite was consumed successfully; for one-time invites this is terminal. |
| `expired` | `expiresAt` passed. |
| `revoked` | Admin/policy revoked invite before use or reuse. |
| `exhausted` | `usedCount` reached `maxUses`. |

### 8.4 EmailVerificationChallenge Status

| Status | Meaning |
| --- | --- |
| `created` | Challenge was generated. |
| `sent` | Code/challenge was sent to target email. |
| `verified` | Code matched and constraints passed. |
| `expired` | Challenge expired before verification. |
| `failed` | Verification failed but may still allow retry if not rate-limited. |
| `locked` / `rate_limited` | Too many attempts or abuse policy triggered. |

### 8.5 AppRelease Status

| Status | Meaning |
| --- | --- |
| `draft` | Metadata exists but is not active for clients. |
| `active` | Release is published for its channel. |
| `deprecated` | Release should show warning or upgrade guidance. |
| `blocked` | Release is not allowed by policy. |
| `rolled_back` | Release was superseded by rollback policy. |

## 9. RBAC Matrix

This matrix is product-level. Exact permission keys, database representation, middleware checks, and UI affordances are TBD in implementation plan.

Legend:

- `yes`: allowed by default;
- `no`: not allowed;
- `policy`: allowed only if organization policy grants it;
- `read-only`: can view state but cannot mutate it.

| Action | Owner | Admin | Manager | Support/IT | Auditor |
| --- | --- | --- | --- | --- | --- |
| Create organization | yes | no | no | no | no |
| Manage admins | yes | policy | no | no | read-only |
| Create internal invite | yes | yes | policy | policy | no |
| Create external invite | yes | yes | policy | policy | no |
| Approve/activate member | yes | yes | policy | policy | no |
| Suspend/revoke member | yes | yes | policy | policy | no |
| Publish directory | yes | yes | no | policy | read-only |
| Edit directory fields | yes | yes | policy | policy | read-only |
| Manage managed groups | yes | yes | policy | policy | read-only |
| Reassign external contact | yes | yes | policy | policy | read-only |
| Revoke/archive external contact | yes | yes | policy | policy | read-only |
| Change provider profiles | yes | yes | no | policy | read-only |
| Publish APK release metadata | yes | yes | no | policy | read-only |
| View diagnostics | yes | yes | policy | yes | read-only |
| Export diagnostic report | yes | yes | policy | policy | read-only |
| View audit log | yes | yes | no | policy | read-only |

RBAC rules:

- Manager must not receive broad admin privileges by default.
- Support/IT should be operationally useful but limited and auditable.
- Auditor must not mutate state.
- Any policy-based grant must produce audit-visible behavior when used for sensitive actions.

## 10. MVP Workflows

### 10.1 Organization Setup

1. Owner creates organization.
2. Control Plane creates organization/workspace record.
3. Owner or initial admin is assigned.
4. Initial provider profile policy is selected or created.
5. Initial empty directory version is prepared.
6. Initial app release metadata may be added if an APK release exists.
7. Audit events are recorded for organization creation and role assignment.

### 10.2 Internal Invite Creation

1. Admin opens invite creation.
2. Admin chooses internal invite type.
3. Admin sets `allowedEmail` or `allowedDomain`.
4. Admin sets expiry and `maxUses`.
5. Control Plane generates raw invite token once and stores `tokenHash`.
6. Admin sends invite link through the chosen channel.
7. Audit event is recorded.

### 10.3 Employee Enrollment

1. User opens join link.
2. Landing page resolves invite safely and shows employee join context.
3. User downloads APK if needed.
4. App receives invite through deep link or fallback code.
5. App resolves invite through Control Plane.
6. User enters or confirms target email.
7. Control Plane sends verification code/challenge to the email.
8. User enters verification code in app.
9. Control Plane verifies code and checks invite constraints.
10. Transport check may run to verify IMAP/SMTP readiness, but this is not ownership proof.
11. Control Plane activates membership if invite, policy, verification, and lifecycle checks pass.
12. Client becomes eligible for directory sync.
13. Audit events are recorded for invite use, verification, activation, and any failures.

### 10.4 External Invite Creation

1. Manager or admin selects "Invite external contact".
2. User selects contact type: client, supplier, partner, contractor, or other.
3. User sets name/company/email where known.
4. User selects visibility scope.
5. User selects assigned employee/team.
6. Control Plane checks RBAC and policy.
7. Control Plane creates external contact draft and/or external invite.
8. Raw invite token is generated once and `tokenHash` is stored.
9. Audit event is recorded.

### 10.5 External Contact Enrollment

1. External contact opens external invite link.
2. Landing page states that this is scoped external access, not employee membership.
3. External contact downloads APK if needed.
4. App receives external invite through deep link or fallback code.
5. App resolves external invite through Control Plane.
6. Control Plane sends verification code/challenge to expected email.
7. External contact enters verification code.
8. Control Plane verifies code and checks external invite constraints.
9. Control Plane activates `ExternalRelationship`.
10. External visible directory is generated for the allowed employee/team/contact scope.
11. Internal corporate directory is not exposed.
12. Audit events are recorded for invite use, verification, activation, and failures.

### 10.6 Directory Publish

1. Admin changes member, external contact, or managed group data.
2. Control Plane validates RBAC and policy.
3. Directory Authority builds a new canonical directory payload.
4. `directoryVersion` increments.
5. `directoryHash` is generated from canonical payload.
6. Manifest is updated with snapshot reference and stale policy.
7. Audit event is recorded.
8. Clients sync later when Control Plane is reachable.

### 10.7 Member Revoke

1. Admin revokes member.
2. Membership status changes to `revoked`.
3. Managed group rosters are updated to remove revoked member.
4. Directory version/hash changes.
5. External relationships assigned to this member require reassignment or suspension according to policy.
6. Audit event is recorded.
7. Clients enforce new state after sync; historical IMAP messages remain outside Control Plane control.

### 10.8 External Contact Reassignment

1. Admin selects external contact or external relationship.
2. Admin assigns a new employee/team.
3. Control Plane updates assignment and relationship metadata.
4. Directory visible scope updates and version/hash may change.
5. Old handler loses access after sync/enforcement.
6. External contact may see updated allowed contact/team according to product policy.
7. No internal HR reason is exposed to the external contact.
8. Audit event is recorded.

### 10.9 Provider Profile Update

1. Admin updates recommended provider profile or custom profile.
2. Control Plane validates organization/workspace scope.
3. Diagnostic status remains evidence-based and is not automatically upgraded by editing settings.
4. Clients receive update when Control Plane sync is available.
5. Audit event is recorded.

### 10.10 APK Release Metadata Publish

1. Admin creates or updates `AppRelease` metadata.
2. Metadata includes URL/storage reference, SHA-256, version, version code, channel, size, release date, signing info, and version policy thresholds.
3. APK binary remains outside git.
4. Download endpoint may redirect or proxy later, depending on release storage decision.
5. Clients check release policy when Control Plane is available or rely on cached policy while stale.
6. Audit event is recorded.

### 10.11 Diagnostic Evidence Upload

1. Standalone diagnostics APK or future client collects local evidence.
2. Report is sanitized before upload.
3. Control Plane stores report reference and metadata.
4. Report links to organization/workspace, provider profile, network context, result, and source.
5. Provider diagnostic status can be updated based on accepted evidence.
6. Raw logs, raw AUTH, credentials, and app passwords are not stored.
7. Audit event is recorded.

## 11. Email Verification Design

Email ownership proof is a product-level requirement for internal membership activation and external relationship activation.

MVP requirements:

- Control Plane sends a verification code or challenge to the target email.
- User enters code in the app or approved join flow.
- Code expires.
- Attempts are rate-limited.
- Code is stored hashed where feasible.
- Verification is tied to invite, organization/workspace, and target email.
- Verified email must satisfy `allowedEmail` or `allowedDomain` invite constraints.
- Verification success does not by itself grant membership; activation also requires invite validity, policy checks, and lifecycle state.
- IMAP/SMTP login or diagnostics can support transport readiness, but does not replace email ownership verification.
- Later automatic IMAP challenge reading requires a separate privacy/security design.

Failure cases:

- expired code;
- repeated failed attempts;
- wrong email for invite;
- invite revoked during verification;
- invite exhausted during verification;
- Control Plane unavailable before activation.

Each failure must produce safe user-facing state and audit-relevant records without exposing sensitive details.

## 12. Directory Authority Design

Control Plane is the directory source of truth.

Responsibilities:

- own `currentDirectoryVersion`;
- own `currentDirectoryHash`;
- generate `DirectoryManifest`;
- generate or reference `DirectorySnapshot`;
- separate Internal Members from External Contacts;
- publish visible directory rules per user/context;
- publish managed group roster source data;
- publish stale policy values;
- increment version/hash when membership, external relationship, visible directory, or managed group semantics change.

Canonical payload principles inherited from PRD:

- stable JSON canonicalization;
- UTF-8 encoding;
- stable object field ordering;
- stable array sorting by IDs such as `memberId`, `externalContactId`, `groupId`, and `relationshipId`;
- lowercase normalized emails;
- explicit null/empty normalization;
- include `organizationId` and/or `workspaceId`;
- include only fields that affect directory semantics;
- include member status, external contact status, visibility scope, managed group membership, assigned owner/team, and directory-visible profile fields;
- exclude volatile fields such as `generatedAt`, server time, request ID, pagination cursors, transient sync metadata, and retry counters;
- SHA-256 unless a later compliance decision changes it.

Boundaries for next Directory Blueprint:

- exact snapshot payload schema;
- visible directory algorithm;
- group roster semantics;
- stale vs expired thresholds;
- hash validation procedure;
- client sync protocol.

## 13. Stale Control Plane / Cached Directory Behavior

Control Plane may be unavailable in whitelist or restricted-network mode. Provider IMAP/SMTP transport may still work.

Control Plane must publish policy values:

- `lastControlPlaneSyncAt` as client-observed state;
- `lastDirectorySyncAt` as client-observed state;
- `directoryStaleAfter`;
- `directoryExpiredAfter`;
- `staleDirectoryWarning`;
- `stalePolicyWarning`;
- managed group stale-send behavior.

Allowed behavior while state is stale:

- messaging known active contacts may continue if local policy allows;
- cached provider profile may be used if policy allows;
- app release update check may wait unless cached policy blocks current version;
- diagnostics may run locally and upload later.

Restricted behavior while state is stale:

- accepting new internal invite requires Control Plane availability;
- accepting new external invite requires Control Plane availability;
- applying revoke/suspend/reassign requires sync;
- starting a new managed group from stale roster should be blocked or policy-gated;
- sending to a managed group must use current active roster or block/warn when stale beyond threshold.

Control Plane must not pretend to enforce immediate revoke while the client is offline or unable to sync. It must define state and publish updates; clients apply them after sync.

## 14. Provider Profiles And Diagnostics Evidence

Provider profiles define organization/workspace-scoped transport guidance. They do not implement IMAP/SMTP and do not store user credentials.

MVP provider profile requirements:

- Mail.ru / VK Mail baseline profile family;
- manual/custom profile support;
- IMAP host/port/security settings;
- SMTP host/port/security settings;
- auth method description;
- app password hints;
- diagnostic status;
- last diagnostic evidence reference;
- organization/workspace scope.

Diagnostic status values:

- `untested`;
- `wifi_verified`;
- `normal_mobile_verified`;
- `whitelist_verified`;
- `failed`;
- `degraded`.

Diagnostic evidence requirements:

- evidence is linked to provider profile and network context;
- evidence can be collected while Control Plane is unavailable and uploaded later;
- evidence must be sanitized;
- raw logs, raw AUTH, app passwords, and private data must not be stored;
- local diagnostic pass does not activate membership or external relationship.

## 15. APK Release And Distribution

Control Plane stores release metadata, not APK binaries in git.

MVP release metadata:

- `versionName`;
- `versionCode`;
- `channel`: `internal`, `beta`, `stable`;
- APK URL or storage reference;
- SHA-256;
- size;
- release date;
- signing info;
- release notes reference;
- `minSupportedVersion`;
- `forceUpgradeBelowVersion`;
- `deprecatedVersion`;
- `blockedVersion`;
- status.

Distribution assumptions:

- APK may live in GitHub Releases, controlled backend storage, object storage, or a future download endpoint that redirects/proxies.
- Backend download route may be `https://messenger-imap.speechbattle.com/download/android/latest`.
- APK-by-email is an Android emergency fallback only.
- APK-by-email does not grant trust, membership, or activation.
- iOS is out of current scope.
- APK signing keys must not be stored in repo, documentation, compose files, `.env`, Traefik labels, or on the deploy host by default.

## 16. Audit Log

Audit events are required for sensitive product state changes and security-relevant failures.

Events to record:

- organization creation;
- role changes;
- internal invite created, revoked, used, failed, expired, exhausted;
- external invite created, revoked, used, failed, expired, exhausted;
- email verification sent, verified, failed, expired, rate-limited;
- membership activated, suspended, revoked;
- directory published;
- managed group changed;
- external contact activated, reassigned, suspended, revoked, archived;
- provider profile changed;
- diagnostic evidence uploaded or accepted;
- app release metadata created, changed, deprecated, blocked, rolled back.

Requirements:

- include actor, action, target, timestamp, severity, and organization/workspace scope;
- metadata must be redacted and safe;
- no secrets, raw tokens, raw AUTH, app passwords, private keys, or raw logs in audit metadata;
- auditor/read-only views should be supported later;
- audit retention is an open decision.

## 17. Security And Privacy

Control Plane security requirements:

- no user app passwords in Control Plane;
- no raw diagnostic logs;
- no raw AUTH payloads;
- no APK signing keys;
- no database passwords in repo;
- no SSH private keys in repo;
- invite token hashes instead of raw token storage where feasible;
- verification code hashes instead of plaintext storage where feasible;
- invite token and fallback code rate limiting;
- email verification attempt rate limiting;
- failed attempt audit;
- external contacts must never receive internal directory;
- sensitive admin actions must be audited;
- support/IT access must be limited and auditable;
- diagnostic report storage must be sanitized and scoped.

Privacy boundaries:

- external users see only allowed contacts/team/relationship context;
- reassignment must not expose internal HR reasons;
- revocation cannot erase previously delivered IMAP messages or information already seen outside current product state;
- imported contacts are not cryptographically verified by default;
- SecureJoin or equivalent verification indicators require separate product/technical design.

## 18. Infrastructure Assumptions

Infrastructure facts and constraints inherited from current docs:

- public domain: `messenger-imap.speechbattle.com`;
- public IP: `146.19.211.30`;
- internal deploy host: `192.168.7.64`;
- SSH context: `roman@192.168.7.64`, no keys or secrets in repo;
- Traefik already exists on the server;
- existing services must not be disrupted;
- candidate path `/opt/stacks/messenger-imap` is not an approved deployment path;
- candidate Docker network `traefik-net` is not automatically approved;
- future Control Plane should use its own database/container/volume by default;
- do not reuse existing `postgres-dev` without explicit architecture and data-isolation decision;
- no direct host port exposure for web/API/database by default;
- any direct host port exposure requires Deployment Blueprint justification;
- real secrets must stay outside git;
- no deployment happens in this Blueprint.

Expected future public routes, subject to Deployment Blueprint:

```text
https://messenger-imap.speechbattle.com/
https://messenger-imap.speechbattle.com/admin
https://messenger-imap.speechbattle.com/join/{inviteToken}
https://messenger-imap.speechbattle.com/download/android/latest
https://messenger-imap.speechbattle.com/api/...
```

Deployment remains blocked until a separate Deployment Blueprint defines stack path, network plan, Traefik labels, database, storage, secrets, backup, and rollback.

## 19. MVP Implementation Boundaries

Later implementation planning may include:

- Control Plane skeleton;
- admin authentication/session;
- organization/workspace model;
- user/membership model;
- RBAC enforcement points;
- internal invite model;
- external invite and external relationship model;
- email verification challenge flow;
- directory manifest/snapshot ownership;
- provider profile metadata;
- diagnostics evidence reference/upload;
- APK release metadata;
- audit log.

Do not include yet:

- Android client implementation;
- deployment;
- Docker Compose;
- Traefik changes;
- signed IMAP/system-account directory fallback;
- background reliability work;
- full CRM/helpdesk;
- full external project rooms;
- iOS support;
- APK signing pipeline;
- Delta Chat fork changes;
- chatmail/core changes.

## 20. Open Questions

- Backend stack for Control Plane.
- Database choice.
- Exact stale and expired thresholds.
- Exact RBAC permission keys and policy model.
- Exact email verification delivery provider.
- Whether invite emails are sent by Control Plane or manually by admins in MVP.
- Release storage choice: GitHub Releases, backend storage, object storage, redirect/proxy.
- APK signing flow and signing metadata publication.
- One active workspace UI vs multi-workspace UI in MVP.
- Manager default permission for internal invites.
- Manager default permission for external invites.
- External invite default visibility scope.
- Whether admin approval is required for external relationships in MVP.
- Diagnostic evidence retention.
- Audit retention.
- External relationship reassignment chat/history behavior.
- Whether domain invites are MVP or later.
- GPL/MPL compliance path for future Android distribution.
- Android fork integration assumptions after accepted thin-fork decision.

## 21. Acceptance Criteria For This Blueprint

This Blueprint is accepted when:

- it uses existing PRDs, roadmap, research, and infrastructure docs;
- it defines Control Plane MVP scope clearly;
- it excludes message server role clearly;
- it defines core domain entities;
- it defines state machines;
- it defines a product-level RBAC matrix;
- it defines internal and external invite workflows;
- it includes email verification;
- it includes directory authority;
- it includes stale Control Plane and cached directory behavior;
- it includes provider profile and diagnostics evidence boundaries;
- it includes APK release metadata and distribution assumptions;
- it includes audit log requirements;
- it includes security and privacy requirements;
- it includes infrastructure assumptions without deployment;
- it lists open questions;
- it does not write code, deploy, change Traefik, change Android prototype, or modify upstream Delta Chat / Chatmail.
