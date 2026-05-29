# Invite Onboarding & Distribution MVP Blueprint Report

Date: 2026-05-26

Status: Draft

Project: `messenger-imap`

## 1. What Was Created

Created:

- [Invite Onboarding & Distribution MVP Blueprint](INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md)

The Blueprint defines the MVP onboarding and distribution architecture frame: internal invite, external invite, invite token/fallback code handling, join landing page contract, APK download handoff, email verification, provider setup, diagnostics gate, activation, first directory sync, stale/unavailable behavior, abuse controls, audit, security/privacy, MVP scope, later scope, boundaries with other Blueprints, and open questions.

No code, SQL migrations, OpenAPI contracts, Android UI, Docker Compose, deployment files, APK binaries, server changes, or upstream Delta Chat / Chatmail changes were created.

## 2. Source Documents Used

Primary sources:

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

## 3. Key Decisions Inherited

- APK download does not equal membership.
- Invite/enrollment is required.
- Internal invite creates `Membership`.
- External invite creates `ExternalRelationship`.
- Email ownership verification is required.
- Provider diagnostics do not replace email verification.
- Control Plane is required for activation in MVP.
- Control Plane may be unavailable in whitelist/restricted mode.
- External contacts do not receive internal directory.
- APK-by-email is Android emergency fallback only.
- iOS is out of current scope.
- Signed IMAP/system-account control updates are later scope.

## 4. Main Open Decisions

- Whether one-time invites are mandatory in MVP.
- Whether domain invites are deferred.
- Default manager/admin rights for external invites.
- Whether admin approval is required for external contact activation.
- Default external invite visibility scope.
- Whether Control Plane sends invite emails or admins deliver links manually.
- Default invite lifetime.
- Fallback code length and rate-limit policy.
- App link verification approach for internal APK distribution.
- Support path for users who cannot sideload APKs.
- Whether diagnostics block activation or warn only.
- Email sender/provider for verification codes.
- Enrollment session retention.

## 5. Recommended Next Blueprint

Recommended next Blueprint:

```text
docs/blueprints/ANDROID_CLIENT_MVP_BLUEPRINT.md
```

Reason:

Control Plane, Directory, and Invite Onboarding now define the authority and onboarding contracts that the Android client must consume. The Android Blueprint should be reviewed under the accepted thin Delta Chat Android fork assumption and decide how the app presents invite entry, provider setup, diagnostics, first directory sync, stale state, and internal/external contact separation.
