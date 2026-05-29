# Project Pre-Implementation Anamnesis And Readiness Audit

Date: 2026-05-29

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Scope: documentation readiness audit before autonomous implementation planning.

## 1. Executive Summary

Overall status: the project has a strong pre-implementation documentation baseline. It is no longer just an idea or prototype: it has accepted diagnostic evidence, a product PRD package, refined product decisions, infrastructure assumptions, a server audit, a roadmap, and merged MVP Blueprints for Control Plane, Directory, and Invite Onboarding / Distribution.

Readiness: documentation is sufficient for an agent to continue autonomous implementation planning, but not sufficient for broad unsupervised implementation. The next safe phase is an explicit Implementation Plan or Agent Implementation Handoff that converts accepted PRDs/Blueprints into small slices, chooses stacks, defines tests, and records blocking decisions.

Closed stages:

- MVP-0a Android foreground IMAP/SMTP diagnostics accepted.
- Mail.ru transport baseline accepted; Mail.ru / VK Mail family is the first provider baseline.
- Product PRD baseline created.
- PRD refinement completed.
- Infrastructure assumptions and read-only server audit completed.
- Project Roadmap created.
- Corporate Control Plane MVP Blueprint merged into `main` through PR #3.
- Corporate Directory MVP Blueprint content is in `main`; PR #4 itself is closed after fast-forward consolidation.
- Invite Onboarding & Distribution MVP Blueprint is in `main` through PR #5.

Open delivery status:

- Android Client MVP Blueprint exists in open PR #6, not in `main`.
- PR #7 is an open draft follow-up adding explicit PR #3/#4 review reports and execution report; it is not in `main`.
- GitHub default branch is currently `bootstrap/project-import`, while this project flow treats `main` as the integration branch. This should be fixed or explicitly documented before relying on default-branch automation.

Main blockers before full implementation:

- thin Delta Chat Android fork vs custom Android shell over `chatmail/core`;
- GPL/MPL compliance and source distribution path;
- Control Plane backend stack and database choice;
- Deployment Blueprint, backup/rollback plan, and Traefik plan;
- APK signing flow and release storage;
- exact stale/expired thresholds;
- email verification sender/provider;
- Provider / Diagnostics product Blueprint;
- exact implementation slices and test plans.

Verdict: `READY_WITH_BLOCKERS`. The docs are enough to plan the implementation, but not enough to start all implementation domains safely.

## 2. Project Goal And Product Thesis

The project builds an Android-first corporate messenger that uses IMAP/SMTP as message transport and adds a corporate product layer around it: Control Plane, Corporate Directory, invite onboarding, APK distribution, provider profiles, diagnostics, audit, and external contacts.

Why IMAP/SMTP:

- MVP-0a diagnostics proved foreground IMAP/SMTP transport can work on Android for the first provider baseline.
- Delta Chat / Chatmail already provide IMAP/SMTP-backed messaging capabilities, contacts, chats, groups, attachments, voice messages, and provider setup primitives.
- The product explicitly avoids rewriting IMAP/SMTP transport in MVP.

Why Android-first:

- The accepted runtime evidence is Android-based.
- The target MVP surface is a downloadable Android APK.
- iOS is explicitly out of current scope.
- Android sideload and APK distribution constraints are product requirements, not incidental implementation details.

Why Control Plane:

- IMAP/SMTP providers carry messages, but they do not own corporate organization state.
- Control Plane owns organizations/workspaces, memberships, roles/RBAC, invites, email verification, directory publication, provider profiles, release metadata, diagnostics evidence references, audit, and stale-mode policy.
- Control Plane is explicitly not a message server.

Why Corporate Directory is key:

- The product's B2B value is knowing who is an active internal member, who is suspended/revoked, which external contacts are visible, and which managed groups use current active roster authority.
- DirectoryManifest, DirectorySnapshot, `directoryVersion`, `directoryHash`, canonical payload rules, visible directory views, and stale behavior are documented.
- Historical chat membership, local contacts, provider address books, and imported vCards are not corporate authority.

