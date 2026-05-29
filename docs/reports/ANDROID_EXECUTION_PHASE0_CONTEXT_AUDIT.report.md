# Android Execution Phase 0 Context Audit

Date: 2026-05-29

Phase: 0 - Project knowledge and documentation audit

Branch/repo: `android/autonomous-execution` in `Kwentin3/messenger-imap`

## Source Docs Used

Present in current branch:

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`
- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP_REPORT.md`
- `docs/roadmap/PROJECT_ROADMAP.md`
- `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md`
- `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md`
- `docs/product/decisions/PRODUCT_DECISIONS_LOG.md`
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`
- `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md`
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`
- `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md`
- `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md`
- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`
- `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md`
- `docs/infrastructure/SERVER_AUDIT_REPORT.md`
- `docs/research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md`
- `docs/hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md`
- `docs/upstream/UPSTREAM_PROJECTS.md`
- `docs/upstream/LICENSE_NOTES.md`

Found outside current branch/main:

- `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md` in `origin/docs/fork-strategy-decision`, PR #9.
- `docs/reports/PROJECT_PRE_IMPLEMENTATION_ANAMNESIS_AND_READINESS_AUDIT.report.md` in `origin/docs/pre-implementation-anamnesis-readiness-audit`, PR #8.
- `docs/reports/PROJECT_PRE_IMPLEMENTATION_ANAMNESIS_AND_READINESS_AUDIT_ADDENDUM_FORK_DECISION.report.md` in `origin/docs/fork-strategy-decision`, PR #9.
- `docs/blueprints/ANDROID_CLIENT_MVP_BLUEPRINT.md` in `origin/blueprint/android-client-mvp`, PR #6.

## Current Branch/PR State

Open PRs observed:

| PR | Branch | Base | Status | Mergeability | Role |
| --- | --- | --- | --- | --- | --- |
| #10 | `docs/android-messenger-autonomous-roadmap` | `main` | open draft | mergeable | Adds the autonomous Android roadmap used by this execution. |
| #9 | `docs/fork-strategy-decision` | `main` | open draft | mergeable | Contains accepted thin-fork decision docs. |
| #8 | `docs/pre-implementation-anamnesis-readiness-audit` | `main` | open draft | mergeable | Contains pre-implementation readiness audit. |
| #7 | `docs/pr3-pr4-invite-review-reports` | `main` | open draft | mergeable | Review/report follow-up docs. |
| #6 | `blueprint/android-client-mvp` | `main` | open | mergeable | Android Client MVP Blueprint draft. |

The GitHub repository default branch is currently `bootstrap/project-import`, but the project execution baseline and open PRs target `main`. This execution therefore treats `main` as the explicit product baseline branch and does not rely on repository default branch inference.

## Accepted Baselines

- Product is Android-first.
- Message transport remains IMAP/SMTP.
- Control Plane is not a message server.
- Control Plane owns organization, membership, invites, verification, directory metadata, provider profiles, diagnostics references, audit, and release metadata.
- Corporate Directory owns directory manifest/snapshot semantics, canonical hash, visible views, stale behavior, and internal/external separation.
- Invite Blueprint owns landing/app handoff, fallback code, email verification, provider setup handoff, diagnostics gate, activation, and first directory sync.
- APK download does not equal membership.
- Internal invite creates `Membership`.
- External invite creates `ExternalRelationship`.
- External contacts must not receive internal corporate directory.
- Mail.ru / VK Mail is accepted as first transport baseline, but product architecture must stay provider-agnostic.
- Delta Chat Android / chatmail capabilities are upstream baselines, not vendor-copied into `messenger-imap`.
- MVP Android implementation path is thin fork Delta Chat Android according to accepted task context and PR #9 branch docs.

## Open Blockers

- PR #9 fork strategy decision is not merged into `main`.
- PR #8 pre-implementation audit is not merged into `main`.
- PR #6 Android Client Blueprint is not merged and still contains pre-decision fork-vs-shell language.
- GPL/MPL compliance and source distribution workflow remain blockers before modified APK distribution.
- Android fork repository must be created or verified before fork build work.
- Clean Delta Chat Android fork build must be proven before product changes.
- APK signing, release storage, app identity, and deployment remain blocked by later decisions/Blueprints.
- Provider/Diagnostics exact in-client scope remains open.

## Missing Documents

No mandatory source was completely missing. Several required docs are not in `main`/current branch and were found in open branches/PRs as listed above.

## Contradictions

- Current `main`-derived Android PRD and Android Client Blueprint PR #6 still describe fork-vs-shell as open.
- PR #9 resolves fork-vs-shell for MVP, but that decision is not merged into `main`.
- Repository default branch is `bootstrap/project-import`, while project work is targeting `main`.

These contradictions do not block Phase 1 because the user task and PR #9 establish thin fork as accepted execution context. They must remain visible in reports until PR #9 is merged or otherwise reconciled.

## What Was Done

- Checked local branch/worktree state.
- Fetched origin.
- Listed open PRs and branch state.
- Checked mandatory document presence in the current branch.
- Located missing required docs in PR branches.
- Confirmed accepted Android path from task context and PR #9 branch docs.

## What Was Not Done

- No Android repo was created.
- No Delta Chat Android source was cloned into `messenger-imap`.
- No server, Traefik, Docker, or deployment action was performed.
- No code was changed.
- No PR was merged or retargeted.

## Files Changed

- `docs/reports/ANDROID_EXECUTION_PHASE0_CONTEXT_AUDIT.report.md`

## Commands/Checks Run

- `git status -sb`
- `git worktree list`
- `git branch --show-current`
- `git fetch origin`
- `gh pr list --repo Kwentin3/messenger-imap --state open`
- `gh repo view Kwentin3/messenger-imap`
- `git cat-file -e <branch>:<path>` for missing required docs
- required-path presence check with `Test-Path`

## Tests Run

No implementation tests were applicable in Phase 0.

## Acceptance Criteria Result

Pass:

- all required docs were reviewed or marked by availability;
- not-in-main docs were found in branches/PRs;
- fork decision was found and summarized;
- accepted Blueprints were identified;
- Android Client Blueprint PR #6 was identified;
- branch/PR state was identified;
- no implementation started;
- this report was created.

## Gate Result

Gate to Phase 1: passed.

The agent knows which docs are baseline, which docs are branch/PR-only, and which blockers remain.

## Blockers

No Phase 0 stop condition was triggered.

## Next Phase Decision

Proceed to Phase 1: repository and branch baseline audit.
