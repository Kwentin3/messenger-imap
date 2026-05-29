# Android Internal Smoke 0.1.1 Runtime Confirmation Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

Release: https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.1

## Executive Summary

The owner reported that APK `0.1.1` installed and launched successfully on an Android/Huawei device.

This confirms that the startup crash seen in broken release `0.1.0` is not reproduced at basic launch level. Full runtime smoke remains pending unless separately confirmed.

## APK

- Tag: `android-internal-smoke-0.1.1`
- File: `messenger-imap-android-foss-debug-2.50.0.apk`
- SHA-256: `FB7FA4913A4E8161472B2C2A94D68F84927538D9A92782A336E2A5346F361110`
- ABI coverage: `arm64-v8a`

## Owner-Reported Device Result

- Device family: Android / Huawei
- Install result: success
- Launch result: success
- Crash on startup: no

## Limitations

- Confirmation covers install and launch only.
- Full smoke checklist is still pending unless separately confirmed:
  - standard Delta Chat setup path reachable;
  - corporate onboarding entry visible, where present in the tested branch;
  - fallback invite code entry opens;
  - dummy code does not expose raw token;
  - back navigation works.
- Release `0.1.1` includes `lib/arm64-v8a/libnative-utils.so` per `docs/reports/ANDROID_INTERNAL_SMOKE_APK_RELEASE_0_1_1.report.md`.

## Safety

- APK was published through GitHub Releases, not committed to git.
- No signing keys, `.env`, raw logs, or credentials are included in this report.