Why APK download is not membership:

- APK possession only means the app binary is available.
- Invite token possession is not membership.
- Internal membership activation requires valid invite, email verification, policy checks, and Control Plane activation.
- External invite activation creates an `ExternalRelationship`, not internal membership.

Why external contacts are separate from internal members:

- External contacts are scoped counterparties such as clients, suppliers, contractors, or partners.
- External contacts do not receive the full internal directory.
- External invites create external relationships with visibility scope, not employee memberships.

## 3. Accepted Evidence And Completed Research

| Evidence / research | Status | What it proves | Main limits |
| --- | --- | --- | --- |
| MVP-0a Diagnostics | Accepted | Standalone Android Diagnostics APK can run foreground IMAP/SMTP checks and export sanitized JSON. | Not a messenger product; no production background guarantees. |
| Mail.ru mobile / Wi-Fi transport evidence | Accepted | Mail.ru foreground IMAP/SMTP can pass DNS/TCP/TLS/auth/send/receive correlation in accepted runs. | Full provider/operator matrix not complete. |
| Mail.ru / VK Mail baseline | Accepted baseline | Mail.ru / VK Mail family is first provider baseline using Mail.ru endpoints. | VK Mail still needs separate runtime validation if required. |
| Whitelist/restricted mobile context | Accepted with caveat | Tester-confirmed restricted mobile context supports the hypothesis that Mail.ru IMAP/SMTP can work where allowed services work. | Some JSON labels say `normal_mobile`; original files are not retroactively edited. |
| Delta Chat / Chatmail capabilities research | Completed | Existing upstream covers account setup, contacts, chats, groups, broadcasts, attachments, voice messages, QR/SecureJoin, provider setup, connectivity. | No ready-made trusted corporate directory distribution channel. |
| Provider-agnostic requirement | Accepted | Mail.ru is not the architecture boundary; provider profiles and diagnostics status are required. | Provider catalog beyond first baseline remains open. |
| Android diagnostics prototype | Implemented and validated | Diagnostic APK built, installed, launched, and exported sanitized evidence. | Remains separate from production messenger. |
| Infrastructure audit | Completed read-only | Server has Docker, existing Traefik, `traefik-net`, existing services, and candidate deploy assumptions. | Does not approve deployment. |

Primary source documents:

- `docs/reports/2026-05-13/ANDROID_DIAGNOSTICS_MVP0A_CLOSURE.report.md`
- `docs/reports/2026-05-13/IMAPDIAG_MAILRU_NORMAL_MOBILE_SMOKE.report.md`
- `docs/research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md`
- `docs/hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md`
- `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md`
- `docs/infrastructure/SERVER_AUDIT_REPORT.md`

## 4. Open-Source Base / Fork Strategy Status

Upstream projects considered:

| Upstream | Role | Current handling |
| --- | --- | --- |
| `deltachat-android` | Android client baseline and likely thin-fork candidate | Not vendor-copied; future fork/submodule decision still open. |
| `chatmail/core` | Core IMAP/SMTP, contact, chat, group, transport capability reference | Not copied; do not modify unless a Blueprint justifies it. |
| `provider-db` | Provider profile and compatibility reference | Not copied; document relevant provider facts only. |
| `deltachat-desktop` | Discovery material | Not imported. |
| `chatmail-relay` | Backend/reference material | Not imported. |

What is known:

- Delta Chat Android has an existing Android app, account setup, local chats, contacts, group UI, attachments, voice messages, connectivity UI, and resource-level customization points.
- `chatmail/core` exposes contact, chat, group, provider, configuration, and JSON-RPC capabilities that could support a custom shell.
- `provider-db` has provider metadata for Mail.ru, VK Mail, Yandex, Rambler, and others.

What is decided:

