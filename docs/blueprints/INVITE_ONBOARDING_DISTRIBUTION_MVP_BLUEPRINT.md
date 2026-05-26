# Invite Onboarding & Distribution MVP Blueprint

Date: 2026-05-26

Status: Draft

Scope: MVP Blueprint

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Source documents:

- [Project Roadmap](../roadmap/PROJECT_ROADMAP.md)
- [Corporate Control Plane MVP Blueprint](CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md)
- [Corporate Directory MVP Blueprint](CORPORATE_DIRECTORY_MVP_BLUEPRINT.md)
- [Invite Onboarding & Distribution PRD](../product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md)
- [Corporate IMAP Messenger Root PRD](../product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Product PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Corporate Control Plane PRD](../product/domains/PRD_CORPORATE_CONTROL_PLANE.md)
- [Corporate Directory PRD](../product/domains/PRD_CORPORATE_DIRECTORY.md)
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

Invite Onboarding & Distribution is the MVP bridge between a downloadable Android APK and trusted organization access. It defines how a user receives an invite, reaches a join landing page, installs or opens the app, passes invite resolution, proves mailbox ownership, configures provider transport, runs required diagnostics, activates membership or an external relationship, and completes first directory sync.

This Blueprint follows the Control Plane and Directory Blueprints because onboarding depends on their authority models:

- Control Plane owns invites, email verification, membership activation, external relationship activation, release metadata, provider profile policy, audit, and stale-mode behavior.
- Directory owns first visible directory sync after activation.

APK download is not membership. An invite token is not membership. Email verification is not membership by itself. IMAP/SMTP login or diagnostics prove transport readiness, not product-level ownership or organization access. Activation requires Control Plane availability in MVP.

The Blueprint defines MVP domain boundaries, logical components, core entities, invite and enrollment state machines, internal/external workflows, landing page behavior, Android handoff, email verification, provider setup, diagnostics gate, first directory sync, stale/unavailable behavior, abuse controls, audit, security/privacy, and MVP boundaries. It does not define OpenAPI, SQL migrations, Android UI implementation, Docker Compose, deployment, or Delta Chat / chatmail/core changes.

## 2. Source Documents And Inherited Decisions

| Source document | Decisions inherited |
| --- | --- |
| [Project Roadmap](../roadmap/PROJECT_ROADMAP.md) | Invite Blueprint follows Control Plane and Directory; Android Client Blueprint remains next. |
| [Control Plane Blueprint](CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md) | Control Plane owns invite records, token hashes, email verification challenges, activation, release metadata, audit, provider profile policy, and stale mode. |
| [Directory Blueprint](CORPORATE_DIRECTORY_MVP_BLUEPRINT.md) | First directory sync follows activation; external contacts receive scoped visible directory only. |
| [Invite PRD](../product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md) | Internal invite creates membership; external invite creates external relationship; landing page, APK download, app link, fallback code, QR, provider setup, diagnostics, and activation are part of assisted onboarding. |
| [Root PRD](../product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md) | APK download is not membership; product must be Android-first, provider-agnostic, and Control Plane-backed. |
| [PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md) | Email ownership proof uses verification code; Control Plane may be unavailable; invite activation is delayed without Control Plane. |
| [Control Plane PRD](../product/domains/PRD_CORPORATE_CONTROL_PLANE.md) | Admins manage invites, constraints, external invites, email verification, release metadata, and audit. |
| [Corporate Directory PRD](../product/domains/PRD_CORPORATE_DIRECTORY.md) | Directory access begins after activation and sync; external contacts do not receive internal directory. |
| [External Contacts PRD](../product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md) | One-to-one external invite is MVP; external invite creates `ExternalRelationship`, not `Membership`. |
| [Android Client PRD](../product/domains/PRD_ANDROID_MESSENGER_CLIENT.md) | App handles invite token/deep link/fallback code, provider setup, transport check, first directory sync, and external invite wording. |
| [Provider Profiles PRD](../product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md) | Provider profiles are provider-agnostic and separate from credentials; diagnostics are evidence-based. |
| [Diagnostics PRD](../product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md) | Diagnostics can support onboarding readiness but do not activate membership or external relationships. |
| [Decisions Log](../product/decisions/PRODUCT_DECISIONS_LOG.md) | Accepted decisions include invite/enrollment required, email verification, invite typing, stale Control Plane, and no internal directory exposure. |
| [Product Context Handoff](../product/handoff/PRODUCT_CONTEXT_HANDOFF.md) | Do not treat all invite links as employee invites; do not use diagnostics as email ownership proof. |
| [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md) | Future route map includes `/join/{inviteToken}` and `/download/android/latest`; no deployment in this Blueprint. |
| [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md) | Existing Traefik/server work is deployment scope, not onboarding Blueprint scope. |
| [Delta Chat / Chatmail Research](../research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md) | Delta Chat has QR/contact/group/provider primitives; product onboarding must be added at app/control-plane layer. |
| [Delta Chat Feature Map](../hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md) | Existing invite/contact primitives can be reused where safe, but corporate membership and directory authority are product-layer models. |

