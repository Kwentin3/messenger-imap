# Corporate Control Plane MVP Blueprint Report

Date: 2026-05-26

Status: Draft

Project: `messenger-imap`

## 1. What Was Created

Created:

- [Corporate Control Plane MVP Blueprint](CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md)

The Blueprint translates the accepted PRD package and Project Roadmap into a technical MVP architecture frame for the Control Plane. It defines scope, non-goals, logical components, core domain entities, state machines, RBAC, workflows, email verification, directory authority, stale Control Plane behavior, provider profiles, diagnostics evidence, APK release metadata, audit, security/privacy, infrastructure assumptions, MVP boundaries, open questions, and acceptance criteria.

No code, deployment files, SQL migrations, OpenAPI contracts, Docker Compose, Android changes, or upstream Delta Chat / Chatmail changes were created.

## 2. Source Documents Used

Primary sources:

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

## 3. Key Decisions Inherited

- Control Plane is required and comes before Android product implementation.
- Control Plane is not a message server.
- Messages continue through IMAP/SMTP providers.
- Mail.ru / VK Mail is the accepted first transport baseline, not a Mail.ru-only architecture.
- Architecture must remain provider-agnostic.
- APK download does not equal organization membership.
- Internal invite creates `Membership`.
- External invite creates `ExternalRelationship`.
- Email ownership verification is required for activation.
- Corporate Directory is a core B2B feature.
- Control Plane may be unavailable in whitelist/restricted mode.
- Stale directory and stale policy mode are required.
- Provider diagnostics do not replace email verification.
- External contacts must not receive internal corporate directory.
- APK-by-email is Android emergency fallback only.
- iOS is out of current scope.
- Deployment requires a separate Deployment Blueprint.

## 4. Main Open Decisions

- Backend stack.
- Database choice.
- Exact stale/expired thresholds.
- RBAC permission keys and policy model.
- Email verification delivery provider and delivery ownership.
- Release storage choice.
- APK signing flow.
- One active workspace vs multi-workspace UI in MVP.
- Manager default invite permissions.
- External invite default visibility and approval policy.
- Diagnostic evidence retention.
- Audit retention.
- External relationship reassignment chat/history behavior.
- Thin Delta Chat Android fork vs custom Android shell.
- GPL/MPL compliance path for future distribution.

## 5. Recommended Next Blueprint

Recommended next Blueprint:

```text
docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md
```

Reason:

DirectoryManifest, DirectorySnapshot, canonical hash rules, internal/external directory separation, visible directory per user, managed group roster authority, revocation behavior, and stale directory behavior are the next dependencies that Android onboarding, invite activation, managed groups, and external contacts rely on.
