# Workspace Project Anamnesis

Date: 2026-05-26

Workspace: `d:\Users\Roman\Desktop\Проекты\mesenger`

Target GitHub repository: `https://github.com/Kwentin3/messenger-imap`

Scope: inventory and import guidance before bootstrapping/importing the current Corporate IMAP Messenger workspace into GitHub.

## 1. Executive Summary

The workspace contains a documented Corporate IMAP Messenger project, a working standalone Android Diagnostics APK prototype, accepted MVP-0a diagnostic evidence, product PRD documents, local upstream discovery clones, and a Delta Chat Android worktree baseline.

The workspace root is not currently a Git repository. There is no local clone or configured remote for `Kwentin3/messenger-imap` inside the workspace. The existing Git repositories are nested upstream/reference repositories under `imap-messenger-research/upstream/`; `worktrees/deltachat-android-corporate/` is a git-worktree of the local `deltachat-android` upstream clone.

Primary import recommendation: initialize/import a clean root repository containing canonical project docs, the Android Diagnostics prototype source, selected sanitized runtime JSON evidence, and import hygiene files. Do not import upstream clones, worktrees, build outputs, APK binaries, node_modules, local account databases, `.git` metadata, or raw local artifacts.

## 2. Workspace Root And Git Status

| Item | Status |
|---|---|
| Workspace root | `d:\Users\Roman\Desktop\Проекты\mesenger` |
| Root `.git` | Not present |
| Root `git status -sb` | Fails with `fatal: not a git repository` |
| Local clone of `Kwentin3/messenger-imap` | Not found in workspace |
| Remote pointing to `Kwentin3/messenger-imap` | Not found in nested `.git/config` files |
| Nested Git repos | Present under `imap-messenger-research/upstream/` |
| Android Git worktree | Present at `worktrees/deltachat-android-corporate/` |
| Main accidental import risk | `git add .` from root would sweep in upstream clones, worktree files, build outputs, APKs, `.gradle`, `node_modules`, and local DB artifacts unless `.gitignore` is created first |

Nested `.git` directories found:

| Path | Meaning |
|---|---|
| `imap-messenger-research/upstream/chatmail-relay/.git` | Local upstream reference clone |
| `imap-messenger-research/upstream/core/.git` | Local upstream reference clone |
| `imap-messenger-research/upstream/deltachat-android/.git` | Local upstream reference clone and owner of Android worktree |
| `imap-messenger-research/upstream/deltachat-desktop/.git` | Local upstream reference clone |
| `imap-messenger-research/upstream/provider-db/.git` | Local upstream reference clone |

`worktrees/deltachat-android-corporate/.git` is a gitdir pointer file, not an independent root repository.

## 3. Major Directories Inventory

| Path | Files / size observed | Role | GitHub import guidance |
|---|---:|---|---|
| `docs/` | 50 files / ~0.39 MB | Main documentation corpus | Import, after deciding how to handle duplicate `docs/out` copies |
| `docs/product/` | 11 files / ~0.10 MB | PRD package and product decisions | Import |
| `docs/blueprints/` | 4 files / ~0.05 MB | Technical/product blueprint inputs and decisions | Import |
| `docs/research/` | 14 files / ~0.09 MB | Research notes and runtime JSON evidence | Import selected/current files; keep sanitized JSON evidence |
| `docs/reports/` | 9 existing report files before this report / ~0.07 MB | Dated reports | Import; this report adds a root report and dated copy |
| `docs/hand_off/` | 4 files / ~0.02 MB | Handoff prompts/maps | Import if still useful for next-stage work |
| `docs/out/` | Delivery-buffer duplicate docs | Review/output buffer, not canonical storage | Prefer not to import duplicates, or keep only if explicitly needed |
| `prototypes/` | 155 files / ~0.86 MB | Prototype root | Import selected prototype source, excluding build artifacts |
| `prototypes/android-diagnostics/` | 155 files / ~0.86 MB including local build artifacts | Standalone Android Diagnostics APK prototype | Import source, Gradle wrapper, docs, schema, sample reports; exclude `.gradle/`, `build/`, `app/build/`, APKs |
| `imap-messenger-research/` | 129398 files / ~5656 MB | Research area with upstream clones and local smoke account DB | Do not import as-is |
| `imap-messenger-research/prototypes/imap-diagnostics/` | Small Node IMAP diagnostics prototype | Early script prototype | Import only if still useful; otherwise document as historical |
| `imap-messenger-research/rpc-smoke-accounts/` | Local DB/WAL/SHM files | Local Delta Chat RPC smoke account state | Do not import; potentially sensitive/local runtime state |
| `imap-messenger-research/upstream/` | 129388 files / ~5654 MB | Local upstream discovery clones | Do not import; document upstream refs only |
| `worktrees/` | 8597 files / ~487 MB | Local Delta Chat Android worktree baseline | Do not import into root project repo |

