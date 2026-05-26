# Delta Chat Corporate Feature Map

Date: 2026-05-13

This map summarizes existing Delta Chat / chatmail capabilities for a corporate Android IMAP/SMTP messenger MVP. Mail.ru/VK Mail is the first accepted transport baseline, but the product architecture must stay provider-agnostic.

| Feature | Already exists in Delta Chat | Exists but needs adaptation | Missing | Risk level | Recommended path | Relevant files/APIs/docs |
|---|---:|---:|---:|---|---|---|
| Core contact model | Yes | Minor | No | Medium | Use core contacts; avoid direct DB writes | `core/src/contact.rs:485`, `core/deltachat-jsonrpc/src/api.rs:1668` |
| Programmatic contact creation | Yes | Minor | No | Medium | Use JSON-RPC/FFI APIs only | `api.rs:1668`, `deltachat-ffi/src/lib.rs:2173` |
| Android system contacts import | Yes | Corporate policy needed | No | Medium | Optional; MVP can avoid device contacts | `ContactAccessor.java:53`, `SystemContactPhoto.java` |
| Corporate address book import | Partly | Yes | No | High | Start with explicit vCard/manual import; no silent sync | `api.rs:1855`, `contact.rs:383` |
| vCard creation/export | Yes | Minor | No | Low | Reuse `make_vcard` for sharing/export | `contact.rs:282`, `api.rs:1879` |
| vCard parse/import | Yes | Multi-contact UX needs verification | No | Medium | Explicit import screen with preview | `api.rs:1843`, `api.rs:1855`, `ConversationItem.java:1022` |
| vCard attachment UI | Yes | Yes | No | Low | Reuse existing display/import path | `VcardView.java:61`, `AttachmentManager.java:751` |
| Automatic admin address book sync | No | Significant | Yes | High | Defer; design signed/versioned managed import | No ready primitive found |
| Contact QR/invite | Yes | Minor | No | Medium | Use for verification/invites, not bulk directory | `core/src/qr.rs:49`, `securejoin/qrinvite.rs` |
| Group invite QR | Yes | Minor | No | Medium | Use existing invite semantics | `core/src/qr.rs:77`, `QrShowFragment.java:134` |
| Group chats | Yes | Minor | No | Medium | Reuse core group creation/membership | `core/src/chat.rs:3520`, `api.rs:1047`, `Rpc.java:682` |
| Group member add/remove | Yes | Minor | No | High | Use APIs; do not alter membership protocol | `chat.rs:3827`, `chat.rs:4102`, `api.rs:982` |
| Group system messages | Yes | No | No | High | Keep core-generated system messages | `mimeparser.rs:185`, `api/types/message.rs:380` |
| Corporate starter groups | Partly | Yes | No | Medium | Provision after contacts/accounts are configured | `create_group`, `add_contact_to_chat` |
| Broadcast/announcement channel | Yes | UX/policy adaptation | No | Medium | Use broadcast channels for announcements | `chat.rs:3613`, `api.rs:1089`, `Rpc.java:718` |
| Mailing-list-like behavior | Yes | Needs evaluation | No | Medium | Do not lead with it for MVP | `Chattype::Mailinglist`, `core/src/chat.rs:80` |
| One-to-one chat | Yes | Minor | No | Low | Reuse as MVP default | Core chat/message APIs |
| Attachments | Yes | Policy adaptation | No | Low | Reuse existing attachment pipeline | `api.rs:2458`, `api.rs:2674`, `Rpc.java:1491` |
| Audio attachments | Yes | Minor | No | Low | Reuse audio viewtype | `message.rs:2317`, `api/types/message.rs:295` |
| Voice messages | Yes | Minor | No | Low | Reuse recorder/playback UI and core viewtype | `message.rs:2322`, `headerdef.rs:69`, Android audio package |
| Audio transcription | No | Significant | Yes | Medium | Defer; design local sidecar/generated text later | No transcription code found |
| Manual IMAP/SMTP settings | Yes | Product UX adaptation | No | Medium | Reuse `EnteredLoginParam` and relay setup | `login_param.rs:23`, `EditRelayActivity.java:489` |
| Provider database presets | Yes | Product policy needed | No | Medium | Use presets, do not hardcode Mail.ru only | `core/src/provider.rs:118`, `provider-db/_providers/*.md` |
| Mail.ru/VK Mail preset family | Yes | Diagnostic status wrapper | No | Low | Treat as accepted baseline after MVP-0a evidence | `mail.ru.md`, `vk.com.md` |
| Yandex preset | Yes | Needs runtime diagnostics | No | Medium | Candidate preset, not whitelist-ready by assumption | `provider-db/_providers/yandex.ru.md` |
| Rambler preset | Yes | Needs runtime diagnostics | No | Medium | Candidate preset, not whitelist-ready by assumption | `provider-db/_providers/rambler.ru.md` |
| Provider diagnostic status | No | Yes | Yes | Medium | Add product-layer status: untested/wifi/mobile/whitelist/failed/degraded | New product model needed |
| Connectivity status UI | Yes | Limited | No | Medium | Use for health summary, not field proof | `connectivity.rs:17`, `ConnectivityActivity.java:11` |
| Full transport diagnostics | Partly | Yes | No | Medium | Keep standalone diagnostics APK; later sanitized in-client screen | Diagnostics APK plus `get_connectivity_html` |
| Raw log export | Yes | Must be constrained | No | High | Do not use as field report; sanitize separately | `LogViewFragment.java:130`, `LogViewFragment.java:166` |
| App name customization | Yes | Requires fork/rebuild | No | Medium | Resource-level fork change after Blueprint | `strings.xml:4`, `AndroidManifest.xml:83` |
| Icon customization | Yes | Requires fork/rebuild | No | Medium | Resource-level fork change after Blueprint | `AndroidManifest.xml:83`, mipmap/drawable resources |
| Package name/application ID | Yes | High-risk rebuild/change | No | High | Delay until distribution/legal plan is clear | `build.gradle:34`, `AndroidManifest.xml:517` |
| Themes/colors | Yes | Low-risk if scoped | No | Low | Adapt resources and existing theme system | `themes.xml:12`, `colors.xml:3`, `DynamicTheme.java:50` |
| Default wallpapers/backgrounds | Yes | Minor | No | Low | Product polish after functional Blueprint | `themes.xml`, strings/background resources |
| Notification branding | Yes | Medium | No | Medium | Change carefully; notification channels persist | `NotificationCenter.java:191`, `NotificationCenter.java:596` |
| Managed provider profiles | Partly | Yes | No | Medium | Add product-layer policy/config model | Core provider setup plus new wrapper |
| Managed corporate allowlist | No | Yes | Yes | Medium | Design outside core first | New product layer |
| Credential storage | Existing app handles account auth | Needs review | No | High | Reuse existing mechanisms unless Blueprint proves gap | Account setup/configure code |
| Autocrypt/SecureJoin | Yes | Avoid changes | No | High | Treat as high-risk core protocol area | `securejoin/*`, `qr.rs`, contact key APIs |
| Background receive reliability | Existing app has behavior | Separate stage | No | High | Do not mix with foreground MVP proof | Android services/notifications, separate Blueprint |
| GPL Android fork compliance | Yes, license exists | Legal review | No | High | Plan source obligations before distribution | `deltachat-android/README.md`, `LICENSE` |
| MPL core/provider-db compliance | Yes, license exists | Legal review | No | Medium | Preserve notices and modification boundaries | `core/LICENSE`, `provider-db/LICENSE` |

## Provider-agnostic rule

The corporate messenger must not become Mail.ru-only. Provider support should be modeled as profiles:

- Presets: Mail.ru/VK Mail, Yandex, Rambler, later other providers.
- Manual/custom profile: IMAP host, port, encryption, username mode; SMTP host, port, encryption, username mode; auth mode; app-password hint.
- Diagnostic status per profile: `untested`, `wifi_verified`, `normal_mobile_verified`, `whitelist_verified`, `failed`, `degraded`.

Whitelist-ready status is evidence-based only. A provider brand or website being reachable in a mobile whitelist does not prove IMAP/SMTP transport.

## Safe next step

Use this feature map as input to `docs/blueprints/ANDROID_IMAP_MESSENGER_MVP_BLUEPRINT.md`. The Blueprint should decide fork vs custom shell and define the product-layer models for provider profiles, diagnostic gates, corporate contact import, and MVP onboarding.