Inherited decisions:

- APK download does not equal membership.
- Invite/enrollment is mandatory for membership activation.
- Internal invite creates `Membership`.
- External invite creates `ExternalRelationship`.
- Email ownership verification is required.
- Verification code/challenge is product-level proof of mailbox ownership.
- Provider diagnostics do not replace email verification.
- Control Plane availability is required for activation in MVP.
- Control Plane may be unavailable in whitelist/restricted mode.
- App may install and provider setup/diagnostics may proceed while activation is pending.
- External contacts must not receive internal corporate directory.
- Invite tokens are sensitive.
- Invite links must not contain credentials or app passwords.
- APK-by-email is Android emergency fallback only.
- iOS is out of current scope.

## 3. Goals

MVP Invite Onboarding & Distribution must:

- define internal invite and external invite boundaries;
- define invite token/fallback code handling;
- define join landing page responsibilities;
- define Android app link/deep link handoff;
- define fallback invite code and optional QR behavior;
- define APK download and release metadata handoff;
- define email verification code flow;
- define provider profile selection during onboarding;
- define transport diagnostics gate;
- define membership activation;
- define external relationship activation;
- define first directory sync after activation;
- define Control Plane unavailable/stale behavior;
- define invite abuse controls;
- define audit events;
- define security/privacy constraints;
- define MVP scope and later scope.

## 4. Non-Goals

This Blueprint does not include:

- OpenAPI specification;
- SQL migrations or physical database schema;
- Android UI implementation;
- Android app link asset hosting implementation;
- Docker Compose;
- deployment;
- Traefik changes;
- APK signing pipeline;
- APK binary storage in git;
- iOS distribution;
- full MDM;
- app store/TestFlight flow;
- full CRM/helpdesk;
- external organization invite implementation;
- team/project external invite implementation;
- signed IMAP/system-account onboarding fallback;
- Delta Chat Android fork changes;
- chatmail/core changes.

## 5. System Context

Conceptual context:

```text
Admin / Manager
  -> Control Plane Admin UI
  -> create internal/external invite
  -> deliver invite link or fallback code

Invite recipient
  -> Join Landing Page
  -> APK download / open installed app
  -> Android onboarding flow
  -> Control Plane invite resolution
  -> Email verification challenge
  -> Provider profile setup
  -> Diagnostics gate where required
  -> Activation
  -> First visible directory sync

Android Client
  <-> Control Plane
      invite resolution, verification, activation, directory, release policy

Android Client
  <-> IMAP/SMTP Provider
      message transport and diagnostics only
```

MVP routes expected by infrastructure assumptions, not implemented here:

```text
https://messenger-imap.speechbattle.com/join/{inviteToken}
https://messenger-imap.speechbattle.com/download/android/latest
https://messenger-imap.speechbattle.com/api/...
```

## 6. Domain Ownership And Boundaries