- Do not vendor-copy upstream projects into this repository.
- Do not start by changing `chatmail/core`.
- Do not start a deep fork just for documentation.
- Preserve provider-agnostic architecture.
- Treat upstream as capability baseline, not as already corporate-ready.

What is not decided:

- thin Delta Chat Android fork vs custom shell over `chatmail/core`;
- exact legal/compliance model for GPL/MPL distribution;
- whether app distribution will require source-publication workflow for a modified GPL Android client;
- package ID, app name, branding, and distribution channel;
- whether Android system contacts permission is avoided in MVP.

License/compliance notes:

- Delta Chat Android is documented as GPLv3+ in the local license notes.
- `chatmail/core` and `provider-db` are documented as MPL-2.0.
- A distributed modified Android client likely requires GPL source distribution compliance.
- MPL reuse/modification needs file-level compliance.
- The license notes are engineering notes, not legal advice.

Documents fixing this:

- `docs/upstream/UPSTREAM_PROJECTS.md`
- `docs/upstream/LICENSE_NOTES.md`
- `docs/reports/2026-05-13/OPEN_SOURCE_LICENSE_NOTES.report.md`
- `docs/blueprints/ANDROID_FORK_STRATEGY_DECISION.md`
- `docs/research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md`
- `docs/hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md`

## 5. Product Documentation Inventory

| Document | Status | Covers | Open questions / gaps |
| --- | --- | --- | --- |
| `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md` | High-level PRD | Product vision, users, evidence, principles, domains, MVP/later/non-goals, architecture assumptions, risks. | Fork vs shell, GPL, provider list, stale thresholds, invite policy, email verification UX, workspace UI, release lifecycle, background target, branding. |
| `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` | Domain PRD | Android-first client, onboarding, provider setup, transport test, directory sync, chats, external contacts, groups, security, stale UX. | Fork vs shell, credential storage, contacts permission, diagnostics scope, background behavior, package/distribution, external contact mode. |
| `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md` | Domain PRD | Admin/backend authority, organizations, members, roles, invites, directory, releases, diagnostics, policies, audit. | Admin identity, role granularity, domain invites, external invite permissions, provider profile scope, diagnostics evidence threshold, APK hosting, audit retention. |
| `docs/product/domains/PRD_CORPORATE_DIRECTORY.md` | Detailed domain PRD | Directory as core B2B feature, statuses, sync, version/hash, revocation, import, groups, trust. | Member fields, authoritative source, canonical payload format, long offline behavior, snapshots vs diffs, external visibility, stale warnings. |
| `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md` | Domain PRD | Invite enrollment, landing page, APK download, deep link/fallback, email verification, activation, external invite flow. | One-time/domain invites, invite ownership, external approval, default visibility, token lifetime, app links, sideload support, SHA visibility, first distribution channel. |
| `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md` | Domain PRD | Provider profiles, Mail.ru/VK baseline, Yandex/Rambler candidates, custom profiles, diagnostic statuses. | Provider UI list, app-password guidance, custom/admin-defined profiles, profile changes, recommended evidence threshold, global vs org-specific layering. |
| `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md` | Domain PRD | Diagnostics, report requirements, provider diagnostic status, admin view, standalone/in-client boundary. | In-client vs standalone scope, activation gate, network labels, freshness, upload/approval, blocking vs warning, report schema, background separation. |
| `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md` | Domain PRD | External contacts, relationships, statuses, external invites, visibility scopes, reassignment, policies, risks. | Invite roles, admin approval, external-to-external communication, project rooms, reassignment, CRM needs, same APK/mode, default visibility, audit retention. |
| `docs/product/decisions/PRODUCT_DECISIONS_LOG.md` | Decision log | Accepted product decisions and open decision themes. | Still lists fork/shell, compliance, provider list, directory payload, stale thresholds, invite policy, RBAC mapping, release lifecycle, external policy, background target. |
| `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md` | Handoff | What is being built, proven evidence, baseline, principles, current doc structure, unresolved decisions, next work. | Still points to Invite review and Android Client Blueprint; Android Blueprint now exists only in open PR #6. |
| `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md` | Product refinement | Stale mode, install/distribution, email ownership proof, multi-workspace, managed groups, trust states, RBAC, canonical hash, invite abuse, external reassignment, app release lifecycle. | Refinement document intentionally leaves implementation choices to Blueprints/plans. |
| `docs/roadmap/PROJECT_ROADMAP.md` | Draft roadmap | Stage order, do-not-start list, blockers, MVP boundary, risk register, near-term plan. | Stage 3 still marked `CURRENT`; Stage 6 says drafted/not accepted; Android Blueprint PR #6 is not reflected on `main`. |