Other searched directory classes:

| Directory class | Found |
|---|---|
| `forks/` | Not found |
| `releases/` | Not found |
| root-level `tools/` | Not found |
| nested `tools/` | Found inside upstream source only, not project-owned tooling |

## 4. Product Documentation Inventory

| Document | Status | Path(s) |
|---|---|---|
| `PRD_ROOT_CORPORATE_IMAP_MESSENGER.md` | found | `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md` |
| `DOMAIN_PRD_INDEX.md` | found | `docs/product/DOMAIN_PRD_INDEX.md` |
| `PRD_ANDROID_MESSENGER_CLIENT.md` | found | `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` |
| `PRD_CORPORATE_CONTROL_PLANE.md` | found | `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md` |
| `PRD_CORPORATE_DIRECTORY.md` | found | `docs/product/domains/PRD_CORPORATE_DIRECTORY.md` |
| `PRD_INVITE_ONBOARDING_DISTRIBUTION.md` | found | `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md` |
| `PRD_PROVIDER_TRANSPORT_PROFILES.md` | found | `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md` |
| `PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md` | found | `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md` |
| `PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md` | missing | Not found |
| `PRODUCT_DECISIONS_LOG.md` | found | `docs/product/decisions/PRODUCT_DECISIONS_LOG.md` |
| `PRODUCT_CONTEXT_HANDOFF.md` | found | `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md` |
| `PRODUCT_PRD_PACKAGE_REPORT.md` | found | `docs/product/PRODUCT_PRD_PACKAGE_REPORT.md` |

Current PRD package covers six domains:

| Domain | Current status |
|---|---|
| Android Messenger Client | Draft PRD present |
| Corporate Control Plane | Draft PRD present |
| Corporate Directory | Draft PRD present |
| Invite Onboarding & Distribution | Draft PRD present |
| Provider Transport Profiles | Draft PRD present |
| Diagnostics & Transport Verification | Draft PRD present |
| External Contacts / Guest Access | Product idea mentioned in task context, but dedicated PRD is missing |

## 5. Diagnostics Track Inventory

| Document / artifact | Status | Path(s) |
|---|---|---|
| `ANDROID_DIAGNOSTICS_APK_BLUEPRINT.md` | found | `docs/blueprints/ANDROID_DIAGNOSTICS_APK_BLUEPRINT.md` |
| `ANDROID_MAIL_LIBRARY_DECISION.md` | found | `docs/blueprints/ANDROID_MAIL_LIBRARY_DECISION.md` |
| `ANDROID_DIAGNOSTICS_APK_IMPLEMENTATION_DETAILED.report.md` | found | `docs/reports/2026-05-13/ANDROID_DIAGNOSTICS_APK_IMPLEMENTATION_DETAILED.report.md` |
| `ANDROID_DIAGNOSTICS_APK_BUILD_RUNTIME_VALIDATION.report.md` | found | `docs/reports/2026-05-13/ANDROID_DIAGNOSTICS_APK_BUILD_RUNTIME_VALIDATION.report.md` |
| `ANDROID_DIAGNOSTICS_APK_ANAMNESIS.report.md` | found | `docs/reports/2026-05-13/ANDROID_DIAGNOSTICS_APK_ANAMNESIS.report.md` |
| `ANDROID_DIAGNOSTICS_MVP0A_CLOSURE.report.md` | found | `docs/reports/2026-05-13/ANDROID_DIAGNOSTICS_MVP0A_CLOSURE.report.md` |
| `IMAPDIAG_MAILRU_NORMAL_MOBILE_SMOKE.report.md` | found | `docs/reports/2026-05-13/IMAPDIAG_MAILRU_NORMAL_MOBILE_SMOKE.report.md` |
| Runtime JSON reports | found | `docs/research/imapdiag_*.json` |

