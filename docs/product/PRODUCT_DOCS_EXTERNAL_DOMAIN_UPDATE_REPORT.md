# Product Docs External Domain Update Report

Date: 2026-05-26

Scope: product documentation update for External Contacts & Guest Access before GitHub bootstrap/import.

## 1. Executive Summary

The missing External Contacts & Guest Access domain has been completed as a full product PRD and integrated into the product documentation package.

The PRD package now distinguishes internal employee membership from external contact relationships:

- Internal invite creates `Membership`.
- External invite creates `ExternalRelationship`.
- External contacts do not receive the internal corporate directory.
- External contacts are organization-controlled relationships and can be revoked, archived, suspended, or reassigned.

No code, Android Diagnostics APK changes, Delta Chat Android changes, or chatmail/core changes were made.

## 2. Files Created

| File | Purpose |
|---|---|
| `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md` | Full product PRD for clients, suppliers, partners, contractors, guest access, external relationships, visibility scopes, invites, reassignment, control-plane requirements, MVP scope, later scope, acceptance criteria, and open questions |
| `docs/product/PRODUCT_DOCS_EXTERNAL_DOMAIN_UPDATE_REPORT.md` | This consistency and update report |

## 3. Files Updated

| File | Update summary |
|---|---|
| `docs/product/DOMAIN_PRD_INDEX.md` | Added External Contacts & Guest Access as the seventh product domain with dependencies and MVP impact |
| `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md` | Added external contacts to product scope, principles, domains, MVP/later/non-goals, risks, open decisions, and linked domain PRDs |
| `docs/product/domains/PRD_CORPORATE_DIRECTORY.md` | Added Internal Members vs External Contacts separation, Visible Directory, external sections, scopes, revocation/reassignment effects, and no internal-directory exposure |
| `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md` | Added Internal Organization Invite vs External Contact Invite, external join flow, constraints, and rule that external invite creates `ExternalRelationship`, not `Membership` |
| `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md` | Added external contact management, external invites, external relationship lifecycle, reassignment, revoke/archive/suspend, policies, and audit events |
| `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` | Added external invite handling, external contact badge/warnings, external directory section, scoped directory for external users, and no internal directory exposure |
| `docs/product/decisions/PRODUCT_DECISIONS_LOG.md` | Added accepted decisions for external contacts, invite separation, `ExternalRelationship`, no internal directory exposure, and organization-owned reassignment |
| `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md` | Added external contacts as part of product context and future Blueprint requirements |
| `docs/product/PRODUCT_PRD_PACKAGE_REPORT.md` | Updated PRD package report from six to seven domains and added external domain open questions |

## 4. Decisions Added

| Decision | Result |
|---|---|
| Product supports external contacts and counterparties | External Contacts & Guest Access is now a first-class product domain |
| Internal invite and external invite are different | Invite type must be explicit in product and future Blueprint |
| External invite creates `ExternalRelationship`, not `Membership` | External invite cannot activate employee membership |
| External contacts do not receive internal corporate directory | Directory and client must provide scoped visible-directory views |
| External contacts belong to the organization and can be reassigned | Admins can revoke, archive, suspend, or reassign relationships |

## 5. Consistency Checks

| Check | Result |
|---|---|
| No contradiction that all invited users become employees | Passed: invite docs and root PRD distinguish internal and external invites |
| Internal Member and External Contact are separated | Passed: directory, client, control plane, and external PRD define separate concepts |
| APK download does not equal membership | Preserved |
| External invite does not expose internal directory | Passed: repeated in root, directory, invite, client, control plane, external PRD |
| Corporate Directory supports different visibility | Passed: Visible Directory and external visibility scopes added |
| Control Plane manages external contacts | Passed: external contact management and audit requirements added |
| Invite PRD distinguishes link types | Passed: Internal Organization Invite and External Contact Invite added |
| Provider model is not mixed with external contact model | Passed: external relationship is product/contact scope; provider setup remains transport scope |

## 6. Contradictions Fixed

Before this update, the PRD package described invite onboarding mainly as employee membership activation and did not define how client/counterparty communication should be represented.

Fixed product risks:

- External contacts are no longer implicitly forced into employee membership.
- External contacts are no longer absent from the directory model.
- Invite links are no longer treated as only employee enrollment artifacts.
- Control plane responsibilities now include external relationship lifecycle and reassignment.
- Android client requirements now include external badges, warnings, and scoped directory behavior.

## 7. Remaining Open Questions

- Who can invite external contacts in MVP: admin only, manager only, or configurable?
- Is admin approval required for external contacts or only for broad visibility scopes?
- Is email ownership verification required for external contacts?
- Can external contacts communicate with each other?
- Are external project rooms needed in MVP or later?
- How exactly should client reassignment work when a manager leaves?
- Is CRM integration required in early field trials?
- Should external contacts use the same APK and app mode as employees?
- What is the default external contact visibility scope?
- What audit retention period is required for external relationship events?

## 8. Bootstrap Readiness

The product PRD package is now ready for clean GitHub import as documentation.

Import must still preserve workspace hygiene:

- do not vendor-copy Delta Chat / Chatmail upstream repositories;
- do not import worktrees;
- do not import APK/build artifacts;
- do not import `.gradle`, `node_modules`, local DBs, raw logcat, credentials, or secrets.
