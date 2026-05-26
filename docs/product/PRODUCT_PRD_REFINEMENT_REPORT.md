# Product PRD Refinement Report

Date: 2026-05-26

Status: completed product documentation refinement before technical Blueprints

## 1. Executive Summary

The PRD package was refined to address Control Plane availability in whitelist/restricted-network mode, stale directory behavior, email ownership verification, multi-workspace scoping, managed group enforcement after revoke, trust and identity states, Control Plane RBAC, canonical directory hash rules, invite abuse controls, external contact reassignment UX, and app release lifecycle.

The updates remain product-level. No code, Android prototype files, Gradle files, upstream repositories, APKs, build artifacts, raw logs, or secrets were intentionally changed or added.

## 2. Files Created

| File | Purpose |
| --- | --- |
| `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md` | Cross-domain product review addendum for stale Control Plane mode, email verification, workspace scoping, group enforcement, trust/RBAC, directory hash, invite abuse, external reassignment, and release lifecycle |
| `docs/product/PRODUCT_PRD_REFINEMENT_REPORT.md` | Summary report for this PRD package refinement |

## 3. Files Updated

| File | Update summary |
| --- | --- |
| `docs/product/DOMAIN_PRD_INDEX.md` | Added review addendum reference, cross-domain refinement dependency, and updated later/non-goal framing |
| `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md` | Added review addendum link, Control Plane stale mode, email verification, workspace scoping, managed group enforcement, trust/release/invite refinements, APK-by-email fallback, and iOS out-of-scope note |
| `docs/product/domains/PRD_CORPORATE_DIRECTORY.md` | Added stale/expired directory behavior, signed fallback boundary, canonical hash rules, workspace scope, managed roster enforcement, and trust note |
| `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md` | Added whitelist availability limitations, RBAC matrix, email verification challenge, invite abuse controls, release lifecycle fields, APK-by-email fallback, iOS out-of-scope note, external reassignment workflow, and audit events |
| `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md` | Added email verification code flow, Control Plane-required activation, invite abuse controls, rate-limit/audit requirements, Android APK-by-email fallback, and iOS out-of-scope note |
| `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` | Added stale mode UX, email verification code entry, workspace scope placeholder, managed group stale roster behavior, release policy warnings, Android APK-by-email note, iOS out-of-scope note, trust state UI, and external reassignment UX |
| `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md` | Added organization/workspace and network-context scoping, stale Control Plane behavior, delayed profile changes, and separation of email verification from transport diagnostics |
| `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md` | Added local diagnostics while Control Plane is unavailable, delayed upload behavior, and separation of diagnostics from enrollment verification |
| `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md` | Added external email verification, delayed activation when Control Plane unavailable, stale external visible directory behavior, reassignment UX, and external invite abuse controls |
| `docs/product/decisions/PRODUCT_DECISIONS_LOG.md` | Added decisions for stale Control Plane mode, primary HTTPS sync, later signed IMAP/system-account fallback, email verification, workspace scoping, managed group enforcement, trust states, RBAC, Android APK-by-email fallback, and iOS out of scope |
| `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md` | Added review addendum reference and future Blueprint requirements |
| `docs/product/PRODUCT_PRD_PACKAGE_REPORT.md` | Added review addendum status, updated package scope, added remaining open questions and Blueprint warnings |

## 4. Decisions Added

| Decision | Result |
| --- | --- |
| Control Plane may be unavailable in whitelist mode | Stale directory/policy cache mode is required |
| Primary directory sync path | HTTPS Control Plane remains primary MVP path |
| IMAP/system-account control updates | Later/fallback scope unless explicitly selected |
| Email ownership proof | Verification code/challenge required for membership or external relationship activation |
| Multi-workspace readiness | Data model and PRDs must not block organization/workspace scoping |
| Managed groups after revoke | Managed sends must use current active roster, not stale historical membership |
| Trust/identity model | Installed app, invite, email verification, membership, external contact, imported contact, and cryptographic verification are separate states |
| Control Plane RBAC | Owner/Admin/Manager/Support/IT/Auditor matrix required |
| APK-by-email | Android emergency fallback only, not primary distribution |
| iOS | Out of current scope |

## 5. Product Risks Reduced

| Risk | Reduction |
| --- | --- |
| Assuming Control Plane works in whitelist mode | Docs now explicitly say Control Plane may be unavailable while IMAP/SMTP can continue |
| Stale directory misuse | Stale/expired concepts, warnings, and managed group restrictions are documented |
| Wrong mailbox enrollment | Email verification code/challenge is required for activation |
| Global single-organization assumption | Organization/workspace scoping is documented across PRDs |
| Revoked member still receiving managed group sends | Managed group current-roster enforcement is documented |
| Trust state confusion | Trust/identity state model distinguishes app install, invite, verification, membership, external relationship, imported contact, and cryptographic verification |
| Admin role ambiguity | Product-level RBAC matrix added |
| Directory hash ambiguity | Canonical payload principles added |
| Invite abuse | Expiry, max uses, expected email/domain, rate limits, audit, and revoke controls documented |
| Unsafe release distribution | App release lifecycle and Android-only APK-by-email fallback documented |

## 6. Remaining Open Questions

- What exact values should `directoryStaleAfter` and `directoryExpiredAfter` use in MVP?
- Which actions are blocked vs warned when directory or policy state is stale?
- Does MVP allow one active workspace UI or multiple workspace switching?
- Should the app ever read email verification challenges through IMAP, or should manual code entry remain the only path?
- Which roles can create external invites by default?
- Which external visibility scopes require admin approval?
- What exact permission keys map from the Control Plane RBAC matrix?
- What are initial `minSupportedVersion`, `forceUpgradeBelowVersion`, `deprecatedVersion`, and `blockedVersion` policies?
- How should SecureJoin or equivalent cryptographic verification be exposed if selected?
- What is the exact external reassignment chat/history behavior?

## 7. Recommended Next Blueprint Order

1. Corporate Control Plane Blueprint: RBAC, invites, email verification, stale sync model, audit, app releases, and external reassignment.
2. Corporate Directory Blueprint: canonical hash payload, workspace scope, stale/expired directory state, managed group roster enforcement, and signed fallback boundary.
3. Android Messenger Client Blueprint: stale UX, email code entry, trust state display, workspace handling, managed group warnings, release policy, and external reassignment UX.
4. Invite Onboarding & Distribution Blueprint: internal/external invite activation, abuse controls, rate limits, and Android installation fallback.
5. External Contacts & Guest Access Blueprint: external relationship lifecycle, reassignment, stale visible directory, and no-internal-directory enforcement.
6. Provider / Diagnostics Blueprint slice: provider profile scoping, diagnostic upload delay, and separation from email verification.
