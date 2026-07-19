# Android Messenger Autonomous Execution Roadmap

Date: 2026-05-29

Status: Draft

Project: Corporate IMAP Messenger / `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Scope: autonomous execution roadmap for Android messenger implementation.

Related decision: MVP Android implementation path is a thin fork of Delta Chat Android.

## 1. Executive Summary

The project is moving from PRD and Blueprint preparation toward controlled implementation planning for the Android messenger. The accepted implementation direction is a thin fork of Delta Chat Android, not a custom Android messenger from scratch and not a custom shell over `chatmail/core` for MVP.

This roadmap is written as execution rails for an autonomous agent. It starts with knowledge and branch audit, then fork intake planning, Android fork setup, clean build proof, Delta Chat Android architecture audit, safe customization mapping, and only then product implementation slices. Each phase has outputs, acceptance criteria, gate conditions, and stop conditions.

The roadmap intentionally keeps early work boring: audit before action, build before modification, Blueprint before code, and report after every phase. The Android fork must preserve upstream relationship, avoid early rebrand/package rename, keep provider-agnostic architecture, avoid Mail.ru-only shortcuts, and forbid early changes to `chatmail/core`, JNI, sync, encryption, MIME, database migrations, and release/signing pipelines.

## 2. Source Documents And Availability

The source set below was reviewed for this roadmap. Some required governance documents are not in `main` yet; where that is true, this roadmap treats the document as branch/PR context and requires Phase 0 to reconfirm its status before implementation.

| Source document | Availability used for this roadmap | Notes |
| --- | --- | --- |
| `docs/roadmap/PROJECT_ROADMAP.md` | Present in `main` | This branch updates it with autonomous Android roadmap links and thin-fork execution wording. |
| `docs/reports/PROJECT_PRE_IMPLEMENTATION_ANAMNESIS_AND_READINESS_AUDIT.report.md` | Found in branch `origin/docs/pre-implementation-anamnesis-readiness-audit`, PR #8; not in `main` | Verdict was `READY_WITH_BLOCKERS`; fork-vs-shell was a blocker there. |
| `docs/reports/PROJECT_PRE_IMPLEMENTATION_ANAMNESIS_AND_READINESS_AUDIT_ADDENDUM_FORK_DECISION.report.md` | Found in branch `origin/docs/fork-strategy-decision`, PR #9; not in `main` | Addendum marks fork-vs-shell resolved for MVP and keeps compliance/release/deployment blockers. |
| `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md` | Found in branch `origin/docs/fork-strategy-decision`, PR #9; not in `main` | Accepted decision: thin fork Delta Chat Android. |
| `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md` | Present in `main` | Defines Android-first IMAP/SMTP messenger, Control Plane, Directory, invites, external contacts, provider diagnostics, and risks. |
| `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md` | Present in `main` | Adds stale mode, verification, RBAC, directory hash, invite abuse, release lifecycle. |
| `docs/product/decisions/PRODUCT_DECISIONS_LOG.md` | Present in `main` | Accepted product decisions; fork decision entry expected after PR #9. |
| `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md` | Present in `main` | Handoff still contains pre-fork-decision wording in `main`; this roadmap updates references. |
| `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md` | Present in `main` | Accepted Control Plane authority baseline. |
| `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md` | Present in `main` | Accepted Directory sync and authority baseline. |
| `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md` | Present in `main` | Invite/distribution flow baseline for Android onboarding. |
| `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` | Present in `main` | Android PRD still says fork-vs-shell is undecided; Phase 0 must reconcile with accepted decision. |
| `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md` | Present in `main` | Provider-agnostic profile and diagnostics status requirements. |
| `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md` | Present in `main` | Transport diagnostics scope, Check Transport, sanitized reports. |
| `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md` | Present in `main` | External relationship, badges, scoped visibility, no internal directory exposure. |
| `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md` | Present in `main` | Domain, Traefik, server, database isolation, secrets, APK distribution constraints. |
| `docs/infrastructure/SERVER_AUDIT_REPORT.md` | Present in `main` | Read-only server audit; no deployment approval. |
| `docs/research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md` | Present in `main` | Delta Chat / chatmail capability baseline and license risk. |
| `docs/hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md` | Present in `main` | Existing capabilities, safe reuse paths, high-risk areas. |
| `docs/upstream/UPSTREAM_PROJECTS.md` | Present in `main` | Upstream projects are references only; no vendor-copy into this repo. |
| `docs/upstream/LICENSE_NOTES.md` | Present in `main` | GPL/MPL obligations remain release blockers. |
| `docs/blueprints/ANDROID_CLIENT_MVP_BLUEPRINT.md` | Found in branch `origin/blueprint/android-client-mvp`, PR #6; not in `main` | Draft Blueprint must be reviewed/updated under thin-fork assumption. |

## 3. Core Implementation Decision

MVP uses a thin fork of Delta Chat Android.

Rejected for MVP:

- custom Android shell over `chatmail/core`;
- Android messenger from scratch;
- modifying `chatmail/core` as the first step.

Repository boundary:

- `Kwentin3/messenger-imap` remains the product, meta, documentation, Control Plane coordination, and implementation coordination repository.
- `Kwentin3/messenger-imap-android` is the future Android fork repository.
- Delta Chat Android must not be vendor-copied into `messenger-imap`.
- The Android fork must preserve a proper upstream relationship and record upstream commits.
- Control Plane remains a separate backend/web/admin layer and is not the message server.
- Messages continue to use IMAP/SMTP transport.

Forbidden without a later Blueprint:

- `chatmail/core` changes;
- JNI boundary changes;
- sync engine changes;
- encryption/SecureJoin changes;
- MIME pipeline changes;
- database migrations that alter upstream semantics;
- deep upstream rewrites;
- signing/release pipeline changes;
- deployment/server/Traefik changes.

## 4. Execution Principles

- Audit before action.
- Build before modification.
- Blueprint before code.
- Minimal changes before product features.
- Isolate corporate code where the Android app structure permits it.
- Preserve upstream relationship and track upstream commits.
- Keep provider-agnostic architecture.
- Do not hardcode Mail.ru-only flows.
- Do not claim whitelist-ready status without diagnostics.
- Do not store secrets, `.env`, provider credentials, app passwords, private keys, signing keys, raw AUTH payloads, or raw logs in git.
- Do not commit APK, AAB, build outputs, keystores, or signing artifacts.
- Do not deploy during Android fork work.
- Do not change server, Traefik, DNS, or Docker stacks during Android fork work.
- Do not change `chatmail/core` in early phases.
- Do not rebrand, rename package ID, rename app, replace icons, or create release identity before clean build.
- Do not request Android contacts permission in MVP unless a later accepted Blueprint justifies it.
- Keep APK download separate from membership.
- Keep internal membership separate from external relationship.
- Every phase must have acceptance criteria.
- Every phase must produce a report.
- Gate transitions must be explicit.

## 5. Phase Overview

| Phase | Name | Purpose | Output | Gate to next phase |
| --- | --- | --- | --- | --- |
| 0 | Project knowledge and documentation audit | Establish the current documented truth before action. | `docs/reports/ANDROID_EXECUTION_PHASE0_CONTEXT_AUDIT.report.md` | Baseline docs, open PRs, missing docs, and blockers are known. |
| 1 | Repository and branch baseline audit | Determine where work should happen and what is authoritative. | `docs/reports/ANDROID_EXECUTION_PHASE1_REPO_BASELINE.report.md` | `main` or explicit baseline branch is identified. |
| 2 | Android fork intake planning | Define safe build-only fork intake before repo work. | `docs/blueprints/ANDROID_FORK_INTAKE_BLUEPRINT.md` and report | Intake plan is complete and no blocker prevents repo creation. |
| 3 | Android fork repository creation and upstream wiring | Create/verify Android fork repo and remotes. | `ANDROID_FORK_REPOSITORY_SETUP.report.md` | Repo/remotes/branch are correct. |
| 4 | Clean upstream/fork build baseline | Prove the fork builds before product changes. | `ANDROID_FORK_BUILD_BASELINE.report.md` | Clean build passes, or architecture-only reading is explicitly limited. |
| 5 | Delta Chat Android architecture audit | Understand app structure and risk zones. | `ANDROID_FORK_ARCHITECTURE_AUDIT.report.md`; `docs/hand_off/ANDROID_FORK_SAFE_EXTENSION_MAP.md` | Safe extension map exists. |
| 6 | Safe customization map and corporate extension points | Map corporate features to safe app/product-layer extension points. | `docs/blueprints/ANDROID_FORK_SAFE_CUSTOMIZATION_BLUEPRINT.md` | MVP slices can be implemented without high-risk upstream changes. |
| 7 | Android MVP implementation plan | Convert accepted boundaries into ordered autonomous slices. | `docs/implementation/ANDROID_CLIENT_MVP_IMPLEMENTATION_PLAN.md` | First implementation slice is low-risk and independently testable. |
| 8 | Corporate onboarding foundation slice | Add minimal corporate onboarding entry without activation authority. | Slice report | App builds; existing Delta Chat flows still work. |
| 9 | Provider profile and transport check slice | Add provider profile wrapper and Check Transport entry. | Slice report | Provider-agnostic setup remains intact. |
| 10 | Corporate directory read-only sync slice | Add read-only manifest/snapshot client adapter. | Slice report | Directory cache states are represented and build/tests pass. |
| 11 | Invite deep link / fallback code slice | Add app-side invite token/code entry. | Slice report | Invite types are represented without token leakage. |
| 12 | External contact badge / warning slice | Separate external contacts visually and behaviorally. | Slice report | External contacts do not mix with internal members. |
| 13 | Release metadata / update warning slice | Add release policy representation without auto-update/signing. | Slice report | Version policy can warn/block according to metadata. |
| 14 | Integration test and QA hardening | Verify corporate changes do not break base messenger behavior. | `ANDROID_MVP_INTEGRATION_QA_REPORT.md` | Smoke/regression checks pass or blockers are explicit. |
| 15 | MVP handoff and next-stage planning | Consolidate results, blockers, divergence, compliance, next stage. | `ANDROID_MVP_HANDOFF_REPORT.md` | Handoff is complete and no hidden work remains. |

## 6. Phase 0: Project Knowledge And Documentation Audit

Purpose: collect all current project knowledge before any implementation, repository creation, or fork action.

Agent must:

- read all source documents listed in this roadmap;
- check which required docs are in `main`;
- find required docs in open branches/PRs when missing from `main`;
- identify current open PRs and branch dependencies;
- determine which documents are accepted baseline and which are draft/open;
- confirm that the thin Delta Chat Android fork decision is accepted or record if it is still only in an open PR;
- identify which Blueprints are accepted/merged;
- inspect Android Client MVP Blueprint PR #6 if it exists;
- collect remaining blockers, especially GPL/MPL compliance, signing, release storage, deployment, diagnostics policy, and backend stack;
- record contradictions such as `main` docs still saying fork-vs-shell is open.

Output:

- `docs/reports/ANDROID_EXECUTION_PHASE0_CONTEXT_AUDIT.report.md`

Report sections:

- documents read;
- current branch/PR state;
- accepted baselines;
- open blockers;
- missing documents;
- contradictions;
- recommended baseline for next phases.

Acceptance criteria:

- all required docs are reviewed or marked missing;
- not-in-main docs are located by branch/PR and not invented;
- fork decision is found and summarized;
- accepted Blueprints are identified;
- Android Client Blueprint PR #6 status is identified if it exists;
- current branch state is identified;
- no implementation starts;
- report is created.

Gate to Phase 1: Phase 0 is accepted only if the agent can state which docs are baseline, which docs are drafts/open PRs, and which blockers remain.

## 7. Phase 1: Repository And Branch Baseline Audit

Purpose: understand the GitHub repository state and decide where the Android execution route is coordinated.

Agent must:

- check the default branch and confirm whether `main` is authoritative for product docs;
- check open PRs, especially PR #6 Android Client Blueprint and docs/fork-decision PRs if still open;
- check whether the pre-implementation audit is merged;
- list stale branches and likely duplicate docs without deleting anything;
- identify whether a new Android execution branch should be based on `main` or another explicit baseline branch;
- verify that `docs/out` or other local buffers are not accidentally staged;
- avoid merging, retargeting, deleting, or force-pushing branches unless the task owner explicitly authorizes it.

Output:

- `docs/reports/ANDROID_EXECUTION_PHASE1_REPO_BASELINE.report.md`

Acceptance criteria:

- `main`/default branch status is known;
- open PRs are listed with base/head/status;
- branch cleanup recommendations are listed without performing deletion;
- baseline branch for Phase 2 is identified;
- no merge is performed unless explicitly allowed;
- no code changes are made.

Gate to Phase 2: proceed only if `main` or an explicit baseline branch is identified and there is no unresolved PR dependency that invalidates Android fork intake planning.

## 8. Phase 2: Android Fork Intake Planning

Purpose: design the first safe Android fork intake without making product changes.

Agent must create:

- `docs/blueprints/ANDROID_FORK_INTAKE_BLUEPRINT.md`
- `docs/blueprints/ANDROID_FORK_INTAKE_BLUEPRINT_REPORT.md`

The Blueprint must include:

- target repo: `Kwentin3/messenger-imap-android`;
- upstream repo: official Delta Chat Android, currently documented as `https://github.com/deltachat/deltachat-android.git`;
- fork/upstream remote strategy;
- branch strategy;
- build prerequisites;
- expected Android SDK, JDK, NDK, Rust, Gradle, and submodule requirements, based on upstream docs at the time of intake;
- upstream commit recording requirements;
- no package ID rename;
- no app rename;
- no branding changes;
- no icon changes;
- no Control Plane integration;
- no corporate features;
- no `chatmail/core` changes;
- local debug build only;
- report requirements;
- no APK committed to git;
- GPL/source distribution compliance notes.

