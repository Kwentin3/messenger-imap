# Corporate IMAP Messenger Project Roadmap

Date: 2026-05-26

Status: Draft

Project: `messenger-imap`

Scope: roadmap between PRD and Blueprint / implementation

Repository: `https://github.com/Kwentin3/messenger-imap`

## 1. Executive Summary

Corporate IMAP Messenger has moved from research, transport diagnostics, and product PRD work into Blueprint planning.

The main risk now is scope creep: starting Android UI beyond the approved thin-fork intake, deployment, APK distribution, or backend implementation before the product architecture boundaries are clear. This roadmap fixes the execution order and keeps the project focused between PRD and implementation.

The current key Blueprint artifact is:

```text
docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md
```

Corporate Control Plane and Corporate Directory are already merged baselines. Invite Onboarding & Distribution is the current drafted Blueprint because it connects invite authority, APK distribution handoff, email verification, provider setup, diagnostics gate, activation, and first directory sync.

Current status: Invite Onboarding & Distribution MVP Blueprint drafted; review and acceptance are still pending.

## 2. Accepted Baseline

Accepted project baseline:

- MVP-0a Diagnostics accepted.
- Mail.ru / VK Mail transport baseline accepted.
- Product must remain provider-agnostic.
- No Mail.ru-only architecture.
- Control Plane is required.
- Corporate Directory is the core B2B feature.
- Invite/enrollment is required.
- APK download does not equal membership.
- External Contacts are separate from Internal Members.
- External invite creates ExternalRelationship, not Membership.
- Control Plane may be unavailable in whitelist/restricted mode.
- Stale directory and stale policy mode are required.
- Android-first.
- MVP Android implementation path: thin fork of Delta Chat Android.
- Future Android fork repository: `Kwentin3/messenger-imap-android`.
- Current repository remains product/meta/docs/control-plane coordination: `Kwentin3/messenger-imap`.
- Control Plane backend working hypothesis: Node.js / TypeScript + PostgreSQL.
- iOS is out of current scope.
- Background reliability is deferred.
- Delta Chat / Chatmail are upstream capability baselines, not vendor-copied into this repo.
- Deployment must not happen without Deployment Blueprint and rollback/backup plan.
- Existing Traefik and server services must not be disrupted.

## 3. Current Documentation State

Product docs:

- [Root PRD](../product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md);
- [Android Messenger Client PRD](../product/domains/PRD_ANDROID_MESSENGER_CLIENT.md);
- [Corporate Control Plane PRD](../product/domains/PRD_CORPORATE_CONTROL_PLANE.md);
- [Corporate Directory PRD](../product/domains/PRD_CORPORATE_DIRECTORY.md);
- [Invite Onboarding & Distribution PRD](../product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md);
- [Provider Transport Profiles PRD](../product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md);
- [Diagnostics & Transport Verification PRD](../product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md);
- [External Contacts & Guest Access PRD](../product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md);
- [Product PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md);
- [Product Decisions Log](../product/decisions/PRODUCT_DECISIONS_LOG.md);
- [Product Context Handoff](../product/handoff/PRODUCT_CONTEXT_HANDOFF.md).

Infrastructure docs:

- [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md);
- [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md).

Prototype:

- Android Diagnostics APK prototype in `prototypes/android-diagnostics/`.

Research:

- Delta Chat / Chatmail capabilities;
- corporate feature map;
- provider compatibility notes;
- Android architecture and onboarding notes;
- diagnostic evidence reports.

## 4. Roadmap Principles

- Docs-first before implementation.
- Blueprint before code.
- Control Plane before Android product implementation.
- Directory model before group/member automation.
- Invite/enrollment before public APK distribution.
- Provider-agnostic model before provider-specific UX polish.
- No Mail.ru-only architecture.
- No silent address book import.
- No chatmail/core changes without Blueprint.
- No Delta Chat Android fork changes beyond the documented intake slice without Blueprint.
- No deployment without Deployment Blueprint.
- No direct server changes without runbook.
- No Traefik changes without deployment plan and rollback path.
- No APK signing/distribution without release policy.
- No direct host port exposure for new services by default.
- No reuse of existing `postgres-dev` without data-isolation decision.
- No secrets, `.env`, keys, APKs, build artifacts, or raw logs in git.

## 5. Stages

### Stage 0. Transport Diagnostics - DONE

Scope:

- standalone Android Diagnostics APK;
- Mail.ru / VK Mail baseline;
- real Android testing;
- closure report.

Status: accepted.

Exit criteria: completed.

### Stage 1. Product PRD Baseline - DONE / ACCEPTED

Scope:

- root PRD;
- domain PRDs;
- external contacts;
- decisions log;
- context handoff.

Status: accepted.

### Stage 2. Product PRD Refinement - DONE / ACCEPTED

Scope:

- stale Control Plane mode;
- email verification;
- multi-workspace;
- trust states;
- RBAC;
- canonical directory hash;
- invite abuse;
- release lifecycle;
- infrastructure assumptions;
- server audit.

Status: accepted.

### Stage 3. Roadmap & Blueprint Planning - CURRENT

Scope:

- create this roadmap;
- identify Blueprint order;
- define blockers;
- prevent scope drift.

Exit criteria:

- roadmap exists;
- next Blueprint order accepted;
- blocking decisions listed;
- no implementation started prematurely.

### Stage 4. Corporate Control Plane MVP Blueprint - MERGED / BASELINE

Purpose:

Define backend/admin portal/source-of-truth for:

- organizations/workspaces;
- users/memberships;
- roles/RBAC;
- internal invites;
- external invites;
- email verification;
- directory manifest/snapshot ownership;
- provider profiles;
- APK release metadata;
- diagnostics evidence;
- audit log;
- stale mode policies.

Why first:

All other domains depend on Control Plane.

Current artifact:

- [Corporate Control Plane MVP Blueprint](../blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md)
- [Corporate Control Plane MVP Blueprint Report](../blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT_REPORT.md)

Status: merged into `main` as current Control Plane Blueprint baseline.

### Stage 5. Corporate Directory MVP Blueprint - MERGED / BASELINE

Purpose:

Define:

- `DirectoryManifest`;
- `DirectorySnapshot`;
- version/hash;
- canonical payload;
- internal/external directory spaces;
- visible directory per user;
- member statuses;
- external contact visibility;
- managed groups;
- revoke behavior;
- stale directory behavior.

Current artifact:

- [Corporate Directory MVP Blueprint](../blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md)
- [Corporate Directory MVP Blueprint Report](../blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT_REPORT.md)

Status: merged into `main` as current Directory Blueprint baseline.

### Stage 6. Invite Onboarding & Distribution Blueprint - DRAFTED

Purpose:

Define:

- assisted one-shot invite flow;
- landing page;
- APK download;
- deep link / app link;
- fallback invite code;
- email verification code;
- internal invite vs external invite;
- release metadata;
- APK distribution.

Current artifact:

- [Invite Onboarding & Distribution MVP Blueprint](../blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md)
- [Invite Onboarding & Distribution MVP Blueprint Report](../blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT_REPORT.md)

Status: drafted, not yet accepted.

### Stage 7. Android Client MVP Blueprint

Purpose:

Define:

- thin-fork implementation assumption and remaining Android fork intake decisions;
- app onboarding;
- provider setup;
- check transport;
- directory sync;
- one-to-one chats;
- basic groups;
- external contact badges;
- stale directory UX;
- release warning UX.

Note:

Android Client Blueprint should not start until Control Plane and Directory assumptions are clear.

### Stage 8. External Contacts & Guest Access Blueprint

Purpose:

Define:

- external relationships;
- external invite activation;
- visibility scopes;
- reassignment;
- external contact UX;
- external directory view;
- no internal directory exposure.

This may be included in Control Plane/Directory Blueprint if scope is tightly controlled, but it must not be forgotten.

### Stage 9. Provider / Diagnostics Blueprint Slice

Purpose:

Define:

- provider profile model;
- diagnostic status;
- in-client check scope;
- standalone diagnostics relationship;
- delayed diagnostic upload;
- provider evidence model.

### Stage 10. Deployment Blueprint

Purpose:

Define:

- server deployment;
- Docker Compose / Portainer;
- Traefik labels;
- networks;
- own database;
- storage;
- secrets;
- backups;
- rollback;
- release storage;
- no direct host ports.

Must use:

- [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md);
- [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md).

### Stage 11. Implementation Planning

Purpose:

Break accepted Blueprints into implementation slices.

Example slices:

- Control Plane skeleton;
- admin auth;
- organization model;
- invite model;
- directory manifest/snapshot;
- Android onboarding spike;
- release metadata endpoint;
- APK download page.

### Stage 12. MVP Implementation

Only after the relevant Blueprints are accepted.

## 6. Blueprint Order

Ordered Blueprint sequence:

1. Corporate Control Plane MVP Blueprint.
2. Corporate Directory MVP Blueprint.
3. Review and accept Invite Onboarding & Distribution MVP Blueprint.
4. Android Client MVP Blueprint.
5. External Contacts & Guest Access Blueprint.
6. Provider / Diagnostics Blueprint Slice.
7. Deployment Blueprint.
8. Implementation Plan.

Deployment Blueprint can start earlier as a draft only after Control Plane stack assumptions are known. Real deploy design must not precede Control Plane architecture.

## 7. Critical Blockers / Decisions

Implementation-blocking decisions:

