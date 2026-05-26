# Android IMAP Messenger MVP Blueprint Inputs

Date: 2026-05-13

These inputs prepare the future `docs/blueprints/ANDROID_IMAP_MESSENGER_MVP_BLUEPRINT.md`. This is not the Blueprint itself.

## Baseline decisions already accepted

- MVP-0a diagnostic track is accepted.
- Mail.ru/VK Mail transport family is the first accepted baseline.
- Mail.ru/VK Mail endpoints: IMAP `imap.mail.ru:993`, SMTP `smtp.mail.ru:465`.
- The messenger architecture must be provider-agnostic.
- Mail.ru must not be the only hardcoded path.
- Diagnostics APK remains a separate validation tool until in-client diagnostics are designed.
- Background/locked-screen reliability is a separate stage.

## What can be taken ready-made

- IMAP/SMTP account setup and transport management from chatmail/core.
- Provider database lookup and manual server settings.
- Local chat/message model.
- One-to-one chats.
- Group chats.
- Broadcast channels for announcement-style communication.
- Contact model.
- vCard generation, parsing, and import primitives.
- Attachments.
- Audio and voice messages.
- QR/SecureJoin primitives for verification and invites.
- Connectivity status and setup error reporting.
- Android resource/build customization mechanisms.

Key references:

- Contacts: `imap-messenger-research/upstream/core/src/contact.rs`.
- Provider setup: `imap-messenger-research/upstream/core/src/provider.rs`, `core/src/configure.rs`, `core/deltachat-jsonrpc/src/api/types/login_param.rs`.
- Groups/broadcast: `imap-messenger-research/upstream/core/src/chat.rs`.
- vCard: `core/src/contact.rs`, `core/src/message.rs`, `core/src/qr.rs`.
- Android manual setup: `imap-messenger-research/upstream/deltachat-android/src/main/java/org/thoughtcrime/securesms/EditRelayActivity.java`.
- Android connectivity: `ConnectivityActivity.java`, `DcHelper.java`.
- Android branding: `build.gradle`, `AndroidManifest.xml`, `strings.xml`, `themes.xml`, `colors.xml`.

## What needs adaptation

- Product onboarding around provider profiles instead of generic email relay setup.
- Provider presets for Mail.ru/VK Mail, Yandex, Rambler, and later providers.
- Manual/custom provider profile UX.
- Diagnostic gate for provider profiles.
- Per-provider diagnostic status: `untested`, `wifi_verified`, `normal_mobile_verified`, `whitelist_verified`, `failed`, `degraded`.
- Corporate contact import flow using core contacts/vCard without requiring Android system contacts.
- Optional device contacts access policy.
- Explicit address book import preview and confirmation.
- Corporate starter group provisioning after contacts/accounts are configured.
- Announcement channel UX using broadcast channels.
- Resource-level branding after legal/distribution decision.
- Sanitized diagnostic report export if diagnostics move into the app.

## What must be designed separately

- Fork vs custom shell decision:
  - Thin Delta Chat Android fork.
  - Custom Android shell over chatmail/core.
  - Own minimal IMAP/SMTP messenger.
- Corporate address book authority model.
- Trusted admin model for directory updates.
- Address book signing, versioning, replay protection, audit, and rollback.
- Credential storage policy and threat model.
- Managed provider configuration distribution.
- Corporate domain allowlist and profile restrictions.
- Background receive reliability and battery optimization.
- In-client diagnostics report schema and sanitization rules.
- License/compliance plan for distributing a modified Android client.
- E2EE posture for MVP: keep existing Autocrypt/SecureJoin semantics, require verification, or defer stronger policy.
- Later audio transcription storage and privacy model.

## What not to do in MVP

- Do not rewrite IMAP/SMTP transport.
- Do not modify chatmail/core sync, MIME, queueing, encryption, group protocol, JNI, or database migrations without a specific design.
- Do not silently import address books from arbitrary messages.
- Do not treat imported contacts as cryptographically verified.
- Do not make a Mail.ru-only architecture.
- Do not treat a provider website being whitelisted as proof that IMAP/SMTP endpoints work.
- Do not merge the standalone diagnostics APK into the messenger before a diagnostics integration design.
- Do not start a broad rebrand before the fork/compliance path is chosen.
- Do not promise production readiness after the MVP Blueprint.
- Do not include test credentials, app passwords, real raw email addresses, or raw logs in docs or builds.

## Human decisions required

