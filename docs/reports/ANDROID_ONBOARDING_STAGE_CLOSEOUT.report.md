# Android Onboarding Stage Closeout Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Meta repo: https://github.com/Kwentin3/messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

Status: closeout completed, runtime smoke pending

## 1. Executive Summary

The current Android onboarding stage was closed out. The two safe app-layer onboarding slices were merged into Android `main`, a new internal smoke APK was built from the merged Android main code, and GitHub pre-release `android-internal-smoke-0.1.2` was published.

Merged Android scope:

- corporate `Join organization` welcome entry;
- corporate onboarding placeholder screen;
- local fallback invite-code input;
- local placeholder classification for empty/internal/external/invalid states;
- parser unit test;
- native build guard preserved.

No package ID, application ID, app name, launcher icon, signing config, notification channel identity, provider-db/native lookup, chatmail/core, JNI/FFI, sync, encryption, MIME, database migrations, backend, server, deployment, or Traefik changes were made.

## 2. Branch / PR Audit Result

Detailed branch audit:

- `docs/reports/ANDROID_ONBOARDING_STAGE_CLOSEOUT_BRANCH_AUDIT.report.md`

Summary:

- Android PR #4 was merged first.
- Android PR #5 was retargeted from stacked base to `main` and merged second.
- Android PR #1 was closed as superseded and left unmerged.
- Meta PR #16, #17, and #18 were merged because their reports/plans are required source material for this closeout.
- Meta PR #13 was closed as superseded by the 0.1.2 release documentation.

## 3. Android PR Merge Result

| PR | Result | Merge commit |
| --- | --- | --- |
| Android PR #4: `feature/corporate-onboarding-entry-wording` | Merged into `main`. | `fce21bda47568804b9f742498b31621e51a4bde5` |
| Android PR #5: `feature/corporate-onboarding-basic-entry-state` | Retargeted to `main`, then merged. | `97eb105d25fde492fea5749e2b31b8b509538b8e` |

Android PR #1 was closed as superseded because it was broader, conflicting, and replaced for this stage by the two safe onboarding slices.

## 4. Meta Docs / Report Merge Result

| PR | Result |
| --- | --- |
| Meta PR #16 | Merged. Added Android branding/onboarding refactor audit, refactor plan, runtime confirmation report, and `docs/out` copies. |
| Meta PR #17 | Merged. Added Android corporate onboarding entry wording slice report. |
| Meta PR #18 | Merged. Added Android corporate onboarding basic entry-state slice report. |

This closeout branch adds:

- `docs/reports/ANDROID_ONBOARDING_STAGE_CLOSEOUT_BRANCH_AUDIT.report.md`
- `docs/reports/ANDROID_ONBOARDING_STAGE_CLOSEOUT.report.md`
- updates to `README.md`
- updates to `docs/README.md`
- update to `docs/reports/ANDROID_INTERNAL_SMOKE_0_1_1_RUNTIME_CONFIRMATION.report.md`

## 5. Final Android Main Commit

Android code merge commit used to build APK `0.1.2`:

- `97eb105d25fde492fea5749e2b31b8b509538b8e`

Android `main` after README-only update:

- `98c68941441faea1f94b79ecad3ab53570316ba4`

The APK was built before the README-only commit. The README commit does not affect APK contents.

## 6. Build And Test Result

Environment:

```text
JAVA_HOME=C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot
```

Native guard:

```text
.\gradlew.bat verifyNativeCoreLibraries --stacktrace
```

Result:

- `BUILD SUCCESSFUL`

Parser unit test:

```text
.\gradlew.bat testFossDebugUnitTest --tests org.thoughtcrime.securesms.corporate.CorporateInviteCodeParserTest --stacktrace
```

Result:

- `BUILD SUCCESSFUL`

FOSS debug APK:

```text
.\gradlew.bat assembleFossDebug -PABI_FILTER=arm64-v8a --stacktrace
```

Result:

- `BUILD SUCCESSFUL`
- local APK produced under `build/outputs/apk/foss/debug/`
- APK was not committed to git

APK native library inspection:

```text
tar -tf build/outputs/apk/foss/debug/messenger-imap-android-foss-debug-2.50.0.apk | Select-String libnative-utils.so
```

Result:

```text
lib/arm64-v8a/libnative-utils.so
```

## 7. Release 0.1.2 URL

GitHub pre-release:

- https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.2

Direct APK download:

- https://github.com/Kwentin3/messenger-imap-android/releases/download/android-internal-smoke-0.1.2/messenger-imap-android-foss-debug-2.50.0.apk

Release type:

- pre-release;
- not production;
- runtime smoke pending.

## 8. APK Filename And SHA-256

APK:

- `messenger-imap-android-foss-debug-2.50.0.apk`

SHA-256:

- `9510CEDBC9FA30099339A6B03DAAA6DBBFF1F8446406193B1BA1799F24A599EF`

ABI:

- `arm64-v8a`

## 9. README Links Updated

Updated:

- Android repo `README.md` on `main`;
- meta repo `README.md`;
- meta repo `docs/README.md`.

The README links now point to `android-internal-smoke-0.1.2`, and the SHA-256 matches the uploaded release asset.

## 10. Runtime Smoke Checklist

Owner smoke checklist for `0.1.2`:

1. Uninstall previous APK if needed.
2. Install `0.1.2` FOSS debug APK.
3. Launch app.
4. Confirm no crash.
5. Confirm welcome screen opens.
6. Confirm `Join organization` is visible first.
7. Open `Join organization`.
8. Press check with empty code.
9. Enter `INT-TEST-001`; confirm internal placeholder.
10. Enter `EXT-TEST-001`; confirm external placeholder.
11. Enter `BADCODE`; confirm invalid placeholder.
12. Confirm raw code is cleared and not repeated in status.
13. Back navigation works.
14. `Create New Profile` opens existing setup.
15. `I Already Have a Profile` opens existing flow.
16. Manual IMAP/SMTP remains reachable.

## 11. Closed / Superseded PRs

Closed during closeout:

- Android PR #1: superseded by merged safe onboarding slices in Android `main`.
- Meta PR #13: superseded by `0.1.2` closeout release documentation.

Already closed before closeout:

- Android PR #2 and #3: older README APK-link PRs.
- Meta PR #14 and #15: older README navigation PRs.

Left open:

- Meta PR #6-#12 remain open because they are outside this onboarding closeout and need separate owner decision.

## 12. Remaining Blockers

- Runtime smoke for `0.1.2` is pending.
- Control Plane backend is not implemented.
- Invite resolution is local placeholder logic only.
- No real email verification.
- No real membership activation.
- No real external relationship activation.
- No real Directory API sync.
- App identity remains Delta Chat by design until a separate identity/release decision.

## 13. Next Recommended Slice

Recommended next Android slice:

`feature/corporate-onboarding-provider-setup-handoff`

Scope:

- add a clear handoff from corporate onboarding to existing provider/account setup;
- keep manual IMAP/SMTP reachable;
- show provider setup as separate from invite detection and membership activation;
- no provider-db rewrite;
- no backend/API calls;
- no Mail.ru-only flow;
- no package/app/signing identity changes.
