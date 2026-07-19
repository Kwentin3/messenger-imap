# Corporate IMAP Messenger Root PRD

Date: 2026-05-14

Working product name: **Corporate IMAP Messenger**

Russian description: **Корпоративный IMAP/SMTP-мессенджер устойчивой связи**

Status: high-level product PRD, not a technical Blueprint.

Related documents:

- [Domain PRD Index](DOMAIN_PRD_INDEX.md)
- [Android Messenger Client PRD](domains/PRD_ANDROID_MESSENGER_CLIENT.md)
- [Corporate Control Plane PRD](domains/PRD_CORPORATE_CONTROL_PLANE.md)
- [Corporate Directory PRD](domains/PRD_CORPORATE_DIRECTORY.md)
- [Invite Onboarding & Distribution PRD](domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md)
- [Provider Transport Profiles PRD](domains/PRD_PROVIDER_TRANSPORT_PROFILES.md)
- [Diagnostics & Transport Verification PRD](domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md)
- [External Contacts & Guest Access PRD](domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md)
- [Product PRD Review Addendum](PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Product Decisions Log](decisions/PRODUCT_DECISIONS_LOG.md)
- [Product Context Handoff](handoff/PRODUCT_CONTEXT_HANDOFF.md)
- [Implementation Fork Strategy Decision](../decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md)

## 1. Executive Summary

Corporate IMAP Messenger is an Android-first corporate messenger that uses existing email infrastructure, specifically IMAP and SMTP, as the transport for messages. It is intended for organizations that need a managed internal communication channel that can continue to work when ordinary consumer messengers or non-whitelisted services are unavailable.

The product is for companies with employees, managers, support teams, field staff, administrators, and controlled external contacts who need a managed communication channel, controlled corporate address book, invite-based enrollment, member lifecycle management, guest access rules, and a distributable Android client.

IMAP/SMTP is chosen because email infrastructure is mature, widely deployed, and can remain reachable in network environments where other communication services are blocked or unavailable. The product does not treat email as a user-facing inbox experience; it treats IMAP/SMTP as the underlying transport for a messenger-style client.

Android-first is chosen because the current diagnostic evidence and near-term field use case are Android mobile-first. The first product stages should optimize for Android app distribution, sideload realities, mobile credential handling, foreground transport validation, and later background reliability.

A Corporate Control Plane is required because an IMAP/SMTP messenger alone is not a corporate product. The organization needs a trusted source of membership, invites, provider profiles, directory records, policies, app releases, and diagnostic status. The control plane is not a message server; messages still flow through IMAP/SMTP providers.

The Control Plane may be unavailable in mobile whitelist or restricted-network mode. In that state, IMAP/SMTP messaging may continue if provider transport works, but directory, policy, invite, revoke, release metadata, and diagnostic upload sync can become stale until the Control Plane is reachable again.

The product must distinguish internal membership from external communication. An internal invite creates organization membership. An external invite creates an external relationship with a client, supplier, partner, contractor, or other counterparty. External contacts must not receive the internal corporate directory or appear as employees.

MVP-0a diagnostics already proved enough to move forward: a standalone Android Diagnostics APK was built, installed, launched, and used to validate Mail.ru IMAP/SMTP transport in real foreground Android conditions. The accepted endpoints are:

- IMAP: `imap.mail.ru:993`
- SMTP: `smtp.mail.ru:465`

Mail.ru / VK Mail is the first accepted transport baseline, but it must not become the only path. The product architecture must be provider-agnostic from the start. Yandex, Rambler, and manual/custom IMAP/SMTP profiles remain candidate/custom profiles until diagnostics produce evidence for their target network contexts.

## 2. Product Vision

Build a corporate messenger for resilient communication that uses email infrastructure as message transport and a centralized Corporate Control Plane for organization management.

The product combines:

- an Android messenger client;
- IMAP/SMTP provider connectivity;
- invite-based organization enrollment;
- a centrally managed corporate directory;
- admin-managed provider profiles and policies;
- diagnostic verification before provider trust;
- controlled app distribution.

The intended experience is a messenger, not an email client. Users should see people, groups, announcements, attachments, and voice messages. Administrators should manage membership, directory records, invitations, policy defaults, and app releases without becoming operators of a separate real-time message server.