Delta Chat / Chatmail research inventory:

| Document | Status | Path(s) |
|---|---|---|
| `DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md` | found | `docs/research/...`; duplicate in `docs/out/...` |
| `DELTACHAT_CORPORATE_FEATURE_MAP.md` | found | `docs/hand_off/...`; duplicate in `docs/out/...` |
| `ANDROID_IMAP_MESSENGER_MVP_BLUEPRINT_INPUTS.md` | found | `docs/blueprints/...`; duplicate in `docs/out/...` |
| `DELTA_CHAT_REPOS.md` | found | `imap-messenger-research/upstream-notes/...`; duplicate in `docs/out/...` |
| `ARCHITECTURE_ADAPTATION_PLAN.md` | found | `docs/out/ARCHITECTURE_ADAPTATION_PLAN.md` |
| `PROVIDER_COMPATIBILITY_NOTES.md` | found | `docs/out/PROVIDER_COMPATIBILITY_NOTES.md` |
| `WHITE_LIST_FIELD_TEST_PROTOCOL.md` | found | `docs/research/...`; duplicate in `docs/out/...` |

Notes:

- `docs/out/` appears to be a delivery/review buffer with duplicate docs. Before GitHub import, choose canonical locations for any docs that currently exist only in `docs/out/`.
- `ARCHITECTURE_ADAPTATION_PLAN.md` and `PROVIDER_COMPATIBILITY_NOTES.md` are currently only under `docs/out/`; if still current, move/copy them into canonical `docs/blueprints/` or `docs/research/` before import.

## 6. Android Diagnostics Prototype Status

Prototype root: `prototypes/android-diagnostics/`

| Required item | Status | Path |
|---|---|---|
| `README.md` | found | `prototypes/android-diagnostics/README.md` |
| `settings.gradle` | found | `prototypes/android-diagnostics/settings.gradle` |
| `build.gradle` | found | `prototypes/android-diagnostics/build.gradle` |
| `gradlew` | found | `prototypes/android-diagnostics/gradlew` |
| `gradlew.bat` | found | `prototypes/android-diagnostics/gradlew.bat` |
| `gradle/` | found | `prototypes/android-diagnostics/gradle/` |
| `app/` | found | `prototypes/android-diagnostics/app/` |
| `app/build.gradle` | found | `prototypes/android-diagnostics/app/build.gradle` |
| `app/src/main/AndroidManifest.xml` | found | `prototypes/android-diagnostics/app/src/main/AndroidManifest.xml` |
| `app/src/main/java/` | found | `prototypes/android-diagnostics/app/src/main/java/` |
| `schemas/diagnostic-report.schema.json` | found | `prototypes/android-diagnostics/schemas/diagnostic-report.schema.json` |
| `sample-reports/` | found | `prototypes/android-diagnostics/sample-reports/` |
| `docs/IMPLEMENTATION_NOTES.md` | found | `prototypes/android-diagnostics/docs/IMPLEMENTATION_NOTES.md` |
| `docs/MANUAL_QA_CHECKLIST.md` | found | `prototypes/android-diagnostics/docs/MANUAL_QA_CHECKLIST.md` |
| `docs/SELF_AUDIT_REPORT.md` | found | `prototypes/android-diagnostics/docs/SELF_AUDIT_REPORT.md` |
| `docs/IMPLEMENTATION_REPORT.md` | found | `prototypes/android-diagnostics/docs/IMPLEMENTATION_REPORT.md` |
| `docs/BUILD_RUNTIME_VALIDATION_REPORT.md` | found | `prototypes/android-diagnostics/docs/BUILD_RUNTIME_VALIDATION_REPORT.md` |