## 6. Blueprint Inventory

| Blueprint | Path / PR | Status | Covers | Not yet closed |
| --- | --- | --- | --- | --- |
| Corporate Control Plane MVP Blueprint | `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md`; PR #3 | Merged into `main`; accepted baseline by content | Control Plane boundaries, domain model, state machines, RBAC, workflows, verification, stale mode, provider profiles, releases, audit, infra assumptions. | Backend stack, DB choice, stale thresholds, RBAC keys, email provider, release storage, signing, fork/shell, GPL/MPL. |
| Corporate Directory MVP Blueprint | `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`; PR #4 content | Content is in `main`; PR #4 closed after fast-forward consolidation | Manifest/snapshot, version/hash, canonical payload, visible directory, statuses, sync, stale states, managed roster authority, trust semantics. | Canonical JSON standard, full vs delta, visible directory computation, external snapshot shape, stale send rules, publish workflow. |
| Invite Onboarding & Distribution MVP Blueprint | `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md`; PR #5 | Merged into `main`; roadmap still says drafted/not accepted | Internal/external invites, token/fallback code, landing page, APK handoff, email verification, provider setup, diagnostics gate, activation, first sync. | One-time/domain invite policy, external invite defaults, Control Plane vs manual email sending, invite lifetime, fallback code, diagnostics activation policy, email sender. |
| Android Client MVP Blueprint | `docs/blueprints/ANDROID_CLIENT_MVP_BLUEPRINT.md`; PR #6 | Exists in open PR #6; not in `main` | Client domains, local state, onboarding, directory sync, stale UX, provider setup, diagnostics, external contact UX, fork-vs-shell criteria, slices, validation. | PR review/merge, fork-vs-shell decision, compliance, package ID, credential storage, contacts permission, in-client diagnostics, background expectation. |
| External Contacts & Guest Access Blueprint | Not present | Missing | PRD exists and parts are covered by Control Plane, Directory, Invite, and Android drafts. | Needs dedicated Blueprint for policies, external UX, visibility, reassignment, project/team scope, lifecycle automation. |
| Provider / Diagnostics Blueprint Slice | Not present | Missing | PRDs and MVP-0a Diagnostics Blueprint exist; Android PR #6 recommends this next. | Exact in-client diagnostics, evidence freshness, activation gate, provider verification policy, report retention, upload/approval. |
| Deployment Blueprint | Not present | Missing | Infrastructure assumptions and server audit exist. | Stack, path, network, Traefik labels, secrets, DB, storage, backup, rollback, smoke tests. |
| Android Diagnostics APK Blueprint | `docs/blueprints/ANDROID_DIAGNOSTICS_APK_BLUEPRINT.md` | MVP-0a prototype Blueprint, not product messenger Blueprint | Foreground diagnostics, JSON report schema draft, safety, pass/fail, backlog. | Not a Control Plane/provider diagnostics product slice. |

## 7. Infrastructure Documentation Inventory

