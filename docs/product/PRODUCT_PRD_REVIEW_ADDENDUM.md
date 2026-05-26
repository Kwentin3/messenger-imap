# Product PRD Review Addendum

Date: 2026-05-26

Status: Product review refinement before technical Blueprints

## 1. Executive Summary

The current PRD package is accepted as the baseline product model for Corporate IMAP Messenger.

Before writing technical Blueprints, the package must explicitly refine several product areas:

- Control Plane availability in mobile whitelist and restricted-network modes;
- stale directory and stale policy behavior;
- Android installation under limited internet;
- email ownership verification;
- multi-organization / workspace scoping;
- managed group enforcement after member revocation;
- trust and identity states;
- Control Plane RBAC;
- canonical directory hash rules;
- invite abuse controls;
- external contact reassignment UX;
- app release lifecycle policy.

These refinements are product-level requirements. They are not implementation specifications and should not be read as final protocol, database, UI, cryptographic, or deployment design.

## 2. Control Plane Availability And Stale Directory Mode

The Control Plane may be unavailable in mobile whitelist or restricted-network mode. This must be treated as an expected operating condition, not as an edge case.

If provider IMAP/SMTP transport remains available, message exchange can continue even when Control Plane sync is delayed. However, directory updates, policy updates, invite activation, revocation, release metadata, audit upload, diagnostic upload, and external contact lifecycle changes depend on Control Plane availability unless a later signed fallback channel is implemented.

Product concepts:

| Concept | Meaning |
| --- | --- |
| `lastControlPlaneSyncAt` | Last successful client sync with the Control Plane |
| `lastDirectorySyncAt` | Last successful directory snapshot sync |
| `directoryStaleAfter` | Age after which cached directory should be marked stale |
| `directoryExpiredAfter` | Age after which sensitive directory-dependent actions should be blocked or require policy override |
| `staleDirectoryWarning` | User-visible warning that directory data may be outdated |
| `stalePolicyWarning` | User/admin-visible warning that policy state may be outdated |

Allowed behavior while directory/control-plane state is stale:

| Behavior | Requirement |
| --- | --- |
| Messaging known active contacts | May continue if transport works and local policy does not block it |
| Starting a new managed group | Should be restricted or require fresh directory state |
| Sending to an existing managed group | Must use current active roster if available; if stale beyond policy threshold, block or warn according to policy |
| Accepting a new internal invite | Requires Control Plane availability |
| Accepting a new external invite | Requires Control Plane availability |
| Applying member revoke/suspend | Requires sync; until then the client may have stale local state |
| Applying external contact revoke/reassign | Requires sync; until then the client may have stale local state |
| App release update check | Can wait until Control Plane is available unless version policy is already cached |
| Diagnostics collection | May run locally; upload can be delayed |

Future fallback:

- signed directory and policy updates may be distributed through an IMAP/SMTP system account;
- system-account update distribution is later scope unless explicitly selected for MVP;
- any automatic fallback update must be signed, versioned, replay-protected, and scoped by organization/workspace before the client applies it.

HTTPS Control Plane sync remains the primary MVP path.

## 3. Client Installation And Distribution Under Limited Internet

Normal internet access is usually required for the Android APK download and install flow.

APK-by-email is an acceptable emergency Android distribution fallback when users can receive mail but cannot access the normal download channel. It is not the primary product distribution path and must not replace release metadata, signing, version policy, or safe onboarding.

Rules:

- APK-by-email applies only to Android.
- APK-by-email must be treated as emergency or controlled fallback distribution.
- APK-by-email does not imply membership, trust, or successful enrollment.
- The client must still complete invite, email verification, provider setup, and policy checks.
- iPhone/iOS support is out of current scope.
- iOS would require a separate App Store, TestFlight, MDM-like, or enterprise distribution path later.

## 4. Email Ownership Proof

Membership activation and external relationship activation require product-level proof of mailbox ownership.

Preferred product mechanism:

1. Control Plane sends a verification code or challenge to the target email address.
2. User enters the code in the app.
3. Control Plane verifies that the code matches the invite and expected email constraints.
4. Membership or external relationship activation can proceed only after verification and policy checks.

Later option:

- the app may read and validate an email challenge through configured IMAP if designed securely;
- this requires explicit privacy, permission, and security design;
- it must not create a requirement to scan unrelated mailbox content.

Important distinctions:

- IMAP/SMTP login success is transport readiness evidence.
- Email verification code is product-level mailbox ownership proof.
- Email verified does not equal active membership.
- Invite token present does not equal active membership.
- `allowedEmail` and `allowedDomain` constraints must be checked against the verified email address.

