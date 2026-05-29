# README Navigation Simplification Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

## 1. Summary

README navigation was simplified so `main` is the intended primary branch and the root README is the project entry point. The README now shows the Android internal smoke APK link near the top.

Status update: this report is historical for the broken `android-internal-smoke-0.1.0` release. The root README and `docs/README.md` now point to replacement pre-release `android-internal-smoke-0.1.1`. Do not use the `0.1.0` APK links below for runtime smoke.

## 2. Default Branch

- Before: `bootstrap/project-import`
- After: `main`

The default branch was changed through GitHub after the `main` README update was pushed.

## 3. Updated In Main Project Repo

Updated in `Kwentin3/messenger-imap`:

- root `README.md`;
- `docs/README.md`;
- this report: `docs/reports/README_NAVIGATION_SIMPLIFICATION.report.md`.

Root README now includes:

- [Android internal smoke release 0.1.0](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0);
- [direct FOSS debug APK download](https://github.com/Kwentin3/messenger-imap-android/releases/download/android-internal-smoke-0.1.0/messenger-imap-android-foss-debug-2.50.0.apk);
- [Android fork repo](https://github.com/Kwentin3/messenger-imap-android);
- [Main project repo](https://github.com/Kwentin3/messenger-imap);
- recommended APK name;
- SHA-256;
- runtime smoke pending status;
- updated runtime smoke status after owner test: both FOSS and GPlay assets installed on Huawei and crashed on launch;
- warning that the APK is not production;
- current status and repository roles;
- safety note that APKs are published through GitHub Releases, not committed to git.

## 4. Updated In Android Repo

Updated in `Kwentin3/messenger-imap-android`:

- root `README.md`.

The Android README now starts with the Corporate IMAP Messenger Android Fork block and links the internal smoke APK release. The original upstream Delta Chat README remains below the project-specific block.

## 5. Obsolete README PRs / Branches

Closed as obsolete after direct `main`/default-branch updates:

- `Kwentin3/messenger-imap` PR #14: `docs/root-readme-navigation`;
- `Kwentin3/messenger-imap` PR #15: `docs/root-readme-navigation-fix`;
- `Kwentin3/messenger-imap-android` PR #2: `docs/root-readme-apk-link`;
- `Kwentin3/messenger-imap-android` PR #3: `docs/root-readme-apk-link-fix`.

Branches were not deleted.

## 6. Links Verified

Verified:

- root README link to Android internal smoke release;
- root README direct FOSS debug APK download link;
- root README link to Android fork repo;
- root README link to main project repo;
- relative documentation links in root `README.md`;
- relative documentation links in `docs/README.md`;
- Android root README links.

Direct APK download verification:

- HTTP redirect chain reaches `200 OK`;
- downloaded size: `35771911` bytes;
- downloaded SHA-256: `E50768D6DB2D1B26A76FB53A37D16ADD374E76DA544B9D2C8408B500AB320410`.

Runtime smoke update:

- Owner installed both release assets on a Huawei phone.
- Both APKs installed successfully.
- Both APKs crashed on launch.
- Current APK release is therefore marked broken and must not be treated as a successful smoke build.

## 7. Safety Confirmation

- APK committed to git: no.
- AAB committed to git: no.
- Build outputs committed to git: no.
- `.env` committed to git: no.
- Secrets committed to git: no.
- Signing keys committed to git: no.
- Server / Traefik changes: no.
- Android source/build/package/signing changes: no.