| Domain | Owns | Does not own |
| --- | --- | --- |
| Control Plane | Invite records, token hashes, constraints, email verification, activation, release metadata, audit. | IMAP/SMTP transport, Android UI implementation, APK signing. |
| Invite Onboarding | Flow contract from invite creation through activation and first sync. | Membership source of truth, directory payload internals, transport protocol internals. |
| Directory | First visible directory sync after activation and scoped directory visibility. | Invite token validation, email challenge delivery. |
| Android Client | User-facing onboarding steps, token entry, provider setup, diagnostics invocation, local state display. | Product authority for membership/external relationship. |
| Provider Profiles | Provider recommendations, settings, diagnostic status. | User credentials storage in Control Plane. |
| Diagnostics | Transport evidence and troubleshooting. | Email ownership proof or membership activation. |
| Deployment | Routes, Traefik, storage, TLS, env/secrets. | Product flow decisions in this Blueprint. |

## 7. Core Entities

This section defines logical entities and key fields only.

### 7.1 InternalInvite

Fields:

- `inviteId`;
- `organizationId`;
- `workspaceId` optional;
- `inviteType`: `individual_email`, `one_time`, `domain`, `limited_group`;
- `allowedEmail` optional;
- `allowedDomain` optional;
- `tokenHash`;
- `fallbackCodeHash` optional;
- `expiresAt`;
- `maxUses`;
- `usedCount`;
- `status`;
- `createdBy`;
- `createdAt`;
- `revokedAt` optional;
- `revokedBy` optional.

MVP:

- individual email invite;
- one-time invite if feasible;
- domain and limited group invite deferred unless explicitly selected.

### 7.2 ExternalInvite

Fields:

- `inviteId`;
- `organizationId`;
- `workspaceId` optional;
- `externalContactType`;
- `visibilityScope`;
- `assignedEmployeeId`;
- `assignedTeamId` optional;
- `allowedEmail` optional;
- `tokenHash`;
- `fallbackCodeHash` optional;
- `expiresAt`;
- `maxUses`;
- `usedCount`;
- `status`;
- `createdBy`;
- `createdAt`;
- `revokedAt` optional.

MVP:

- one-to-one external contact invite;
- default visibility `inviter_only` or assigned employee;
- team/project/external organization invites later.

### 7.3 InviteToken

Fields:

- raw token only shown/delivered at creation time;
- `tokenHash`;
- `tokenType`: `internal` or `external`;
- `inviteId`;
- `organizationId`;
- `workspaceId` optional;
- `expiresAt`;
- `createdAt`.

Rules:

- raw token is sensitive;
- store token hashes where feasible;
- do not log raw token;
- do not put credentials or app passwords in invite links.

### 7.4 FallbackInviteCode

Fields:

- raw fallback code shown only when policy allows;
- `fallbackCodeHash`;
- `inviteId`;
- `expiresAt`;
- `attemptsCount`;
- `status`.

Purpose:

- manual code entry when deep link/app link handoff fails;
- support-assisted enrollment;
- QR code may encode invite link or fallback code according to policy.

### 7.5 InviteLandingContext

Fields:

- `inviteId`;
- `organizationDisplayName`;
- `inviteKind`: `internal_employee` or `external_contact`;
- `inviteStatus`;
- `releaseChannel`;
- `latestAndroidReleaseRef`;
- `fallbackCodeAllowed`;
- `qrAllowed`;
- `supportContact` optional;
- safe user-facing message.

Rules:

- landing context must not expose hidden invite constraints unnecessarily;
- landing page must distinguish employee join from external contact access;
- landing page must not imply APK download grants membership.

### 7.6 EnrollmentSession

Fields:

- `enrollmentSessionId`;
- `organizationId`;
- `workspaceId` optional;
- `inviteId`;
- `inviteKind`;
- `principalEmail`;
- `userId` optional;
- `membershipId` optional;
- `externalContactId` optional;
- `externalRelationshipId` optional;
- `status`;
- `startedAt`;
- `lastUpdatedAt`;
- `expiresAt`.

