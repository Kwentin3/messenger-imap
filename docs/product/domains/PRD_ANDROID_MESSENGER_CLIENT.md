# Android Messenger Client PRD

Date: 2026-05-14

Status: high-level domain PRD.

Root PRD: [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)

## 1. Purpose

Define the high-level product requirements for the Android-first messenger client for Corporate IMAP Messenger.

The Android client is the employee-facing and guest-facing product surface. It must let an invited employee enroll into an organization, configure an IMAP/SMTP provider profile, verify transport, sync the corporate directory, and use messenger-style communication. It must also let external contacts join through external invites without receiving internal membership or the internal corporate directory.

Implementation strategy decision update: MVP uses a thin fork of Delta Chat Android. Custom Android shell over `chatmail/core` is rejected for MVP, and `chatmail/core` changes require a separate Blueprint. Existing Delta Chat Android / chatmail capabilities should be reused through a safe, compliant, thin fork path.

## 2. Context

MVP-0a diagnostics proved that a standalone Android APK can validate foreground IMAP/SMTP transport and that Mail.ru transport can work as the first accepted baseline. The next product step is an Android messenger client that turns the transport proof into a managed corporate communication experience.

Delta Chat Android and chatmail/core already provide relevant capabilities: IMAP/SMTP transport, local chat model, one-to-one chats, groups, broadcast channels, contacts, vCard primitives, attachments, voice/audio messages, provider setup, and connectivity status.

The missing product layer is corporate: invite enrollment, trusted membership, managed directory, external contact scoping, provider profile policy, diagnostic gates, and control-plane sync.

## 3. Goals

- Provide an Android-first messenger experience backed by IMAP/SMTP transport.
- Support invite-based organization enrollment.
- Configure provider profiles without hardcoding the product to Mail.ru.
- Use Mail.ru / VK Mail as the first accepted baseline while supporting manual/custom profiles.
- Provide a clear transport check during onboarding and settings.
- Sync the corporate directory from the control plane.
- Support external contact invite handling and scoped guest access.
- Show external contact badges and warnings in directory and chat.
- Support one-to-one chats, basic groups, attachments, and voice messages.
- Consume announcements or broadcast-style communication if reused messenger capabilities support it.
- Protect credentials and avoid leaking secrets in diagnostics.
- Keep background and locked-screen reliability as a separate stage.

## 4. Non-goals

- No custom Android shell over `chatmail/core` in MVP.
- No rewrite of IMAP/SMTP transport.
- No modification of chatmail/core without Blueprint-level justification.
- No Mail.ru-only architecture.
- No video calls.
- No real-time voice calls.
- No production-grade background guarantees in first MVP.
- No silent arbitrary contact import.
- No internal directory exposure to external contacts.
- No assumption that every invite creates employee membership.
- No full UI rebrand before legal, package ID, branding, signing, and distribution decisions.
- No raw logcat or secret export as diagnostic evidence.

## 5. User Roles

- Invited Employee: installs or opens the app through an invite flow and completes enrollment.
- Employee: uses chats, groups, announcements, attachments, voice messages, and directory search.
- Revoked Employee: loses active membership and should lose access to current corporate directory state after next sync.
- Support / IT Administrator: helps users with install, provider setup, credentials, diagnostics, and support reports.
- Field Tester: runs transport checks in known network contexts and exports sanitized reports.
- External Contact / Guest: joins through an external invite and can communicate only with allowed employee, team, or room.

## 6. Core Flows

### First Launch

The app opens into an enrollment-oriented flow, not a generic email client. The user should understand that organization membership requires an invite or enrollment code.

MVP behavior:

- show the working product/app identity approved for internal testing;
- offer invite token/deep link handling or fallback code entry;
- allow provider profile setup only in the context of an organization enrollment or explicit test/support mode;
- do not grant corporate directory access before activation.

### Invite Token Entry / Deep Link

The app receives an invite token through a deep link, app link, QR code, or manual fallback code. The token identifies the target organization and invite policy but is not by itself proof of final membership. The app must distinguish internal organization invites from external contact invites.

MVP behavior:

- validate token format locally before attempting enrollment;
- call the control plane to resolve invite status;
- show clear failure states for expired, revoked, already used, or mismatched invites;
- proceed to provider setup when the invite is valid enough to continue;
- for external invites, present external enrollment wording and avoid promising employee membership.

