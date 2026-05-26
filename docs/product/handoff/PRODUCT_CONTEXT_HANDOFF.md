# Product Context Handoff

Date: 2026-05-14

Status: handoff context for future Blueprint/product work.

Primary references:

- [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Product PRD Review Addendum](../PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Product Decisions Log](../decisions/PRODUCT_DECISIONS_LOG.md)
- [Domain PRD Index](../DOMAIN_PRD_INDEX.md)
- [Infrastructure Assumptions](../../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)

## 1. What We Are Building

Corporate IMAP Messenger is an Android-first corporate messenger that uses IMAP/SMTP as message transport and a Corporate Control Plane for organization management.

Working Russian description: Корпоративный IMAP/SMTP-мессенджер устойчивой связи.

The product is not a generic email client and not a new message server. Users get a messenger-style Android experience. Administrators get organization membership, invites, directory, provider profiles, app releases, policies, and diagnostic status.

The product now also includes External Contacts & Guest Access. Clients, suppliers, partners, contractors, and other counterparties can be invited into scoped external relationships without becoming employees or receiving the internal corporate directory.

Infrastructure context is now recorded for future Control Plane and deployment work. The public hostname is `messenger-imap.speechbattle.com`, DNS resolves to `146.19.211.30`, the internal deploy host is `192.168.7.64`, SSH context is `roman@192.168.7.64`, and Traefik already exists on the server. Future deployment work must be non-destructive and must not disrupt existing services.

## 2. What Has Already Been Proven

MVP-0a diagnostics are accepted.

The project already built and ran a standalone Android Diagnostics APK. It validated foreground IMAP/SMTP transport for the first accepted baseline. The evidence is sufficient to move into product PRD and Blueprint work.

Known limitations remain accepted:

- not all providers tested;
- not all operators tested;
- background receive not proven;
- locked-screen receive not proven;
- full provider/operator/background matrix deferred.

Do not restart the project as if transport feasibility is unknown.

## 3. Accepted Baseline

Mail.ru / VK Mail family is the first accepted transport baseline.

Baseline endpoints:

- IMAP: `imap.mail.ru:993`
- SMTP: `smtp.mail.ru:465`

This is a baseline, not a product boundary. The product must remain provider-agnostic.

Yandex, Rambler, and manual/custom IMAP/SMTP profiles remain candidate/custom paths that need diagnostics before being marked verified or whitelist-ready.

## 4. Key Product Principles

- Provider-agnostic architecture.
- Transport verification before trust.
- Whitelist-ready status is evidence-based.
- APK download is not membership.
- Invite/enrollment controls membership.
- Internal invite creates membership.
- External invite creates external relationship, not membership.
- External contacts do not receive the internal corporate directory.
- Corporate directory is the core B2B feature.
- Control plane manages trust, directory, membership, policies, invites, releases, and diagnostics.
- Control plane may be unavailable in whitelist mode; stale directory/policy cache is required.
- Control plane is not a message server.
- Messages use IMAP/SMTP transport.
- IMAP/SMTP messages may continue while Control Plane sync is stale.
- Invite activation requires Control Plane availability in MVP.
- Email ownership proof uses a verification code/challenge and is separate from IMAP/SMTP transport diagnostics.
- All organization-scoped state must carry organization/workspace scope.
- Managed groups must use current active roster and not stale historical local membership after revoke.
- Trust states must distinguish installed app, invite present, email verified, active member, external contact, imported contact, and cryptographic verification.
- APK-by-email is Android emergency fallback only, not the primary flow.
- iOS is out of current scope.
- Deployment must integrate with existing Traefik without breaking existing services.
- Real secrets, SSH private keys, `.env` files, APK signing keys, provider passwords, and raw logs must not be committed.
- Diagnostic reports must be sanitized.
- No silent unsafe address book import.
- Do not modify chatmail/core unless justified by Blueprint.
- Do not promise production-grade background delivery in first MVP.

## 5. Current Document Structure

Product package:

- [Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Domain PRD Index](../DOMAIN_PRD_INDEX.md)
- [Android Messenger Client PRD](../domains/PRD_ANDROID_MESSENGER_CLIENT.md)
- [Corporate Control Plane PRD](../domains/PRD_CORPORATE_CONTROL_PLANE.md)
- [Corporate Directory PRD](../domains/PRD_CORPORATE_DIRECTORY.md)
- [Invite Onboarding & Distribution PRD](../domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md)
- [Provider Transport Profiles PRD](../domains/PRD_PROVIDER_TRANSPORT_PROFILES.md)
- [Diagnostics & Transport Verification PRD](../domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md)
- [External Contacts & Guest Access PRD](../domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md)
- [Product PRD Review Addendum](../PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Product Decisions Log](../decisions/PRODUCT_DECISIONS_LOG.md)
- [Infrastructure Assumptions](../../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)

