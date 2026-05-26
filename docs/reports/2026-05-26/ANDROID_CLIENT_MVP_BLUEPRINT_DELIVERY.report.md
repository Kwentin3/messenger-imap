# Android Client MVP Blueprint Delivery Report

Date: 2026-05-26

Status: Draft delivery report

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Branch: `blueprint/android-client-mvp`

Android Blueprint content commit: `8361e9a0f13bc1fcf9a9e2edfa8f20efc1a343ec`

Android Blueprint PR: `https://github.com/Kwentin3/messenger-imap/pull/6`

## 1. Executive Summary

PR #5, Invite Onboarding & Distribution MVP Blueprint, was merged into `main` before Android Blueprint work started.

The Android Client MVP Blueprint was then drafted from updated `main` on `blueprint/android-client-mvp`. The Blueprint converts current product and architecture documentation into a client-side MVP architecture frame without writing Android code.

PR #6 was opened from `blueprint/android-client-mvp` to `main`.

## 2. Upstream PR Status

| Item | Result |
| --- | --- |
| PR #3 | Merged |
| PR #4 | Content merged into `main`; stacked PR closed after no-diff retarget |
| PR #5 | Merged |
| PR #5 URL | `https://github.com/Kwentin3/messenger-imap/pull/5` |
| `main` before Android branch | `84ce167f249edb9c027e76039ee3ff0896627fb3` |
| Android Blueprint branch | `blueprint/android-client-mvp` |
| Android Blueprint content commit | `8361e9a0f13bc1fcf9a9e2edfa8f20efc1a343ec` |
| Android Blueprint PR | `https://github.com/Kwentin3/messenger-imap/pull/6` |

## 3. What Was Created

Created:

- `docs/blueprints/ANDROID_CLIENT_MVP_BLUEPRINT.md`
- `docs/blueprints/ANDROID_CLIENT_MVP_BLUEPRINT_REPORT.md`

The Android Blueprint covers:

- Android client source documents and inherited decisions;
- client goals and non-goals;
- system context;
- client domain ownership;
- fork-vs-shell implementation decision criteria;
- local client state model;
- onboarding flows for internal employees and external contacts;
- Control Plane unavailable behavior;
- directory sync and hash verification;
- one-to-one chat and managed group behavior;
- diagnostics/support export;
- release policy UX;
- user modes;
- provider setup contract;
- stale/offline behavior;
- security/privacy requirements;
- local storage boundaries;
- MVP implementation slices;
- validation plan;
- open questions and acceptance criteria.

## 4. Files Updated

Updated:

- `docs/README.md`
- `docs/roadmap/PROJECT_ROADMAP.md`
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`

The Roadmap now marks Invite Onboarding & Distribution as merged baseline and Android Client MVP Blueprint as drafted.

## 5. Source Documents Used

The Blueprint uses:

- `docs/roadmap/PROJECT_ROADMAP.md`
- `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md`
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`
- `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md`
- `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md`
- `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md`
- `docs/product/domains/PRD_CORPORATE_DIRECTORY.md`
- `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md`
- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`
- `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md`
- `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md`
- `docs/product/decisions/PRODUCT_DECISIONS_LOG.md`
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`
- `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md`
- `docs/infrastructure/SERVER_AUDIT_REPORT.md`
- `docs/research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md`
- `docs/hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md`

## 6. Decisions Preserved

- APK download does not equal membership.
- Invite token does not equal membership.
- Email verification code is required.
- Internal invite creates membership.
- External invite creates external relationship.
- Control Plane may be unavailable in whitelist mode.
- Android must support stale/cached state.
- External contacts do not receive internal directory.
- Provider diagnostics do not replace email verification.
- Background reliability is deferred.
- No silent Android address book import.
- No Mail.ru-only architecture.
- iOS remains out of scope.

## 7. Safety Result

No code changes were made.

No Android project files, Gradle files, APK binaries, build artifacts, raw logs, `.env` files, SSH keys, private keys, keystores, credentials, server files, Traefik config, Delta Chat upstream files, or chatmail/core files were changed.

## 8. Remaining Actions

Recommended next steps:

1. Review and accept Android Client MVP Blueprint.
2. Run fork-vs-shell implementation decision spike.
3. Prepare Provider / Diagnostics MVP Blueprint.
4. Prepare implementation plan only after the relevant Blueprints are accepted.