### Provider Setup

The user selects or receives a provider profile. The MVP must support Mail.ru / VK Mail and manual/custom profiles. Yandex and Rambler can appear as candidate profiles only with product wording that avoids claiming whitelist-ready status without evidence.

MVP behavior:

- select provider profile;
- show provider-specific credential guidance such as app-password hint where relevant;
- collect required username and credential inputs;
- support manual IMAP/SMTP host, port, encryption, username mode, and auth method for custom profiles;
- keep provider profile data separate from secrets.

### Transport Test

The app exposes a product action such as **Check Transport** during onboarding or settings. The exact UI label can change later, but the product function must be clear.

MVP behavior:

- run or invoke a basic diagnostic gate defined by the Diagnostics PRD;
- show pass/fail and failing stage where available;
- require sanitized reporting for support export;
- avoid raw AUTH, app passwords, raw logcat, and sensitive payloads.

### First Directory Sync

After invite validation, provider setup, and minimum transport verification, the app syncs the corporate directory from the control plane.

MVP behavior:

- request directory manifest;
- compare local version/hash;
- download snapshot when needed;
- apply active member records and managed groups;
- show or use only active directory members for corporate lookup;
- handle revoked/suspended states according to the Corporate Directory PRD.
- for external contacts, fetch only the allowed visible directory, not the full internal member directory.

### One-to-One Chat

Employees can start one-to-one chats with active directory members.

MVP behavior:

- search corporate directory;
- open chat with an active member;
- send and receive text messages through IMAP/SMTP;
- show connectivity or delivery state using reused messenger capabilities where available;
- avoid presenting imported directory contacts as cryptographically verified unless the underlying trust model actually verifies them.

### External Contact Directory And Chat

Employees can see external contacts in a separate external section according to visibility scope. External contacts can communicate only with allowed employees, teams, or rooms.

MVP behavior:

- show External Contacts separately from Internal Members;
- show a clear external badge in directory, contact picker, chat header, and chat info;
- warn employees when a conversation includes an external contact;
- prevent external contacts from seeing the full internal directory;
- avoid placing external contacts into internal groups or "All employees";
- support one-to-one external relationship as the MVP default.

### Group Chat

The MVP may support basic groups or starter groups.

MVP behavior:

- use active directory members as eligible group participants;
- allow admin-managed starter groups or user-created groups depending on later Blueprint decision;
- remove or hide revoked members from managed group views after directory sync;
- do not redesign underlying group protocol in PRD.

### Broadcast / Announcement Consumption

The client should be able to consume organization announcements if reused Delta Chat / chatmail broadcast capabilities are selected.

MVP behavior:

- receive announcement-style messages;
- clearly distinguish announcement consumption from ordinary editable group membership where product semantics require it;
- defer advanced announcement authoring policy to control plane and later Blueprint.

### Attachment Send / Receive

Employees can send and receive ordinary attachments supported by reused messenger capabilities.

MVP behavior:

- support basic file/image/document attachments if provided by the chosen implementation path;
- apply corporate policy defaults later if needed;
- do not use attachments as a silent address book import channel.

### Voice Message Send / Receive

Voice/audio messages are allowed as an existing capability.

MVP behavior:

- allow voice message send/receive when supported by reused client/core capabilities;
- treat audio transcription as later scope;
- do not add real-time voice calls.

## 7. Functional Requirements

- The client must support invite-based organization enrollment.
- The client must distinguish app possession from organization membership.
- The client must support provider profile selection and manual/custom setup.
- The client must support Mail.ru / VK Mail baseline profile.
- The client must not assume Yandex, Rambler, or custom providers are whitelist-ready without diagnostics.
- The client must provide a transport check function during onboarding or settings.
- The client must sync a corporate directory from the control plane.
- The client must distinguish Internal Members from External Contacts.
- The client must show external contacts in a separate section.
- The client must show external badges/warnings in conversations with external contacts.
- The client must support external invite handling without creating membership.
- The client must provide a scoped visible directory for external users.
- The client must store a local directory cache with version/hash metadata.
- The client must support one-to-one chat in MVP.
- The client should support basic groups or starter groups in MVP.
- The client should support attachments and voice messages through reused capabilities.
- The client must protect credentials and diagnostic exports.
- The client must surface support-friendly failure states for invite, provider, diagnostics, and directory sync failures.