Acceptance criteria:

- fork intake is build-only and reproducibility-focused;
- upstream relationship is preserved;
- forbidden changes are listed;
- build validation plan exists;
- license/compliance warning is explicit;
- no code is executed or changed unless explicitly required for planning.

Gate to Phase 3: proceed only if the intake Blueprint is complete and no blocker prevents repository creation or fork verification.

## 9. Phase 3: Android Fork Repository Creation And Upstream Wiring

Purpose: create or verify the proper Android fork repository and upstream remotes.

Agent must:

- create or verify `Kwentin3/messenger-imap-android` if permissions allow;
- if repo creation is not possible, create exact manual instructions;
- fork/clone Delta Chat Android properly, not vendor-copying it into `messenger-imap`;
- set remotes:
  - `origin` = `Kwentin3/messenger-imap-android`;
  - `upstream` = official Delta Chat Android;
- record upstream commit hash and date;
- create initial branch `intake/upstream-build-baseline`;
- avoid code changes and product changes.

Outputs:

- `docs/reports/ANDROID_FORK_REPOSITORY_SETUP.report.md` in `messenger-imap`, or equivalent report in the Android repo plus a link from `messenger-imap`;
- README/intake note in the Android repo if allowed.

Acceptance criteria:

- repo exists or exact manual creation instructions exist;
- upstream remote is recorded;
- origin remote is recorded;
- upstream commit is recorded;
- initial branch exists;
- no product changes are made;
- no upstream source is copied into `messenger-imap`;
- no secrets or build artifacts are committed.

