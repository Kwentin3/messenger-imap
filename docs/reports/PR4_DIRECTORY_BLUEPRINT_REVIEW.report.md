# PR #4 Directory Blueprint Review Report

Date: 2026-05-29

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

PR: `https://github.com/Kwentin3/messenger-imap/pull/4`

## 1. Executive Summary

PR #4 was reviewed as the Corporate Directory MVP Blueprint baseline.

Decision: accepted for content.

Blockers: none.

Important merge note: PR #4 is closed without a GitHub PR merge commit because its commits were fast-forwarded into `main` after PR #3. The PR head commit `937edaf00aee096411b977cba9e4a4095fab58e5` is contained in `main`. GitHub could not retarget PR #4 to `main` with a remaining diff after that fast-forward consolidation.

The review found docs-only Blueprint changes, no code/build/deployment changes, no APK artifacts, and no secrets. The Blueprint builds on the accepted Control Plane Blueprint and defines Directory manifest/snapshot, version/hash, canonical payload rules, visible directory, stale states, managed roster authority, and internal/external separation.

## 2. PR Metadata

| Field | Value |
| --- | --- |
| PR number | `#4` |
| Title | `Add Corporate Directory MVP Blueprint` |
| State | `CLOSED` |
| Base at close | `blueprint/control-plane-mvp` |
| Head | `blueprint/corporate-directory-mvp` |
| Author | `Kwentin3` |
| Changed files | `5` |
| Additions / deletions | `1317 / 3` |
| PR merge commit | None |
| Head commits | `ff0f045161b28b0ad758de86ddec07ba1f3a614e`, `937edaf00aee096411b977cba9e4a4095fab58e5` |
| Closed at | `2026-05-26T12:34:50Z` |

PR body summary:

- add Corporate Directory MVP Blueprint;
- add Blueprint report;
- update roadmap status and docs index.

PR body dependency:

- stacked on PR #3 / branch `blueprint/control-plane-mvp`.

PR body checks:

- docs-only changes;
- no code, SQL, OpenAPI, APK, build artifacts, or deployment changes;
- secret scan reviewed, with matches only in documentation terms.

## 3. Files Reviewed

Changed by PR #4 content:

- `docs/README.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT_REPORT.md`
- `docs/reports/2026-05-26/CORPORATE_DIRECTORY_MVP_BLUEPRINT_DELIVERY.report.md`
- `docs/roadmap/PROJECT_ROADMAP.md`

Source documents used for review:

- `docs/roadmap/PROJECT_ROADMAP.md`
- `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md`
- `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md`
- `docs/product/decisions/PRODUCT_DECISIONS_LOG.md`
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`
- `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md`
- `docs/product/domains/PRD_CORPORATE_DIRECTORY.md`
- `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md`
- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`
- `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md`
- `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md`
- `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md`
- `docs/infrastructure/SERVER_AUDIT_REPORT.md`
- `docs/research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md`
- `docs/hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md`
- `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT_REPORT.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT_REPORT.md`

## 4. Review Checklist

| Check | Result |
| --- | --- |
| Title reviewed | Pass |
| Body reviewed | Pass |
| Changed files reviewed | Pass |
| Diff reviewed | Pass |
| Mergeability reviewed | Pass for content; PR itself was closed after content was already in `main` |
| Base/head reviewed | Pass; stacked PR was `blueprint/control-plane-mvp` <- `blueprint/corporate-directory-mvp` |
| Docs-only changes | Pass |
| No secrets | Pass; matches were documentation/security terms only |
| No code/build artifacts | Pass |
| Blueprint relies on accepted Control Plane Blueprint | Pass |
| Defines `DirectoryManifest` | Pass |
| Defines `DirectorySnapshot` | Pass |
| Defines `directoryVersion` and `directoryHash` | Pass |
| Defines canonical payload rules | Pass |
| Defines visible directory per user/principal | Pass |
| Separates Internal Members and External Contacts | Pass |
| Describes employee/admin/support/external views | Pass |
| Describes pending/active/suspended/revoked statuses | Pass |
| Describes archived/reassigned external relationship states | Pass |
| Describes stale/expired/unavailable/hash_mismatch states | Pass |
| Describes sync model | Pass |
| Describes revocation/suspension behavior | Pass |
| Describes managed group roster authority | Pass |
| States historical local chat membership is not managed group authority | Pass |
| Describes trust/verification semantics | Pass |
| Prohibits silent arbitrary address book import | Pass |
| Defers signed IMAP/system-account updates | Pass |
| Defines Control Plane responsibilities | Pass |
| Defines Android client responsibilities | Pass |
| Includes audit/security/privacy | Pass |
| Does not write SQL/OpenAPI/Android UI/deployment | Pass |
| Does not contradict Control Plane Blueprint | Pass |

## 5. Findings

No blocking findings.

Notes:

- Directory authority is assigned to Control Plane publication, not local contacts, historical chats, provider address books, or vCard imports.
- Version/hash and canonical snapshot rules are present and tied to client verification.
- Stale, expired, unavailable, and hash mismatch states are explicit.
- External contacts receive scoped visible directory only and are not treated as internal members.
- Signed IMAP/system-account directory update delivery remains later scope.

Process note:

- PR #4 was not merged through GitHub's PR merge operation. Its content was consolidated into `main` by fast-forwarding through the PR head after PR #3, then PR #4 was closed with an explanatory comment.

## 6. Blockers

None.

## 7. Decision

Accepted for content.

## 8. Merge Result

Directory Blueprint content is present in `main`.

Main commit containing the PR #4 head:

```text
937edaf00aee096411b977cba9e4a4095fab58e5
```

GitHub PR #4 merge result:

```text
closed without PR merge commit
```

## 9. Main Branch Commit After Merge

`main` after Directory content consolidation:

```text
937edaf00aee096411b977cba9e4a4095fab58e5
```
