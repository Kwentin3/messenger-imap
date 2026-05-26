# Delta Chat / Chatmail Capabilities for Corporate Messenger

Date: 2026-05-13

## Executive summary

Delta Chat Android plus chatmail/core already cover a large part of an Android-first IMAP/SMTP-backed messenger MVP: account setup over IMAP/SMTP, local chats, contacts, vCard import/export primitives, group chats, broadcast channels, attachments, audio/voice messages, QR/SecureJoin flows, connectivity status, and Android resource-level customization.

The strongest MVP path is a provider-agnostic Android fork or shell that keeps chatmail/core semantics intact and adapts onboarding, provider profiles, diagnostics gates, corporate contact import, and branding at the Android/product layer. Mail.ru/VK Mail should be treated as the first accepted transport baseline, not as the only architecture path. Yandex, Rambler, and manual/custom profiles are reasonable candidates, but whitelist-ready status must be assigned only after diagnostics.

No ready-made "trusted admin automatically syncs a corporate address book to all clients" mechanism was found. Delta Chat has vCard, contact sharing, QR invites, groups, broadcast channels, and backup/migration primitives, but automatic silent import from messages or attachments would need a separate trust, signing, versioning, and audit design.

License risk is material: Delta Chat Android is GPLv3+, while chatmail/core and provider-db are MPL-2.0. A distributed modified Android client likely requires GPL compliance. This report is an engineering note, not legal advice.

## Contacts/address book

chatmail/core has its own contact model. `core/src/contact.rs` defines `ContactId`, `Contact`, contact lookup, contact creation, blocking, deletion, listing, and encryption-related metadata. Relevant entry points include `ContactId` at `imap-messenger-research/upstream/core/src/contact.rs:53`, `Contact` at `imap-messenger-research/upstream/core/src/contact.rs:485`, `Origin` at `imap-messenger-research/upstream/core/src/contact.rs:542`, `Contact::create` at `imap-messenger-research/upstream/core/src/contact.rs:765`, `Contact::get_all` at `imap-messenger-research/upstream/core/src/contact.rs:1171`, and encryption info retrieval at `imap-messenger-research/upstream/core/src/contact.rs:1384`.

The JSON-RPC layer exposes contact operations suitable for Android/product integration: `create_contact`, `get_contacts`, `parse_vcard`, `import_vcard`, and `make_vcard` in `imap-messenger-research/upstream/core/deltachat-jsonrpc/src/api.rs:1668`, `:1742`, `:1843`, `:1855`, and `:1879`. The FFI layer also exposes `dc_create_contact`, `dc_make_vcard`, `dc_import_vcard`, and `dc_get_contacts` in `imap-messenger-research/upstream/core/deltachat-ffi/src/lib.rs:2173`, `:2212`, `:2229`, and `:2254`.

Android also has optional system contact integration. `ContactAccessor` reads Android contacts through `ContactsContract` in `imap-messenger-research/upstream/deltachat-android/src/main/java/org/thoughtcrime/securesms/contacts/ContactAccessor.java:53`, and system contact photos are read in `SystemContactPhoto.java`. This is useful but not required for a corporate MVP. A corporate address book can be imported into Delta Chat's own contact model through core APIs without requiring access to the device address book.

Contacts are connected to encryption state. `Contact::is_key_contact`, `public_key`, `get_authname`, and `get_encrinfo` exist in `core/src/contact.rs`. SecureJoin tests show that verified contact state and authenticated names are updated through the SecureJoin handshake, not by arbitrary plain contact insertion. Manual imports should avoid claiming verification or replacing key state. Use core APIs and preserve Autocrypt/SecureJoin semantics.

## vCard/import/export

vCard support exists in core and Android:

- Core creates and imports vCards through `make_vcard` and `import_vcard` in `imap-messenger-research/upstream/core/src/contact.rs:282` and `:383`.
- Message view types include vCard handling in `imap-messenger-research/upstream/core/src/message.rs:2350`.
- QR parsing recognizes vCard content through `decode_vcard` in `imap-messenger-research/upstream/core/src/qr.rs:1086`.
- JSON-RPC exposes `parse_vcard`, `import_vcard`, `import_vcard_contents`, and `make_vcard` in `imap-messenger-research/upstream/core/deltachat-jsonrpc/src/api.rs:1843`, `:1855`, `:1869`, and `:1879`.
- Android can attach a contact as `vcard.vcf` from a conversation path through `ConversationActivity.java:1213`.
- Android displays and imports received vCards through `ConversationItem.java:1022` and `VcardView.java:61`.
- Attachment selection recognizes `.vcf`, `.vcard`, and `text/vcard` in `AttachmentManager.java:751`.

For MVP, vCard is the safest starting primitive for corporate contact import. Current Android UI evidence points to user-confirmed import, not silent managed sync. The composer path appears to accept one vCard attachment in the checked code path, while core has broader parse/import primitives. Multi-contact vCard import should be verified and designed explicitly before relying on it for a full organization directory.

## Address book distribution options

Existing primitives:

- vCard sharing and importing.
- QR contact invite / contact verification flows.
- Group invite and broadcast channel invite QR flows.
- Broadcast channels for one-to-many announcements.
- Attachments that can carry contact files.
- Account backup / migration QR logic, but this is device/account migration, not address book distribution.

No ready-made trusted corporate address book distribution channel was found. A design where an admin sends an address book attachment and clients automatically import it is technically plausible, but should not be treated as a built-in Delta Chat capability.

Security risks for automatic import:

- Sender spoofing or compromised admin mailbox.
- Malicious replacement of contact names or addresses.
- Key-confusion risk if imported data is allowed to affect encryption/trust state.
- Replay of old address book versions.
- Silent addition of unwanted contacts or groups.
- Lack of audit and rollback.

Safer MVP order:

1. Manual import of a signed or checksum-published vCard/directory file with user confirmation.
2. Managed provider/account configuration distributed outside the chat channel.
3. Admin message with attachment plus explicit import screen, version, hash, issuer, and preview.
4. Later, trusted admin distribution with signature verification and versioning.

For MVP, do not silently import arbitrary vCards from chat messages. Do not mark imported contacts as verified. Do not modify SecureJoin/Autocrypt to fit an address book workflow.

## Group chats and broadcast options

Group functionality is already present in core and Android.

Core `Chattype` includes `Group`, `Mailinglist`, `OutBroadcast`, `InBroadcast`, and `Single` in `imap-messenger-research/upstream/core/src/chat.rs:80`. Group creation exists through `create_group` and `create_group_unencrypted` in `core/src/chat.rs:3520` and `:3525`. Broadcast creation exists through `create_broadcast` in `core/src/chat.rs:3613`.

Group membership changes are handled through core APIs. Adding a contact to a promoted group sends a system message automatically according to `add_contact_to_chat` in `core/src/chat.rs:3827`. Removing a member is handled by `remove_contact_from_chat` in `core/src/chat.rs:4102`. Group name, description, and profile image changes also send special status messages through `set_chat_name`, `set_chat_description`, and `set_chat_profile_image`.

JSON-RPC exposes the same operations: `add_contact_to_chat`, `remove_contact_from_chat`, `get_chat_contacts`, `create_group`, `create_group_unencrypted`, and `create_broadcast` around `imap-messenger-research/upstream/core/deltachat-jsonrpc/src/api.rs:982`, `:998`, `:1021`, `:1047`, and `:1089`. The Android RPC wrapper mirrors these APIs in `imap-messenger-research/upstream/deltachat-android/src/main/java/chat/delta/rpc/Rpc.java:618`, `:632`, `:653`, `:682`, and `:718`.

Android has UI routes for creating groups and channels. `NewConversationActivity.java` starts `GroupCreateActivity` and can pass `CREATE_BROADCAST`. `ProfileFragment.java` and `ContactMultiSelectionActivity.java` cover member management flows.

