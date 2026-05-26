# Delta Chat Android Fork Build Baseline Report

Date: 2026-05-13

Scope: build baseline for the local Delta Chat Android worktree prepared for the corporate IMAP/SMTP messenger MVP.

## Executive summary

The local Android worktree was created and a debug build baseline was attempted.

Direct Gradle execution from the workspace path failed on Windows because the path contains non-ASCII characters. Running the same build through a temporary ASCII drive mapping with `subst M:` succeeded.

No source changes were made to Delta Chat Android to make the build pass.

## Repository

- Worktree: `worktrees/deltachat-android-corporate/`
- Branch: `research/corporate-imap-messenger-baseline`
- Delta Chat Android HEAD: `be07043b474c8a73679a696f149658b5b904f217`
- Core submodule path: `jni/deltachat-core-rust`
- Core submodule HEAD: `dab7ca19fec91fe2462cb70eb52ec407343b4b2d`

## Environment

- OS shell: Windows PowerShell.
- JDK used: `C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot`
- Android SDK used: `C:\Android\android-sdk`
- Gradle wrapper: project wrapper, Gradle 8.13.
- Android Gradle Plugin observed in build: 8.11.1.

## Commands

Submodule initialization:

```powershell
git -C worktrees\deltachat-android-corporate submodule update --init --recursive
```

Direct build attempt:

```powershell
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
$env:ANDROID_HOME='C:\Android\android-sdk'
$env:ANDROID_SDK_ROOT='C:\Android\android-sdk'
.\gradlew.bat assembleDebug --no-daemon
```

Direct result: failed because the project path contains non-ASCII characters.

Successful build workaround:

```powershell
subst M: D:\Users\Roman\Desktop\Проекты\mesenger\worktrees\deltachat-android-corporate
M:
$env:JAVA_HOME='C:\Program Files\Microsoft\jdk-17.0.19.10-hotspot'
$env:ANDROID_HOME='C:\Android\android-sdk'
$env:ANDROID_SDK_ROOT='C:\Android\android-sdk'
.\gradlew.bat assembleDebug --no-daemon
subst M: /D
```

## Result

Build result: success through ASCII `subst` path.

Gradle result:

- `BUILD SUCCESSFUL`
- duration: about 8 minutes 20 seconds
- actionable tasks: 67 executed

Generated APKs:

- `worktrees/deltachat-android-corporate/build/outputs/apk/foss/debug/generated-M-c26c0c-foss-debug-2.49.0.apk`
- `worktrees/deltachat-android-corporate/build/outputs/apk/gplay/debug/generated-M-c26c0c-gplay-debug-2.49.0.apk`

Observed APK sizes:

- FOSS debug: 31,834,060 bytes
- Gplay debug: 32,336,738 bytes

## Warnings and blockers

Warnings observed:

- deprecated `android.defaults.buildfeatures.buildconfig=true`;
- debug build type has minify enabled, but optimizations/obfuscation are disabled for debuggable builds;
- Jetifier warnings for mixed AndroidX/support-library references in third-party libraries;
- unable to strip `libjingle_peerconnection_so.so`, packaged as-is;
- resource warnings for location-streaming strings without required default values;
- deprecated Gradle features incompatible with Gradle 9.0.

Blocker for normal Windows usage:

- direct build under `D:\Users\Roman\Desktop\Проекты\...` fails because Android Gradle Plugin rejects non-ASCII project paths on Windows.

Workaround:

- use an ASCII path, for example `subst M:` or a real ASCII workspace path.

## Impact on next Blueprint

The build baseline is good enough for the next architecture Blueprint. It proves the Delta Chat Android baseline can compile locally without source changes when path constraints are handled.

The next Blueprint should decide whether the product route is a thin fork, custom shell over core, or another architecture before starting UI or transport changes.