Gate to Phase 4: proceed only if repo, remotes, and branch are correct.

## 10. Phase 4: Clean Upstream/Fork Build Baseline

Purpose: prove that the Delta Chat Android fork builds before any product modification.

Agent must:

- read upstream build docs;
- install or verify required build toolchain;
- initialize submodules if required by upstream;
- build the appropriate debug/FOSS variant if upstream provides variants;
- record exact commands;
- record environment;
- record build errors/warnings;
- avoid product logic fixes;
- avoid package/app rename;
- avoid branding;
- avoid Control Plane integration;
- avoid corporate features.

Output:

- `ANDROID_FORK_BUILD_BASELINE.report.md`

Report must include:

- OS;
- Java/JDK version;
- Android SDK/NDK versions;
- Rust version if required;
- Gradle version;
- upstream commit;
- build command;
- variant built;
- APK artifact path;
- build success/failure;
- blockers;
- remediation notes;
- confirmation that APK/build artifacts were not committed.

Acceptance criteria:

- clean build passes, or blockers are documented with precise error and upstream context;
- build command is reproducible;
- no product modifications are made;
- APK is not committed;
- report is created.

Gate to Phase 5: proceed if clean build passes. If build fails, Phase 5 may proceed only for read-only architecture audit, not implementation.

## 11. Phase 5: Delta Chat Android Architecture Audit

