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

## MVP Impact

The decisions define an MVP that includes Android-first onboarding, provider profiles, invite-based activation, control-plane directory authority, external contacts/guest access, basic diagnostics, Mail.ru / VK Mail baseline, manual/custom profile support, and safe directory sync.

## Later Impact

Deferred work includes background reliability, signed directory updates, broader provider/operator validation, audio transcription, advanced policy, and production distribution strategy.

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

## Open Decision Themes

- Thin Delta Chat Android fork vs custom shell over chatmail/core.
- GPL/MPL compliance and distribution model.
- First MVP provider list beyond Mail.ru / VK Mail.
- Directory authority and canonical payload.
- Invite policy and activation rules.
- External invite policy, visibility scopes, and reassignment behavior.
- Background reliability target.
- Branding, package identity, and distribution channel.
- Admin identity and role model.
