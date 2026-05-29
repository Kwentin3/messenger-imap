# Corporate IMAP Messenger Domain PRD Index

Date: 2026-05-14

Status: high-level product documentation index.

Root document: [Corporate IMAP Messenger Root PRD](PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)

Review addendum: [Product PRD Review Addendum](PRODUCT_PRD_REVIEW_ADDENDUM.md)

## Domain Map

| Domain | PRD file | Purpose | MVP priority | Owner TBD | Key dependencies | Status |
|---|---|---|---|---|---|---|
| Android Messenger Client | [PRD_ANDROID_MESSENGER_CLIENT.md](domains/PRD_ANDROID_MESSENGER_CLIENT.md) | Defines the Android-first user client, onboarding, provider setup, messaging, directory use, diagnostics entry points, and credential/security expectations. | P0 | TBD | Provider profiles, invite onboarding, corporate directory, diagnostics, accepted thin Delta Chat Android fork decision | Draft PRD |
| Corporate Control Plane | [PRD_CORPORATE_CONTROL_PLANE.md](domains/PRD_CORPORATE_CONTROL_PLANE.md) | Defines the admin/backend product for organizations, members, invites, directory, provider profiles, app releases, policies, and diagnostic status. | P0 | TBD | Directory authority, invite policy, app distribution, admin roles | Draft PRD |
| Corporate Directory | [PRD_CORPORATE_DIRECTORY.md](domains/PRD_CORPORATE_DIRECTORY.md) | Defines the centrally managed directory, member statuses, version/hash sync, revocation behavior, managed groups, and local cache rules. | P0 | TBD | Control plane, membership lifecycle, client sync, trust model | Draft PRD |
| Invite Onboarding & Distribution | [PRD_INVITE_ONBOARDING_DISTRIBUTION.md](domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md) | Defines assisted one-shot onboarding, invite tokens, APK download, deep link/fallback code, enrollment, membership activation, and external relationship activation. | P0 | TBD | Control plane, Android client, provider setup, diagnostics, app release management, external contacts | Draft PRD |
| Provider Transport Profiles | [PRD_PROVIDER_TRANSPORT_PROFILES.md](domains/PRD_PROVIDER_TRANSPORT_PROFILES.md) | Defines IMAP/SMTP provider profiles, Mail.ru / VK Mail baseline, Yandex/Rambler candidates, manual/custom profiles, and diagnostic status. | P0 | TBD | Diagnostics, Android setup flow, control plane policy, evidence artifacts | Draft PRD |
| Diagnostics & Transport Verification | [PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md](domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md) | Defines transport checks, sanitized reports, provider verification status, onboarding check action, support flow, and admin visibility. | P0 | TBD | Standalone diagnostics evidence, provider profiles, Android client, admin portal | Draft PRD |
| External Contacts & Guest Access | [PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md](domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md) | Defines client, supplier, partner, contractor, and other counterparty communication without turning external contacts into employees or exposing the internal directory. | P0 | TBD | Corporate Directory, Invite Onboarding & Distribution, Corporate Control Plane, Android Messenger Client | Draft PRD |

## MVP Dependencies

The MVP requires all seven domains to work together:

- Android client cannot activate a corporate user without invite onboarding, provider setup, diagnostics, and first directory sync.
- Control plane is required for membership, directory authority, invites, provider defaults, app releases, and diagnostic visibility.
- Corporate directory is the main B2B differentiator and must not be replaced by unsafe silent contact import.
- Provider profiles keep the product provider-agnostic and prevent Mail.ru-only architecture.
- Diagnostics provide evidence for provider trust and whitelist-ready status.
- Distribution and enrollment keep APK access separate from organization membership.
- External Contacts & Guest Access keeps client/counterparty communication separate from employee membership and prevents internal directory exposure to guests.
- Product PRD Review Addendum defines cross-domain refinements for Control Plane stale mode, email verification, workspace scoping, managed group enforcement, trust/RBAC, invite abuse, and app release lifecycle.

## Later / Deferred Themes

- Production-grade background and locked-screen reliability.
- Signed directory updates and stronger replay protection.
- Signed IMAP/system-account directory/policy fallback if selected after MVP.
- Multi-workspace UI if deferred from MVP.
- Broader provider/operator/region validation matrix.
- Advanced policy management.
- External organizations, project rooms, and CRM/helpdesk integrations.
- Full managed distribution or app store strategy.
- Audio transcription and richer media processing.
- Additional platforms.
- iOS distribution path.

## Non-goals for This PRD Package

- No technical Blueprint.
- No code.
- No UI mockups.
- No detailed API specification.
- No database schema.
- No Delta Chat Android fork changes beyond the documented intake slice.
- No changes to chatmail/core.
- No promise that all providers are whitelist-ready.
- No production-ready claim.
- No assumption that Control Plane is reachable in whitelist mode.
- No iOS support in current scope.

## Cross-Document Conventions

Every domain PRD should preserve the same product framing:

- MVP: minimum coherent product scope for first usable corporate IMAP/SMTP messenger.
- Later: capabilities that require more evidence, policy, trust design, or technical hardening.
- Non-goals: explicit exclusions to avoid scope drift and unsafe assumptions.

Open decisions are tracked centrally in the [Product Decisions Log](decisions/PRODUCT_DECISIONS_LOG.md) and summarized for future work in the [Product Context Handoff](handoff/PRODUCT_CONTEXT_HANDOFF.md).
