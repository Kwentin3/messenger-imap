# Android Onboarding Stage Closeout Branch Audit Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Meta repo: https://github.com/Kwentin3/messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

Status: closeout audit completed

## Executive Summary

The Android onboarding closeout started with two active Android onboarding PRs:

- Android PR #4: `feature/corporate-onboarding-entry-wording` -> `main`
- Android PR #5: `feature/corporate-onboarding-basic-entry-state` -> `feature/corporate-onboarding-entry-wording`

Both were mergeable. PR #4 was merged first. PR #5 was then retargeted to `main`, verified to contain only the basic entry-state diff, and merged.

The older Android PR #1, `feature/release-metadata-warning`, was broader than this closeout scope, conflicting, and superseded by the two safe onboarding slices. It was closed with an explanatory comment. Its branch was not deleted.

## Android Branches Reviewed

| Branch | Status | Closeout decision |
| --- | --- | --- |
| `main` | Active default integration branch. | Updated through PR #4 and PR #5. |
| `feature/corporate-onboarding-entry-wording` | Merged through PR #4. | Leave branch for history; no aggressive deletion. |
| `feature/corporate-onboarding-basic-entry-state` | Merged through PR #5. | Leave branch for history; no aggressive deletion. |
| `feature/release-metadata-warning` | Old broad placeholder branch; PR #1 was conflicting. | Closed as superseded; branch retained. |
| `feature/corporate-onboarding-foundation` | Historical slice branch. | Retained for history. |
| `feature/provider-transport-check` | Historical slice branch, not part of closeout. | Retained for future review. |
| `feature/directory-readonly-sync` | Historical slice branch, not part of closeout. | Retained for future review. |
| `feature/invite-deeplink-fallback` | Historical slice branch, not part of closeout. | Retained for future review. |
| `feature/external-contact-badges` | Historical slice branch, not part of closeout. | Retained for future review. |
| `docs/root-readme-apk-link`, `docs/root-readme-apk-link-fix` | Old README branches; PR #2 and #3 already closed. | No action. |

## Android PRs Reviewed

| PR | Branch | Base | Initial status | Closeout result |
| --- | --- | --- | --- | --- |
| #4 | `feature/corporate-onboarding-entry-wording` | `main` | Open, mergeable. | Merged. Merge commit `fce21bda47568804b9f742498b31621e51a4bde5`. |
| #5 | `feature/corporate-onboarding-basic-entry-state` | `feature/corporate-onboarding-entry-wording` | Open, mergeable, stacked. | Retargeted to `main`, verified, merged. Merge commit `97eb105d25fde492fea5749e2b31b8b509538b8e`. |
| #1 | `feature/release-metadata-warning` | `main` | Open draft, conflicting, broader than closeout. | Closed as superseded by PR #4 and PR #5. |
| #2 | `docs/root-readme-apk-link` | `main` | Already closed. | No action. |
| #3 | `docs/root-readme-apk-link-fix` | `main` | Already closed. | No action. |

## Meta PRs Reviewed

| PR | Branch | Status | Closeout decision |
| --- | --- | --- | --- |
| #16 | `docs/android-branding-onboarding-audit` | Open, mergeable. | Merged; required source audit and refactor plan. |
| #17 | `docs/android-corporate-onboarding-entry-report` | Open, mergeable. | Merged; required first slice report. |
| #18 | `docs/android-corporate-onboarding-basic-entry-state-report` | Open, mergeable. | Merged; required second slice report. |
| #13 | `docs/android-internal-smoke-apk-release` | Open against old `android/autonomous-execution` branch. | Closed as superseded by 0.1.2 closeout. |
| #14, #15 | README navigation PRs | Already closed. | No action. |
| #6-#12 | Older roadmap/blueprint/audit PRs | Still open. | Left open because they are outside this onboarding closeout scope and may need separate owner decision. |

## Merge Order

1. Merge Android PR #4 into `main`.
2. Retarget Android PR #5 to `main`.
3. Verify PR #5 diff only contains basic entry-state files.
4. Merge Android PR #5 into `main`.
5. Merge meta PR #16, #17, #18.
6. Close superseded Android PR #1 and meta PR #13.
7. Build and publish Android internal smoke APK `0.1.2`.
8. Update README links and closeout reports.

## Risks

- Runtime smoke for `0.1.2` remains pending on a physical device.
- Historical Android feature branches remain available and may confuse future branch selection if not explicitly ignored.
- Older meta roadmap/draft PRs remain open because they are outside this closeout scope.
- App identity remains Delta Chat by design; this closeout intentionally avoided package/app/icon/signing changes.