## 5. Multi-Organization / Workspace Model

The product must not assume that one user belongs to exactly one global organization.

A user may be:

- an internal member of one organization;
- an external contact of another organization;
- a participant in multiple workspaces later.

Principles:

- all organization-scoped objects must carry `organizationId` and/or `workspaceId`;
- directory snapshots are scoped by organization/workspace;
- membership state is scoped by organization/workspace;
- external relationships are scoped by organization/workspace;
- invite state is scoped by organization/workspace;
- provider profile policy is scoped by organization/workspace;
- diagnostics and diagnostic status are scoped by organization/workspace and network context;
- audit events are scoped by organization/workspace.

Open MVP decision:

- MVP may support one active workspace in UI if implementation scope requires;
- the data model and PRDs must remain conceptually multi-workspace and must not block future multi-workspace UI.

## 6. Managed Group Enforcement After Revoke

Managed groups must use the latest active roster from the current directory/control-plane state.

Definitions:

| Concept | Meaning |
| --- | --- |
| Managed group | Organization-controlled group whose roster is derived from Control Plane / Directory |
| Ordinary chat | User-created or historical chat that is not treated as a managed roster authority |
| Historical local group | Local chat state that may still show old participants from prior messages or prior group membership |

Rules:

- revoked or suspended members are removed from managed rosters after sync;
- historical messages remain visible according to retention policy, but do not imply active membership;
- sending to a managed group must use the current active roster, not stale historical local membership;
- if the roster is stale beyond policy threshold, new managed sends should be blocked or require explicit policy override;
- if a historical chat includes a revoked member, the client should show a warning;
- new managed sends to a revoked member must be blocked or require explicit non-managed/manual override according to policy;
- Blueprint must define how local chat UI distinguishes managed roster from historical participants.

## 7. Trust And Identity State Model

Trust must be explicit. Installation, invite possession, email verification, directory presence, and cryptographic verification are different states.

| State | Meaning | Source | UI implication | Can receive internal directory? |
| --- | --- | --- | --- | --- |
| `app_installed` | User has installed the client | Android package installation | No organization trust implied | No |
| `invite_present` | User has an invite token or fallback code | Invite link, QR, code entry | Show invite flow, not membership | No |
| `email_ownership_verified` | User proved access to the mailbox | Control Plane challenge/code | Can proceed to activation checks | No by itself |
| `pending_internal_member` | Internal invite accepted but activation not complete | Control Plane | Limited onboarding state | No full directory until activation |
| `active_internal_member` | Active employee/member of the organization | Control Plane membership | Full allowed employee UX | Yes, according to policy |
| `suspended_internal_member` | Temporarily disabled member | Control Plane membership status | Block or restrict activity | No fresh directory |
| `revoked_internal_member` | Removed member | Control Plane membership status | Block organization access after sync | No |
| `external_contact_pending` | External relationship invite not fully activated | Control Plane external relationship | Limited external onboarding | No |
| `external_contact_active` | Active external relationship | Control Plane external relationship | Scoped guest/contact UX | No internal directory; only allowed contacts |
| `external_contact_revoked` | External relationship terminated | Control Plane external relationship | Block external relationship after sync | No |
| `directory_contact_imported` | Contact exists from import or local discovery | Local import or user action | Mark as unverified/imported | No |
| `securejoin_verified` | Contact cryptographically verified if SecureJoin or equivalent is used | Cryptographic verification flow | Higher trust indicator | Depends on membership/contact state |

Important rules:

- imported contact is not cryptographically verified by default;
- email verified does not equal active membership;
- invite token does not equal membership;
- external active does not equal internal membership;
- directory active does not necessarily mean cryptographic identity verification;
- any SecureJoin or equivalent verification model must be documented separately before it becomes a product requirement.

## 8. Control Plane RBAC Matrix

This is a product-level matrix. Exact permission names and enforcement points belong to later Blueprints.

| Action | Owner | Admin | Manager | Support/IT | Auditor |
| --- | --- | --- | --- | --- | --- |
| Create organization | yes | no | no | no | no |
| Manage admins | yes | policy | no | no | read-only |
| Create internal invite | yes | yes | policy | policy | no |
| Create external invite | yes | yes | policy | policy | no |
| Approve member | yes | yes | policy | policy | no |
| Suspend/revoke member | yes | yes | policy | policy | no |
| Publish directory | yes | yes | no | policy | read-only |
| Edit directory fields | yes | yes | policy | policy | read-only |
| Manage managed groups | yes | yes | policy | policy | read-only |
| Reassign external contact | yes | yes | policy | policy | read-only |
| Revoke/archive external contact | yes | yes | policy | policy | read-only |
| Change provider profiles | yes | yes | no | policy | read-only |
| Publish APK release | yes | yes | no | policy | read-only |
| View diagnostics | yes | yes | policy | yes | read-only |
| Export diagnostic report | yes | yes | policy | policy | read-only |
| View audit log | yes | yes | no | policy | read-only |

