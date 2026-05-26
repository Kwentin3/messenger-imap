# Corporate IMAP Messenger

Corporate IMAP Messenger is an Android-first corporate messenger concept that uses existing IMAP/SMTP providers as message transport and a Corporate Control Plane for organization management.

The project started from a transport hypothesis: email infrastructure may remain reachable in restricted or whitelist mobile network contexts where ordinary messengers are unavailable. MVP-0a validated the first accepted baseline with a standalone Android Diagnostics APK and Mail.ru IMAP/SMTP foreground transport evidence.

## Current Status

- MVP-0a diagnostics: accepted.
- First transport baseline: Mail.ru / VK Mail family.
- Product direction: provider-agnostic, not Mail.ru-only.
- Android Diagnostics prototype: available under `prototypes/android-diagnostics/`.
- Product PRD package: available under `docs/product/`.
- Delta Chat / Chatmail upstream projects: documented as references only, not vendor-copied.

## Repository Layout

```text
docs/
  blueprints/   Product and technical planning inputs.
  hand_off/     Handoff notes and prompt drafts.
  product/      Product PRDs, decisions, and product context.
  reports/      Research, implementation, validation, and bootstrap reports.
  research/     Transport, provider, Delta Chat / Chatmail research, and evidence JSON.
  upstream/     Upstream project references and license notes.
prototypes/
  android-diagnostics/  Standalone Android Diagnostics APK source and docs.
releases/
  README.md     Policy for binary release artifacts.
```

## Import Boundaries

This repository intentionally does not include:

- Delta Chat / Chatmail upstream clones;
- local git worktrees;
- APK build outputs;
- `.gradle`, `build`, `app/build`;
- `node_modules`;
- local account databases;
- raw logcat or raw protocol logs;
- real credentials, app passwords, tokens, or raw AUTH payloads.

## Next Recommended Step

Review the PRD package and decide the implementation route: thin Delta Chat Android fork, custom Android shell over chatmail/core, or another architecture. Then create the Android IMAP Messenger MVP Blueprint and Corporate Control Plane Blueprint.
