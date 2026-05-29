# Product Decisions Log

Date: 2026-05-14

Status: product decision log for Corporate IMAP Messenger.

Root PRD: [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)

This log records accepted product decisions that shape the PRD package. It is not a technical ADR and does not replace later Blueprints.

## Decisions

| Date | Decision | Rationale | Implications | Open follow-up |
|---|---|---|---|---|
| 2026-05-13 | MVP-0a diagnostics accepted. | Standalone Android Diagnostics APK was built, installed, launched, and produced accepted foreground IMAP/SMTP evidence. | Product work can move from diagnostics-only to PRD and Blueprint. | Define which diagnostics move into the messenger MVP. |
| 2026-05-13 | Mail.ru / VK Mail is the first accepted transport baseline. | Mail.ru IMAP/SMTP passed accepted diagnostics, and VK Mail shares the transport family for product planning. | MVP can use Mail.ru / VK Mail as first baseline provider family. | Validate VK Mail runtime separately if needed; do not generalize to all providers. |
| 2026-05-14 | Product must be provider-agnostic. | Mail.ru evidence proves a baseline, not the entire product architecture. | Provider profile layer is required; custom profiles are required. | Decide first MVP provider list beyond Mail.ru / VK Mail. |
| 2026-05-14 | No Mail.ru-only architecture. | Hardcoding one provider would block enterprise use and contradict diagnostic evidence rules. | Android onboarding, control plane, and diagnostics must route through provider profiles. | Blueprint must prevent provider-specific shortcuts from becoming architecture. |
| 2026-05-14 | APK download does not equal membership. | App binaries can be public or semi-public, but organization access must be controlled. | Invite/enrollment is mandatory for membership activation. | Define exact invite token and activation policy. |
| 2026-05-14 | Invite/enrollment required. | The organization needs control over who joins and when. | Join flow must validate invite, provider setup, diagnostics policy, and activation. | Decide invite types for MVP: individual, one-time, domain, limited group. |
| 2026-05-14 | Corporate directory requires control plane. | A corporate address book needs centralized authority, statuses, versions, and admin operations. | Directory is sourced from control plane, not unmanaged local imports. | Define first authoritative source of directory records. |
| 2026-05-14 | Directory uses version/hash. | Clients need a safe way to detect and verify directory changes. | DirectoryManifest and DirectorySnapshot concepts are required. | Blueprint must define canonical payload and sync protocol. |
| 2026-05-14 | Control plane is not message server. | Product uses IMAP/SMTP as transport and should avoid creating a parallel chat server. | Control plane manages trust, directory, membership, policies, releases, and diagnostics. | Define boundaries for metadata and audit without storing message content. |
| 2026-05-14 | Background reliability deferred. | MVP-0a proved foreground transport, not locked-screen or background behavior. | First product MVP must not claim production-grade background delivery. | Plan MVP-0b background/locked-screen validation. |
| 2026-05-14 | No silent unsafe address book import in MVP. | Silent imports can create identity, spoofing, and trust confusion risks. | Directory sync comes from control plane; manual vCard import requires preview/confirmation if included. | Design signed directory updates later. |
| 2026-05-14 | Voice messages allowed as existing capability. | Delta Chat / chatmail capabilities include audio/voice message support. | MVP may include voice messages if reused safely. | Keep audio transcription as later scope. |
| 2026-05-14 | Video calls out of scope. | Real-time media is outside IMAP/SMTP messenger MVP and not part of accepted capabilities. | MVP excludes video calls and real-time voice calls. | Revisit only if product direction changes substantially. |
| 2026-05-14 | Audio transcription later. | Transcripts can be more sensitive than audio and need privacy/storage design. | Do not add transcription to MVP scope. | Create separate privacy/product design if needed. |
| 2026-05-14 | Whitelist-ready status must be evidence-based. | Website availability or provider brand does not prove IMAP/SMTP endpoints work in restricted networks. | Diagnostic status must be tied to report evidence and network context. | Define report freshness and approval policy. |
| 2026-05-14 | Do not modify chatmail/core unless justified. | Existing core capabilities should be reused conservatively; core changes increase risk. | Product layer should adapt onboarding, profiles, diagnostics, and directory first. | Blueprint decides whether any core change is necessary. |
| 2026-05-26 | Product supports external contacts and counterparties. | Real B2B communication includes clients, suppliers, partners, contractors, and other non-employees. | External Contacts & Guest Access is a first-class PRD domain. | Define MVP roles allowed to invite external contacts. |
| 2026-05-26 | Internal invite and external invite are different. | Employee enrollment and guest/counterparty communication have different trust and visibility requirements. | Invite flows must route by invite type and must not treat all invite links as employee membership links. | Blueprint must define invite token typing and validation behavior. |
| 2026-05-26 | External invite creates ExternalRelationship, not Membership. | External contacts need scoped communication without employee rights. | Control plane, Android client, and directory must model ExternalRelationship separately from Membership. | Define exact lifecycle fields and sync payload. |
| 2026-05-26 | External contacts do not receive internal corporate directory. | Directory exposure to external parties is a major confidentiality risk. | External users receive only allowed contact/team/room visibility. | Define scoped visible directory behavior. |
| 2026-05-26 | External contacts belong to the organization and can be reassigned. | Client relationships must survive manager departure and remain under organization control. | Admins can revoke, archive, suspend, or reassign external contacts. | Define reassignment notification and audit details. |
| 2026-05-26 | Control Plane may be unavailable in whitelist mode; stale cache mode is required. | Restricted mobile networks may allow IMAP/SMTP provider transport but block the Control Plane. | Messaging may continue through IMAP/SMTP while directory, policy, invite, revoke, release, and diagnostic upload sync become stale. | Define stale/expired thresholds and blocked actions. |
| 2026-05-26 | Primary directory sync is Control Plane HTTPS; signed IMAP/system-account sync is later fallback. | Control Plane remains the authoritative product path, while IMAP fallback requires signing and replay protection. | MVP should not depend on IMAP-based control updates unless explicitly selected. | Design signed fallback protocol only after MVP scope decision. |
| 2026-05-26 | Email ownership proof uses verification code/challenge. | Entering an email or passing IMAP/SMTP login is not enough for product-level ownership proof. | Membership and external relationship activation require verified email and invite policy checks. | Decide whether later IMAP challenge reading is allowed. |
| 2026-05-26 | Multi-workspace must not be blocked by the data model. | One user may be internal member in one organization and external contact in another. | Directory, invites, membership, provider profile, diagnostics, policy, and external relationships are scoped by organization/workspace. | Decide one active workspace UI vs multi-workspace UI for MVP. |
| 2026-05-26 | Managed groups must enforce current active roster. | Local historical chat membership may include revoked members. | Managed sends use current directory roster; stale roster sends warn/block; revoked members are not active recipients. | Define exact client UI and override behavior. |
| 2026-05-26 | Trust and identity states must be explicit. | App installed, invite present, email verified, active member, external contact, imported contact, and cryptographic verification are different states. | UI and policy must not conflate these states. | Decide SecureJoin/equivalent indicators for MVP. |
| 2026-05-26 | Control Plane RBAC matrix is required. | Admin roles need product-level permission boundaries before Blueprint. | Owner, Admin, Manager, Support/IT, and Auditor permissions must be designed from the matrix. | Convert product matrix into exact permissions in Blueprint. |
| 2026-05-26 | APK-by-email Android emergency fallback is accepted, not primary. | Some emergency scenarios may allow mail but not normal APK download. | Android APK can be distributed by email as fallback, but install does not imply membership and release metadata/signing still matter. | Define operator guidance and support warnings. |
| 2026-05-26 | iOS is out of current scope. | Current evidence and distribution assumptions are Android-first. | iOS distribution requires separate App Store/TestFlight/MDM-like path later. | Revisit after Android MVP scope is stable. |
| 2026-05-29 | MVP uses thin Delta Chat Android fork. | Existing Delta Chat Android client and Chatmail capabilities are the fastest credible MVP base and reduce product risk. | Create future Android fork repo `Kwentin3/messenger-imap-android`; keep current repo as product/meta/docs/control-plane coordination; reject custom Android shell over `chatmail/core` for MVP; do not modify `chatmail/core` as first step. | Plan GPL/source distribution, fork intake, package identity, signing, and upstream merge strategy. |
| 2026-05-29 | Control Plane backend working hypothesis is Node.js / TypeScript + PostgreSQL. | This stack is fast for MVP, fits web/admin/API work, has good JSON ergonomics, and fits Docker/Traefik/PostgreSQL deployment assumptions. | Future Control Plane implementation planning can use this as a working assumption. | Confirm before implementation lock; keep own database/container/volume rule. |