| Topic | Current documented state |
| --- | --- |
| Public domain | `messenger-imap.speechbattle.com` |
| Public IP | `146.19.211.30` |
| Internal deploy host | `192.168.7.64` |
| Reverse proxy | Existing Traefik on the server |
| Traefik container | Observed container `traefik` |
| Traefik public ports | Host ports `80` and `443` already published by Traefik |
| Candidate Docker network | `traefik-net`, candidate only, not approved |
| Candidate deploy path | `/opt/stacks/messenger-imap`, candidate only, not approved |
| Routing model | One domain, path routing through Traefik preferred before multiple subdomains |
| Direct host ports | New web/API/database services should not publish direct host ports by default |
| Database rule | Control Plane should use its own database/container/volume by default |
| Existing DB reuse | Do not reuse `postgres-dev` without explicit architecture/data-isolation decision |
| Secrets policy | No real secrets in git/docs/examples/logs/PR comments; use external secret storage |
| APK signing policy | Signing key must not be in repo, server docs, `.env`, compose, Traefik labels, or deploy host by default |
| APK binaries | Must not be committed to git |
| Deployment authorization | No deployment is approved without Deployment Blueprint, backup, rollback, and explicit network/path decisions |

Infrastructure source documents:

- `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md`
- `docs/infrastructure/SERVER_AUDIT_REPORT.md`
- `docs/reports/BRANCH_CONSOLIDATION_REPORT.md`

## 8. Answers To "What Are We Building?"

| Question | Answer in docs | Source document | Status |
| --- | --- | --- | --- |
| What is Corporate IMAP Messenger? | Android-first corporate messenger using IMAP/SMTP transport plus Control Plane, Directory, invites, provider profiles, diagnostics, external contacts. | Root PRD, Product Handoff | Full |
| Who are users? | Employees/internal members, admins/owners, managers, support/IT/auditors, and external contacts/counterparties. | Root PRD, Control Plane PRD, External Contacts PRD | Full |
| What is Control Plane? | Backend/admin authority for organization state, memberships, invites, directory, provider profiles, releases, diagnostics, audit, stale policies; not a message server. | Control Plane PRD/Blueprint | Full |
| What is Corporate Directory? | Centrally managed identity/visibility authority with manifest/snapshot, statuses, version/hash, visible views, managed roster behavior. | Directory PRD/Blueprint | Full |
| What is internal invite? | Invite that can lead to internal `Membership` activation after validity, policy, email verification, and diagnostics/activation policy. | Invite PRD/Blueprint, Control Plane Blueprint | Full |
| What is external invite? | Invite that can lead to scoped `ExternalRelationship`, not membership. | Invite PRD/Blueprint, External Contacts PRD | Full |
| What is External Contact? | Non-employee counterparty with scoped visibility and relationship state. | External Contacts PRD, Directory Blueprint | Full |
| How does email verification work? | Control Plane sends code/challenge to target email; user enters code; success proves mailbox ownership but does not by itself activate membership. | PRD Addendum, Control Plane Blueprint, Invite Blueprint | Full concept, implementation provider open |
| How does DirectoryManifest work? | Lightweight sync entry point with version/hash/snapshot reference/stale policy and visible scope. | Directory Blueprint | Full concept |
| How does DirectorySnapshot work? | Canonical payload with visible directory records; client verifies hash against manifest. | Directory Blueprint | Full concept, canonical library open |
| How does stale directory work? | Client stores cache and marks fresh/stale/expired/unavailable/hash_mismatch; actions warn/block by policy. | Directory PRD/Blueprint, Android PRD | Full concept, thresholds open |
| How is APK distributed? | Control Plane stores release metadata; binary storage can be GitHub Releases/backend/object storage; landing page shows download metadata; APK-by-email is emergency fallback only. | Invite Blueprint, Infrastructure Assumptions, Control Plane Blueprint | Partial; storage/signing open |
| How does provider profile work? | Provider profiles are org/workspace-scoped transport guidance and diagnostic status; credentials stay local. | Provider Profiles PRD, Control Plane Blueprint | Full concept, product Blueprint missing |
| Where is boundary between IMAP/SMTP and Control Plane? | IMAP/SMTP carries messages through providers; Control Plane carries product authority/state and can be unavailable while messages still work. | Root PRD, Control Plane Blueprint, Roadmap | Full |

