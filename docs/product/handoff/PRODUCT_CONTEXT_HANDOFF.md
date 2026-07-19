# Product Context Handoff

Date: 2026-05-14

Status: handoff context for future Blueprint/product work.

Primary references:

- [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Product PRD Review Addendum](../PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Product Decisions Log](../decisions/PRODUCT_DECISIONS_LOG.md)
- [Domain PRD Index](../DOMAIN_PRD_INDEX.md)
- [Infrastructure Assumptions](../../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)
- [Server Audit Report](../../infrastructure/SERVER_AUDIT_REPORT.md)
- [Project Roadmap](../../roadmap/PROJECT_ROADMAP.md)

## 1. What We Are Building

Corporate IMAP Messenger is an Android-first corporate messenger that uses IMAP/SMTP as message transport and a Corporate Control Plane for organization management.

Working Russian description: Корпоративный IMAP/SMTP-мессенджер устойчивой связи.

The product is not a generic email client and not a new message server. Users get a messenger-style Android experience. Administrators get organization membership, invites, directory, provider profiles, app releases, policies, and diagnostic status.

The product now also includes External Contacts & Guest Access. Clients, suppliers, partners, contractors, and other counterparties can be invited into scoped external relationships without becoming employees or receiving the internal corporate directory.

Infrastructure context is now recorded for future Control Plane and deployment work. The public hostname is `messenger-imap.speechbattle.com`, DNS resolves to `146.19.211.30`, the internal deploy host is `192.168.7.64`, SSH context is `roman@192.168.7.64`, and Traefik already exists on the server. A read-only server audit identified Traefik container `traefik`, shared Docker network `traefik-net`, and `/opt/stacks` as the likely stack convention. Future deployment work must be non-destructive and must not disrupt existing services.

The Project Roadmap is the controlling document between the PRD package and technical Blueprints. It fixes the stage order, MVP boundary, blockers, do-not-start-yet list, and near-term Blueprint sequence.

Current Blueprint status as of 2026-05-26:

- Corporate Control Plane MVP Blueprint is merged into `main`.
- Corporate Directory MVP Blueprint content is merged into `main`.
- Invite Onboarding & Distribution MVP Blueprint is drafted for review.
- The next recommended Blueprint after Invite review is Android Client MVP Blueprint.

Implementation strategy decision as of 2026-05-29:

- MVP Android implementation path is a thin fork of Delta Chat Android.
- Future Android fork repository is `Kwentin3/messenger-imap-android`.
- Current repository `Kwentin3/messenger-imap` remains product/meta/docs/control-plane coordination.
- Custom Android shell over `chatmail/core` is rejected for MVP.
- `chatmail/core` modifications require a separate Blueprint.
- Control Plane backend working hypothesis is Node.js / TypeScript + PostgreSQL.

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
- Use thin Delta Chat Android fork for MVP Android implementation.
- Do not vendor-copy Delta Chat Android into this repository.
- Keep corporate Android changes thin and isolated where possible.
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
- [Server Audit Report](../../infrastructure/SERVER_AUDIT_REPORT.md)
- [Project Roadmap](../../roadmap/PROJECT_ROADMAP.md)

Blueprint package:

- [Corporate Control Plane MVP Blueprint](../../blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md)
- [Corporate Directory MVP Blueprint](../../blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md)
- [Invite Onboarding & Distribution MVP Blueprint](../../blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md)

## 6. Main Unresolved Decisions

- GPL/MPL compliance and distribution acceptability.
- Android fork visibility, intake plan, upstream merge strategy, package identity, signing, and branding.
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
- Do not start Delta Chat Android fork changes beyond the documented intake slice.
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

1. Review and accept [Invite Onboarding & Distribution MVP Blueprint](../../blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md).
2. Review/update Android Client MVP Blueprint under the thin Delta Chat Android fork assumption.
3. Create Android fork intake plan for `Kwentin3/messenger-imap-android`.
4. Create Provider / Diagnostics Blueprint.
5. Keep Deployment Blueprint blocked until Control Plane stack assumptions are concrete.
6. Use `docs/infrastructure/SERVER_AUDIT_REPORT.md` as input for deployment planning only after deployment design starts.

## 9. MVP / Later / Non-goals Framing

MVP should deliver Android client, invite enrollment with email verification, external contact invite handling, provider setup, Mail.ru / VK Mail baseline, manual/custom provider support, basic diagnostics, one-to-one chat, basic groups, corporate directory sync with stale cache behavior, external contacts section, and control-plane admin management.

Later should deliver background reliability, signed IMAP/system-account directory/policy fallback, expanded provider validation, external organizations/project rooms, multi-workspace UI if deferred, advanced policy, richer distribution, iOS strategy, audio transcription, and broader platform strategy.

Non-goals remain video calls, real-time voice calls, production background guarantees, full MDM, silent unsafe import, all-provider whitelist proof, iOS support in current scope, and IMAP/SMTP transport rewrite.