Prototype source files include:

| Area | Files |
|---|---|
| Java diagnostics app | `CheckResult.java`, `DiagnosticConfig.java`, `DiagnosticReport.java`, `DiagnosticRunner.java`, `ImapSession.java`, `MainActivity.java`, `NetProbe.java`, `NetworkMetadata.java`, `ProviderConfig.java`, `Redactor.java`, `SmtpSession.java`, `TimeoutPolicy.java` |
| Android resources | `strings.xml`, `styles.xml` |
| Report schema | `schemas/diagnostic-report.schema.json` |
| Redacted sample reports | `sample-auth-fail.redacted.json`, `sample-inconclusive-vpn.redacted.json`, `sample-network-whitelist-fail.redacted.json`, `sample-transport-pass.redacted.json` |

Known build/runtime status from existing reports:

| Item | Status |
|---|---|
| Build | Passed previously with `.\gradlew.bat clean assembleDebug --no-daemon` |
| Runtime launch | Passed on Huawei Android 12 / SDK 31 device |
| APK artifact | Present at `prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk` |
| APK size | 36,052 bytes observed |
| Manifest permissions | `INTERNET`, `ACCESS_NETWORK_STATE` |
| Runtime logcat no-secret QA | Still not fully captured as controlled adb/logcat evidence |
| APK import policy | Do not commit APK into Git; use GitHub Releases if binary distribution is needed |

Prototype-local `.gitignore` exists and excludes `.gradle/`, `build/`, `app/build/`, `local.properties`, `*.apk`, `*.ap_`, and `*.idsig`. A root `.gitignore` is still required before repository bootstrap.

## 7. Runtime Evidence Artifacts

Runtime JSON evidence found under `docs/research/`:

| File | Provider | Network type | Manual mode | Operator / region | Delivery mode | Result | Secret scan status | Evidence guidance |
|---|---|---|---|---|---|---|---|---|
| `imapdiag_20260513_220755_mailru_unknown_operator_normal_mobile_foreground.json` | `mailru` | `mobile` | `normal_mobile` | `unknown_operator` / blank | `single_account_smoke` | `diagnostic_only` | No app password/raw AUTH/logcat found; contains only generated `@diagnostics.invalid` Message-ID and masked emails | Keep as smoke evidence |
| `imapdiag_20260513_222425_mailru_unknown_operator_wifi_control_foreground.json` | `mailru` | `wifi` | `wifi_control` | `unknown_operator` / blank | `two_account_canonical` | `transport_pass` | No app password/raw AUTH/logcat found; contains only generated `@diagnostics.invalid` Message-ID and masked emails | Keep as canonical Wi-Fi control evidence |
| `imapdiag_20260513_222425_mailru_unknown_operator_wifi_control_foreground (1).json` | `mailru` | `wifi` | `wifi_control` | `unknown_operator` / blank | `two_account_canonical` | `transport_pass` | Same runId as previous file; appears duplicate | Prefer not to import duplicate unless preserving exact workspace history |
| `imapdiag_20260513_225420_mailru_mts_normal_mobile_foreground_transport.json` | `mailru` | `mobile` | `normal_mobile` | `mts` / `Krasnodar kray` | `two_account_canonical` | `transport_pass` | No app password/raw AUTH/logcat found; contains only generated `@diagnostics.invalid` Message-ID and masked emails | Keep as accepted mobile evidence |

Known accepted evidence from closure report:

- Mail.ru Wi-Fi control `two_account_canonical`: `transport_pass`.
- Mail.ru mobile MTS Krasnodar kray `two_account_canonical`: `transport_pass`.
- Mail.ru mobile `single_account_smoke`: IMAP/SMTP DNS, TCP, TLS, login/auth connectivity passed.
- Canonical messages were accepted by SMTP and received through IMAP by generated Message-ID.
- JSON files are sanitized evidence artifacts, not raw protocol logs.

Important interpretation note:

- Some JSON reports are formally labeled `normal_mobile`.
- Existing closure report records tester-confirmed whitelist/restricted mobile context for at least part of the field evidence.
- Original JSON files should not be edited retroactively; the interpretation belongs in reports.

## 8. Delta Chat / Chatmail Upstream Inventory

| Repo | Local path | Branch | Current commit | Remotes | Status | Project role | Import guidance |
|---|---|---|---|---|---|---|---|
| `chatmail-relay` | `imap-messenger-research/upstream/chatmail-relay` | `main` | `ed664cd feat(config): load default values from Config(), not chatmail.ini.f (#853)` | `origin=https://github.com/chatmail/relay.git` | clean | Chatmail relay reference | Do not import; document only |
| `chatmail/core` | `imap-messenger-research/upstream/core` | `main` | `0bb4c3d feat: enable draft-pqc feature on pgp crate` | `origin=https://github.com/chatmail/core.git` | clean | Core/chatmail reference | Do not import; document only |
| `deltachat-android` | `imap-messenger-research/upstream/deltachat-android` | `main` | `be07043 prefer 'audio' over 'switch speaker'...` | `origin=https://github.com/deltachat/deltachat-android.git`; `upstream=https://github.com/deltachat/deltachat-android.git` | clean | Android upstream baseline and worktree owner | Do not import as vendor copy |
| `deltachat-desktop` | `imap-messenger-research/upstream/deltachat-desktop` | `main` | `90f4132 refactor: remove unused top CSS` | `origin=https://github.com/deltachat/deltachat-desktop.git` | clean | Desktop reference | Do not import |
| `provider-db` | `imap-messenger-research/upstream/provider-db` | `master` | `2cba4b7 feat: Remove delete_server_after...` | `origin=https://github.com/deltachat/provider-db.git` | clean | Provider preset reference | Do not import; document only |
| Android corporate worktree | `worktrees/deltachat-android-corporate` | `research/corporate-imap-messenger-baseline` | `be07043 prefer 'audio' over 'switch speaker'...` | same Delta Chat Android remotes | clean | Local baseline for fork planning/build proof | Do not import into root repo |

Android worktree facts:

- `worktrees/deltachat-android-corporate` is a git-worktree of `imap-messenger-research/upstream/deltachat-android`.
- It is not connected to `Kwentin3/messenger-imap`.
- `origin` still points to the official Delta Chat Android repository.
- No product code changes were observed on the baseline branch.
- Existing reports state the Delta Chat Android baseline build succeeded through an ASCII path workaround, with no source changes.

Recommended handling:

- Keep upstream repositories outside the root import.
- Record upstream URLs, branches, and commits in docs.
- If the product route becomes a Delta Chat Android fork, create/configure a separate owned fork repository for Android source rather than vendor-copying it into `messenger-imap`.

## 9. Product Decisions Currently Accepted

Accepted decisions found in `docs/product/decisions/PRODUCT_DECISIONS_LOG.md` and closure reports:

| Decision | Current status |
|---|---|
| MVP-0a diagnostics accepted | Accepted |
| Mail.ru / VK Mail transport family accepted as first baseline | Accepted, with VK Mail still needing separate runtime verification if required |
| Product must be provider-agnostic | Accepted |
| No Mail.ru-only architecture | Accepted |
| APK download does not equal organization membership | Accepted |
| Invite/enrollment required | Accepted |
| Corporate directory is core B2B feature | Accepted |
| Corporate directory requires control plane | Accepted |
| Directory version/hash model | Accepted conceptually |
| Control plane is not a message server | Accepted |
| Background reliability deferred | Accepted |
| No silent unsafe address book import in MVP | Accepted |
| Voice messages may be reused if safe | Accepted |
| Video calls out of scope | Accepted |
| Audio transcription later | Accepted |
| Whitelist-ready status must be evidence-based | Accepted |
| Do not modify chatmail/core unless justified | Accepted |
| Delta Chat fork not productively started | Supported by current inventory: baseline worktree exists, no product changes observed |
| Internal invite and external contact invite must be separated | Stated in task context; no dedicated PRD found yet |
| External contacts / counterparties are a new product domain | Stated in task context; dedicated PRD missing |