## 9. Answers To "How Are We Building It?"

| Area | Documentation answer | Completeness |
| --- | --- | --- |
| Architecture layers | IMAP/SMTP provider, Android client, Control Plane, Directory, Invite, Provider Profiles, Diagnostics, release storage, infra. | Full at conceptual Blueprint level |
| Fork/custom shell decision | Options and criteria exist; Android PR #6 adds decision slice; no final decision. | Partial / blocking |
| Backend / Control Plane | Domain model, modules, workflows, RBAC, audit, stale policies are defined. Backend stack/API/DB not chosen. | Partial / planning-ready |
| Directory authority | Control Plane owns manifest/snapshot/version/hash/visibility; local/imported data is not authority. | Full concept |
| Invite onboarding | End-to-end flow, states, tokens, verification, landing page, APK handoff, activation, first sync defined. | Full concept, policy details open |
| APK distribution | Metadata and channels identified; no APK in git; no signing secrets in repo; emergency email fallback. | Partial / release decisions open |
| Provider profiles | Model in PRD and Control Plane; no product Provider/Diagnostics Blueprint. | Partial |
| Diagnostics | MVP-0a evidence and PRD exist; in-client diagnostics scope open. | Partial |
| Local cache | Directory cache and stale modes defined; Android PR #6 covers client state but not merged. | Partial |
| Stale mode | Required and described across PRD/Addendum/Directory/Invite/Android drafts. | Full concept, thresholds open |
| Audit log | Control Plane, Directory, Invite events defined. | Full concept, retention open |
| RBAC | Product-level Control Plane matrix defined. | Full concept, exact permission keys open |
| Infrastructure constraints | Domain/IP/Traefik/network/path/secrets/DB rules documented. | Full constraints, Deployment Blueprint missing |

## 10. Answers To "How Do We Verify It?"

Available verification documentation:

- MVP-0a diagnostics pass/fail rules and closure report.
- Transport diagnostics report schema draft in Android Diagnostics APK Blueprint.
- PRD acceptance criteria in each product/domain PRD.
- Blueprint acceptance criteria in Control Plane, Directory, Invite, and Android PR #6.
- PR delivery reports for Control Plane, Directory consolidation, and Invite.
- Docs-only, no-secrets, no-build-artifact checks documented in delivery reports.
- Infrastructure audit safety rules and no-change confirmation.

Verification gaps before implementation:

| Test plan needed | Needed because |
| --- | --- |
| Control Plane tests | Domain state machines, RBAC, invite/verification activation, audit, stale policy, release metadata need executable checks. |
| Directory hash/canonicalization tests | `directoryHash` correctness depends on canonical JSON rules and stable sorting/exclusion rules. |
| Invite onboarding tests | Token, fallback code, email verification, activation, expired/revoked/exhausted states, abuse limits need coverage. |
| Android client onboarding tests | App link/fallback, verification entry, provider setup, stale UX, first sync, external mode, release warnings need device/emulator plans. |
| Provider diagnostics tests | In-client vs standalone diagnostics, report schema, no-secret export, evidence freshness and provider status need tests. |
| Deployment smoke tests | Traefik routing, no direct ports, DB isolation, backups, rollback, health checks, APK download metadata need smoke plan after Deployment Blueprint. |

## 11. Implementation Readiness Matrix

