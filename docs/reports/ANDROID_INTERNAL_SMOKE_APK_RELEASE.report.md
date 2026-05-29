# Android Internal Smoke APK Release Report

Date: 2026-05-29

Status: Published as GitHub pre-release for runtime smoke

Project: Corporate IMAP Messenger / messenger-imap

Meta repository: https://github.com/Kwentin3/messenger-imap

Android repository: https://github.com/Kwentin3/messenger-imap-android

## Executive Summary

An Android debug APK was built from the Android fork branch `feature/release-metadata-warning` and published as a GitHub pre-release asset for owner-side runtime smoke testing.

This is an internal debug build only. It is not a production release, not runtime verified, and must not be distributed externally.

## Release

- Release URL: https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0
- Tag: `android-internal-smoke-0.1.0`
- Release title: `Android Internal Smoke APK 0.1.0`
- Release type: pre-release
- Draft: no
- Runtime smoke status: pending

## Build Source

- Android branch: `feature/release-metadata-warning`
- Build commit: `8a51805d49ab5b36a551a7d80cf688b6e0cafb91`
- Android README documentation commit: `c8d51ef583f9ddd1571d4d0cdcf163c9f243f8f3`
- Build command: `.\gradlew.bat assembleDebug --stacktrace`
- Build result: successful

The release tag targets the build commit. The later README commit only documents the internal smoke release.

## Uploaded Assets

Recommended smoke APK:

- `messenger-imap-android-foss-debug-2.50.0.apk`
- SHA-256: `E50768D6DB2D1B26A76FB53A37D16ADD374E76DA544B9D2C8408B500AB320410`
- Uploaded: yes

Additional optional APK:

- `messenger-imap-android-gplay-debug-2.50.0.apk`
- SHA-256: `DEFD61F0019040AB687583650C0F8490C159B58223C0C680B003631813D959FF`
- Uploaded: yes

## Safety Checks

- APK committed to git: no
- AAB committed to git: no
- Keystore/signing key committed to git: no
- `.env` committed to git: no
- Production release created: no
- Package ID changed: no
- Signing config changed: no
- Runtime verified claim made: no

## Known Limitations

- Control Plane backend is not implemented.
- Directory/API integration remains placeholder/fixture-level.
- Invite activation remains placeholder-level.
- Runtime device/emulator smoke is still required.
- This debug build is for internal sideload smoke only.

## Runtime Smoke Checklist

After downloading the recommended FOSS debug APK, the owner should run:

1. Install APK on an Android device.
2. Launch the app.
3. Confirm there is no crash on startup.
4. Confirm the regular Delta Chat welcome/account setup path is still reachable.
5. Confirm the corporate onboarding entry is visible.
6. Open the corporate onboarding placeholder.
7. Open the fallback invite code entry.
8. Enter a dummy code and confirm the raw token is not displayed or logged.
9. Confirm back navigation works.
10. Collect screenshots and notes if any failure occurs.

## Next Action

Install `messenger-imap-android-foss-debug-2.50.0.apk` from the GitHub pre-release on a real Android device and run the runtime smoke checklist before accepting the Android roadmap execution as runtime-smoked.
