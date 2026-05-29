# Android IMAP Messenger Next Stage Handoff

Date: 2026-05-13

Scope: handoff from MVP-0a Android diagnostics to the next Android messenger architecture stage.

## 1. Executive summary

The next stage is not more diagnostics as the primary workstream. It is the architecture stage for an Android messenger MVP backed by provider-agnostic IMAP/SMTP transport.

MVP-0a established enough evidence to continue. Mail.ru is accepted as the first transport baseline, while the product architecture must remain provider-agnostic from the start.

## 2. Product direction

The product direction is:

- not Mail.ru-only;
- provider-agnostic IMAP/SMTP messenger;
- Mail.ru is the accepted baseline provider;
- other providers are supported through presets and manual profiles;
- whitelist-ready status is evidence-based and must not be assumed from provider name alone.

Mail.ru may be the first tested and default baseline, but it must not become the only hardcoded path.

## 3. Provider architecture principle

The next product stage should introduce explicit provider/transport profiles.

Recommended model:

- `ProviderProfile` describes product-level provider identity and UX hints;
- `TransportProfile` describes concrete IMAP/SMTP connection settings;
- preset profiles cover known providers;
- custom/manual profile supports providers outside presets;
- diagnostics status is attached to the profile, not implied globally.

Each profile should include:

- IMAP settings;
- SMTP settings;
- encryption settings;
- auth mode;
- app-password guidance;
- last known diagnostics status.

The messenger should route all provider-specific behavior through this profile layer.

## 4. Required provider fields

Minimum provider profile fields:

- provider id;
- display name;
- email domain hints;
- IMAP host;
- IMAP port;
- IMAP encryption: SSL/TLS or STARTTLS;
- IMAP username mode;
- SMTP host;
- SMTP port;
- SMTP encryption: SSL/TLS or STARTTLS;
- SMTP username mode;
- auth method;
- app-password hint;
- diagnostic status;
- last successful diagnostic report id.

Additional fields can be added later for OAuth, provider docs links, folder mapping, quota behavior, rate limits and managed configuration.

## 5. Presets

Initial presets:

- Mail.ru;
- VK Mail;
- Yandex;
- manual/custom.

Mail.ru can be the first accepted and best-tested preset. It must not be hardcoded as the only provider path.

## 6. Diagnostics integration principle

Diagnostics should become a configuration gate for the messenger.

The product should allow a provider/account to be:

- configured;
- tested;
- marked as verified after a successful diagnostic run;
- classified as whitelist-ready only after field evidence supports that status.

Failed diagnostics should produce an actionable failed stage, for example DNS, TCP, TLS, IMAP auth, SMTP auth, SMTP send, receive correlation or Spam/Junk placement.

## 7. Architecture options for next stage

Option A: thin fork of Delta Chat Android.

Pros: mature messenger UI, existing chat workflow, existing IMAP/SMTP messenger domain knowledge. Cons: fork complexity, licensing and upstream-change cost, risk of starting with UI/rebrand before product constraints are clear.

Option B: custom Android shell over chatmail/core.

Pros: reuse of a known mail-messenger core and potentially less UI baggage than a full fork. Cons: integration/JNI/build complexity, core assumptions may not match provider-agnostic whitelist diagnostics, licensing review still required. Current status: rejected for MVP by the accepted thin Delta Chat Android fork decision.

Option C: own minimal messenger over IMAP/SMTP.

Pros: maximum control over provider profiles, diagnostics gate, credential UX and MVP scope. Cons: must implement local storage, threading, polling/IDLE, reliability, security and future compatibility ourselves.

Option D: continue diagnostics only.

Pros: improves field confidence across providers/operators. Cons: delays product architecture; no longer necessary as the only workstream because MVP-0a has served its decision purpose.

Do not choose solely by familiarity. Choose by fit against provider-agnostic profiles, credential handling, background strategy, licensing constraints, MVP speed and long-term maintenance.

## 8. What not to repeat

- Do not prove again that Mail.ru transport is possible.
- Do not start with a full UI/rebrand.
- Do not enter core/sync/encryption internals without a specific reason.
- Do not create a Mail.ru-only architecture.
- Do not mix background reliability with the already accepted foreground transport proof.

## 9. Next recommended blueprint

Create:

```text
docs/blueprints/ANDROID_IMAP_MESSENGER_MVP_BLUEPRINT.md
```

Blueprint goal:

- Android-first messenger MVP;
- provider-agnostic IMAP/SMTP transport;
- Mail.ru baseline;
- manual/custom provider profiles;
- account onboarding;
- basic one-to-one chat;
- local storage;
- send/receive;
- polling/IDLE strategy;
- background reliability as a separate stage;
- security and credential handling.

The blueprint should explicitly separate MVP foreground messaging from later background/locked-screen reliability work.

## 10. Open questions for next stage

- Use a Delta Chat Android fork or a custom shell? Resolved for MVP: thin Delta Chat Android fork.
- Use chatmail/core or own thin IMAP/SMTP logic?
- How should credentials be stored?
- How should background receive work?
- Is E2EE required for MVP?
- How should a corporate address book work?
- How should managed provider config work?
- Is iOS support required later?
- What are the legal implications of GPL/MPL dependencies and forks?

The fork route is now answered for MVP. Remaining questions still need implementation planning before Android fork changes go beyond the build-only intake slice.