Broadcast channels are useful for announcements. The core docs note that recipients of an outgoing broadcast see messages as one-to-one chats and do not see other recipients. This is closer to announcements than to a shared team chat. Mailing-list behavior exists but is not the first choice for a controlled corporate MVP without deeper evaluation.

Do not change group membership logic, message headers, sync behavior, or encryption rules in MVP. Corporate starter groups can be created via existing APIs after contacts/accounts are configured, but the exact provisioning workflow needs Blueprint-level design.

## Provider setup/manual IMAP/SMTP

Provider setup is already provider-agnostic at the core level.

`core/src/provider.rs` defines provider records with IDs, hints, server lists, OAuth2 data, strict TLS info, and SMTP recipient limits. `get_provider_info_by_addr`, `get_provider_info`, and `get_provider_by_id` exist at `imap-messenger-research/upstream/core/src/provider.rs:167`, `:177`, and `:195`.

`core/src/configure.rs` implements configuration flows and modern transport management through `configure`, `add_or_update_transport`, and `list_transports` at `imap-messenger-research/upstream/core/src/configure.rs:78`, `:114`, and `:213`. JSON-RPC exposes `get_provider_info`, `configure`, `add_or_update_transport`, `add_transport`, and `list_transports` in `imap-messenger-research/upstream/core/deltachat-jsonrpc/src/api.rs:348`, `:487`, `:534`, `:541`, and `:558`.

Manual server settings are represented by `EnteredLoginParam` in `imap-messenger-research/upstream/core/deltachat-jsonrpc/src/api/types/login_param.rs:23`. Fields include IMAP server, SMTP server, ports, security, usernames, password, and certificate checks. Android exposes a manual setup path in `EditRelayActivity.java`; relevant points include transport listing at `:117`, advanced settings expansion at `:185`, manual field construction at `:489`, and error display at `:519`.

Provider-db already contains candidate presets:

- Mail.ru: `imap.mail.ru:993`, `smtp.mail.ru:465`, domains including `mail.ru`, `inbox.ru`, `bk.ru`, `list.ru`, status `PREPARATION` in `imap-messenger-research/upstream/provider-db/_providers/mail.ru.md`.
- VK Mail: same Mail.ru endpoints in `imap-messenger-research/upstream/provider-db/_providers/vk.com.md`.
- Yandex: IMAP/SMTP over SSL, with provider-specific enablement notes in `imap-messenger-research/upstream/provider-db/_providers/yandex.ru.md`.
- Rambler: IMAP/SMTP over SSL, with mail client access enablement notes in `imap-messenger-research/upstream/provider-db/_providers/rambler.ru.md`.

Mail.ru and VK Mail should be treated as one transport family for baseline purposes: IMAP `imap.mail.ru:993`, SMTP `smtp.mail.ru:465`. This does not justify a Mail.ru-only architecture. Provider profiles should include presets and manual/custom configuration, plus diagnostic status per profile: `untested`, `wifi_verified`, `normal_mobile_verified`, `whitelist_verified`, `failed`, `degraded`.

## Diagnostics/connectivity

Delta Chat has built-in connectivity status and setup error reporting, but not the full transport diagnostics produced by the standalone Android Diagnostics APK.

Core connectivity state lives in `imap-messenger-research/upstream/core/src/scheduler/connectivity.rs`. It defines rough connectivity states and emits `ConnectivityChanged` events. JSON-RPC exposes `get_connectivity` and `get_connectivity_html` in `imap-messenger-research/upstream/core/deltachat-jsonrpc/src/api.rs:2072` and `:2091`.

Android has a `ConnectivityActivity` that displays core connectivity HTML in `imap-messenger-research/upstream/deltachat-android/src/main/java/org/thoughtcrime/securesms/ConnectivityActivity.java:11`. Settings and account selection link to it. Android also maps connectivity to short UI summaries in `DcHelper.java:466` and account/avatar indicators.

