# Corporate Directory MVP Blueprint Report

Date: 2026-05-26

Status: Draft

Project: `messenger-imap`

Branching note: this report was drafted on `blueprint/corporate-directory-mvp`, stacked on `blueprint/control-plane-mvp` because PR #3 is still open.

## 1. What Was Created

Created:

- [Corporate Directory MVP Blueprint](CORPORATE_DIRECTORY_MVP_BLUEPRINT.md)

The Blueprint defines the MVP directory architecture frame: `DirectoryManifest`, `DirectorySnapshot`, canonical payload and hash rules, version semantics, visible directory per principal, internal/external separation, status behavior, stale/expired cache behavior, revocation/suspension handling, managed group roster authority, Control Plane responsibilities, Android client responsibilities, security/privacy requirements, MVP scope, later scope, and open questions.

No code, SQL migrations, OpenAPI contracts, Android UI, Docker Compose, deployment files, server changes, or upstream Delta Chat / Chatmail changes were created.

## 2. Source Documents Used

Primary sources:

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

## 3. Key Decisions Inherited

- Control Plane is the source of truth.
- Directory is centrally managed.
- Directory is not a message transport feature.
- Android client keeps a local cache but does not become directory authority.
- Internal members and external contacts must remain separate.
- Visible directory per principal is required.
- External contacts must not receive internal directory.
- Directory version/hash is required.
- Canonical payload rules must be deterministic.
- Stale/expired directory mode is required.
- Managed groups use current active roster from Directory/Control Plane.
- Revocation removes members from active directory and managed rosters after sync.
- No silent unsafe address book import.
- vCard/contact primitives may be reused but are not corporate authority.
- Signed IMAP/system-account updates are later scope.

## 4. Main Open Decisions

- Exact stale/expired thresholds.
- Exact canonical JSON standard/library.
- Full snapshot only vs MVP deltas.
- Precomputed visible directory vs computed per request.
- External visible directory shape: minimal snapshot vs allowed contact cards.
- Suspended member visibility to ordinary employees.
- Local nickname behavior for corporate contacts.
- Whether manual vCard import is in MVP.
- Managed group send behavior when stale.
- Whether snapshot includes provider profile or release policy references.
- Directory audit retention.
- Directory publish workflow: explicit admin publish vs automatic publish after accepted changes.

## 5. Recommended Next Blueprint

Recommended next Blueprint:

```text
docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md
```

Reason:

Directory and Control Plane now define the authority state needed for onboarding. The next dependency is the full invite/enrollment flow: landing page, internal vs external invite behavior, email verification code, APK download handoff, provider setup sequencing, first directory sync, stale/unavailable activation behavior, and fallback invite code handling.
