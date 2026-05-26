# Delta Chat / Chatmail research for corporate IMAP-backed messenger

Date: 2026-05-13.
Workspace: `imap-messenger-research/`.

## 1. Executive summary

Delta Chat / Chatmail is a strong technical base for the hypothesis. The best first path is not a full product fork, but a wrapper or small client over `chatmail/core` JSON-RPC. Core already handles IMAP, SMTP, TLS, MIME, local SQLite storage, queues, chats, groups and encryption. Desktop already consumes core through JSON-RPC and npm-published prebuilt `deltachat-rpc-server` binaries.

For MVP-0, use JSON-RPC or a small diagnostics tool to prove transport reachability and mail provider behavior. Do not rebrand or rewrite core yet. The largest unknown is not Delta Chat architecture; it is whether mobile whitelist mode permits `imap.*:993` and `smtp.*:465` endpoints for Mail.ru/VK/Yandex.

## 2. Repositories studied

See `upstream-notes/DELTA_CHAT_REPOS.md`.

Cloned:

- `upstream/core` from `https://github.com/chatmail/core`, commit `0bb4c3d`.
- `upstream/deltachat-desktop` from `https://github.com/deltachat/deltachat-desktop`, commit `90f4132`.
- `upstream/deltachat-android` from `https://github.com/deltachat/deltachat-android`, commit `be07043`.
- `upstream/provider-db` from `https://github.com/deltachat/provider-db`.
- `upstream/chatmail-relay` from `https://github.com/chatmail/relay`.

Official discovery sources:

- https://cosmos.delta.chat/
- https://github.com/chatmail/core
- https://github.com/deltachat/deltachat-desktop
- https://github.com/deltachat/deltachat-android
- https://jsonrpc.delta.chat/
- https://py.delta.chat/
- https://delta.chat/en/2025-02-11-why-jsonrpc-bindings-exist

## 3. What was cloned

All required upstream repositories were cloned into `imap-messenger-research/upstream/`.
All upstream git statuses are clean after build attempts.
No target GitHub repository was provided, so no remote fork or delivery branch was created. The work is isolated in the research directory.

## 4. Build/run results

Core from source:

- Command that would be needed: `cargo test --all` or `cargo run --locked -p deltachat-repl -- <db>`.
- Result: not attempted from source because Rust/Cargo are not installed in this environment.
- Blocker: install Rust toolchain; on Windows core README also warns Perl may be needed.

Desktop:

- `pnpm install --frozen-lockfile` succeeded.
- First `pnpm -w build:electron` failed because shallow clone has no tags and build script could not run `git describe`.
- `VERSION_INFO_GIT_REF=research-90f4132 pnpm -w build:electron` succeeded.
- GUI was not launched; core/RPC smoke test was run instead.

RPC smoke test:

- Used `@deltachat/stdio-rpc-server` and `@deltachat/jsonrpc-client` from desktop dependencies.
- Started prebuilt Windows `deltachat-rpc-server.exe`.
- Called `getSystemInfo`, `addAccount`, `getAllAccountIds`, `getProviderInfo`.
- Provider info was returned for `mail.ru`, `vk.com`, `yandex.ru`.

Android:

- Repository and build docs inspected.
- Build not attempted. It requires recursive core submodule, Rust, Android SDK/NDK and Gradle environment; too heavy for first discovery on this machine.

## 5. What failed or was skipped

- Core source build skipped: no Rust/Cargo.
- Android build skipped: no confirmed Android SDK/NDK/Rust environment.
- Real provider login/send/receive skipped: no app passwords were provided and secrets must not be stored.
- Whitelist-mode conclusions skipped: current network is not a mobile whitelist test environment.

## 6. Architecture map

Account management:

- `upstream/core/src/accounts.rs`: multi-account manager, accounts directory/config, background fetch across accounts.
- `upstream/core/src/context.rs`: per-account runtime context, scheduler, DB, events, IO start/stop.
- `upstream/core/deltachat-jsonrpc/src/api.rs`: JSON-RPC account APIs such as `add_account`, `start_io`, `get_all_accounts`.

IMAP/SMTP configuration:

- `upstream/core/src/config.rs`: legacy config keys and current configured keys.
- `upstream/core/src/login_param.rs`: `EnteredLoginParam`, `EnteredImapLoginParam`, `EnteredSmtpLoginParam`.
- `upstream/core/src/configure.rs`: `add_or_update_transport`, autoconfig, IMAP/SMTP connection checks.
- `upstream/core/src/transport.rs`: `ConfiguredLoginParam`, transport candidates, provider-db expansion.

Local database/cache:

- `upstream/core/src/sql.rs`: SQLite open/migrations/access wrapper.
- `upstream/core/src/sql/migrations.rs`: tables including `imap`, `imap_sync`, `smtp`, `imap_send`, `transports`.
- Desktop README says each account DB is a SQLite file.

Sync and receiving:

- `upstream/core/src/scheduler.rs`: IO scheduler, inbox loops, SMTP loop, idle/fake-idle behavior.
- `upstream/core/src/imap.rs` and `upstream/core/src/imap/*`: IMAP sessions, capabilities, fetch/move/delete, IDLE.
- `upstream/core/src/receive_imf.rs`: inbound message assignment into chats.

Sending:

- `upstream/core/src/chat.rs`: high-level chat/message send path.
- `upstream/core/src/smtp.rs` and `upstream/core/src/smtp/connect.rs`: SMTP connection, login, queue flushing and send.
- `smtp` DB table stores outgoing payloads/retries.

Chats, contacts, groups:

