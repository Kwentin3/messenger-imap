# Android IMAP Messenger MVP Blueprint Prompt Draft

Date: 2026-05-13

Target Blueprint:

```text
docs/blueprints/ANDROID_IMAP_MESSENGER_MVP_BLUEPRINT.md
```

## Draft task

Prepare the Android IMAP/SMTP-backed messenger MVP Blueprint for the next product stage.

Context:

- MVP-0a Android Diagnostics APK is closed and accepted.
- Mail.ru IMAP/SMTP is accepted as the first transport baseline.
- A local Delta Chat Android worktree baseline exists at `worktrees/deltachat-android-corporate/`.
- Branch: `research/corporate-imap-messenger-baseline`.
- Delta Chat Android baseline build passes through an ASCII path workaround.
- `chatmail/core` must not be changed unless the Blueprint explicitly justifies it.

The Blueprint must design:

- Android-first messenger MVP;
- provider-agnostic IMAP/SMTP transport;
- use of Delta Chat / Chatmail as base;
- accepted thin Delta Chat Android fork path, with custom shell over chatmail/core rejected for MVP;
- account onboarding;
- provider presets and manual/custom provider profile;
- basic one-to-one chat;
- local message storage;
- send/receive flow;
- IDLE/polling strategy;
- credential storage;
- diagnostics integration;
- background reliability as a separate stage;
- safe customization boundaries;
- license constraints and compliance checkpoints.

Hard constraints:

- do not make the product Mail.ru-only;
- do not re-prove that Mail.ru transport is possible;
- do not start a full rebrand before architecture;
- do not change JNI, sync, encryption or database migrations without a design reason;
- do not merge diagnostics APK into the Android client before integration is designed;
- do not commit secrets, app passwords, real test email addresses or raw logs;
- do not promise production-ready reliability.

Required outputs:

- architecture recommendation;
- provider profile model;
- onboarding flow;
- diagnostics gate model;
- storage and credential strategy;
- MVP send/receive strategy;
- background work deferred plan;
- thin-fork intake and customization plan;
- licensing/compliance section;
- risk list and next implementation tickets.