Purpose:

- tie together invite resolution, email verification, provider setup, diagnostics gate, activation, and first sync.

### 7.7 EmailVerificationChallenge

Fields:

- `challengeId`;
- `organizationId`;
- `workspaceId` optional;
- `inviteId`;
- `enrollmentSessionId`;
- `email`;
- `codeHash`;
- `expiresAt`;
- `attemptsCount`;
- `status`.

Rules:

- verification code is sent to target email;
- code is stored hashed where feasible;
- verified email must satisfy `allowedEmail` or `allowedDomain`;
- later IMAP challenge reading requires separate security design.

### 7.8 ProviderSetupContext

Fields:

- `organizationId`;
- `workspaceId` optional;
- `providerProfileId`;
- `providerFamily`;
- `displayName`;
- IMAP settings reference;
- SMTP settings reference;
- auth method guidance;
- diagnostic status reference;
- app password hint text.

Rules:

- contains no user credentials;
- Mail.ru / VK Mail baseline is supported;
- custom/manual provider setup remains possible where policy allows.

### 7.9 DiagnosticGate

Fields:

- `providerProfileId`;
- `networkContext`;
- `requiredForActivation`;
- `acceptedStatuses`;
- `lastDiagnosticReportId` optional;
- `result`: `not_run`, `passed`, `failed`, `degraded`, `skipped_by_policy`.

Rules:

- diagnostics can be required by policy;
- local diagnostic pass does not prove email ownership;
- diagnostic evidence upload can be delayed if Control Plane is unavailable.

### 7.10 ActivationResult

Fields:

- `activationType`: `membership` or `external_relationship`;
- `organizationId`;
- `workspaceId` optional;
- `membershipId` optional;
- `externalRelationshipId` optional;
- `status`: `activated`, `pending`, `blocked`, `failed`;
- `reasonCode` optional;
- `firstDirectorySyncRequired`;
- `auditEventId`.

### 7.11 AppReleaseRef

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
- release notes reference.

Rules:

- APK binary is not committed to git;
- APK-by-email is emergency fallback only;
- release metadata still matters even if APK is delivered by email.

## 8. State Machines

### 8.1 Invite Status

| Status | Meaning |
| --- | --- |
| `created` | Invite record exists but may not yet be delivered. |
| `active` | Invite can be resolved and used if constraints pass. |
| `used` | Invite was successfully consumed; terminal for one-time invites. |
| `expired` | Invite is past `expiresAt`. |
| `revoked` | Admin/policy revoked invite. |
| `exhausted` | `usedCount` reached `maxUses`. |

### 8.2 EnrollmentSession Status

| Status | Meaning |
| --- | --- |
| `started` | User opened link or entered code. |
| `invite_resolved` | Control Plane resolved invite and constraints enough to continue. |
| `email_challenge_sent` | Verification code was sent. |
| `email_verified` | Mailbox ownership was proven for this flow. |
| `provider_setup_started` | User started provider setup. |
| `provider_ready` | Provider configuration is locally ready enough to continue. |
| `diagnostics_passed` | Required diagnostics passed or policy allowed continuation. |
| `activation_pending` | Waiting for Control Plane availability or policy approval. |
| `activated` | Membership or external relationship activated. |
| `directory_synced` | First visible directory sync completed. |
| `failed` | Terminal failure requiring restart/support. |
| `abandoned` | Session expired or user stopped. |

### 8.3 EmailVerificationChallenge Status

| Status | Meaning |
| --- | --- |
| `created` | Challenge generated. |
| `sent` | Code sent to email. |
| `verified` | Code accepted and constraints passed. |
| `expired` | Challenge expired. |
| `failed` | Attempt failed but may allow retry. |
| `rate_limited` | Too many attempts or abuse policy triggered. |

### 8.4 Activation Status