- `upstream/core/src/chat.rs`: single/group/broadcast chats, encrypted/unencrypted groups, membership changes.
- `upstream/core/src/contact.rs`: contacts and address/key-contact model.
- JSON-RPC exposes `createContact`, `createChatByContactId`, `createGroupChat`, `addContactToChat`, etc.

Encryption:

- `upstream/core/src/e2ee.rs`: PGP encryption helpers and secret key creation.
- `upstream/core/src/aheader.rs`, `securejoin.rs`, `decrypt.rs`, `mimefactory.rs`: Autocrypt, SecureJoin, decryption, MIME construction.
- This layer should not be modified for MVP-0.

UI to core:

- `deltachat-desktop` imports `@deltachat/jsonrpc-client` and `@deltachat/stdio-rpc-server`.
- Frontend calls `BackendRemote.rpc.*`.
- `packages/frontend/src/components/LoginForm.tsx` already calls `getProviderInfo`.
- `packages/frontend/src/components/dialogs/ConfigureProgressDialog.tsx` calls `addOrUpdateTransport`.

Provider presets:

- `upstream/core/src/provider/data.rs` includes Mail.ru, VK and Yandex presets.
- `upstream/provider-db` is the upstream data source.

Diagnostics:

- Current core exposes coarse connectivity via `getConnectivity` and HTML diagnostics via desktop dialog.
- Detailed DNS/TCP/TLS/login/send/receive/IDLE/spam diagnostics are better added as a separate MVP tool first, then possibly promoted to JSON-RPC API if needed.

## 7. Best fork path

Recommended path:

1. MVP-0: own thin diagnostic/client shell over `chatmail/core` JSON-RPC.
2. MVP-1: custom corporate UI over `deltachat-rpc-server`.
3. Later: thin `deltachat-desktop` fork if reusing existing UI is faster.
4. Android fork only after transport proof and desktop/RPC learnings.

Avoid:

- own IMAP implementation for MVP-0, except the standalone diagnostic probe;
- changing core sync/encryption/group logic;
- mass rebrand before transport proof.

## 8. Licensing risks

- `chatmail/core`, provider-db and JSON-RPC npm packages are MPL-2.0. A proprietary wrapper is more plausible here, but modifications to MPL-covered files must remain under MPL terms.
- `deltachat-desktop` and `deltachat-android` are GPL-3.0/GPL-3.0-or-later. Distributed forks of those clients likely trigger GPL source distribution obligations for the forked client.
- `chatmail/relay` is MIT, but relay/self-hosting is not the main whitelist scenario.
- Commercial/corporate distribution needs legal review, especially if shipping a modified GPL desktop or Android client.

## 9. Technical risks

- Mail providers may rate-limit, classify messages as spam, reject automated patterns or require account-specific settings.
- App passwords and IMAP enablement create support burden.
- Group chat over email is subtle; changing membership/sync logic is high-risk.
- Delta Chat's contact model distinguishes address-contacts and key-contacts; corporate address book must not assume email string equals internal contact identity.
- Existing provider entries are in `Preparation` status, so they need field validation.
- Mobile OS background networking/push behavior may be harder than desktop RPC.

## 10. Whitelist risks

- Web UI availability does not prove IMAP/SMTP availability.
- Whitelist mode may allow `mail.ru` web but block `imap.mail.ru:993` or `smtp.mail.ru:465`.
- DNS may resolve but TCP/TLS may be blocked.
- Different operators may treat the same provider differently.
- TLS SNI and certificate chains may matter; endpoints must be tested exactly.

## 11. Field test requirements

See `WHITE_LIST_FIELD_TEST_PROTOCOL.md`.

Minimum must cover:

- Wi-Fi control, normal mobile internet, whitelist-mode mobile internet.
- MTS, Beeline, MegaFon, T2.
- Mail.ru, VK Mail, Yandex.
- DNS, TCP, TLS, login, SMTP send, IMAP receive, IMAP IDLE and spam folder status.

## 12. Recommendation for next stage

Proceed with a transport MVP over JSON-RPC, not a full fork. Use two controlled accounts per provider and test real send/receive through Delta Chat core. In parallel, run the standalone endpoint diagnostic in the field matrix. Only after successful field proof decide whether to fork desktop UI or build a minimal custom shell.

## 13. MVP-0 backlog

1. Create test accounts and app passwords for Mail.ru, VK Mail and Yandex.
2. Run `prototypes/imap-diagnostics` with secrets passed only via environment variables.
3. Add Message-ID correlation to diagnostics.
4. Add JSON output file naming by provider/operator/network/timestamp.
5. Write a JSON-RPC script that configures account with `addOrUpdateTransport`.
6. Send and receive test messages between two controlled accounts through core.
7. Validate IMAP IDLE latency and fallback behavior.
8. Record spam/Junk placement and provider throttling.
9. Add corporate domain allowlist before account configuration.
10. Define managed config schema and address book import.
11. Decide UI route after transport evidence.
12. Perform license review before distributing any forked GPL client.

## 14. Local artifacts

- `prototypes/imap-diagnostics`: no-dependency diagnostic script.
- `docs/research/PROVIDER_COMPATIBILITY_NOTES.md`: provider presets and current no-secret checks.
- `docs/research/WHITE_LIST_FIELD_TEST_PROTOCOL.md`: field matrix and pass/fail rules.
- `docs/research/ARCHITECTURE_ADAPTATION_PLAN.md`: recommended adaptation route.
- `upstream-notes/DELTA_CHAT_REPOS.md`: upstream inventory.

