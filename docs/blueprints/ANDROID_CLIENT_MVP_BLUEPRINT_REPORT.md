# Android Client MVP Blueprint Report

Date: 2026-05-26

Status: Draft

Project: `messenger-imap`

## 1. What Was Created

Created:

- [Android Client MVP Blueprint](ANDROID_CLIENT_MVP_BLUEPRINT.md)

The Blueprint defines the MVP Android client architecture frame: onboarding, invite handoff, email verification entry, provider setup, diagnostics surface, activation handling, first directory sync, local cache, stale/offline behavior, internal/external directory separation, external contact UX, managed group roster expectations, release policy UX, security/privacy constraints, fork-vs-shell decision criteria, implementation slices, validation plan, open questions, and acceptance criteria.

No code, Android project files, Gradle files, SQL migrations, OpenAPI contracts, Docker Compose, deployment files, APK binaries, server changes, or upstream Delta Chat / Chatmail changes were created.

## 2. Source Documents Used

Primary sources:

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

## 3. Key Decisions Inherited

- Android-first MVP.
- iOS is out of current scope.
- Mail.ru / VK Mail is the first accepted transport baseline.
- Product remains provider-agnostic.
- APK download does not equal membership.
- Invite token does not equal membership.
- Email verification code is required.
- Internal invite creates membership.
- External invite creates external relationship.
- Control Plane can be unavailable in whitelist/restricted mode.
- Android client must support cached/stale state.
- Corporate Directory is the source of active identity and visibility.
- External contacts must not receive internal directory.
- Managed groups use current Directory roster, not historical local chat membership.
- Background reliability is deferred.
- Delta Chat / chatmail capabilities should be reused where safe, but upstream modifications require a later implementation decision.

## 4. Main Open Decisions

- Thin Delta Chat Android fork or custom Android shell over chatmail/core.
- GPL/MPL compliance and distribution path.
- Android package ID and internal app name.
- Credential storage approach.
- Whether Android contacts permission is avoided entirely in MVP.
- App link/deep link verification approach.
- QR scanning vs fallback code for first MVP.
- Whether diagnostics block activation or warn only.
- Exact in-client diagnostic scope.
- Managed group behavior when directory is stale.
- One active workspace UI vs multi-workspace UI.
- Whether external contacts use the same APK mode or a constrained guest mode.
- Basic group ownership: user-created, admin-managed, or both.
- Background receive expectation for the first field trial.
- Release signing and update channel policy.

## 5. Recommended Next Blueprint

Recommended next Blueprint:

```text
docs/blueprints/PROVIDER_DIAGNOSTICS_MVP_BLUEPRINT.md
```

Reason:

The Android Client Blueprint deliberately leaves exact in-client diagnostic scope, evidence freshness, activation blocking policy, report retention, and provider verification rules open. Provider / Diagnostics Blueprint should define those boundaries before Android implementation planning locks the client behavior.

Parallel planning item:

```text
Fork-vs-shell Android implementation spike
```

This spike should decide whether the MVP proceeds as a thin Delta Chat Android fork or a custom shell over chatmail/core.