## 3. Problem Statement

Organizations may need a communication channel when regular messengers are unavailable because of network limits, mobile operator restrictions, policy constraints, or service outages.

Email infrastructure can remain available in environments where other communication platforms are blocked. However, raw email does not provide the controlled, app-like corporate messenger experience required by employees and administrators.

The key product problems are:

- ordinary messengers can be unavailable under network restrictions;
- companies need a managed corporate communication channel;
- IMAP/SMTP endpoints may remain reachable in whitelist or restricted-network contexts;
- a generic IMAP messenger without a corporate address book is not enough;
- a generic IMAP messenger without a control plane cannot reliably manage membership, trust, directory state, provider defaults, invites, or app releases;
- real B2B communication often includes external clients and counterparties who need controlled access without becoming employees;
- downloading an APK must not automatically grant organization access;
- provider availability must be proven by transport diagnostics, not inferred from provider brand or website access.

## 4. Target Users / Roles

**Organization Owner**  
Owns the organization account and top-level product decisions. Can authorize administrators, high-level policies, and distribution approach.

**Admin / Manager**  
Manages members, invites, directory records, groups, app releases, provider profile defaults, and diagnostic evidence visibility.

**Employee**  
Uses the Android client for one-to-one messaging, group communication, announcements, attachments, voice messages, and directory lookup.

**Invited Employee**  
Has received an invite but is not fully active until enrollment, provider setup, diagnostics, and membership activation are complete.

**Revoked Employee**  
Was previously a member but is no longer active. Must be removed from the active directory and managed groups. Their client should clear or hide corporate directory data after the next successful sync, while acknowledging that previously seen data cannot be physically erased from human memory or unmanaged copies.

**Support / IT Administrator**  
Helps users install the APK, configure provider credentials, read diagnostic status, and resolve onboarding failures.

**Field Tester**  
Runs provider and network diagnostics in specific operator, region, Wi-Fi, mobile, or whitelist contexts. Must not export secrets or raw logs.

**External Contact / Guest**  
A non-employee client, supplier, partner, contractor, or counterparty contact. Can communicate only within the external relationship scope allowed by the organization and must not receive the internal corporate directory.

## 5. Accepted Evidence

The following evidence is accepted for moving beyond diagnostics into product PRD and Blueprint work:

- MVP-0a diagnostics are accepted as successful for the next stage.
- A standalone Android Diagnostics APK was built, installed, launched, and used on a real Android device.
- Mail.ru transport passed foreground diagnostic checks including IMAP/SMTP connectivity and send/receive correlation.
- Mail.ru / VK Mail is accepted as the first transport baseline.
- The accepted baseline endpoints are `imap.mail.ru:993` and `smtp.mail.ru:465`.
- The mobile whitelist/restricted-network context is accepted by management decision based on tester confirmation.
- Full provider, operator, background, and locked-screen matrices are deferred.

Accepted evidence does not mean:

- all providers are verified;
- all mobile operators are verified;
- background delivery is proven;
- locked-screen behavior is proven;
- production readiness is achieved;
- Mail.ru-only architecture is acceptable.

## 6. Product Principles

- Provider-agnostic architecture is mandatory from the start.
- Transport verification must happen before product trust.
- APK download is not organization membership.
- Invite and enrollment controls membership.
- Internal invite creates membership.
- External invite creates an external relationship, not membership.
- Corporate directory is the core B2B product feature.
- External contacts are separated from internal members and do not receive the internal corporate directory.
- Control plane manages trust, policies, membership, provider defaults, app releases, and directory authority.
- Control plane may be unavailable in whitelist mode; the client must support visible stale directory and stale policy state.
- Messages use IMAP/SMTP transport.
- Control plane is not the message server.
- Email ownership proof is required for membership or external relationship activation; IMAP/SMTP login is transport readiness evidence, not product-level ownership proof.
- Organization/workspace scoping is mandatory for membership, directory, invites, provider profiles, diagnostics, policies, and external relationships.
- Managed groups must send using current active roster, not stale historical local membership.
- Diagnostic reports must be sanitized.
- No app passwords, real credentials, raw AUTH payloads, raw logcat, or sensitive message payloads in reports.
- No silent unsafe address book import.
- Mail.ru / VK Mail is the first accepted baseline, not the product boundary.
- Whitelist-ready provider status must be evidence-based.
- Provider website reachability is not proof that IMAP/SMTP endpoints work.
- Do not modify chatmail/core unless justified by a later technical design.
- MVP Android implementation path is a thin fork of Delta Chat Android.
- Delta Chat Android and chatmail/core capabilities should be reused where they fit, subject to licensing and Blueprint boundaries.
- Revocation can update product-visible directory state, but cannot guarantee erasure of information already seen by people or stored outside managed app state.