| Status | Meaning |
| --- | --- |
| `pending` | Required work not complete or Control Plane unavailable. |
| `activated` | Membership or external relationship active. |
| `blocked` | Policy, invite, verification, diagnostics, or status prevents activation. |
| `failed` | Non-recoverable flow failure without new invite/session. |

## 9. MVP Workflows

### 9.1 Internal Invite Creation

1. Owner/Admin creates internal invite.
2. Control Plane checks RBAC and policy.
3. Admin sets `allowedEmail` or `allowedDomain`.
4. Admin sets expiry and `maxUses`.
5. Control Plane creates invite and stores token hash.
6. Raw link/code is shown only for controlled delivery.
7. Audit event is recorded.

### 9.2 Internal Employee Enrollment

1. User opens invite link or enters fallback code.
2. Join landing page resolves safe invite context.
3. User downloads APK or opens installed app.
4. Android receives token through app link/deep link or manual fallback entry.
5. Android asks Control Plane to resolve invite.
6. User enters or confirms expected email.
7. Control Plane sends verification code/challenge.
8. User enters verification code.
9. Control Plane verifies code and invite constraints.
10. User selects or receives provider profile.
11. User completes IMAP/SMTP provider setup locally.
12. Diagnostics run if policy requires.
13. Control Plane activates `Membership`.
14. Android performs first visible directory sync.
15. User enters active employee state.

### 9.3 External Invite Creation

1. Admin or policy-enabled manager selects external invite.
2. User enters external contact metadata.
3. User selects contact type and visibility scope.
4. User assigns employee/team.
5. Control Plane validates policy and RBAC.
6. Control Plane creates external invite and draft relationship context.
7. Raw link/code is shown only for controlled delivery.
8. Audit event is recorded.

### 9.4 External Contact Enrollment

1. External contact opens invite link or enters fallback code.
2. Landing page clearly states scoped external access, not employee membership.
3. External contact downloads APK or opens installed app.
4. Android receives token or fallback code.
5. Android asks Control Plane to resolve external invite.
6. Control Plane sends verification code/challenge to external email.
7. External contact enters verification code.
8. Control Plane verifies email and invite constraints.
9. Provider setup and diagnostics proceed where policy requires.
10. Control Plane activates `ExternalRelationship`.
11. Android performs first scoped external visible directory sync.
12. External contact sees only allowed contact/team scope.

### 9.5 APK Download / Install Handoff

1. Landing page shows Android release metadata.
2. User clicks explicit APK download button.
3. User completes Android sideload/install flow.
4. If app link works, installed app receives invite reference.
5. If handoff fails, user enters fallback code or scans QR where allowed.
6. APK possession alone does not activate trust, membership, or external relationship.

### 9.6 APK-By-Email Emergency Fallback

1. Support/admin sends APK as emergency Android fallback only where policy permits.
2. User installs APK manually.
3. User still needs invite link/code.
4. App still requires Control Plane invite resolution, email verification, provider setup, policy checks, and directory sync.
5. Support should provide release version, SHA-256, and signing expectation through a trusted channel.

### 9.7 Control Plane Unavailable During Onboarding

If Control Plane is unavailable:

- APK installation can proceed if artifact is available;
- provider setup may proceed locally if app has enough cached or embedded provider profile context;
- diagnostics may run locally if implemented;
- invite resolution may be unavailable;
- email verification may be unavailable;
- membership activation is delayed;
- external relationship activation is delayed;
- first directory sync is delayed;
- UI must show pending/unavailable state and must not imply active membership.

## 10. Landing Page Contract

Landing page responsibilities:

- show organization display name if invite is valid enough to reveal it;
- distinguish internal employee join from external contact access;
- explain that membership or external access requires enrollment;
- show APK release metadata and download link where available;
- show fallback code or QR only when policy allows;
- show expired, revoked, exhausted, invalid, or already-used states safely;
- provide support guidance;
- avoid exposing raw constraints or secrets unnecessarily;
- avoid promising provider availability or whitelist readiness without diagnostics.

Landing page must not:

