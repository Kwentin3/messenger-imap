# Android build feasibility

Дата: 2026-05-13

## Executive summary

`deltachat-android` является релевантной Android-базой для мобильного MVP, но debug APK на текущей машине не собрался из-за отсутствующего Android/Rust/JDK окружения. Это не архитектурный блокер, а environment blocker.

Репозиторий проверен локально: `imap-messenger-research/upstream/deltachat-android`. Submodule `jni/deltachat-core-rust` инициализирован и указывает на `chatmail/core`.

## Проверенная рабочая зона

- Android repo: `imap-messenger-research/upstream/deltachat-android`
- Remote: `https://github.com/deltachat/deltachat-android.git`
- Core submodule: `jni/deltachat-core-rust`
- Core submodule URL: `https://github.com/chatmail/core`
- Checked-out core revision: `dab7ca19fec91fe2462cb70eb52ec407343b4b2d`
- Android versionName из `build.gradle`: `2.49.0`
- Android Gradle Plugin: `8.11.1`
- compileSdk/targetSdk: `36`
- minSdk: `21`
- NDK version: `27.0.12077973`
- Flavors: `foss`, `gplay`

## Проверка окружения

Команды проверки:

```powershell
git --version
java -version
javac -version
gradle -v
rustc --version
cargo --version
adb version
$env:ANDROID_HOME
$env:ANDROID_SDK_ROOT
$env:ANDROID_NDK_ROOT
```

Результат:

- `git` установлен.
- `java` / `javac` не найдены.
- `gradle` не найден, но в проекте есть Gradle wrapper.
- `rustc` / `cargo` не найдены.
- `adb` не найден.
- `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `ANDROID_NDK_ROOT` не заданы.

## Подготовка submodules

Выполнено:

```powershell
git -C imap-messenger-research/upstream/deltachat-android submodule update --init --recursive
```

Результат: submodule `jni/deltachat-core-rust` успешно инициализирован.

## Попытка native build

Команда:

```powershell
cd imap-messenger-research/upstream/deltachat-android
bash scripts/ndk-make.sh arm64-v8a
```

Результат: команда не дошла до сборки core. Windows `bash` в этой среде не предоставил рабочий Unix shell. Независимые блокеры: нет Rust toolchain и не задан `ANDROID_NDK_ROOT`.

Вероятный фикс:

- установить Git Bash/MSYS2 или WSL с дистрибутивом Linux;
- установить Rust через `rustup`;
- установить Android NDK `27.0.12077973`;
- задать `ANDROID_NDK_ROOT`;
- выполнить `scripts/install-toolchains.sh`, затем `scripts/ndk-make.sh`.

## Попытка Gradle build

Команда:

```powershell
cd imap-messenger-research/upstream/deltachat-android
.\gradlew.bat assembleFossDebug
```

Результат:

```text
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
```

APK не собран.

## Минимальная инструкция для повторения

На Windows практичный путь:

1. Установить Android Studio.
2. Через SDK Manager установить:
   - Android SDK Platform 36;
   - Platform Tools;
   - Android SDK Build Tools;
   - NDK `27.0.12077973`;
   - CMake, если Android Studio попросит.
3. Установить JDK 17+ или использовать JBR из Android Studio, задать `JAVA_HOME`.
4. Установить Rust через `rustup`.
5. Установить Git Bash/MSYS2 или WSL.
6. В Git Bash/WSL:

```bash
cd imap-messenger-research/upstream/deltachat-android
git submodule update --init --recursive
scripts/install-toolchains.sh
export ANDROID_NDK_ROOT=/path/to/Android/Sdk/ndk/27.0.12077973
scripts/ndk-make.sh arm64-v8a
```

7. В PowerShell или Git Bash:

```powershell
.\gradlew.bat assembleFossDebug
```

Ожидаемый APK: `build/outputs/apk/foss/debug/`, имя генерируется Gradle с суффиксом версии.

## Рекомендация

`deltachat-android` подходит как база для Android MVP-0, но сначала нужно поднять воспроизводимое Android build окружение. Для самого первого полевого transport MVP быстрее и безопаснее сделать отдельный минимальный Android diagnostics app или debug screen, чем сразу форкать весь UX.