## 7. Product Domains

**Android Messenger Client**  
The Android-first user application for onboarding, provider setup, diagnostics, chats, groups, announcements, attachments, voice messages, and corporate directory use.

**Corporate Control Plane**  
The organization management backend and admin portal for members, invites, directory, groups, provider profiles, app releases, policies, and diagnostic status. It is not a message server.

**Corporate Directory**  
The centrally managed source of truth for active members, member statuses, managed groups, directory versions, hashes, and local client cache behavior.

**Invite Onboarding & Distribution**  
The assisted one-shot flow for inviting employees or external contacts, reaching a landing page, downloading the APK, entering or carrying an invite token, setting up a provider, passing diagnostics, activating membership or an external relationship, and syncing the appropriate visible directory.

**Provider Transport Profiles**  
The product layer for IMAP/SMTP provider presets, manual/custom profiles, diagnostic status, and provider-specific guidance without hardcoding the product to one provider.

**Diagnostics & Transport Verification**  
The diagnostic product capability for proving provider transport behavior through DNS, TCP, TLS, IMAP, SMTP, send/receive correlation, Spam/Junk checks, and sanitized reports.

**External Contacts & Guest Access**  
The product domain for clients, suppliers, partners, contractors, and other counterparties. It defines external invites, external relationships, visibility scopes, external directory sections, badges, reassignment, revocation, and the rule that guest access is not employee membership.

## 8. MVP Scope

The high-level MVP should include:

- Android client.
- Invite-based onboarding.
- Provider profile setup.
- Mail.ru / VK Mail baseline.
- Manual/custom IMAP/SMTP profile.
- Yandex and Rambler as candidate profiles where supported by diagnostics and product policy.
- One-to-one chats.
- Basic group support or starter groups.
- Announcement or broadcast consumption if available through reused messenger capabilities.
- Attachments.
- Voice/audio messages if available through reused capabilities.
- Corporate directory sync from the control plane.
- Local cached directory with visible stale/expired state when Control Plane sync is unavailable.
- Email verification code flow for internal membership and external relationship activation.
- External contacts section with one-to-one external invite and visible external badge.
- External invite that creates an external relationship, not internal membership.
- Admin portal for members, invites, directory, basic groups, app releases, and diagnostic status.
- Admin control for external contact revoke/archive/reassign.
- APK download flow.
- Android APK-by-email emergency fallback as a non-primary distribution option.
- Basic diagnostic gate for provider setup.
- Sanitized diagnostic report export or reference to standalone diagnostic evidence.

The MVP should be honest about limitations. It should demonstrate a usable corporate messenger path, not claim mature production-grade mobile background delivery or full provider coverage.

## 9. Later / Deferred Scope

Later stages may include:

- background and locked-screen reliability hardening;
- larger field diagnostic campaigns by provider, operator, region, and network mode;
- signed directory update distribution;
- signed directory/policy update distribution through an IMAP/SMTP system account as later/fallback scope;
- stronger directory trust and rollback model;
- advanced policy controls;
- richer admin audit and support workflows;
- external organizations, external project rooms, broader guest visibility scopes, and CRM/helpdesk integrations;
- broader provider catalog;
- full app store or managed distribution strategy;
- iOS or desktop clients;
- audio transcription with a separate privacy and storage design;
- deeper compliance automation.

## 10. Non-goals

The first MVP explicitly excludes:

- video calls;
- real-time voice calls;
- production-grade background guarantees;
- full MDM;
- complex E2EE policy redesign;
- full automatic address book sync without a trust model;
- silent address book import;
- treating external contacts as employees by default;
- exposing the internal corporate directory to external contacts;
- full app store distribution strategy;
- iOS support and iOS distribution path;
- proof that all providers work in whitelist environments;
- rewriting IMAP/SMTP transport;
- modifying chatmail/core internals without a specific Blueprint-level justification;
- promising a production-ready product after the MVP.

