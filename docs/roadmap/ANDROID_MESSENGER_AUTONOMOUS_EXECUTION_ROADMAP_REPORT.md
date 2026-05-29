# Android Messenger Autonomous Execution Roadmap Report

Date: 2026-05-29

Status: Draft

Project: Corporate IMAP Messenger / `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

## 1. What Was Created

Created:

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`

The roadmap defines an autonomous, phase-gated execution path for building the Android messenger as a thin fork of Delta Chat Android. It is not implementation code and does not create the Android fork repository.

The roadmap includes:

- source document availability;
- core thin-fork decision;
- execution principles;
- phase overview table;
- detailed Phases 0-15;
- per-phase outputs;
- per-phase acceptance criteria;
- gate conditions;
- stop conditions;
- global reporting requirements;
- global roadmap acceptance criteria.

## 2. Source Docs Used

Documents present in `main` and used:

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

Documents found in open branches/PRs and used as non-main context:

- `docs/reports/PROJECT_PRE_IMPLEMENTATION_ANAMNESIS_AND_READINESS_AUDIT.report.md` from `origin/docs/pre-implementation-anamnesis-readiness-audit`, PR #8.
- `docs/reports/PROJECT_PRE_IMPLEMENTATION_ANAMNESIS_AND_READINESS_AUDIT_ADDENDUM_FORK_DECISION.report.md` from `origin/docs/fork-strategy-decision`, PR #9.
- `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md` from `origin/docs/fork-strategy-decision`, PR #9.
- `docs/blueprints/ANDROID_CLIENT_MVP_BLUEPRINT.md` from `origin/blueprint/android-client-mvp`, PR #6.

## 3. Key Decisions Inherited

- MVP Android implementation path is a thin fork of Delta Chat Android.
- `Kwentin3/messenger-imap-android` is the future Android fork repository.
- `Kwentin3/messenger-imap` remains product/meta/docs/Control Plane coordination.
- Custom Android shell over `chatmail/core` is rejected for MVP.
- Android messenger from scratch is rejected for MVP.
- `chatmail/core` changes are forbidden without a separate Blueprint.
- Control Plane is not a message server.
- IMAP/SMTP remains message transport.
- APK download does not equal membership.
- Internal invite creates membership.
- External invite creates external relationship.
- Provider profiles must stay provider-agnostic.
- GPL/MPL compliance remains a release blocker before distributing modified APKs.

## 4. Main Open Dependencies

- PR #9 fork strategy decision should be merged or otherwise reconciled into `main`.
- PR #8 pre-implementation audit should be merged or otherwise treated as branch-only context.
- PR #6 Android Client MVP Blueprint should be reviewed/updated under the thin-fork assumption.
- GPL/source distribution workflow must be planned before distributing modified APKs.
- Android fork repository must be created or verified in a later phase.
- Clean Delta Chat Android build must be proven before product changes.
- Provider/Diagnostics Blueprint remains needed for exact diagnostic scope.
- Deployment Blueprint remains required before server/Traefik/deployment changes.
- APK signing and release storage remain unresolved.

## 5. Recommended Next Action

Execute Phase 0 of the new roadmap:

- create `docs/reports/ANDROID_EXECUTION_PHASE0_CONTEXT_AUDIT.report.md`;
- reconfirm open PR/branch state;
- determine whether PR #6, PR #8, and PR #9 are merged;
- identify the authoritative baseline for Android fork intake;
- record blockers before creating or touching the Android fork repository.

Recommended next Blueprint after Phase 0/1:

- `docs/blueprints/ANDROID_FORK_INTAKE_BLUEPRINT.md`

Reason: the strategic path is now thin fork, but implementation must first prove safe fork intake and clean build reproducibility before adding corporate product behavior.

## 6. Checks

This delivery is docs-only by design.

No Android code, server files, Traefik configuration, deployment files, upstream source, APK, AAB, keystore, `.env`, signing key, or raw logs are intended to be added.