## 8. Corporate Directory Behavior

The Android client is a consumer of the control-plane directory. It must not become the source of truth for corporate membership.

MVP behavior:

- cache the active directory locally;
- track directory version and hash;
- apply updates only after hash verification;
- show active members by default;
- show external contacts separately according to visibility scope;
- show external badges/type labels;
- never expose the full internal directory to external contacts;
- hide or remove revoked members from active directory views after sync;
- clear or hide corporate directory data for a revoked client after next successful sync;
- avoid guaranteeing erasure of previously seen data.

Manual vCard import may be considered only with explicit preview and user confirmation. Silent arbitrary imports are not allowed.

## 9. Provider Profile Behavior

Provider profiles are product-level configuration objects that describe IMAP/SMTP settings and guidance. They are not credential stores.

MVP behavior:

- support Mail.ru / VK Mail family profile;
- support manual/custom profile;
- treat Yandex and Rambler as candidate profiles until diagnostics support the desired status;
- display diagnostic status where available;
- attach transport verification results to provider profile and network context;
- avoid hardcoded Mail.ru-only flows.

## 10. Diagnostics Behavior

Diagnostics are part of product trust, onboarding, and support.

MVP behavior:

- expose a **Check Transport** function or equivalent;
- show whether DNS, TCP, TLS, IMAP, SMTP, send/receive, or placement checks failed where available;
- allow support export only as sanitized JSON or sanitized structured report;
- never include app passwords, raw AUTH commands, raw logcat, real message payloads, or unnecessary personal data;
- allow standalone diagnostics evidence to remain authoritative until in-client diagnostics are designed.

## 11. Credential / Security Requirements

- Store credentials using Android-appropriate secure storage selected in Blueprint.
- Never store provider credentials inside provider profile definitions.
- Never include credentials in support reports.
- Avoid disabling TLS verification except in explicit debug/test contexts controlled by policy.
- Do not request Android contacts permission unless product policy and Blueprint justify it.
- Avoid marking directory-imported contacts as verified if verification did not occur.
- Treat invite tokens as sensitive enrollment artifacts.
- Treat external invite tokens as sensitive guest-access artifacts.
- Do not treat external invites as employee membership invites.
- Make revoked membership visible to the client through directory/control-plane sync.

## 12. Branding / Customization High-Level

The working product name is Corporate IMAP Messenger. The final brand is not fixed.

MVP branding should be minimal:

- internal app name;
- internal icon if needed;
- organization-specific strings only where safe;
- no broad rebrand before legal, package ID, signing, and distribution decisions.

## 13. MVP Scope

- Android app flow for invite enrollment.
- Provider setup with Mail.ru / VK Mail baseline and manual/custom profile.
- Basic transport check or diagnostic gate.
- One-to-one chats.
- Basic groups or starter groups.
- Attachments.
- Voice/audio messages if available through reused capabilities.
- Corporate directory sync and local cache.
- External contacts section and external badges.
- External invite handling for one-to-one external relationships.
- Scoped directory for external contacts.
- Support-friendly sanitized diagnostics export or reference.

## 14. Later Scope

- Background and locked-screen reliability.
- Rich integrated diagnostic screen and history.
- Signed directory updates.
- Advanced group provisioning and announcement authoring.
- External project rooms and team-level external relationships.
- External organization-aware contact views.
- Policy-driven attachment limits.
- Organization-specific branding and managed configuration.
- Audio transcription with explicit privacy model.
- Broader provider and network validation.
- Additional platforms.

## 15. Acceptance Criteria

- An invited employee can install/open the Android app and enter or receive an invite token.
- APK possession alone does not activate organization membership.
- The user can configure Mail.ru / VK Mail or manual/custom IMAP/SMTP settings.
- The app provides a transport check function or equivalent diagnostic gate.
- The app does not claim untested providers are whitelist-ready.
- The app can perform first corporate directory sync from the control plane.
- The app separates Internal Members and External Contacts in directory views.
- The app visibly marks external contacts in directory and chat UI.
- The app handles external invites as external relationships, not membership.
- An external contact cannot see the internal corporate directory.
- The app supports one-to-one chat with active directory members.
- The app supports basic groups or documents the chosen MVP fallback.
- The app supports attachments and voice messages if the reused implementation path exposes them safely.
- Diagnostic exports are sanitized.
- Background reliability is not claimed as production-grade in MVP.