## 11. Architecture Assumptions

High-level product relationship:

```text
Android Client
  <-> IMAP/SMTP Provider
  <-> Corporate Control Plane
```

This diagram is conceptual, not a protocol specification. The Android client communicates with IMAP/SMTP providers for messages and with the Corporate Control Plane for organization state.

Control Plane sync is the primary MVP path for organization state. In restricted whitelist mode, the Control Plane may be unavailable while IMAP/SMTP remains available; clients must rely on cached directory/policy state, show stale warnings, and delay activation or administrative state changes until sync resumes. Signed directory/policy updates via an IMAP/SMTP system account are later/fallback scope.

**Mail Transport Layer**  
Provides IMAP/SMTP connectivity, authentication, message send/receive, folders, and transport status. The MVP should reuse proven Delta Chat / chatmail capabilities where possible.

**Messenger Client Layer**  
Presents chats, groups, announcements, attachments, voice messages, provider setup, diagnostics entry points, and corporate directory UX to the user.

**Corporate Control Plane**  
Owns organization management, invites, membership, directory authority, managed groups, provider profile defaults, APK releases, policy configuration, and diagnostic status summaries.

**Directory Authority**  
Defines the canonical corporate directory snapshot, version, hash, member statuses, and active group membership rules.

Directory hash payloads must be canonical, scoped by `organizationId`/`workspaceId`, sorted by stable IDs, normalized for email case and empty/null handling, and free of volatile fields such as server time, request ID, or pagination metadata.

**Diagnostics Layer**  
Validates whether a provider profile works in a specific network context. Results must be evidence-based and sanitized.

## 12. Roadmap Stages

**Stage 0: Diagnostics MVP-0a - accepted**  
Standalone Android diagnostic APK validated foreground IMAP/SMTP behavior for the first accepted baseline.

**Stage 1: Product PRD / Blueprint**  
Create PRD package, decide open product questions, then write technical Blueprints.

**Stage 2: Android Messenger MVP**  
Deliver the Android client path for onboarding, provider setup, basic messaging, and directory use.

**Stage 3: Control Plane MVP**  
Deliver admin organization, members, invites, provider profiles, releases, and diagnostic status management.

**Stage 4: Directory and Invite Onboarding**  
Deliver reliable directory version/hash sync and assisted one-shot invite onboarding.

**Stage 5: Background Reliability MVP-0b**  
Validate and harden background receive, locked-screen behavior, battery constraints, and support guidance.

**Stage 6: Additional Providers / Field Validation**  
Expand evidence for Yandex, Rambler, custom providers, operators, regions, and whitelist contexts.

**Stage 7: Advanced Directory Trust and Signed Updates**  
Add signed directory updates, stronger issuer trust, rollback semantics, replay protection, and audit depth.

## 13. Risks

**GPL/MPL compliance**  
Delta Chat Android is GPL-covered while chatmail/core is MPL-covered. Distribution of modified binaries may require source and license compliance. Legal review is required before product distribution decisions.

**Provider availability**  
Providers can change auth, rate limits, IMAP/SMTP access policies, app-password requirements, folder behavior, or anti-spam rules.

**Whitelist assumptions**  
A provider website being reachable does not prove IMAP/SMTP endpoints are reachable. Whitelist-ready status needs diagnostic evidence.

**Address book trust**  
Corporate directory distribution can mislead users if authenticity, versioning, and member statuses are not clear.

**Control Plane stale state**

If Control Plane sync is blocked while IMAP/SMTP works, users may continue messaging with stale directory or policy state. The client must make stale/expired state visible and restrict sensitive actions such as new managed group sends or invite activation when required.

**External directory exposure**  
If external contacts are accidentally treated as members, the product can leak internal employee directory data, internal groups, or organizational structure to clients and counterparties.

**Invite and verification abuse**

Forwarded invite links, screenshots, expired invite reuse, wrong-email activation attempts, and repeated failed attempts require expiry, rate limits, audit, revocation, and email ownership verification.

**Revoked employee limitations**  
Revocation can remove members from active directory state and managed groups, but cannot guarantee erasure of previously seen information.