Configuration errors are surfaced through configure progress and `EditRelayActivity`. This is useful for onboarding, but it is not equivalent to DNS/TCP/TLS/IMAP login/IDLE/SMTP auth/send/Message-ID correlation/Spam-Junk diagnostics.

Log export exists but is raw and sensitive. `LogViewFragment.java:130` grabs logcat, and `LogViewFragment.java:166` warns the user that logs may contain sensitive information. This path should not be used as a field diagnostic artifact unless a sanitized export path is designed.

Safe integration path:

1. Keep the standalone Android Diagnostics APK separate for current provider/network validation.
2. In the messenger Blueprint, define a diagnostic gate around provider profiles.
3. Later add an in-client diagnostic screen only if it emits sanitized structured reports and does not expose raw logs or credentials.

## Branding/customization

Android customization is possible through fork/rebuild. Safe areas include resource strings, app name, icons, notification assets, colors, themes, default backgrounds, and product flavors.

Relevant files:

- App ID, namespace, version, build types, and flavors: `imap-messenger-research/upstream/deltachat-android/build.gradle:17`, `:34`, `:105`, `:124`.
- App label, launcher icon, splash theme, providers, and authorities: `imap-messenger-research/upstream/deltachat-android/src/main/AndroidManifest.xml:83`, `:153`, `:517`, `:536`.
- App name: `imap-messenger-research/upstream/deltachat-android/src/main/res/values/strings.xml:4`.
- Theme roots and colors: `imap-messenger-research/upstream/deltachat-android/src/main/res/values/themes.xml:12` and `imap-messenger-research/upstream/deltachat-android/src/main/res/values/colors.xml:3`.
- Runtime theme selection: `DynamicTheme.java:50`.
- Notification icon/color and channel behavior: `NotificationCenter.java:191` and `:596`.

Safe customization:

- App name and visible strings.
- Launcher icon and notification icon.
- Color resources and theme resources.
- Default chat background/wallpaper settings.
- Product flavor for corporate builds.

High-risk customization:

- Package name/application ID without updating providers, links, OAuth, Play Store/F-Droid assumptions, backup identifiers, and notification channel behavior.
- Deep rebrand before license/trademark review.
- UI redesign that changes account setup, chat, group, or encryption flows without tests.
- Any branding that implies proprietary status while distributing GPL-covered modified Android client code.

Managed config may eventually provide organization-level defaults or provider profiles, but it does not replace fork/rebuild for application identity, package ID, icons, or binary branding.

## Voice messages/audio attachments

Audio and voice messages already exist.

Core message view types include `Audio` and `Voice` in `imap-messenger-research/upstream/core/src/message.rs:2317` and `:2322`. JSON-RPC maps these through `MessageViewtype::Audio` and `MessageViewtype::Voice` in `imap-messenger-research/upstream/core/deltachat-jsonrpc/src/api/types/message.rs:295` and `:298`. The `ChatVoiceMessage` header exists in `imap-messenger-research/upstream/core/src/headerdef.rs:69`.

Core stores duration metadata through `Message::get_duration` and `set_duration` at `core/src/message.rs:886` and `:1164`, and attachment metadata can carry audio duration in `core/src/download/post_msg_metadata.rs:23`. Extension mapping treats common audio formats as audio attachments, including AAC, FLAC, M4A, MP3, OGA/OGG, WAV, and others in `core/src/message.rs:1508` through `:1566`.

Android has audio recording and playback components such as `audio/AudioRecorder.java`, `audio/AudioCodec.java`, `MicrophoneRecorderView.java`, `AudioView.java`, and `AudioPlaybackService.java`.

No transcription feature was found in the checked code. A future transcription integration should not mutate core message transport semantics in MVP. Safer options for a later stage are local sidecar metadata, an additional generated text message, or a structured app-level metadata table. The exact storage choice should be made in a separate security/privacy design because transcripts can be more sensitive than audio.