Purpose: understand the Android project structure and identify safe extension points before changing code.

Agent must audit:

- app module structure;
- build flavors and Gradle layout;
- account setup/onboarding;
- provider setup/manual IMAP/SMTP settings;
- deep link/app link handling;
- contacts/address book UI;
- group creation/member management;
- connectivity status;
- diagnostics/logging/export paths;
- settings;
- local storage/DB boundaries;
- JSON-RPC/FFI/JNI/core boundaries;
- notification/background areas;
- resources/themes/branding;
- license files and notices;
- test structure and available smoke tests.

Outputs:

- `docs/reports/ANDROID_FORK_ARCHITECTURE_AUDIT.report.md`
- `docs/hand_off/ANDROID_FORK_SAFE_EXTENSION_MAP.md`

Acceptance criteria:

- safe areas are identified with file/class references;
- high-risk areas are identified with file/class references;
- existing tests/build commands are listed;
- license files/notices are located;
- no code changes are made;
- no core changes are made;
- no implementation starts.

Gate to Phase 6: proceed only if a safe extension map exists and identifies likely files/modules for each corporate slice.

## 12. Phase 6: Safe Customization Map And Corporate Extension Points

Purpose: define where corporate functionality can be added without breaking upstream internals.

Agent must define safe extension points:

- onboarding entry;
- invite deep link handler;
- fallback code entry;
- provider profile wrapper;
- Check Transport entry point;
- corporate directory adapter;
- local directory cache adapter;
- external contact badge UI;
- managed group warning layer;
- release metadata check;
- support-safe diagnostics export.

Agent must define high-risk areas:

- `chatmail/core`;
- JNI/FFI boundary;
- sync engine;
- encryption/SecureJoin/Autocrypt;
- database migrations;
- message MIME pipeline;
- background service;
- notification internals;
- group protocol internals.

Output:

- `docs/blueprints/ANDROID_FORK_SAFE_CUSTOMIZATION_BLUEPRINT.md`

Acceptance criteria:

- each corporate feature is mapped to a safe extension point or explicitly blocked;
- forbidden areas are listed;
- each planned slice has likely files/modules;
- upstream divergence risk is described;
- provider-agnostic rule is preserved;
- no code changes are made.

Gate to Phase 7: proceed only if the safe customization map supports MVP slices without early high-risk upstream changes.

## 13. Phase 7: Android MVP Implementation Plan

Purpose: break Android fork implementation into autonomous, ordered, independently testable slices.

Agent must create:

- `docs/implementation/ANDROID_CLIENT_MVP_IMPLEMENTATION_PLAN.md`

The plan must include these slices:

1. Build baseline preserved.
2. Corporate onboarding entry placeholder.
3. Invite deep link / fallback code handling.
4. Provider profile policy integration placeholder.
5. Check Transport minimal integration.
6. Directory manifest/snapshot read-only sync adapter.
7. Stale directory UX.
8. Internal/external contact separation and badges.
9. Managed group roster warning layer.
10. Release metadata / update warning.
11. Support-safe diagnostics export.
12. QA and regression tests.

For each slice, define:

- purpose;
- inputs;
- likely files/modules affected;
- non-goals;
- acceptance criteria;
- tests;
- rollback/containment;
- dependencies;
- stop conditions.

Acceptance criteria:

- implementation plan exists;
- slices are ordered by risk and dependency;
- each slice is independently testable;
- first slice is low-risk;
- high-risk upstream changes are not required;
- no code changes are made.

Gate to Phase 8: proceed only if the implementation plan is clear, testable, and starts with a low-risk slice.

## 14. Phase 8: Corporate Onboarding Foundation Slice

Purpose: add a minimal corporate onboarding entry point without full membership activation.

Allowed implementation:

- new entry point or settings/debug-accessible screen;
- placeholder for corporate invite flow;
- local state names aligned with Invite Blueprint;
- no actual membership activation authority;
- no required live Control Plane dependency.

Non-goals:

- no full onboarding UI;
- no email verification implementation unless already planned for this slice;
- no provider credential upload;
- no directory access;
- no rebrand/package rename.

Acceptance criteria:

- app builds;
- existing Delta Chat account/chat flows are not broken;
- new entry point is visible only where intended;
- app does not imply invite equals membership;
- no secrets are introduced;
- relevant tests/smoke checks run;
- slice report is created.

Gate to Phase 9: proceed only if build and smoke checks pass and the onboarding entry is isolated.

## 15. Phase 9: Provider Profile And Transport Check Slice

Purpose: prepare provider profile wrapper and Check Transport entry while preserving provider-agnostic behavior.

Allowed implementation:

- product-layer wrapper around existing provider/manual setup;
- Mail.ru / VK Mail baseline label;
- candidate Yandex/Rambler wording as unverified unless diagnostics support them;
- manual/custom provider path preserved;
- Check Transport minimal entry using existing connectivity or a staged diagnostic adapter.