- imply APK download grants membership;
- show internal directory data;
- expose raw invite token beyond necessary link handling;
- collect provider credentials;
- activate membership directly without Control Plane verification.

## 11. Email Verification Design

Email verification is product-level mailbox ownership proof.

Requirements:

- Control Plane sends verification code/challenge to the expected email.
- Challenge is scoped to invite, organization/workspace, and enrollment session.
- Challenge expires.
- Attempts are rate-limited.
- Code is stored hashed where feasible.
- Verified email must satisfy `allowedEmail` or `allowedDomain`.
- Verification success does not by itself activate membership or external relationship.
- Provider login and diagnostics are separate from email ownership proof.
- Later IMAP challenge reading is not MVP and requires separate privacy/security design.

Failure behavior:

- wrong code: safe retry until limit;
- expired code: restart challenge if invite remains valid;
- wrong email: block activation and audit;
- invite revoked/expired during challenge: block activation;
- rate limit: block and audit;
- Control Plane unavailable: activation pending/unavailable.

## 12. Provider Setup And Diagnostics Gate

Provider setup responsibilities:

- use provider profile selected by user or recommended by organization;
- support Mail.ru / VK Mail baseline;
- support manual/custom profile where policy allows;
- avoid Mail.ru-only architecture;
- keep provider profile separate from user credentials.

Diagnostics gate:

- may be required before activation by policy;
- uses provider/network evidence;
- can be standalone diagnostics evidence or in-client check later;
- result can be `passed`, `failed`, `degraded`, `not_run`, or `skipped_by_policy`;
- local diagnostic pass does not prove mailbox ownership;
- failed diagnostics may block activation or produce warning depending on policy;
- diagnostics upload can be delayed if Control Plane is unavailable.

## 13. First Directory Sync

After activation:

- internal membership receives employee visible directory according to Directory Blueprint;
- external relationship receives only scoped external visible directory;
- Android fetches `DirectoryManifest`;
- Android downloads `DirectorySnapshot` when needed;
- Android verifies `directoryHash`;
- Android applies snapshot only after verification;
- Android shows failure if hash mismatch or Control Plane unavailable.

Rules:

- no internal directory before activation;
- external contacts never receive full internal directory;
- invite token presence does not grant directory access;
- pending users remain in onboarding state.

## 14. Invite Abuse And Safety

Abuse scenarios:

- invite forwarding;
- external invite forwarding;
- screenshot leak;
- wrong email using invite;
- expired invite replay;
- repeated failed fallback code attempts;
- repeated failed verification code attempts;
- revoked employee's active invites remaining usable;
- external invite sent to wrong person.

MVP controls:

- expiry;
- `maxUses`;
- token hash storage;
- fallback code hash storage;
- rate limiting;
- failed attempt audit;
- allowed email/domain checks;
- revoke invite;
- revoke all active invites by issuer, admin action;
- suspicious activity visibility for admin/support later.

## 15. Audit Events

Audit events:

- internal invite created;
- external invite created;
- invite link/code resolved;
- invite failed to resolve;
- invite expired;
- invite revoked;
- invite exhausted;
- fallback code attempt failed;
- email verification sent;
- email verification verified;
- email verification failed;
- email verification rate-limited;
- provider setup started/completed where reportable;
- diagnostic gate passed/failed/skipped;
- membership activated;
- external relationship activated;
- activation blocked;
- first directory sync completed/failed where reportable.

Audit metadata must be redacted and must not include raw tokens, codes, provider passwords, app passwords, raw AUTH, raw logs, or APK signing secrets.

## 16. Security And Privacy

Requirements:

- no credentials in invite links;
- no app passwords in invite links;
- no raw invite tokens in persistent logs;
- store token/code hashes where feasible;
- rate-limit invite and verification attempts;
- do not expose internal directory to external contacts;
- do not expose invite constraints unnecessarily;
- do not treat diagnostics as ownership proof;
- do not treat APK-by-email as trust or membership;
- do not store APK signing keys in repo or docs;
- support/admin actions must be auditable.

