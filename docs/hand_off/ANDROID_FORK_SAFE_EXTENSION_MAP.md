# Android Fork Safe Extension Map

Date: 2026-05-29

Status: Draft handoff

Project: Corporate IMAP Messenger / messenger-imap

Android repo: `Kwentin3/messenger-imap-android`

Baseline Android commit: `a3a8b3581f82456bb7fe3342485cef4593c31315`

Related decision: MVP uses a thin fork of Delta Chat Android.

## Purpose

This map records where corporate Android MVP work can be added with the lowest practical upstream risk. It is a handoff for later Android Blueprints and implementation slices. It does not authorize core, JNI, sync, encryption, MIME, or database migration changes.

## Safe Extension Principles

- Keep corporate code isolated in app/product-layer packages where possible.
- Prefer new Activity/adapter/model/wrapper code over editing core Delta Chat logic.
- Preserve existing IMAP/SMTP setup and chat flows.
- Treat Control Plane, Directory, Invite and Diagnostics as external contracts until their Android integration slices are explicitly implemented.
- Do not hardcode Mail.ru-only behavior.
- Do not store app passwords in Control Plane or corporate invite tokens.
- Do not log raw invite tokens, passwords, AUTH strings, or raw diagnostic logs.
- Do not commit APK/AAB/build outputs.

## Safe Extension Points

| Corporate need | Candidate extension point | Relevant upstream files | Safe first use |
| --- | --- | --- | --- |
| Corporate onboarding entry | New isolated corporate Activity launched from welcome/settings | `WelcomeActivity.java`, `ApplicationPreferencesActivity.java`, `AndroidManifest.xml` | Placeholder entry only; no membership activation |
| Invite deep link / app link | New corporate invite Activity and intent-filter | `AndroidManifest.xml`, new corporate package | Parse route/kind safely; do not pass corporate invite tokens into SecureJoin |
| Fallback invite code | Corporate onboarding Activity | new corporate package/resources | Manual code entry placeholder with redacted state |
| Provider profile handoff | Wrapper around existing transport setup | `relay/EditRelayActivity.java`, `relay/RelayListActivity.java`, `chat/delta/rpc/types/EnteredLoginParam.java` | Suggest policy/profile labels; keep custom provider path |
| Check Transport | Wrapper/entry to existing connectivity surface | `ConnectivityActivity.java`, `ApplicationPreferencesActivity.java`, `connect/DcHelper.java` | Show existing connectivity status; no raw log upload |
| Corporate directory read-only sync | New local models/adapters and fixture-backed repository | new corporate package; later contact list surfaces | Parse/verify manifest/snapshot fixture; no Android authority |
| Internal/external separation | Badge/warning adapter layer | `contacts/ContactSelectionListAdapter.java`, `contacts/ContactSelectionListItem.java`, `recipients/Recipient.java` | Fixture-backed badges; no system contacts import |
| Managed group roster warning | UI warning layer near group/chat surfaces | `ConversationActivity.java`, `ConversationListActivity.java`, group creation/member screens | Warning only; no group protocol changes |
| Release metadata warning | New corporate release metadata model and warning screen | new corporate package; settings/welcome entry | Mocked metadata; no auto-update or signing |
| Support-safe diagnostics export | New redacted corporate diagnostics summary | `ConnectivityActivity.java`, `LogViewActivity.java`, `connect/DcHelper.java` | Redacted summary only; no raw logcat/raw AUTH |

## High-Risk Areas

| Area | Why high risk | Rule |
| --- | --- | --- |
| `jni/deltachat-core-rust` / chatmail core | Owns transport, sync, crypto, database, protocol behavior | No changes without separate Blueprint |
| JNI/FFI wrappers | Native contract changes can break core runtime | No changes in early Android MVP slices |
| Sync engine | Can break message delivery and background behavior | Do not modify |
| Encryption/SecureJoin | Security-critical protocol behavior | Do not modify or reuse for corporate invites |
| MIME/message pipeline | Can corrupt messaging compatibility | Do not modify |
| Database migrations | Can break existing accounts and upgrades | No direct migrations before Blueprint |
| Existing system contacts import | Privacy-sensitive and conflicts with corporate directory authority | No silent arbitrary import |
| Background services/notifications | Reliability-sensitive, platform-specific | Audit separately before promises |
| Existing raw log export | May expose secrets or personal data | Add redacted support export separately |
| Package/app rename and signing | Release identity and compliance-sensitive | Later slice only |

## Initial Slice Mapping

1. Build baseline preserved: use `intake/upstream-build-baseline`; no code changes.
2. Corporate onboarding foundation: new corporate Activity, resources and manifest entry.
3. Provider profile / transport check: app-layer policy wrapper; existing setup remains usable.
4. Directory read-only sync: local manifest/snapshot models and sample parser, no server dependency.
5. Invite deep link / fallback code: corporate invite route parser, no activation without Control Plane.
6. External contact badge / warning: fixture-backed badge and warning data, no authority change.
7. Release metadata / update warning: mocked release metadata consumer, no auto-update.
8. QA hardening: rebuild, smoke, no-secret scan and regression notes.

## Stop Conditions

Stop before implementation if a planned change requires:

- chatmail/core, JNI, sync, encryption, MIME or database migration changes;
- signing keys, release APK publication or APK committed to git;
- server deployment, Traefik edits or Control Plane backend changes;
- raw app passwords, `.env`, raw AUTH, raw logcat or other secrets;
- breaking existing account setup, provider setup or basic messaging flow.

## Handoff Result

The Android fork can proceed to an app-layer safe customization Blueprint. Early implementation should start with an isolated corporate onboarding placeholder, then provider/transport wrapper, then read-only directory and invite entry slices.