Video calls or real-time media are out of scope for an IMAP/SMTP-backed MVP.

## Attachments

General attachment support is already present. JSON-RPC `send_msg` accepts `MessageData` in `imap-messenger-research/upstream/core/deltachat-jsonrpc/src/api.rs:2458`, while `misc_send_msg` supports text plus a file path in `api.rs:2674`. Android exposes `Rpc.sendMsg` and `Rpc.miscSendMsg` in `imap-messenger-research/upstream/deltachat-android/src/main/java/chat/delta/rpc/Rpc.java:1491` and `:1578`.

Core `Viewtype` covers file, image, GIF, sticker, audio, voice, video, vCard, text, and webxdc variants. For MVP one-to-one chat, attachments should be treated as an existing capability, with corporate policy controls considered at the Android/product layer.

## Security risks

Key risks for the corporate MVP:

- Silent contact import can mislead users about identity and trust.
- Imported contacts must not be treated as SecureJoin-verified.
- Address book distribution needs authenticity, versioning, replay protection, and user/admin audit.
- Provider presets must not embed passwords or real test accounts.
- Raw log export can leak credentials, addresses, message metadata, device state, or server details.
- Manual IMAP/SMTP settings must not bypass TLS validation without explicit warning and policy.
- Whitelist-ready status must be evidence-based. A web brand being accessible in a mobile whitelist is not proof that IMAP/SMTP endpoints work.
- Background reliability, locked-screen receive, push behavior, and battery optimization are separate from the foreground transport proof already completed.

## License implications

Observed licenses:

- Delta Chat Android: GPLv3+ according to `imap-messenger-research/upstream/deltachat-android/README.md`.
- chatmail/core: MPL-2.0 according to `imap-messenger-research/upstream/core/LICENSE`.
- provider-db: MPL-2.0 according to `imap-messenger-research/upstream/provider-db/LICENSE`.

Engineering implications:

- A thin fork of Delta Chat Android is technically natural, but distributing a modified Android client likely requires GPL source distribution compliance.
- A custom shell over MPL-2.0 core may have different compliance mechanics, but JNI/build integration and app distribution still need legal review.
- Provider-db modifications should preserve attribution and MPL notices.
- Do not make product or procurement promises before legal review of GPL/MPL obligations, trademarks, app store distribution, and source delivery model.

## Recommended MVP path

Recommended path before full Blueprint:

1. Treat Delta Chat Android and chatmail/core as the capability baseline.
2. Do not rewrite IMAP/SMTP, MIME, local storage, queues, groups, or encryption for MVP.
3. Keep architecture provider-agnostic: Mail.ru/VK Mail baseline, Yandex/Rambler candidates, manual/custom profiles.
4. Build onboarding around provider profiles and diagnostic status.
5. Use core contacts and vCard import for corporate address book MVP, with explicit user/admin confirmation.
6. Use existing one-to-one chats, groups, broadcast channels, attachments, and voice messages.
7. Keep the standalone Diagnostics APK separate until an in-client sanitized diagnostic screen is designed.
8. Keep background reliability as a separate stage.
9. Keep license/compliance review on the critical path before distribution of a modified Android client.

## Open questions

- Should the product be a thin Delta Chat Android fork, a custom Android shell over chatmail/core, or a smaller custom IMAP/SMTP client?
- Is GPLv3+ acceptable for the intended distribution model?
- What exact corporate address book source is authoritative?
- Is manual vCard import enough for MVP, or is managed config required immediately?
- Who is allowed to publish/update a corporate address book?
- How are address book versions signed, audited, and rolled back?
- Should imported contacts be address-only until SecureJoin verification?
- How should provider profile diagnostic status be stored and displayed?
- Which providers beyond Mail.ru/VK Mail should be tested first: Yandex, Rambler, or operator-specific corporate mail?
- What background receive reliability target belongs to the stage after foreground MVP?