## 16. Open Questions

- What secure credential storage policy is required for MVP?
- What is the Android fork intake plan for `Kwentin3/messenger-imap-android`?
- Will MVP avoid Android system contacts permission entirely?
- Are starter groups admin-managed from control plane or created by users?
- What exact in-client diagnostic scope is required in the first MVP?
- What minimum background behavior is acceptable for the first field trial?
- What app package identity and distribution channel will be used?
- What visible product name should be used for internal APK builds?
- What external contact visibility scopes are required in the first Android MVP?
- What exact UI warning is required for chats with external contacts?
- Should external contacts use the same APK and app mode as employees?

## 17. Product Review Refinements

These refinements are product requirements from [Product PRD Review Addendum](../PRODUCT_PRD_REVIEW_ADDENDUM.md).

### Control Plane Unavailable / Stale Mode UX

The Android client must expect Control Plane unavailability in whitelist or restricted-network mode.

Required UX states:

- last successful Control Plane sync;
- last successful directory sync;
- stale directory warning;
- stale policy warning;
- pending invite activation when Control Plane is unavailable;
- delayed diagnostic/report upload when Control Plane is unavailable;
- cached app release policy if already known.

The client may continue IMAP/SMTP messaging with known active contacts if transport works and local policy allows it. It must not imply that stale directory state is fresh.

### Email Verification Code Entry

The client must support a verification code/challenge entry flow for internal and external invites.

Rules:

- invite token entry does not equal membership;
- email verification does not equal membership by itself;
- IMAP/SMTP login success is transport readiness evidence, not the product ownership proof;
- activation requires Control Plane verification and policy acceptance.

Later, the app may read a challenge through IMAP only if a secure design is approved.

### Workspace Scope

The client must not assume a global single organization data model.

MVP may choose one active workspace in UI, but client state must be scoped by organization/workspace for membership, visible directory, external relationships, provider profile policy, diagnostics, invites, and cached release policy.

### Managed Group Stale Roster Behavior

Managed group sends must use the current active roster from directory/control-plane state.

If roster state is stale beyond policy threshold:

- the client should block or warn before a managed group send;
- historical local participants must not be treated as managed roster authority;
- historical chats that include revoked members should show a warning;
- new managed sends to revoked members must be blocked or require explicit non-managed/manual override according to policy.

### App Release Version Policy

The client must support release lifecycle warnings:

- `minSupportedVersion`;
- `forceUpgradeBelowVersion`;
- `deprecatedVersion`;
- `blockedVersion`;
- channel: `internal`, `beta`, `stable`;
- APK SHA-256 and signing note as metadata.

When Control Plane is available, the app should warn or block according to policy. When unavailable, it may use last cached policy.

APK-by-email is Android emergency fallback only. It is not primary distribution and does not imply trust or membership. iOS is out of current scope.

### Trust / Identity Status UI

UI must distinguish:

- app installed;
- invite present;
- email ownership verified;
- pending internal member;
- active internal member;
- suspended/revoked internal member;
- pending/active/revoked external contact;
- imported directory/local contact;
- cryptographically verified contact if SecureJoin or equivalent is later used.

Imported contact is not cryptographically verified by default. External active contact does not equal internal membership.

### External Reassignment UX

If an external contact is reassigned because the old manager leaves or loses access:

- old manager loses access after sync/enforcement;
- employee UI should show the reassigned contact according to visibility scope;
- external contact should see updated assigned person/team where policy allows;
- no internal HR reason should be shown to the external contact;
- exact old chat/history behavior is Blueprint scope.

## 18. MVP / Later / Non-goals Summary

MVP covers invite enrollment, external invite handling, provider setup, basic diagnostics, directory sync, internal/external directory separation, one-to-one chats, basic groups, attachments, and voice messages.

Later covers background reliability, advanced diagnostics, signed directory trust, external project rooms, richer policies, branding, transcription, and broader validation.

Non-goals exclude transport rewrite, custom shell over `chatmail/core` for MVP, video calls, real-time voice calls, silent contact import, internal directory exposure to external contacts, and production-grade background guarantees.
