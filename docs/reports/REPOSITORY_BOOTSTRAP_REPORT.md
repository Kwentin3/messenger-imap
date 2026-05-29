# Repository Bootstrap Report

Date: 2026-05-26

## 1. Executive Summary

The `Kwentin3/messenger-imap` repository was prepared as a clean bootstrap/import of the Corporate IMAP Messenger project.

The import includes canonical product documentation, research, reports, upstream reference documentation, and the standalone Android Diagnostics prototype source. It intentionally excludes upstream Delta Chat / Chatmail clones, local worktrees, APK binaries, Gradle/build outputs, node_modules, local account databases, raw logcat, and secrets.

## 2. External Contacts Domain Completion

Before import, the missing External Contacts & Guest Access product domain was completed as a full PRD, not a skeleton.

Created:

- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`

The domain now defines external contacts, external organizations, external relationships, external invites, visibility scopes, ownership/reassignment, directory behavior, control-plane requirements, policy, security/privacy risks, MVP scope, later scope, acceptance criteria, and open questions.

Core product rule:

```text
Internal invite creates Membership.
External invite creates ExternalRelationship.
```

## 3. Files Created / Updated Before Import

Created before import:

- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`
- `docs/product/PRODUCT_DOCS_EXTERNAL_DOMAIN_UPDATE_REPORT.md`
- canonical copies from `docs/out/`:
  - `docs/blueprints/ARCHITECTURE_ADAPTATION_PLAN.md`
  - `docs/research/PROVIDER_COMPATIBILITY_NOTES.md`
  - `docs/research/IMAP_MESSENGER_DELTA_CHAT_RESEARCH.md`

Updated before import:

- `docs/product/DOMAIN_PRD_INDEX.md`
- `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md`
- `docs/product/domains/PRD_CORPORATE_DIRECTORY.md`
- `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md`
- `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md`
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`
- `docs/product/decisions/PRODUCT_DECISIONS_LOG.md`
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`
- `docs/product/PRODUCT_PRD_PACKAGE_REPORT.md`

## 4. Target GitHub Repo

Repository:

```text
https://github.com/Kwentin3/messenger-imap
```

The repository was cloned as an empty repository before import.

## 5. Branch

Branch:

```text
bootstrap/project-import
```

## 6. Commit Hash

Commit hash: generated after this report is committed and pushed.

This report is part of the bootstrap commit, so it cannot self-reference its final immutable commit hash. The final pushed commit hash is recorded in the task final response and can be verified with:

```powershell
git rev-parse HEAD
```

## 7. What Was Imported

Imported:

- root repository hygiene files:
  - `.gitattributes`
  - `README.md`
  - `.gitignore`
  - `SECURITY.md`
  - `CONTRIBUTING.md`
  - `LICENSE_POLICY.md`
- docs:
  - `docs/product/**`
  - `docs/blueprints/**`
  - `docs/research/**`
  - `docs/reports/**`
  - `docs/hand_off/**`
  - `docs/upstream/**`
  - `docs/README.md`
- release policy placeholder:
  - `releases/README.md`
- Android Diagnostics prototype source:
  - `prototypes/android-diagnostics/README.md`
  - Gradle wrapper files
  - `app/src/main/**`
  - `schemas/**`
  - `sample-reports/**`
  - prototype docs

## 8. What Was Intentionally Not Imported

Not imported:

- `imap-messenger-research/upstream/**`
- `worktrees/**`
- Delta Chat / Chatmail `.git` metadata
- APK files
- `.gradle/`
- `build/`
- `app/build/`
- `node_modules/`
- local DB/WAL/SHM files
- raw logcat files
- raw protocol transcripts
- real credentials, app passwords, tokens, or raw AUTH payloads
- `docs/out/` as a duplicate output buffer
- upstream/worktree `google-services.json`

## 9. Upstream Handling

Upstream projects are documented, not vendor-copied:

- `docs/upstream/UPSTREAM_PROJECTS.md`
- `docs/upstream/LICENSE_NOTES.md`

Recorded upstream references:

- Delta Chat Android
- chatmail/core
- provider-db
- Delta Chat Desktop
- chatmail-relay

Future fork/submodule/source import decisions remain TBD and must be made explicitly.

## 10. License / Compliance Notes

`LICENSE_POLICY.md` and `docs/upstream/LICENSE_NOTES.md` document current license concerns.

Key points:

- Delta Chat Android is GPL-covered according to local research notes and upstream license files.
- chatmail/core and provider-db are MPL-2.0 according to local research notes and upstream license files.
- Modified Android binary distribution needs explicit compliance planning.
- APK binaries are not committed; if distributed, they should use GitHub Releases or another controlled channel with source/compliance notes.

## 11. Android Diagnostics Prototype Status

The Android Diagnostics prototype is imported as source and documentation only.

Included:

- Gradle wrapper;
- Android app source;
- manifest;
- schema;
- redacted sample reports;
- implementation/QA docs.

Excluded:

- `.gradle/`;
- `build/`;
- `app/build/`;
- APK artifacts.

Known project status from existing reports:

- debug APK build passed previously;
- runtime launch passed on Huawei Android 12 / SDK 31;
- Mail.ru foreground transport evidence accepted;
- runtime logcat no-secret QA remains a future controlled validation item.

## 12. Product Docs Status

The PRD package now covers seven domains:

- Android Messenger Client;
- Corporate Control Plane;
- Corporate Directory;
- Invite Onboarding & Distribution;
- Provider Transport Profiles;
- Diagnostics & Transport Verification;
- External Contacts & Guest Access.

The product docs now explicitly separate Internal Member from External Contact and Internal Organization Invite from External Contact Invite.

## 13. Secret Scan Result

Pre-commit scans checked for:

- private key markers;
- app password placeholders;
- raw AUTH payload forms;
- generic token/secret/password patterns;
- APK/build artifacts;
- local DB files;
- raw logcat/log files.

Expected matches may appear in documentation and source as terminology, examples, or variable names. No real app passwords, raw AUTH payloads, raw logcat dumps, APKs, local DBs, or unredacted runtime credentials are intentionally imported.

## 14. Build Artifacts Policy

Build artifacts are excluded by `.gitignore` and were checked before commit.

Policy:

- keep APK/AAB files out of Git;
- keep Gradle and Android build outputs out of Git;
- use GitHub Releases for APK handoff if needed;
- include SHA-256, source commit, release notes, and license/compliance notes for any binary release.

## 15. Remaining Tasks

- Create PR from `bootstrap/project-import` to `main` if not automatically created.
- Review PRD package with product/engineering/legal stakeholders.
- Historical bootstrap follow-up: decide thin Delta Chat Android fork vs custom shell over chatmail/core. Resolved for MVP on 2026-05-29: thin Delta Chat Android fork.
- Decide GPL/MPL distribution compliance path.
- Write Android IMAP Messenger MVP Blueprint.
- Write Corporate Control Plane Blueprint.
- Define External Contacts & Guest Access Blueprint requirements.
- Define in-client diagnostics MVP scope.
- Plan next provider/network field validation.

## 16. Recommended Next Step

Open and review a pull request from `bootstrap/project-import` to `main`, then use the imported PRD package to drive the next architecture Blueprints.
