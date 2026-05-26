# Bootstrap Import Detailed Report

Date: 2026-05-26

Repository: `https://github.com/Kwentin3/messenger-imap`

Branch: `bootstrap/project-import`

Base bootstrap commit before this report: `eb069a30b9b0edc062d0b85c016cafb701b4cd05`

## 1. Executive Summary

This report records the completed product documentation cleanup and clean GitHub bootstrap/import for Corporate IMAP Messenger.

The missing External Contacts & Guest Access PRD domain was completed before the repository import. The product documentation package now models internal employees and external contacts as different product concepts. The GitHub repository was initialized with canonical project docs, upstream reference notes, root hygiene files, and the standalone Android Diagnostics prototype source.

The import intentionally did not include upstream Delta Chat / Chatmail repositories, local worktrees, APK binaries, build outputs, local databases, raw logcat, or secrets.

## 2. Target Repository State

| Item | Value |
| --- | --- |
| GitHub repository | `https://github.com/Kwentin3/messenger-imap` |
| Local clone path | `d:\Users\Roman\Desktop\Проекты\messenger-imap` |
| Branch | `bootstrap/project-import` |
| Remote | `origin` |
| Remote URL | `https://github.com/Kwentin3/messenger-imap.git` |
| Bootstrap commit | `eb069a30b9b0edc062d0b85c016cafb701b4cd05` |
| Working tree after bootstrap | clean before adding this report |
| PR status | not created because remote `main` branch does not exist yet |

## 3. External Contacts Domain Completion

Created:

| File | Purpose |
| --- | --- |
| `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md` | Full PRD for external contacts, guests, counterparties, external invites, external relationships, visibility scopes, reassignment, directory behavior, control plane requirements, security risks, MVP scope, later scope, acceptance criteria, and open questions |

The document is not a skeleton. It defines the product model needed for client, supplier, partner, contractor, and other counterparty communication without treating those contacts as employees.

Core accepted rule:

```text
Internal invite creates Membership.
External invite creates ExternalRelationship.
```

## 4. Product Documents Updated

| File | Update summary |
| --- | --- |
| `docs/product/DOMAIN_PRD_INDEX.md` | Added External Contacts & Guest Access as the seventh product domain with dependencies and MVP priority |
| `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md` | Added external contacts to product scope, principles, domains, MVP/later/non-goals, risks, open decisions, and related PRD links |
| `docs/product/domains/PRD_CORPORATE_DIRECTORY.md` | Added Internal Members vs External Contacts separation, Visible Directory, external sections, visibility scopes, and revocation/reassignment effects |
| `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md` | Added Internal Organization Invite vs External Contact Invite, external invite flow, external relationship activation, and constraints |
| `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md` | Added external contact management, external invite management, reassignment, revoke/archive/suspend, external organizations later, policies, and audit events |
| `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` | Added external invite handling, external contact badges, chat warnings, external contacts section, scoped directory for external users, and no internal-directory exposure |
| `docs/product/decisions/PRODUCT_DECISIONS_LOG.md` | Added accepted decisions for external contacts, invite separation, ExternalRelationship, no internal directory exposure, and organization-owned reassignment |
| `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md` | Added External Contacts & Guest Access to the product context and future Blueprint requirements |
| `docs/product/PRODUCT_PRD_PACKAGE_REPORT.md` | Updated package status to seven domains and added external-domain open questions |

Created:

| File | Purpose |
| --- | --- |
| `docs/product/PRODUCT_DOCS_EXTERNAL_DOMAIN_UPDATE_REPORT.md` | Consistency report for the External Contacts product-doc update |

## 5. Product Consistency Result

The updated PRD package now consistently distinguishes:

- `Internal Member` from `External Contact`;
- `Internal Organization Invite` from `External Contact Invite`;
- membership activation from external relationship activation;
- full internal corporate directory from scoped visible directory;
- provider transport setup from external contact product relationships.