- Is GPLv3+ Android client distribution acceptable for the business model?
- Should the MVP be a thin Delta Chat Android fork or a custom shell over chatmail/core?
- Is Mail.ru/VK Mail enough as first field baseline, or must Yandex/Rambler be tested before UI work starts?
- Should the MVP require Android contacts permission, or avoid it entirely?
- Is manual vCard import sufficient for MVP corporate directory, or is managed config required immediately?
- Who is the trusted source of the corporate address book?
- Are starter groups provisioned by an admin, by client-side config, or manually by users?
- Is E2EE required in MVP beyond existing Delta Chat behavior?
- What is the minimum acceptable background receive behavior for the first field trial?
- What app identity/package/distribution channel will be used for internal testing?

## Provider model input

Minimum provider profile:

- Provider ID.
- Display name.
- Email domain hints.
- IMAP host.
- IMAP port.
- IMAP encryption: SSL/TLS or STARTTLS.
- IMAP username mode.
- SMTP host.
- SMTP port.
- SMTP encryption: SSL/TLS or STARTTLS.
- SMTP username mode.
- Auth method.
- App-password hint.
- Diagnostic status.
- Last successful diagnostic report ID.

Initial presets:

- Mail.ru/VK Mail: accepted baseline, but still store diagnostic evidence per environment.
- Yandex: candidate preset, diagnostics required.
- Rambler: candidate preset, diagnostics required.
- Manual/custom: required for provider-agnostic architecture.

## Diagnostics input

Diagnostics should act as a gate, not as a separate product objective:

- A provider can be configured.
- A provider can be tested.
- A provider can be marked verified for a specific network context.
- Whitelist-ready requires field evidence.
- Failed diagnostics should report the failing stage: DNS, TCP, TLS, IMAP login, IMAP select, IMAP IDLE, SMTP auth, SMTP send, receive by Message-ID, Spam/Junk placement.
- Diagnostic reports must be sanitized.
- Raw logcat export is not acceptable as a default evidence artifact.

## Corporate address book input

Recommended MVP path:

1. Use core contact APIs and vCard primitives.
2. Avoid Android system contacts permission unless product policy requires it.
3. Support manual import with preview and confirmation.
4. Do not mark imported contacts as SecureJoin-verified.
5. Keep address book distribution separate from group membership protocol.
6. Defer automatic admin sync until signing/versioning/audit are designed.

Future managed address book requirements:

- Trusted issuer identity.
- Signature verification.
- Version number and effective date.
- Hash of payload.
- Add/update/remove semantics.
- Preview of changes.
- Rollback plan.
- Audit trail.
- Conflict behavior for user-edited contacts.

## Group and broadcast input

Use existing group and broadcast capabilities:

- One-to-one chat is the MVP default.
- Group chats can support teams and projects.
- Broadcast channels can support announcements.
- Membership changes should go through existing core APIs.
- Starter group provisioning should be designed at product layer.

Do not change:

- Group headers.
- Membership sync protocol.
- System message generation.
- Encryption/SecureJoin flows.
- Database migrations.

## Branding input

Branding should be staged:

1. Blueprint decides fork/shell and license path.
2. Internal build can use minimal resource-level branding.
3. Package ID, icons, app name, notification identity, and store metadata require a distribution plan.
4. Full UI rebrand should follow functional onboarding/provider/contact decisions.

Safe branding areas:

- App name string.
- Launcher icon.
- Notification icon/color.
- Theme colors.
- Default backgrounds.
- Corporate strings.

High-risk branding areas:

- Package/application ID.
- Deep links and OAuth assumptions.
- App store/F-Droid metadata.
- Notification channels.
- Trademark and GPL source distribution obligations.

## License input

Observed licenses:

- Delta Chat Android: GPLv3+.
- chatmail/core: MPL-2.0.
- provider-db: MPL-2.0.

Blueprint implications:

- Thin Android fork is likely simplest technically but highest GPL compliance visibility.
- Custom shell over MPL core may provide different boundaries but still needs engineering and legal review.
- Any distribution of modified Android binaries needs a source/compliance plan.
- This is an engineering input only, not legal advice.

## Recommended Blueprint focus

The next Blueprint should answer:

- Which base architecture to use: thin fork, custom shell, or own minimal client.
- How provider profiles and diagnostic status are modeled.
- How onboarding works for presets and manual/custom provider.
- How corporate contacts are imported without breaking trust semantics.
- How one-to-one chat, groups, broadcast channels, attachments, and voice messages are scoped.
- How credentials are stored and protected.
- What diagnostics are required before a provider/profile is marked verified.
- What is explicitly out of scope for the MVP.
