# Corporate Directory MVP Blueprint Delivery Report

Date: 2026-05-26

Status: Draft / delivery report

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Branch: `blueprint/corporate-directory-mvp`

Commit: `ff0f045161b28b0ad758de86ddec07ba1f3a614e`

Pull request: `https://github.com/Kwentin3/messenger-imap/pull/4`

## 1. Executive Summary

Corporate Directory MVP Blueprint was created as the next sequential architecture artifact after the Corporate Control Plane MVP Blueprint.

The Control Plane Blueprint PR #3 is still open and not merged into `main`. Because the Directory Blueprint depends on it, the Directory work was intentionally performed as a stacked branch:

```text
main
  -> blueprint/control-plane-mvp
      -> blueprint/corporate-directory-mvp
```

PR #4 targets `blueprint/control-plane-mvp`, not `main`, so the review diff stays focused on Directory changes and does not duplicate the Control Plane Blueprint diff.

No code, SQL, OpenAPI, Android implementation, deployment files, APK binaries, build artifacts, Traefik changes, server changes, or upstream Delta Chat / Chatmail changes were made.

## 2. Control Plane Blueprint Status Check

Before starting the Directory Blueprint, PR #3 was checked.

| Item | Value |
| --- | --- |
| PR | `https://github.com/Kwentin3/messenger-imap/pull/3` |
| State | `OPEN` |
| Merged | no |
| Base branch | `main` |
| Head branch | `blueprint/control-plane-mvp` |
| Head commit | `417006eb794c129069b0b80c8ab0fef8515e6c5a` |

Decision:

- Do not start Directory Blueprint from old `main`.
- Continue as a stacked branch from `blueprint/control-plane-mvp` because the owner explicitly confirmed executing the sequential steps.

## 3. Branching And PR Strategy

| Branch | Purpose | Status |
| --- | --- | --- |
| `main` | Current canonical branch before Control Plane merge | Does not yet contain PR #3 |
| `blueprint/control-plane-mvp` | Corporate Control Plane MVP Blueprint | PR #3 open |
| `blueprint/corporate-directory-mvp` | Corporate Directory MVP Blueprint | PR #4 open |

PR #4 details:

| Item | Value |
| --- | --- |
| PR | `https://github.com/Kwentin3/messenger-imap/pull/4` |
| Base branch | `blueprint/control-plane-mvp` |
| Head branch | `blueprint/corporate-directory-mvp` |
| Mergeable | yes |
| Commit | `ff0f045161b28b0ad758de86ddec07ba1f3a614e` |

Recommended merge order:

1. Review and merge PR #3 into `main`.
2. Retarget PR #4 to `main` or rebase/update it after PR #3 lands.
3. Review and merge PR #4.

## 4. Files Created

| File | Purpose |
| --- | --- |
| `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md` | Main technical MVP Blueprint for Corporate Directory. |
| `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT_REPORT.md` | Short report summarizing source docs, inherited decisions, open decisions, and next recommended Blueprint. |

## 5. Files Updated

| File | Update |
| --- | --- |
| `docs/roadmap/PROJECT_ROADMAP.md` | Stage 5 marked as drafted; links to Directory Blueprint and report added; next actions updated to review/accept Directory Blueprint. |
| `docs/README.md` | Links to Directory Blueprint and report added under key Blueprint documents. |

## 6. Source Documents Used

The Directory Blueprint used these source documents:

| Source | Role |
| --- | --- |
| `docs/roadmap/PROJECT_ROADMAP.md` | Execution order and current Blueprint sequence. |
| `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md` | Upstream architecture dependency for directory authority, manifest/snapshot ownership, stale policy, audit, and Android sync boundaries. |
| `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT_REPORT.md` | Confirms Corporate Directory as the recommended next Blueprint. |
| `docs/product/domains/PRD_CORPORATE_DIRECTORY.md` | Primary product requirements for Directory. |
| `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md` | Root product framing and MVP boundaries. |
| `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md` | Stale mode, canonical hash, multi-workspace, managed roster, and trust state refinements. |
| `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md` | Control Plane responsibilities and directory-related admin flows. |
| `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md` | Enrollment and first directory sync context. |
| `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md` | External contact visibility, reassignment, and no internal-directory exposure. |
| `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` | Android cache, directory sync, stale UX, contact picker, and managed group expectations. |
| `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md` | Provider profile scoping and separation from directory authority. |
| `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md` | Diagnostics evidence context and delayed upload. |
| `docs/product/decisions/PRODUCT_DECISIONS_LOG.md` | Accepted product decisions. |
| `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md` | Handoff constraints and do-not-redo list. |
| `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md` | Control Plane unavailable/stale mode and no deployment scope. |
| `docs/infrastructure/SERVER_AUDIT_REPORT.md` | Confirms deployment is later and must not be bundled into Directory Blueprint. |
| `docs/research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md` | Delta Chat / Chatmail contact, vCard, group primitives and limits. |
| `docs/hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md` | Product-layer mapping and provider-agnostic constraints. |

## 7. Key Decisions Captured

The Directory Blueprint captures these decisions:

- Directory is an authority model for identity and visibility, not just a contact list.
- Control Plane is the source of truth.
- Android local cache is not authority.
- Internal members and external contacts are separate spaces.
- External contacts must not receive internal directory.
- Visible directory is principal-scoped.
- Admin, employee, external contact, support, and auditor views are different.
- `DirectoryManifest` and `DirectorySnapshot` are required.
- `directoryVersion` is monotonic per organization/workspace.
- `directoryHash` is SHA-256 over canonical snapshot payload.
- Hash verifies integrity/change detection, not issuer authenticity.
- Signed snapshots and signed IMAP/system-account updates are later scope.
- Managed groups use the current active roster from Directory.
- Historical local chat membership is not managed group authority.
- Stale and expired directory states must be explicit.
- No silent arbitrary address book import.
- vCard/contact primitives are implementation tools, not corporate authority.