## 10. Open Product Decisions

| Topic | Current state |
|---|---|
| Thin Delta Chat Android fork vs custom shell over chatmail/core | Historical status was open; resolved for MVP on 2026-05-29: thin Delta Chat Android fork |
| GPL/MPL compliance and distribution model | Open; legal review needed before modified Android distribution |
| First MVP provider list beyond Mail.ru / VK Mail | Open |
| VK Mail runtime evidence | Not yet separately verified |
| Yandex runtime evidence | Not yet verified |
| Directory authority and canonical payload for hash/version | Open |
| Invite token types, lifetime, revocation, activation policy | Open |
| External contacts / guest access product domain | Missing dedicated PRD |
| Background / locked-screen reliability target | Deferred |
| Branding, package identity, APK distribution channel | Open |
| Admin identity, roles, audit boundaries | Open |
| In-client diagnostics scope vs standalone diagnostics scope | Open |

## 11. Build Artifacts And Files Not Safe For Git

| Path / pattern | Found examples | Import policy |
|---|---|---|
| `*.apk` | `prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk`; Delta worktree debug APKs under `worktrees/.../build/outputs/apk/...` | Do not commit; use GitHub Releases if binaries are needed |
| `*.ap_` | Android intermediate resource packages under prototype and worktree build dirs | Do not commit |
| `.gradle/` | `prototypes/android-diagnostics/.gradle`; `worktrees/deltachat-android-corporate/.gradle` | Do not commit |
| `build/`, `app/build/` | Prototype and Delta worktree build outputs | Do not commit |
| `node_modules/` | Large dependency tree under `imap-messenger-research/upstream/deltachat-desktop/node_modules` | Do not commit |
| Upstream `.git/` directories | `imap-messenger-research/upstream/*/.git` | Do not commit |
| Worktree `.git` pointer | `worktrees/deltachat-android-corporate/.git` | Do not commit |
| `google-services.json` | Found in Delta Android upstream clone and worktree | Do not import into root repo unless separately reviewed and intentionally needed |
| `.env.example` | Found in local IMAP diagnostics prototype and upstream desktop packages | Example files may be safe, but review before import |
| Local account DB | `imap-messenger-research/rpc-smoke-accounts/**/*.db*` | Do not commit; potentially sensitive local runtime state |

APK artifacts observed:

| Path | Size |
|---|---:|
| `prototypes/android-diagnostics/app/build/outputs/apk/debug/app-debug.apk` | 36,052 bytes |
| `worktrees/deltachat-android-corporate/build/outputs/apk/foss/debug/generated-M-c26c0c-foss-debug-2.49.0.apk` | 31,834,060 bytes |
| `worktrees/deltachat-android-corporate/build/outputs/apk/gplay/debug/generated-M-c26c0c-gplay-debug-2.49.0.apk` | 32,336,738 bytes |

## 12. Secret / Sensitive Data Scan Summary

Scan scope:

- Project docs: `docs/`
- Project prototype: `prototypes/android-diagnostics/`
- Local IMAP diagnostics prototype: `imap-messenger-research/prototypes/`
- Local RPC smoke account folder listing: `imap-messenger-research/rpc-smoke-accounts/`
- Runtime JSON reports under `docs/research/`

Findings:

| Area | Finding | Action |
|---|---|---|
| Docs | Many expected mentions of password/auth/logcat as design/security language | Safe to import as docs, no secret values copied into this report |
| Android Diagnostics source | Expected runtime password variables and `AUTH PLAIN` implementation | Source is expected; review before publishing but no literal app passwords observed in scan |
| Runtime JSON evidence | Generated `@diagnostics.invalid` Message-ID values and masked emails only | Can be used as evidence; no raw AUTH/logcat/app password found in scan |
| Sample JSON reports | Synthetic/redacted examples | Safe to import |
| `imap-messenger-research/rpc-smoke-accounts/` | Local `accounts.toml`, `dc.db`, `dc.db-shm`, `dc.db-wal` | Do not import; manual review only if future migration needs it |
| Upstream clones | Not fully audited for publication because they are third-party repos plus dependency/build trees | Do not import |
| Raw logcat files | No standalone raw logcat files found by pattern scan | Keep raw logcat out of repo |

No app passwords, raw AUTH payloads, raw protocol transcripts, raw logcat dumps, or unmasked real provider emails should be added to GitHub. This report intentionally does not include any secret contents.

## 13. What Should Be Imported Into GitHub

| Import item | Recommended path | Notes |
|---|---|---|
| Root README | `README.md` | Create during bootstrap to explain project status and structure |
| Canonical product docs | `docs/product/**` | Import |
| Canonical blueprints | `docs/blueprints/**` | Import |
| Canonical research docs | `docs/research/**` | Import, including selected sanitized runtime JSON evidence |
| Existing dated reports | `docs/reports/2026-05-13/**` | Import |
| This workspace anamnesis | `docs/reports/WORKSPACE_PROJECT_ANAMNESIS.report.md` and optional dated copy | Import |
| Handoff docs | `docs/hand_off/**` | Import if still useful |
| Android Diagnostics prototype source | `prototypes/android-diagnostics/**` | Import source/docs/schema/sample reports; exclude build/cache/output artifacts |
| Local Node IMAP diagnostics prototype | `imap-messenger-research/prototypes/imap-diagnostics/**` or moved equivalent | Optional; import only if still useful and after confirming `.env.example` contains no real credentials |
| Upstream reference note | `docs/research/DELTA_CHAT_REPOS.md` or equivalent canonical path | Prefer docs-only reference, not source import |
| Provider compatibility notes | canonical `docs/research/PROVIDER_COMPATIBILITY_NOTES.md` | Move/copy from `docs/out/` if still current |
| Architecture adaptation plan | canonical `docs/blueprints/ARCHITECTURE_ADAPTATION_PLAN.md` or `docs/research/...` | Move/copy from `docs/out/` if still current |

## 14. What Should Not Be Imported

| Do not import | Reason |
|---|---|
| `imap-messenger-research/upstream/**` | Huge local upstream clones; preserve as upstream URLs/commits, not vendor copy |
| `worktrees/**` | Local Delta Chat Android worktree and build outputs; not the project root repo |
| Any `.git/` directory or `.git` worktree pointer | Repository metadata from other repos |
| `node_modules/**` | Dependency cache/build state |
| `.gradle/**` | Local Gradle cache |
| `build/**`, `app/build/**` | Build outputs |
| `*.apk`, `*.aab`, `*.ap_`, `*.idsig` | Binary artifacts; use Releases if needed |
| `local.properties` | Local SDK paths and developer machine state |
| `.env` with real values | Secrets risk |
| `imap-messenger-research/rpc-smoke-accounts/**` | Local account DB/runtime state |
| Raw logcat files | Sensitive runtime data risk |
| `google-services.json` from upstream/worktree | Review-sensitive mobile config; not needed for root repo import |
| Duplicate `docs/out/**` buffer files | Prefer canonical docs unless explicitly preserving output buffer history |

## 15. Recommended Repository Structure For `Kwentin3/messenger-imap`

Recommended initial structure:

```text
README.md
.gitignore
SECURITY.md
CONTRIBUTING.md
LICENSE_POLICY.md
docs/
  blueprints/
  hand_off/
  product/
    decisions/
    domains/
    handoff/
  reports/
    2026-05-13/
    WORKSPACE_PROJECT_ANAMNESIS.report.md
  research/
prototypes/
  android-diagnostics/
    app/
    docs/
    gradle/
    sample-reports/
    schemas/
    README.md
    build.gradle
    gradle.properties
    gradlew
    gradlew.bat
    settings.gradle
```

Recommended root `.gitignore` entries:

```gitignore
# Git metadata / local upstream work
imap-messenger-research/upstream/
worktrees/

# Local runtime state
imap-messenger-research/rpc-smoke-accounts/
*.db
*.db-shm
*.db-wal

# Build outputs
.gradle/
build/
app/build/
**/.gradle/
**/build/
**/app/build/

# Android binaries
*.apk
*.aab
*.ap_
*.idsig

# Local config / secrets
local.properties
.env
.env.*
!.env.example
*.keystore
*.jks
*.pem
*.key

# JS dependencies
node_modules/
**/node_modules/

# Logs
*.log
logcat*.txt
*logcat*
```

Consider whether to ignore `docs/out/` or remove it before import. If keeping it, clearly document that it is a delivery buffer and may duplicate canonical files.

## 16. Remaining Cleanup Before GitHub Push

| Step | Required before push? | Notes |
|---|---|---|
| Create root `.gitignore` first | Yes | Prevent accidental import of upstream/build/secrets |
| Decide canonical handling for `docs/out/` | Recommended | Avoid duplicate/conflicting docs |
| Move/copy `ARCHITECTURE_ADAPTATION_PLAN.md` out of `docs/out/` if current | Recommended | It is currently only in output buffer |
| Move/copy `PROVIDER_COMPATIBILITY_NOTES.md` out of `docs/out/` if current | Recommended | It is currently only in output buffer |
| Decide whether to import Node IMAP diagnostics prototype | Optional | Small, but historical; Android prototype is the accepted track |
| Deduplicate runtime JSON `(1)` copy | Recommended | Same runId as non-suffixed Wi-Fi control report |
| Add `PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md` | Recommended | User context says this is a new product domain; file is missing |
| Add `LICENSE_POLICY.md` | Recommended | Needed because Delta Chat Android is GPLv3+ and chatmail/core/provider-db are MPL-2.0 |
| Add `SECURITY.md` | Recommended | Project handles credentials, diagnostics, redaction, app distribution |
| Add `CONTRIBUTING.md` | Recommended | Useful before collaboration starts |
| Add root `README.md` | Yes | Make repo understandable immediately |
| Keep APKs out of Git | Yes | Use GitHub Releases for binary handoff |
| Keep upstream repos out of Git | Yes | Document remotes and commits instead |
| Re-run sensitive-data scan after staging | Yes | Run on staged files before first commit |

## 17. Next Recommended Action

Bootstrap `Kwentin3/messenger-imap` as a clean root project repository, not as a vendor copy of Delta Chat Android:

1. Create root `.gitignore`, `README.md`, `SECURITY.md`, `CONTRIBUTING.md`, and `LICENSE_POLICY.md`.
2. Import canonical `docs/` content, with `docs/out/` deduplicated or clearly treated as non-canonical.
3. Import `prototypes/android-diagnostics/` source/docs/schema/sample reports while excluding `.gradle/`, build outputs, and APKs.
4. Import selected sanitized runtime JSON evidence from `docs/research/`, preferably dropping the duplicate `(1)` Wi-Fi report.
5. Do not import `imap-messenger-research/upstream/`, `worktrees/`, local DBs, APKs, `node_modules`, `.gradle`, or raw logs.
6. Run a staged-file scan for secrets before the initial commit.
7. Commit as initial project bootstrap and push to `Kwentin3/messenger-imap`.

If the product later chooses a Delta Chat Android fork route, create/configure a separate owned Android fork repository or submodule strategy intentionally. Do not hide that decision inside the root project import.