Non-goals:

- no Mail.ru-only architecture;
- no provider credentials in Control Plane;
- no raw log export;
- no full provider matrix;
- no background reliability claims.

Acceptance criteria:

- provider-agnostic setup remains available;
- custom/manual provider still works or remains clearly preserved;
- Mail.ru/VK Mail baseline is represented without hardcoding the product boundary;
- Check Transport does not expose secrets;
- app builds;
- tests/smoke checks run;
- slice report is created.

Gate to Phase 10: proceed only if provider setup remains safe and diagnostics scope is clear.

## 16. Phase 10: Corporate Directory Read-Only Sync Slice

Purpose: prepare Android client to consume `DirectoryManifest` and `DirectorySnapshot` read-only.

Allowed implementation:

- local models/adapters for manifest/snapshot;
- local fixture first if backend is not ready;
- hash verification test placeholder or implementation depending on available canonicalization decision;
- stale state representation;
- no Android authority over membership;
- no Control Plane writes unless already designed.

Non-goals:

- no server API implementation;
- no full directory UI redesign;
- no silent arbitrary address book import;
- no system contacts permission unless separately approved;
- no database migration unless the safe customization Blueprint explicitly permits it.

Acceptance criteria:

- directory data is scoped by organization/workspace;
- internal/external separation is represented;
- hash verification or a clearly marked test placeholder exists;
- stale/expired/hash-mismatch states are represented;
- app builds;
- parsing/canonical sample tests run where possible;
- slice report is created.

Gate to Phase 11: proceed only if read-only directory state can be represented without becoming authority.

## 17. Phase 11: Invite Deep Link / Fallback Code Slice

Purpose: implement app-side entry for invite token and fallback code.

Allowed implementation:

- deep link intent handling;
- fallback code entry;
- invite type display/state;
- local pending states for valid/invalid/expired/revoked/already used placeholders;
- safe handoff to future Control Plane resolution adapter.

Non-goals:

- no full activation without Control Plane;
- no token persistence in raw logs;
- no credentials in invite links;
- no QR implementation unless already selected by the implementation plan.

Acceptance criteria:

- internal and external invite types are represented separately;
- invalid/expired/revoked placeholder states are supported;
- token is not leaked in logs, crash reports, or reports;
- app builds;
- tests/smoke checks run;
- slice report is created.

Gate to Phase 12: proceed only if invite entry is safe, typed, and does not imply membership.

## 18. Phase 12: External Contact Badge / Warning Slice

Purpose: display external contacts separately and safely.

Allowed implementation:

- external badge in directory/contact/chat surfaces where safe;
- warning for external conversation;
- fixture-backed external contact state if backend is not ready;
- one-to-one external relationship representation.

Non-goals:

- no full external project rooms;
- no external organization admin portal;
- no internal directory visibility for external principals;
- no external contact in "All employees";
- no internal managed group membership for external contacts by default.

Acceptance criteria:

- external contacts are not mixed with internal members;
- external contacts are visibly marked in relevant safe surfaces;
- external principal fixture cannot see internal directory fixture;
- app builds;
- tests/smoke checks run;
- slice report is created.

Gate to Phase 13: proceed only if external/internal separation is visible and enforced in fixtures.

## 19. Phase 13: Release Metadata / Update Warning Slice

Purpose: prepare the client for release metadata policy without implementing signing or auto-update.

Allowed implementation:

- consume mocked or fixture release metadata;
- show deprecated/update/blocked warning where policy requires;
- represent version policy fields from PRD/Blueprint;
- keep APK download/install separate from membership.

Non-goals:

- no auto-update;
- no signing pipeline;
- no release APK distribution;
- no APK artifact committed;
- no app/package rename;
- no deployment/download backend changes.

Acceptance criteria:

- version policy fields are represented;
- app can warn/block according to fixture metadata;
- no APK download automation beyond explicitly allowed handoff behavior;
- app builds;
- tests/smoke checks run;
- slice report is created.

Gate to Phase 14: proceed only if release policy is represented without crossing into signing/distribution/deployment.

## 20. Phase 14: Integration Test And QA Hardening

Purpose: verify that corporate changes do not break basic Delta Chat behavior or safety constraints.

Agent must test:

- app launch;
- baseline account setup path;
- existing one-to-one chat flow where test environment permits;
- provider setup/manual provider path;
- invite entry;
- directory fixture parsing and stale state;
- internal/external separation;
- external badge/warning;
- managed group warning behavior if implemented;
- release metadata warning;
- no-secret logs in checked paths;
- build reproducibility.

Output:

- `ANDROID_MVP_INTEGRATION_QA_REPORT.md`

Acceptance criteria:

- smoke tests pass or blockers are documented with exact failure;
- basic messaging path is not regressed;
- no secret leakage is observed in checked paths;
- build is reproducible from documented commands;
- APK/build artifacts are not committed;
- report is created.

Gate to Phase 15: proceed only if QA report makes residual risks explicit and no critical regression remains hidden.

## 21. Phase 15: MVP Handoff And Next-Stage Planning

Purpose: consolidate implementation results and decide the next stage.

Output:

- `ANDROID_MVP_HANDOFF_REPORT.md`

Report must include:

- implemented slices;
- files/modules changed by slice;
- tests/checks run;
- remaining blockers;
- upstream divergence summary;
- license/compliance notes;
- release blockers;
- deployment blockers;
- known product gaps;
- next recommended slice;
- whether the Android client is ready for Control Plane integration testing.

Acceptance criteria:

- handoff report is complete;
- every implemented slice has a report;
- blockers are explicit;
- upstream divergence is understandable;
- compliance and release blockers are not hidden;
- no hidden work remains.

Gate after Phase 15: start next-stage planning only after the handoff report is accepted and release/deployment/compliance blockers are routed.

## 22. Stop Conditions

Agent must stop and report before proceeding if:

- Android fork repo cannot be created or accessed;
- upstream repo identity cannot be verified;
- clean build fails and cannot be resolved safely;
- required license/compliance issue blocks distribution planning;
- implementation requires `chatmail/core` changes;
- implementation requires JNI, sync, encryption, MIME, or database migration changes not covered by Blueprint;
- implementation requires secrets, signing keys, or real credentials;
- implementation requires server deployment before Deployment Blueprint;
- implementation requires Traefik/server changes;
- tests show existing Delta Chat basic messaging is broken;
- upstream code cannot be built reproducibly;
- external contacts would receive internal directory data;
- invite token handling would leak raw tokens into logs/reports;
- provider profile work becomes Mail.ru-only architecture;
- APK/build artifacts would be committed.

When a stop condition occurs, the agent must create a blocker report with:

- phase;
- exact failure;
- commands/checks run;
- files touched;
- rollback/containment status;
- safe workaround options;
- recommendation to continue, retry, or wait for owner decision.

## 23. Global Reporting Requirements

Every phase must produce a report file that states:

- what was done;
- what was not done;
- files changed;
- tests/checks run;
- secrets/artifact checks;
- blockers;
- open questions;
- gate result for the next phase.

Reports must not include:

- real provider passwords;
- app passwords;
- invite raw tokens;
- raw AUTH payloads;
- private keys;
- signing keys;
- real `.env`;
- raw logs with sensitive values;
- APK/AAB artifacts.

## 24. Global Acceptance Criteria For This Roadmap

This roadmap is accepted if:

1. It starts with knowledge/context audit.
2. It is autonomous and not dependent on manual review after every micro-slice.
3. It has ordered phases.
4. Each phase has outputs.
5. Each phase has acceptance criteria.
6. Each phase has gate conditions.
7. It preserves the thin Delta Chat Android fork decision.
8. It rejects custom shell and Android-from-scratch paths for MVP.
9. It forbids early `chatmail/core` changes.
10. It forbids early rebrand/package rename.
11. It protects upstream relationship.
12. It protects secrets and build artifacts.
13. It supports provider-agnostic architecture.
14. It delays deployment, APK distribution, and signing.
15. It keeps Control Plane separate from message transport.
16. It keeps APK download separate from membership.
17. It keeps internal membership separate from external relationship.
18. It ends with implementation handoff.

## 25. Immediate Next Action

The immediate next action after this roadmap is to execute Phase 0: create `docs/reports/ANDROID_EXECUTION_PHASE0_CONTEXT_AUDIT.report.md`, reconfirm whether PR #6, PR #8, and PR #9 are merged or still open, and select the authoritative baseline for Android fork intake planning.

If PR #9 is still open, Phase 0 must explicitly treat `IMPLEMENTATION_FORK_STRATEGY_DECISION.md` and the fork-decision addendum as accepted task context but not yet merged `main` documentation.
