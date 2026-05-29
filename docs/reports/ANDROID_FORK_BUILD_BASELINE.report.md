# Android Fork Build Baseline Report

Date: 2026-05-29

Phase: 4 - Clean upstream/fork build baseline

Branch/repo: `android/autonomous-execution` in `Kwentin3/messenger-imap`

Android repo: `https://github.com/Kwentin3/messenger-imap-android`

Android branch: `intake/upstream-build-baseline`

Android build checkout used: `C:\work\messenger-imap-android`

## Source Docs Used

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`
- `docs/blueprints/ANDROID_FORK_INTAKE_BLUEPRINT.md`
- Upstream Delta Chat Android `BUILDING.md`
- Upstream Delta Chat Android `build.gradle`
- Upstream Delta Chat Android `.gitmodules`

## What Was Done

- Verified local build tools.
- Initialized Android fork submodule recursively.
- Found that the original checkout path under `d:\Users\Roman\Desktop\Проекты\...` cannot run Android Gradle Plugin on Windows because the path contains non-ASCII characters.
- Created a separate ASCII build checkout at `C:\work\messenger-imap-android`.
- Added upstream remote in the ASCII checkout.
- Initialized submodules recursively in the ASCII checkout.
- Attempted native library rebuild using upstream `scripts/ndk-make.sh`.
- Ran Gradle clean debug build command from the ASCII checkout.
- Produced debug APK artifacts locally.
- Confirmed build artifacts are uncommitted.

## What Was Not Done

- No Android product code was changed.
- No package ID, app name, branding, icon, signing, or release identity was changed.
- No `chatmail/core`, JNI, sync, encryption, MIME, or database migration change was made.
- No release APK was published.
- No APK/AAB/build artifact was committed.
- No server/deployment/Traefik action was performed.

## Environment

| Item | Observed value |
| --- | --- |
| OS | Windows Server 2019 10.0 amd64 |
| Git | `2.53.0.windows.3` |
| Java for Gradle | Microsoft JDK `17.0.19` |
| Gradle wrapper | Gradle `8.13` |
| Android SDK | `C:\Android\android-sdk` |
| Android platform | `platforms;android-36` installed |
| Android build-tools | `35.0.0` and `36.0.0` observed |
| Android NDK | Not installed under `C:\Android\android-sdk\ndk` |
| Rust/rustup/cargo | Not installed in PATH |
| Docker | Docker `19.03.5`, Windows container mode |
| Podman | Not installed |
| Git shell | `C:\Program Files\Git\usr\bin\sh.exe` available |

## Build Commands And Results

Original non-ASCII checkout:

```text
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
.\gradlew.bat assembleDebug --stacktrace
```

Result: failed before build because Android Gradle Plugin rejects non-ASCII Windows project paths.

Native rebuild attempt in ASCII checkout:

```text
$env:PATH='C:\Program Files\Git\usr\bin;'+$env:PATH
sh scripts/ndk-make.sh arm64-v8a
```

Result: failed with:

```text
ANDROID_NDK_ROOT is not set
```

This is expected because Android NDK and Rust toolchains are not installed locally. No broad system dependency installation was performed silently.

Gradle debug build in ASCII checkout:

```text
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
.\gradlew.bat assembleDebug --stacktrace
```

Result:

```text
BUILD SUCCESSFUL in 2m 31s
67 actionable tasks: 67 executed
```

Warnings:

- deprecated Gradle feature warnings;
- debug build minification warnings;
- native debug symbol stripping warning for `libjingle_peerconnection_so.so`.

## APK Artifacts Produced Locally

Artifacts were produced locally and were not committed:

```text
C:\work\messenger-imap-android\build\outputs\apk\foss\debug\messenger-imap-android-foss-debug-2.50.0.apk
C:\work\messenger-imap-android\build\outputs\apk\gplay\debug\messenger-imap-android-gplay-debug-2.50.0.apk
```

Artifact committed: no.

## Upstream Commit

| Ref | Commit |
| --- | --- |
| `HEAD` | `a3a8b3581f82456bb7fe3342485cef4593c31315` |
| submodule `jni/deltachat-core-rust` | `784a6abb3bae6d027062cb9dbc1bf9829905b013` |

## Files Changed

In `messenger-imap`:

- `docs/reports/ANDROID_FORK_BUILD_BASELINE.report.md`

In `messenger-imap-android`:

- no tracked files changed;
- local build output was generated under `build/` and remains uncommitted.

## Commands/Checks Run

- `java -version`
- `git --version`
- `rustc --version`
- `cargo --version`
- `rustup --version`
- `docker --version`
- `podman --version`
- `docker info`
- `sdkmanager --list`
- `git submodule update --init --recursive`
- `git submodule status --recursive`
- `.\gradlew.bat --version`
- `.\gradlew.bat assembleDebug --stacktrace`
- `sh scripts/ndk-make.sh arm64-v8a`
- `git status -sb`
- local APK artifact listing

## Tests Run

- Gradle `assembleDebug` for `fossDebug` and `gplayDebug` variants.

No device/emulator runtime test was run in this phase.

## Acceptance Criteria Result

Pass with documented environment caveat:

- debug APK build passed from ASCII checkout;
- build command is reproducible;
- no product modifications were made;
- APK artifacts were not committed;
- report was created.

Caveat:

- native library rebuild through `scripts/ndk-make.sh` was not validated because Android NDK and Rust are missing from the local Windows environment.
- This does not block app-layer architecture audit or app-layer implementation slices, but it remains a blocker for any future work that requires native/core rebuilds.

## Gate Result

Gate to Phase 5: passed.

The fork can produce clean debug APKs from the upstream/fork branch without product changes. Phase 5 may proceed.

## Blockers

No Phase 4 stop condition was triggered.

Tracked build caveats:

- Android build must use ASCII checkout path on Windows.
- Native rebuild requires explicit NDK/Rust toolchain installation or a Linux/Nix/container build path.

## Next Phase Decision

Proceed to Phase 5: Delta Chat Android architecture audit.