## MVP Impact

The decisions define an MVP that includes Android-first onboarding, provider profiles, invite-based activation, email ownership verification, control-plane directory authority, stale cache behavior, external contacts/guest access, basic diagnostics, Mail.ru / VK Mail baseline, manual/custom profile support, and safe directory sync.

## Later Impact

Deferred work includes background reliability, signed IMAP/system-account directory/policy updates, broader provider/operator validation, audio transcription, advanced policy, multi-workspace UI if not selected for MVP, iOS distribution, and production distribution strategy.

## Non-goals Confirmed by Decisions

- No Mail.ru-only product.
- No production-ready claim.
- No silent arbitrary address book import.
- No video calls or real-time voice calls in MVP.
- No full provider whitelist proof.
- No control-plane message server.
- No IMAP/SMTP transport rewrite.
- No external contact as employee by default.
- No internal directory exposure to external contacts.
- No secrets in diagnostics or docs.
- No assumption that Control Plane works in whitelist mode.
- No iOS support in current scope.

## Open Decision Themes

- GPL/MPL compliance and distribution model.
- Android fork intake, upstream merge strategy, package identity, and signing model.
- First MVP provider list beyond Mail.ru / VK Mail.
- Directory authority and canonical payload.
- Invite policy and activation rules.
- Stale/expired directory thresholds and allowed offline actions.
- Email verification UX and later IMAP challenge reading.
- One active workspace UI vs multi-workspace UI.
- Trust/identity state display and cryptographic verification indicators.
- RBAC-to-permission mapping.
- App release lifecycle policy.
- External invite policy, visibility scopes, and reassignment behavior.
- Background reliability target.
- Branding, package identity, and distribution channel.
- Admin identity and role model.