Legend:

- `yes`: role can perform the action by default;
- `no`: role cannot perform the action;
- `policy`: organization policy may allow or restrict the action;
- `read-only`: role may view state but cannot mutate it.

## 9. Canonical Directory Hash Rules

Directory hash must be deterministic and scoped.

Principles:

- use stable JSON canonicalization;
- encode canonical payload as UTF-8;
- sort arrays by stable IDs such as `memberId`, `externalContactId`, `groupId`, and `relationshipId`;
- use stable object field ordering;
- lowercase and normalize emails before hashing;
- normalize explicit `null`, empty arrays, and empty strings according to a documented rule;
- include `organizationId` and/or `workspaceId`;
- include only fields that affect directory semantics;
- include member status, external contact status, visibility scope, group membership, assigned owner/team, and directory-relevant profile fields;
- exclude volatile fields such as `generatedAt`, server time, request ID, pagination cursors, transient sync metadata, and transport retry counters;
- hash algorithm: SHA-256 unless later compliance requirements select another algorithm.

Blueprint must define the exact canonical payload schema before clients enforce hash validation.

## 10. Invite Abuse And Safety Requirements

Invite links and fallback codes are sensitive artifacts.

Abuse and failure scenarios:

- invite token forwarding;
- external invite forwarding;
- wrong email using an invite;
- expired invite replay;
- invite screenshot leak;
- repeated failed attempts;
- brute-force fallback code attempts;
- external invite sent to the wrong person;
- revoked employee's active invites remaining usable.

Requirements:

- invites must have expiry;
- invites must have `maxUses` where appropriate;
- individual invites should bind to `allowedEmail` where possible;
- domain invites must bind to `allowedDomain`;
- email ownership verification must check the verified email against invite constraints;
- failed attempts must be audit logged;
- repeated failed attempts must be rate limited;
- admins must be able to revoke an invite;
- admins must be able to revoke all active invites by issuer if needed;
- suspicious invite activity should be visible to admin/support, with alerting as later scope.

## 11. External Contact Reassignment UX

External relationships belong to the organization, not personally to the manager who invited or handled the contact.

Rules:

- assigned employee/team can change;
- when a manager leaves, admin can reassign the external contact to another employee or team;
- old manager loses access after sync/enforcement;
- external contact sees the new assigned contact/person/team where product policy allows;
- external contact must not see internal HR detail or the reason for reassignment unless explicitly communicated by the organization;
- old chat may become historical, unavailable, or show a transfer notice;
- Blueprint must define exact chat/history behavior.

## 12. App Release Lifecycle

APK release metadata must support lifecycle control.

Product fields:

| Field | Meaning |
| --- | --- |
| `appReleaseVersion` | Published app version |
| `minSupportedVersion` | Oldest version allowed to continue normal use |
| `forceUpgradeBelowVersion` | Version threshold that requires upgrade before continuing |
| `deprecatedVersion` | Version threshold that should show warning |
| `blockedVersion` | Version threshold that is blocked by policy |
| `channel` | Release channel: `internal`, `beta`, or `stable` |
| `apkSha256` | SHA-256 of the APK artifact |
| `releaseDate` | Release publication date |
| `signingInfo` | Signing certificate/signature note or reference |
| `rollbackStatus` | Whether rollback is allowed, active, or blocked |

Behavior:

- when Control Plane is available, the app should warn or block according to cached or fetched version policy;
- if Control Plane is unavailable, the client may rely on last cached release policy;
- APK-by-email emergency distribution must still provide version, SHA-256, and signing expectations through a trusted channel;
- iOS release lifecycle is out of current scope and must be specified separately later.

## 13. Summary Of Required PRD Updates

Required files to update:

- `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md`;
- `docs/product/domains/PRD_CORPORATE_DIRECTORY.md`;
- `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md`;
- `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md`;
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`;
- `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md`;
- `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md`;
- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`;
- `docs/product/decisions/PRODUCT_DECISIONS_LOG.md`;
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`;
- `docs/product/PRODUCT_PRD_PACKAGE_REPORT.md`;
- `docs/product/PRODUCT_PRD_REFINEMENT_REPORT.md`.
