# Android Fork Intake Blueprint

Date: 2026-05-29

Status: Draft

Project: Corporate IMAP Messenger / `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Scope: build-only intake plan for the future thin fork of Delta Chat Android.

Target Android repo: `https://github.com/Kwentin3/messenger-imap-android`

Upstream Android repo: `https://github.com/deltachat/deltachat-android.git`

## 1. Executive Summary

This Blueprint defines the first safe intake of the Android fork. The purpose is to create or verify the Android fork repository, preserve the upstream relationship, record the upstream commit, and prove a clean debug build before any product customization.

The first intake is build-only. It must not rename the package, rename the app, rebrand assets, add Control Plane integration, add corporate onboarding, change `chatmail/core`, or distribute release APKs.

## 2. Source Documents

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`
- `docs/roadmap/PROJECT_ROADMAP.md`
- `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md` from PR #9 branch context
- `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md`
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`
- `docs/research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md`
- `docs/hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md`
- `docs/upstream/UPSTREAM_PROJECTS.md`
- `docs/upstream/LICENSE_NOTES.md`
- Upstream Delta Chat Android `BUILDING.md` observed from `deltachat/deltachat-android` on 2026-05-29
- Upstream Delta Chat Android `README.md`, `.gitmodules`, and `build.gradle` observed on 2026-05-29

## 3. Inherited Decisions

- MVP Android path is thin fork Delta Chat Android.
- Custom shell over `chatmail/core` is rejected for MVP.
- Android-from-scratch is rejected for MVP.
- `chatmail/core` changes are not allowed without separate Blueprint.
- `messenger-imap` must not vendor-copy Delta Chat Android.
- Control Plane remains separate and is not the message server.
- IMAP/SMTP remains message transport.
- APK download does not equal membership.
- GPL/source distribution path must be planned before modified APK distribution.

## 4. Repository Strategy

Target repository:

```text
Kwentin3/messenger-imap-android
```

Upstream repository:

```text
deltachat/deltachat-android
https://github.com/deltachat/deltachat-android.git
```

Remote strategy in the Android working clone:

```text
origin   = https://github.com/Kwentin3/messenger-imap-android.git
upstream = https://github.com/deltachat/deltachat-android.git
```

Required branch:

```text
intake/upstream-build-baseline
```

Rules:

- use a GitHub fork where possible;
- if a normal GitHub fork cannot use the requested repo name, document exact fallback and do not vendor-copy into `messenger-imap`;
- record `git remote -v`;
- record `git rev-parse HEAD`;
- record `git submodule status --recursive`;
- preserve upstream license notices;
- keep product docs and reports in `messenger-imap`;
- keep Android source and Android-specific changes in `messenger-imap-android`.

## 5. Upstream Baseline Snapshot

Observed on 2026-05-29:

- upstream default branch: `main`;
- upstream `HEAD`: `a3a8b3581f82456bb7fe3342485cef4593c31315`;
- upstream license via GitHub metadata: GPL-3.0;
- upstream `.gitmodules` includes `jni/deltachat-core-rust` from `https://github.com/chatmail/core`;
- upstream `build.gradle` uses Android Gradle Plugin `8.11.1`;
- upstream `build.gradle` currently shows `compileSdk 36`, `minSdkVersion 21`, `targetSdkVersion 36`;
- upstream default `applicationId` is `com.b44t.messenger`;
- upstream `gplay` flavor uses `chat.delta`;
- upstream debug build has `applicationIdSuffix ".beta"`;
- upstream build docs say the project requires API 25 for Android Studio setup;
- upstream build docs build native libraries first, then run Gradle.

This is a point-in-time intake snapshot. Phase 3/4 must re-record exact values from the clone actually used.

## 6. Build Prerequisites

The intake must not assume the local machine already has the Android toolchain. Phase 4 must verify and report:

- OS;
- Git;
- JDK/Java version;
- Android SDK;
- Android SDK platform/build-tools;
- Android NDK;
- `ANDROID_NDK_ROOT`;
- Rust and rustup if using native build path;
- Gradle wrapper version from upstream;
- Docker or Podman availability if using container build path;
- submodules initialized recursively.

Upstream build docs describe these build paths:

- Nix environment;
- Docker/Podman environment;
- manual Android Studio / Android SDK / NDK / Rust environment.

Phase 4 should prefer the least invasive reproducible path available on the machine. On Windows, if Docker/Podman or local Android SDK/NDK is unavailable, the phase must document the blocker rather than installing broad system dependencies silently.

## 7. Clean Build Plan

Build-only validation sequence:

1. Clone or verify `Kwentin3/messenger-imap-android`.
2. Add/fetch `upstream`.
3. Checkout `intake/upstream-build-baseline`.
4. Initialize submodules:

```text
git submodule update --init --recursive
```

5. Build native library according to upstream docs, usually:

```text
scripts/ndk-make.sh
```

6. Build debug APK according to upstream docs, usually:

```text
./gradlew assembleDebug
```

7. Record generated APK path, expected upstream examples:

```text
build/outputs/apk/gplay/debug/
build/outputs/apk/fat/debug/
```

8. Confirm APK/build outputs are not committed.

## 8. Forbidden In Intake

The intake branch must not:

- rename package ID;
- rename app;
- rebrand icons, colors, strings, or notification assets;
- change signing config;
- add release signing keys;
- distribute release APK;
- publish APK to GitHub Releases;
- add Control Plane integration;
- add invite/deep-link behavior beyond what upstream already has;
- add provider profile policy;
- add corporate directory sync;
- add external contact badges;
- change `chatmail/core`;
- change JNI/FFI boundaries;
- change sync, encryption, MIME, or database migrations;
- add server/deployment/Traefik changes;
- commit APK/AAB/build artifacts;
- commit `.env`, provider credentials, app passwords, raw AUTH, raw logs, keystores, or signing keys.

## 9. Reports Required

Phase 3 must produce:

- `docs/reports/ANDROID_FORK_REPOSITORY_SETUP.report.md`

Phase 4 must produce:

- `docs/reports/ANDROID_FORK_BUILD_BASELINE.report.md`

If the Android repo cannot be created/accessed, create:

- `docs/reports/ANDROID_EXECUTION_BLOCKER_PHASE_3.report.md`

If clean build fails and cannot be resolved safely, create:

- `docs/reports/ANDROID_EXECUTION_BLOCKER_PHASE_4.report.md`

## 10. Acceptance Criteria

This Blueprint is acceptable if:

- fork intake is build-only;
- target Android repo and upstream repo are named;
- remote strategy is explicit;
- branch strategy is explicit;
- upstream commit recording is required;
- build prerequisites are listed;
- clean build validation commands are listed;
- forbidden changes are explicit;
- GPL/source distribution notes are explicit;
- no code, upstream source, APK, build artifact, deployment file, or secret is added to `messenger-imap`.

## 11. Gate To Phase 3

Proceed to Phase 3 if:

- this Blueprint exists;
- this Blueprint report exists;
- no blocker prevents repo creation or verification;
- `messenger-imap` remains docs/meta only and has not received vendor-copied Android source.

## 12. Open Questions

- Does `Kwentin3/messenger-imap-android` already exist?
- If it exists, is it a fork of `deltachat/deltachat-android`?
- If it does not exist, does the GitHub account have permission to create it as a fork?
- If GitHub fork naming prevents exact repo name, should the owner accept a non-fork mirror or manually create the named fork repo?
- Should the first build path use Docker/Podman, Nix, Android Studio, or local command-line SDK?
- Which upstream commit should be pinned if upstream moves before Phase 3 starts?