- GPL/MPL compliance path;
- backend stack confirmation for Control Plane; working hypothesis is Node.js / TypeScript + PostgreSQL;
- database choice;
- one active workspace vs multi-workspace UI in MVP;
- email verification flow details;
- initial RBAC permissions;
- release storage choice: GitHub Releases vs backend/object storage;
- APK signing pipeline;
- directory stale thresholds;
- in-client diagnostics scope;
- provider list beyond Mail.ru / VK Mail;
- external invite default policy.

## 8. Do-Not-Start-Yet List

Do not start these until the relevant Blueprint or owner decision exists:

- Android UI implementation beyond documented fork intake;
- Delta Chat fork changes beyond documented Android fork intake slice;
- deep upstream modifications without Blueprint;
- chatmail/core modifications;
- deployment to server;
- Traefik changes;
- production DB setup;
- APK signing pipeline;
- background reliability;
- signed IMAP/system-account directory updates;
- iOS support;
- CRM/helpdesk integration;
- automatic address book sync;
- app store distribution.

## 9. MVP Boundary

MVP should include:

- Control Plane basic admin;
- organization/workspace;
- members;
- invites;
- email verification;
- directory manifest/snapshot;
- Mail.ru / VK Mail baseline provider;
- custom provider profile;
- APK release metadata;
- landing/download page;
- Android client onboarding;
- one-to-one chats;
- basic groups;
- external contact one-to-one invite;
- diagnostics gate.

MVP should not include:

- production-grade background delivery;
- iOS;
- full MDM;
- full CRM/helpdesk;
- video/real-time calls;
- signed IMAP directory updates;
- automatic silent address book distribution;
- full provider/operator matrix;
- all external project room features.

## 10. Risk Register

| Risk | Impact | Mitigation | Current stage |
| --- | --- | --- | --- |
| Scope creep | Blueprints and MVP become too broad to execute | Follow roadmap order and do-not-start-yet list | Current |
| GPL compliance | Wrong distribution path can block release | Thin fork selected; legal/license review and source distribution plan still required before modified APK distribution | Open |
| Control Plane unavailable in whitelist | Invite, directory, release, and policy sync can be stale | Stale mode, delayed sync, future signed fallback later | Accepted PRD refine |
| Stale directory misuse | Users may message outdated members/groups | Stale warnings, expired thresholds, managed roster enforcement | Directory Blueprint baseline |
| Invite abuse | Wrong user may activate or attempt invite replay | Expiry, max uses, email verification, rate limits, audit | Invite Blueprint drafted |
| Revoked member in historical chats | User confusion or accidental sends | Historical warning and current managed roster sends | Blueprint needed |
| APK distribution friction | Users cannot install/update reliably | Release metadata, download route, emergency Android email fallback | Invite Blueprint drafted |
| Signing key handling | Compromised APK trust chain | Define signing pipeline, keep signing keys out of repo/deploy host by default | Open |
| Provider auth changes | Baseline provider may break | Provider profiles, diagnostics evidence, provider-agnostic design | Ongoing |
| Background reliability | MVP may overpromise delivery | Keep background reliability deferred until MVP-0b | Deferred |
| External contact directory leak | Internal directory exposure to guests | External visible directory only, separate relationship model | Accepted PRD refine |
| Deployment impact on existing services | Traefik/server changes could break existing services | Read-only audit, Deployment Blueprint, no direct changes without runbook | Infrastructure stage |

## 11. Near-Term Action Plan

1. Review and accept Invite Onboarding & Distribution MVP Blueprint.
2. Review Android Client MVP Blueprint with the thin-fork assumption.
3. Create/review Android fork intake plan for `Kwentin3/messenger-imap-android`.
4. Write Provider / Diagnostics Blueprint Slice after the Android boundary is clearer.
5. Confirm Control Plane backend stack assumption: Node.js / TypeScript + PostgreSQL.
6. Write Deployment Blueprint after Control Plane stack assumptions are known.

## 12. Success Criteria For Roadmap Phase

Roadmap phase is complete when:

- roadmap is committed;
- next Blueprint is selected;
- blockers are visible;
- no unresolved doc contradiction blocks Blueprint;
- owner accepts stage order.

## 13. Links

- [Root PRD](../product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Product Decisions Log](../product/decisions/PRODUCT_DECISIONS_LOG.md)
- [Product Context Handoff](../product/handoff/PRODUCT_CONTEXT_HANDOFF.md)
- [Product PRD Review Addendum](../product/PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Implementation Fork Strategy Decision](../decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md)
- [Corporate Control Plane MVP Blueprint](../blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md)
- [Corporate Directory MVP Blueprint](../blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md)
- [Invite Onboarding & Distribution MVP Blueprint](../blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md)
- [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)
- [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md)
- [Android Diagnostics MVP-0a Closure](../reports/2026-05-13/ANDROID_DIAGNOSTICS_MVP0A_CLOSURE.report.md)
- [Delta Chat Capabilities Report](../research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md)