| Domain | PRD ready | Blueprint ready | Blocking decisions | Ready for implementation planning? |
| --- | --- | --- | --- | --- |
| Control Plane | Yes | Yes, merged | Backend stack, DB, auth, RBAC keys, email sender, stale thresholds, release storage, audit retention | Yes, with blockers |
| Directory | Yes | Yes, content in `main` | Canonical JSON standard, stale thresholds, external snapshot shape, publish workflow, managed stale-send rules | Yes, with blockers |
| Invite / Distribution | Yes | Yes, in `main`, still marked drafted/not accepted in roadmap | Invite policies, external approval, email sender, token/code lifetime, diagnostics gate, release storage/signing | Yes, with blockers |
| Android Client | Yes | Draft in open PR #6, not baseline | Fork vs shell, GPL/MPL, package ID, credentials, contacts permission, diagnostics scope, background target | Not yet for implementation; ready for review |
| External Contacts | Yes | No dedicated Blueprint | Approval policy, visibility defaults, same APK/mode, reassignment, external-to-external/project scope | No |
| Provider Profiles | Yes | No dedicated product Blueprint | Provider list, evidence threshold, profile layering, profile change policy | No |
| Diagnostics | Yes | MVP-0a prototype Blueprint only; no product slice | In-client scope, activation blocking, evidence freshness, report retention, upload/approval | No |
| Deployment | Infrastructure docs only | No Deployment Blueprint | Stack, Traefik labels, secrets, DB, storage, backup, rollback, smoke tests | No |
| Licensing / Fork strategy | Notes and preliminary strategy exist | No final implementation decision | GPL/MPL legal review, fork vs shell, source publication, trademarks | No |
| Release / APK signing | PRD/Blueprint assumptions exist | No release/signing implementation Blueprint | Signing location, key custody, storage, update policy, metadata publication | No |

## 12. Critical Blockers

Before full implementation, the project must close or explicitly route these blockers:

- thin Delta Chat Android fork vs custom shell over `chatmail/core`;
- GPL/MPL compliance and distribution model;
- backend stack for Control Plane;
- database choice and isolation model;
- deployment stack and Deployment Blueprint;
- APK signing flow and key custody;
- APK/release storage choice;
- stale/expired thresholds and blocked-action policy;
- email verification sender/provider and delivery ownership;
- Android background reliability scope for first field trial;
- exact implementation slices and acceptance tests;
- default external invite visibility and approval policy;
- in-client diagnostics scope and activation gate;
- app identity, package ID, app name, and distribution channel.

## 13. Do-Not-Start-Yet List

The roadmap and docs say not to start these without a separate Blueprint/decision:

- Delta Chat fork changes;
- `chatmail/core` changes;
- deployment to the server;
- Traefik edits;
- production DB setup;
- APK signing pipeline;
- signed IMAP/system-account directory updates;
- iOS work;
- CRM/helpdesk integration;
- app store strategy;
- background reliability promises;
- automatic silent address book import;
- vendor-copying upstream source;
- direct server port exposure;
- reuse of existing `postgres-dev`;
- storage of real secrets, APK signing keys, `.env`, or APK binaries in git.

## 14. Gaps And Contradictions

