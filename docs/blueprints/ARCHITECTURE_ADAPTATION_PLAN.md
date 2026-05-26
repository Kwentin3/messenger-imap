# Architecture adaptation plan

## Recommendation

Do not start with a deep fork of `deltachat-desktop`, `deltachat-android` or `chatmail/core`.

MVP-0 should use `chatmail/core` through JSON-RPC, preferably via the prebuilt `deltachat-rpc-server` and generated JS/TS client. This tests the transport with minimal upstream risk and avoids Rust/Crypto/IMAP/SMTP rewrites.

## Suggested stages

1. Transport MVP:
   - small desktop/CLI diagnostic shell around JSON-RPC or the standalone `prototypes/imap-diagnostics`;
   - provider selector: Mail.ru, VK Mail, Yandex;
   - email/app-password input;
   - explicit diagnostics report;
   - no branding/rebrand work.
2. Core-wrapper MVP:
   - use `@deltachat/stdio-rpc-server` and `@deltachat/jsonrpc-client`;
   - call `addAccount`, `addOrUpdateTransport`, `startIo`, contact/chat/message APIs;
   - enforce corporate domain allowlist in wrapper before passing credentials to core;
   - keep provider presets in wrapper unless upstream provider-db needs a small PR.
3. Thin desktop fork only after transport proof:
   - narrow onboarding changes;
   - hide/disable non-corporate flows in UI;
   - add managed config and diagnostics surfaces;
   - avoid touching `core` unless API is missing.
4. Android later:
   - only after desktop/RPC transport is proven;
   - Android build requires Rust, NDK and submodules; higher cost for first discovery.

## Safe customization points

- Wrapper-side onboarding, provider selector, domain allowlist.
- Corporate address book imported through JSON-RPC contact APIs or preloaded vCards.
- UI flags/config in desktop frontend.
- Provider presets if implemented as data, not transport rewrites.
- Diagnostics as a separate tool or new JSON-RPC methods after MVP-0.

## High-risk areas

- IMAP sync, folder movement, message assignment and deduplication.
- SMTP queue/retry behavior.
- Autocrypt/SecureJoin/rPGP encryption logic.
- Database migrations and account storage format.
- Group membership protocol.
- Multi-device sync and transport sync.

## MVP-0 backlog

1. Use real test accounts for Mail.ru, VK Mail and Yandex with app passwords.
2. Extend diagnostics to correlate SMTP sent message with IMAP received message by Message-ID.
3. Persist diagnostic JSON files per operator/provider/network mode.
4. Build a tiny JSON-RPC client script that configures an account with `addOrUpdateTransport`.
5. Test Delta Chat core message send/receive using two controlled accounts.
6. Add corporate allowlist validation before account configuration.
7. Draft managed config schema: allowed domains, provider presets, support contact, diagnostic upload target.
8. Decide whether MVP UI is custom Electron/Tauri shell over RPC or thin `deltachat-desktop` fork.
9. Run whitelist field matrix with at least 3 repetitions per combination.
10. Legal review of MPL/GPL distribution plan.