## 17. Infrastructure Assumptions

Infrastructure assumptions used but not implemented:

- primary public domain: `messenger-imap.speechbattle.com`;
- future join route: `/join/{inviteToken}`;
- future Android download route: `/download/android/latest`;
- Traefik already exists but must not be changed by this Blueprint;
- deployment path/network decisions belong to Deployment Blueprint;
- APK binaries are not committed to git;
- release metadata can point to GitHub Releases, backend storage, object storage, or redirect/proxy later.

## 18. MVP Scope

MVP includes:

- individual internal email invite;
- one-time invite if feasible;
- one-to-one external contact invite;
- invite expiry and revocation;
- `maxUses`;
- allowed email/domain checks;
- join landing page contract;
- explicit Android APK download handoff;
- app link/deep link token handoff where feasible;
- fallback invite code;
- optional QR representation;
- email verification code flow;
- provider setup after invite resolution;
- diagnostics gate according to policy;
- membership activation;
- external relationship activation;
- first directory sync;
- audit events;
- no internal directory exposure to external contacts.

## 19. Later Scope

Later scope:

- domain invites;
- limited group invites;
- team external invites;
- project room external invites;
- external organization invites;
- richer QR enrollment;
- admin approval workflows for sensitive invites;
- app store distribution;
- MDM distribution;
- automatic updates;
- signed IMAP/system-account onboarding fallback;
- in-client diagnostics history;
- alerting for suspicious invite activity;
- iOS distribution path.

## 20. Boundaries With Other Blueprints

| Blueprint | Owns |
| --- | --- |
| Corporate Control Plane MVP Blueprint | Invite storage, token hashes, email challenge storage, activation state, audit module, release metadata authority. |
| Corporate Directory MVP Blueprint | First visible directory sync, internal/external directory visibility, hash verification, cache behavior. |
| Invite Onboarding & Distribution MVP Blueprint | End-to-end join/enrollment/distribution flow contract. |
| Android Client MVP Blueprint | Screens, local storage, app link implementation, provider setup UI, diagnostic UI, user-facing warnings. |
| External Contacts & Guest Access Blueprint | Rich external relationship UX, broader scopes, reassignment UX, external project rooms. |
| Provider / Diagnostics Blueprint Slice | Exact diagnostic checks, evidence retention, report upload, provider verification policy. |
| Deployment Blueprint | Routes, Traefik labels, storage, email sender, secrets, TLS, rollback. |

## 21. Open Questions

- Are one-time invites mandatory in MVP or optional?
- Are domain invites deferred or included in MVP?
- Who can create external contact invites by default?
- Is admin approval required for external contact activation?
- What is the default external invite visibility scope?
- Should invite emails be sent by Control Plane or manually by admins?
- What is the default invite lifetime?
- What is the fallback code length and rate-limit policy?
- How is app link verification handled for internal APK distribution?
- What support path exists for users who cannot sideload APKs?
- Is transport diagnostics required before activation or allowed after activation with warning?
- Which diagnostic status is sufficient for activation?
- Should APK SHA-256 be user-visible, support-visible, or both?
- Which email sender/provider is used for verification codes?
- How long are enrollment sessions retained?

## 22. Acceptance Criteria

This Blueprint is accepted when:

- it distinguishes internal and external invites;
- it states that APK download does not equal membership;
- it states that invite token possession does not equal membership;
- it defines landing page responsibilities;
- it defines APK download and app handoff behavior;
- it defines fallback invite code behavior;
- it defines email verification code flow;
- it defines provider setup and diagnostics gate boundaries;
- it defines membership activation;
- it defines external relationship activation;
- it defines Control Plane unavailable behavior;
- it defines first directory sync after activation;
- it defines invite abuse controls;
- it defines audit events;
- it excludes SQL/OpenAPI/Android UI/deployment;
- it excludes signed IMAP/system-account updates from MVP;
- it lists open questions.