**Background reliability**  
Android background behavior, battery policies, OEM restrictions, and locked-screen delivery are not proven by MVP-0a foreground diagnostics.

**APK distribution**  
Sideloading has friction, warnings, source permissions, update risks, and support burden. APK-by-email is Android emergency fallback only, not the primary distribution model and not a membership signal.

**Credential security**  
IMAP/SMTP app passwords or credentials must be protected. Reports must never include secrets or raw authentication data.

## 14. Open Decisions

- GPL distribution acceptability and compliance model.
- Android fork repository visibility, package identity, branding, signing, and upstream merge strategy.
- First MVP provider set beyond Mail.ru / VK Mail baseline.
- Directory authority model and trust boundary.
- Control Plane stale/expired thresholds and blocked action policy.
- Invite policy for individual, domain, group, and one-time invites.
- Email verification UX and whether later IMAP challenge reading is allowed.
- MVP workspace model: one active workspace UI vs multi-workspace UI, while keeping all state scoped.
- Trust/identity state UI for invite, email verified, active member, external contact, imported contact, and SecureJoin-equivalent verification.
- External contact invite policy, default visibility scope, approval rules, and reassignment behavior.
- App release lifecycle policy including minimum, deprecated, blocked, rollback, and force-upgrade thresholds.
- Background reliability target for first field trial.
- Branding, package identity, and distribution path.
- Whether Android system contacts permission is allowed or avoided.
- Whether MVP groups are admin-managed starter groups or user-created only.
- Whether E2EE posture remains existing Delta Chat behavior or receives product constraints later.

## 15. Linked Domain PRDs

- [Android Messenger Client PRD](domains/PRD_ANDROID_MESSENGER_CLIENT.md)
- [Corporate Control Plane PRD](domains/PRD_CORPORATE_CONTROL_PLANE.md)
- [Corporate Directory PRD](domains/PRD_CORPORATE_DIRECTORY.md)
- [Invite Onboarding & Distribution PRD](domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md)
- [Provider Transport Profiles PRD](domains/PRD_PROVIDER_TRANSPORT_PROFILES.md)
- [Diagnostics & Transport Verification PRD](domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md)
- [External Contacts & Guest Access PRD](domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md)
- [Product PRD Review Addendum](PRODUCT_PRD_REVIEW_ADDENDUM.md)

## 16. Product Review Refinements

The product review addendum refines the baseline PRD package with these mandatory product constraints:

- Control Plane may be unavailable in whitelist or restricted-network mode.
- IMAP/SMTP messages may continue while Control Plane state is stale.
- Directory, policy, invite, revoke, release metadata, and diagnostic upload sync require Control Plane availability unless later signed IMAP/system-account fallback is designed.
- Membership and external relationship activation require email ownership proof through a verification code/challenge.
- All organization-scoped state must carry `organizationId` and/or `workspaceId`; MVP may still choose one active workspace UI.
- Managed groups must enforce current active roster and not rely on stale historical local group membership after revoke.
- Trust states must distinguish installed app, invite present, email verified, active internal member, external contact, imported contact, and cryptographic verification.
- Control Plane RBAC must be explicit for owner, admin, manager, support/IT, and auditor roles.
- Invite abuse controls must include expiry, max uses, expected email/domain constraints, rate limits, audit, and revocation.
- App release metadata must include lifecycle fields such as minimum supported version, forced upgrade threshold, blocked/deprecated versions, channel, APK SHA-256, signing note, and rollback status.
- Android APK-by-email is emergency fallback only; iOS is out of current scope.

## 17. MVP / Later / Non-goals Summary

MVP focuses on Android client, invite enrollment, provider profiles, Mail.ru / VK Mail baseline, custom profiles, one-to-one messaging, basic groups, corporate directory sync, external contacts with one-to-one guest access, admin management, APK distribution, and basic diagnostics.

Later scope covers background reliability, broader provider validation, signed directory trust, richer policy, external organizations/project rooms, advanced audit, distribution strategy, and additional platforms.

Non-goals exclude video calls, real-time voice calls, production-grade background guarantees, full MDM, full provider whitelist proof, silent unsafe address book import, treating external contacts as employees, exposing the internal directory to guests, and IMAP/SMTP transport rewrites.