## 8. Blueprint Content Summary

The created Blueprint includes:

- document header and source docs;
- executive summary;
- inherited decisions;
- goals and non-goals;
- system context;
- directory authority model;
- core entities;
- directory spaces and visible views;
- internal and external status model;
- version/hash model;
- canonical payload rules;
- sync model;
- stale/expired behavior;
- revocation and suspension behavior;
- managed group roster enforcement;
- trust and verification semantics;
- manual import/vCard rules;
- Control Plane responsibilities;
- Android client responsibilities;
- audit events;
- security/privacy requirements;
- MVP scope;
- later scope;
- boundaries with other Blueprints;
- open questions;
- acceptance criteria.

## 9. Core Entities Defined

| Entity | Purpose |
| --- | --- |
| `DirectoryManifest` | Lightweight sync entry point with version/hash and snapshot reference. |
| `DirectorySnapshot` | Principal-scoped or authority-scoped directory payload. |
| `InternalMemberRecord` | Directory-visible internal member state. |
| `ExternalContactRecord` | Directory-visible external contact and relationship state. |
| `VisibleDirectoryView` | Derived principal-scoped view. |
| `ManagedGroupRecord` | Organization-managed group roster source. |
| `DirectoryPolicy` | Stale/expired and visibility behavior. |
| `LocalDirectoryCache` | Android-side accepted snapshot cache metadata. |

## 10. Status Behavior Captured

Internal member statuses:

- `pending`;
- `active`;
- `suspended`;
- `revoked`.

External contact statuses:

- `pending`;
- `active`;
- `suspended`;
- `revoked`;
- `archived`;
- `reassigned`.

The Blueprint defines how each status affects:

- employee directory visibility;
- admin visibility;
- external contact visibility;
- managed group eligibility;
- one-to-one messaging eligibility;
- cached client behavior.

## 11. Stale / Expired Directory Behavior Captured

Directory cache states:

- `fresh`;
- `stale`;
- `expired`;
- `unavailable`;
- `hash_mismatch`.

Key rules:

- Fresh state allows normal use.
- Stale state allows known-contact messaging if policy permits, with warnings.
- Expired state blocks or strongly restricts managed group sends.
- Control Plane unavailable means cached state may be used but cannot claim current membership.
- Hash mismatch rejects the new snapshot and keeps previous accepted cache only if policy allows.

## 12. Security And Privacy Notes

The Blueprint explicitly states:

- no internal directory exposure to external contacts;
- no secrets in directory payload;
- no app passwords in directory payload;
- no raw AUTH;
- no raw diagnostic logs;
- snapshots must be authorized per principal;
- support/auditor views are RBAC-limited;
- hash mismatch must not auto-apply;
- stale external cache must not expand visibility;
- directory data itself is sensitive business data.

## 13. Open Decisions Left

Open decisions listed in the Blueprint:

- exact `staleAfter` default value;
- exact `expiredAfter` default value;
- canonical JSON standard/library;
- full snapshot only vs MVP deltas;
- precomputed visible directory vs computed per request;
- external visible directory shape;
- suspended member visibility;
- local nickname behavior;
- whether manual vCard import is in MVP;
- managed group send behavior when stale;
- provider profile references in snapshot;
- release policy references in snapshot;
- audit retention for directory events;
- explicit admin publish vs automatic publish;
- multi-workspace UI representation.

## 14. Checks Performed

Checks performed before committing the Directory Blueprint:

```text
git status --short --branch
git diff --name-only
git diff --check
git diff --cached --name-only
git diff --cached --check
rg -n "BEGIN PRIVATE KEY|PRIVATE KEY|app password|AUTH PLAIN|token|secret|password" ...
```

Results:

| Check | Result |
| --- | --- |
| Docs-only changes | pass |
| `git diff --check` | pass |
| `git diff --cached --check` | pass |
| APK/build artifact scan | pass |
| Secret scan | pass; matches were documentation terms only |
| Code changes | none |
| Deployment/server changes | none |
| Android prototype changes | none |
| Delta Chat / Chatmail changes | none |

## 15. Git Output Summary

Directory Blueprint commit:

```text
ff0f045 Add Corporate Directory MVP Blueprint
```

Changed files:

```text
docs/README.md
docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md
docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT_REPORT.md
docs/roadmap/PROJECT_ROADMAP.md
```

PR:

```text
https://github.com/Kwentin3/messenger-imap/pull/4
```

## 16. Remaining Actions

Recommended next actions:

1. Review PR #3: Corporate Control Plane MVP Blueprint.
2. Merge PR #3 into `main` if accepted.
3. Retarget or update PR #4 against `main`.
4. Review PR #4: Corporate Directory MVP Blueprint.
5. After Directory Blueprint acceptance, write:

```text
docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md
```

## 17. Final Confirmation

This report confirms:

- Control Plane Blueprint status was checked before Directory work.
- Directory Blueprint was not started from old `main`.
- Directory work was performed as stacked PR #4.
- Directory Blueprint and report were created.
- Roadmap and docs index were updated.
- Work was docs-only.
- No code was changed.
- No deployment actions were performed.
- No server or Traefik changes were made.
- No APK/build artifacts were added.
- No secrets were added.