Explicitly preserved product rules:

- APK download does not equal organization membership.
- Internal invite creates membership.
- External invite creates external relationship.
- External contacts do not receive the internal corporate directory.
- External contacts are visible only according to policy and visibility scope.
- Control Plane is responsible for external contact lifecycle, reassignment, revocation, and audit.

## 6. Imported Repository Content

Imported into the GitHub repository:

| Area | Imported content |
| --- | --- |
| Root hygiene | `.gitattributes`, `.gitignore`, `README.md`, `SECURITY.md`, `CONTRIBUTING.md`, `LICENSE_POLICY.md` |
| Docs overview | `docs/README.md` |
| Product docs | `docs/product/**` |
| Blueprints | `docs/blueprints/**` |
| Research | `docs/research/**` |
| Reports | `docs/reports/**` |
| Hand-off docs | `docs/hand_off/**` |
| Upstream documentation | `docs/upstream/UPSTREAM_PROJECTS.md`, `docs/upstream/LICENSE_NOTES.md` |
| Release placeholder | `releases/README.md` |
| Android Diagnostics prototype | `prototypes/android-diagnostics/**` source, docs, schemas, Gradle wrapper, and redacted sample reports |

## 7. Explicitly Excluded Content

Not imported:

| Excluded item | Reason |
| --- | --- |
| `imap-messenger-research/upstream/**` | Upstream/reference repos must not be vendor-copied without explicit decision |
| `worktrees/**` | Local worktrees are not project source |
| Delta Chat / Chatmail `.git` metadata | Upstream history and repository state are not part of this bootstrap |
| APK / AAB / AP_ / IDSIG files | Binary build artifacts should not be committed |
| `.gradle/`, `build/`, `app/build/` | Generated build outputs |
| `node_modules/` | Generated dependency tree |
| local DB / WAL / SHM files | Local runtime data |
| raw logcat / raw protocol logs | Sensitive runtime evidence risk |
| `local.properties`, `.env`, `.env.*` | Local machine or secret configuration |
| `*.keystore`, `*.jks`, `*.pem`, `*.key` | Key material |
| `docs/out/` | Duplicate output buffer; unique docs were moved to canonical locations before import |

## 8. Upstream Handling

Upstream projects were documented only:

| Upstream | Handling |
| --- | --- |
| Delta Chat Android | referenced in `docs/upstream/UPSTREAM_PROJECTS.md`; not copied |
| chatmail/core | referenced in `docs/upstream/UPSTREAM_PROJECTS.md`; not copied |
| provider-db | referenced in `docs/upstream/UPSTREAM_PROJECTS.md`; not copied |
| Delta Chat Desktop | referenced for research context; not copied |
| chatmail-relay | referenced for research context; not copied |

License/compliance notes are recorded in:

- `LICENSE_POLICY.md`;
- `docs/upstream/LICENSE_NOTES.md`;
- `docs/reports/2026-05-13/OPEN_SOURCE_LICENSE_NOTES.report.md`.

## 9. Android Diagnostics Prototype Import

Imported:

- Gradle wrapper;
- Android app source;
- manifest;
- schemas;
- redacted sample reports;
- implementation notes;
- manual QA checklist;
- self-audit report;
- implementation report;
- build/runtime validation report.

Excluded:

- APK files;
- `.gradle/`;
- `build/`;
- `app/build/`;
- local machine configuration;
- local runtime databases;
- raw logs.

Known status from existing reports:

- standalone diagnostics APK source exists;
- previous debug build/runtime validation was recorded in reports;
- Mail.ru / VK Mail transport family is accepted as first baseline;
- runtime logcat no-secret QA remains a future controlled validation item.

## 10. Secret And Artifact Scan Summary

Pre-commit checks included:

```powershell
git status
git diff --name-only
rg -n "BEGIN PRIVATE KEY|PRIVATE KEY|app password|AUTH PLAIN|token|secret|password" .
rg -n -S "AUTH PLAIN [A-Za-z0-9+/=]{12,}" .
rg -n -S "[A-Za-z0-9._%+-]+@(mail\.ru|bk\.ru|inbox\.ru|list\.ru|vk\.com|yandex\.[A-Za-z]+|gmail\.com|outlook\.com|hotmail\.com)" .
git diff --cached --name-only
git diff --cached --check
```

Expected matches were found only as documentation terminology, examples, source-code variable names, or redaction/security guidance. No real app passwords, raw AUTH payloads, private keys, provider email addresses, raw logcat dumps, APKs, local databases, or build artifacts were intentionally imported.

The staged forbidden-path check found no committed files matching APK/build/upstream/worktree/local DB/logcat/local config patterns.

## 11. Line Ending And Wrapper Handling

Added:

| File | Purpose |
| --- | --- |
| `.gitattributes` | Keeps Markdown/JSON/Gradle/Java/XML text files normalized and preserves LF for `gradlew` |

The Gradle wrapper shell script was staged with executable mode:

```text
100755 prototypes/android-diagnostics/gradlew
```

## 12. Commit And Push Result

Bootstrap commit:

```text
eb069a30b9b0edc062d0b85c016cafb701b4cd05
```

Commit message:

```text
Bootstrap Corporate IMAP Messenger project docs and diagnostics prototype
```

Push result:

```text
bootstrap/project-import -> origin/bootstrap/project-import
```

The remote repository currently has the bootstrap branch. At the time of PR creation attempt, the remote did not have a `main` base branch.

## 13. Pull Request Attempt

Attempted:

```powershell
gh pr create --base main --head bootstrap/project-import
```

Result:

```text
pull request create failed: GraphQL: Head sha can't be blank, Base sha can't be blank, No commits between main and bootstrap/project-import, Base ref must be a branch
```

Interpretation:

The repository was empty before bootstrap, so `main` was not available as a real branch/ref. A pull request cannot be opened until a base branch exists.

Recommended options:

- set `bootstrap/project-import` as the initial default branch; or
- create `main` from the bootstrap commit; or
- create an initial `main` commit and then open a PR from `bootstrap/project-import`.

## 14. Current Repository Contents At A Glance

The bootstrap branch contains:

- complete product PRD package;
- seven product domains;
- External Contacts & Guest Access domain;
- diagnostics and transport verification docs;
- Delta Chat / Chatmail research;
- upstream project notes;
- standalone Android Diagnostics prototype source;
- root repository hygiene and contribution/security policy files.

The bootstrap branch does not contain:

- upstream repository clones;
- local worktrees;
- APK binaries;
- generated Android build directories;
- raw runtime logs;
- app passwords;
- raw authentication payloads;
- private keys;
- local account databases.

## 15. Remaining Tasks

| Task | Status |
| --- | --- |
| Decide default branch strategy | Open |
| Create PR or initialize `main` | Open |
| Review PRD package | Open |
| Decide Delta Chat fork vs custom shell over chatmail/core | Open |
| Define license/compliance path for modified Android distribution | Open |
| Write Android IMAP Messenger MVP Blueprint | Open |
| Write Corporate Control Plane Blueprint | Open |
| Write External Contacts & Guest Access Blueprint | Open |
| Plan next field validation beyond Mail.ru / VK Mail baseline | Open |
| Decide APK distribution via GitHub Releases | Open |

## 16. Recommended Next Action

Make the bootstrap branch the initial repository baseline, then use the imported PRD package to start the next architecture Blueprint pass.

The most direct path is:

1. Create or set the default branch from `bootstrap/project-import`.
2. Review imported docs and prototype source in GitHub.
3. Start the Android IMAP Messenger MVP Blueprint.
4. Start the Control Plane / Directory / Invite Blueprint set, including External Contacts & Guest Access.