| Gap / contradiction | Impact | Recommendation |
| --- | --- | --- |
| GitHub default branch is `bootstrap/project-import`, while active work targets `main`. | PR defaults, automation, and new contributors may target the wrong branch. | Change default branch to `main` or document why not. |
| Roadmap Stage 3 remains `CURRENT` even though later Blueprints are merged/drafted. | Status drift; agents may misread current phase. | Update roadmap after PR #7 or this audit is accepted. |
| Roadmap Stage 6 says Invite Blueprint drafted/not accepted, while PR #5 is merged. | Merge status and acceptance status are not the same, but this needs explicit wording. | Record formal accept/review state for Invite Blueprint. |
| Android Client Blueprint exists in PR #6 but not in `main`. | Baseline docs do not include current Android planning artifact. | Review/merge or explicitly reject PR #6 before Android planning. |
| PR #7 has review reports but is open draft, not baseline. | PR #3/#4 explicit review reports are not in `main`. | Review/merge PR #7 if those reports are desired baseline. |
| Control Plane and Directory Blueprint files still say `Status: Draft`. | Merged baseline may appear unaccepted when read standalone. | Update headers to accepted/merged after formal acceptance. |
| Product PRDs are still labeled high-level/domain PRD and many are `Draft PRD` in index. | This is acceptable for PRD phase but can confuse implementation readiness. | Add a baseline status note rather than rewriting history. |
| Provider/Diagnostics product Blueprint is missing. | Blocks exact diagnostic gate and provider status implementation. | Create `PROVIDER_DIAGNOSTICS_MVP_BLUEPRINT.md`. |
| Deployment Blueprint is missing. | Blocks any server, Traefik, DB, storage, secrets, or APK download deployment work. | Create Deployment Blueprint before server work. |
| External Contacts Blueprint is missing. | External relationship details are split across PRDs and other Blueprints. | Create dedicated Blueprint before broad external workflow implementation. |
| Exact canonical JSON standard is open. | Directory hash tests and interoperability cannot be implemented safely. | Decide canonical JSON library/standard in Implementation Plan or Directory follow-up. |
| License/legal review remains open. | Can block distributing modified Android APK. | Treat as a release blocker before Android distribution. |
| Old reports include historical gaps that are now closed, such as missing External Contacts PRD. | Agents may trust stale historical statements. | Mark historical reports as archival; prefer roadmap/current PRD inventory. |

No direct PRD-vs-Blueprint contradiction was found on core product semantics. The main issue is status drift and unresolved implementation decisions.

## 15. Recommended Next Steps

1. Decide baseline hygiene:
   - set GitHub default branch to `main`, or document why `bootstrap/project-import` remains default;
   - review/merge PR #7 if explicit PR #3/#4 reports should be baseline;
   - review PR #6 before treating Android Client Blueprint as accepted.

2. Declare baseline documents:
   - Root PRD and domain PRDs;
   - Product PRD Review Addendum;
   - Product Decisions Log;
   - Product Context Handoff;
   - Project Roadmap;
   - Control Plane Blueprint;
   - Directory Blueprint;
   - Invite Onboarding & Distribution Blueprint;
   - Infrastructure Assumptions;
   - Server Audit Report;
   - Delta Chat / Chatmail capabilities research.

3. Next Blueprint:
   - if PR #6 is accepted, write `docs/blueprints/PROVIDER_DIAGNOSTICS_MVP_BLUEPRINT.md`;
   - if PR #6 is not accepted yet, review/accept Android Client MVP Blueprint first.

4. Decisions before Implementation Plan:
   - fork vs shell;
   - GPL/MPL compliance route;
   - Control Plane backend stack and database;
   - email verification provider;
   - release storage and signing key custody;
   - stale/expired thresholds;
   - diagnostics activation policy.

5. Create an explicit Implementation Plan:
   - `docs/implementation/CONTROL_PLANE_DIRECTORY_INVITE_IMPLEMENTATION_PLAN.md` or a similarly scoped first plan;
   - include implementation slices, contracts, tests, migrations/API decisions, local dev setup, and done criteria;
   - keep deployment separate until Deployment Blueprint exists.

6. Create an Agent Implementation Handoff:
   - summarize accepted baselines;
   - list do-not-start constraints;
   - provide file paths, branch/PR state, blockers, test expectations, and first safe slice.

## 16. Final Readiness Verdict

Verdict: `READY_WITH_BLOCKERS`.

Reason:

- The documentation is enough for an agent to understand the product, the evidence, the upstream base, the domain model, the accepted constraints, and the safe sequence of work.
- The documentation is enough to continue autonomous implementation planning.
- The documentation is not yet enough for broad autonomous implementation because several decisions affect architecture, licensing, infrastructure, release safety, and testability.
- The safest next move is not code; it is baseline cleanup, Android Blueprint review, Provider/Diagnostics Blueprint, and a focused Implementation Plan with test plans.