## 6. Main Unresolved Decisions

- Thin Delta Chat Android fork vs custom Android shell over chatmail/core.
- GPL/MPL compliance and distribution acceptability.
- Exact first MVP provider set beyond Mail.ru / VK Mail.
- Directory authority source and canonical payload.
- Stale directory/control-plane thresholds and offline allowed actions.
- Email verification UX and whether later IMAP challenge reading is allowed.
- MVP workspace decision: one active workspace UI vs multi-workspace UI.
- Trust/identity UI states and SecureJoin-equivalent indicators.
- Control Plane RBAC-to-permission mapping.
- App release lifecycle: min, deprecated, blocked, force-upgrade, rollback, and channel policy.
- Invite policy: individual, one-time, domain, limited group.
- External contact invite policy, default visibility, approval rules, and reassignment behavior.
- Admin identity and role granularity.
- Background reliability target for first field trial.
- App branding, package identity, and distribution channel.
- Whether Android system contacts permission is avoided in MVP.
- Whether starter groups are admin-managed or user-created.

## 7. What Not To Redo

- Do not re-prove that Mail.ru foreground IMAP/SMTP transport is possible.
- Do not start a Delta Chat fork just to create PRD docs.
- Do not modify chatmail/core during product documentation work.
- Do not create a Mail.ru-only architecture.
- Do not claim all providers are whitelist-ready.
- Do not treat provider website access as IMAP/SMTP proof.
- Do not treat all invite links as employee invites.
- Do not expose internal directory to external contacts.
- Do not assume Control Plane is reachable in whitelist mode.
- Do not use IMAP/SMTP diagnostics as a replacement for email ownership verification.
- Do not use stale historical group roster as managed group authority.
- Do not treat APK-by-email as primary distribution.
- Do not include iOS in current scope.
- Do not change Traefik, containers, DNS, firewall, or server files during documentation work.
- Do not run deployment actions before a read-only server audit and deployment Blueprint.
- Do not store SSH keys, `.env`, passwords, tokens, APK signing keys, or database credentials in git.
- Do not merge diagnostics into the messenger without a design.
- Do not write UI mockups or detailed API specs from this PRD package.
- Do not include secrets, real accounts, app passwords, raw logs, or raw AUTH.
- Do not promise production readiness.

## 8. Next Recommended Work

1. Review PRDs with product, engineering, support, and legal/compliance stakeholders.
2. Decide thin Delta Chat Android fork vs custom shell over chatmail/core.
3. Decide GPL/MPL distribution acceptability and source compliance path.
4. Write Android IMAP Messenger MVP Blueprint with stale Control Plane UX, email verification, workspace scope, trust states, managed group enforcement, and release policy.
5. Write Corporate Control Plane Blueprint with RBAC, invite abuse controls, verification challenges, stale sync behavior, audit, and release lifecycle.
6. Write Directory Blueprint with canonical hash payload, stale/expired behavior, workspace scope, managed roster enforcement, and signed fallback boundaries.
7. Include External Contacts & Guest Access reassignment, stale visible directory, and external verification in future Blueprints.
8. Perform read-only infrastructure audit and create `docs/infrastructure/SERVER_AUDIT_REPORT.md`.
9. Define in-client diagnostics MVP scope versus standalone diagnostics.
10. Define first field trial provider/network validation plan.
11. After Control Plane stack selection, write `docs/blueprints/DEPLOYMENT_BLUEPRINT.md`.

## 9. MVP / Later / Non-goals Framing

MVP should deliver Android client, invite enrollment with email verification, external contact invite handling, provider setup, Mail.ru / VK Mail baseline, manual/custom provider support, basic diagnostics, one-to-one chat, basic groups, corporate directory sync with stale cache behavior, external contacts section, and control-plane admin management.

Later should deliver background reliability, signed IMAP/system-account directory/policy fallback, expanded provider validation, external organizations/project rooms, multi-workspace UI if deferred, advanced policy, richer distribution, iOS strategy, audio transcription, and broader platform strategy.

Non-goals remain video calls, real-time voice calls, production background guarantees, full MDM, silent unsafe import, all-provider whitelist proof, iOS support in current scope, and IMAP/SMTP transport rewrite.
