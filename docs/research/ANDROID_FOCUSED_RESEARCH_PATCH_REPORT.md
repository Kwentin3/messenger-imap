# Android-focused research patch report

Дата: 2026-05-13

## 1. Executive summary

После уточнения Android-first приоритет меняется: Desktop/RPC остается лабораторным инструментом, но продуктовый MVP должен проверяться на Android device with SIM. `deltachat-android` является правильной основной базой для будущего мобильного fork/wrapper, потому что он уже встраивает core и использует готовые IMAP/SMTP/TLS/MIME/storage/chat/encryption возможности.

Debug APK на текущей машине не собран из-за отсутствия JDK, Android SDK/NDK, Rust и рабочего Unix shell для native build. Это environment blocker, не отрицательный вывод по проекту.

Рекомендация: сначала поднять Android build environment и параллельно сделать minimal Android diagnostics APK для field test транспорта. Полноценный corporate UX fork делать только после подтверждения IMAP/SMTP на реальных SIM и операторах.

## 2. Что изменилось после Android-first

Старый путь `Desktop/RPC MVP -> потом Android` теперь только вспомогательный.

Новый путь:

1. Android discovery.
2. Debug APK / install on device.
3. Android-native transport diagnostics.
4. Field test на SIM.
5. Corporate UX fork.

## 3. Что из предыдущего discovery остается верным

- Core закрывает IMAP, SMTP, TLS, MIME, SQLite cache, queues, chats, groups, encryption.
- Не нужно переписывать sync.
- Не нужно менять encryption/Autocrypt/SecureJoin.
- Provider presets для Mail.ru/VK Mail/Yandex лучше держать совместимыми с core/provider-db.
- Desktop/RPC полезен как лабораторный инструмент.

## 4. Что было неполным

Предыдущий discovery недооценивал Android-specific risks:

- Doze/battery optimization;
- foreground service requirements;
- WorkManager latency;
- FCM/chatmail push vs обычный IMAP provider;
- locked-screen delivery;
- mobile network transitions;
- permissions and Android log leakage.

## 5. Android build status

Сделано:

- найден `deltachat-android`;
- проверен upstream remote;
- инициализирован submodule `jni/deltachat-core-rust`;
- изучены build docs;
- выполнена попытка native build;
- выполнена попытка Gradle build.

Не собрано:

- `bash scripts/ndk-make.sh arm64-v8a` не дошел до сборки из-за нерабочего shell/отсутствия Rust/NDK;
- `.\gradlew.bat assembleFossDebug` остановился на `JAVA_HOME is not set`.

Подробно: `docs/research/ANDROID_BUILD_FEASIBILITY.md`.

## 6. Android architecture map summary

Android app создает `DcAccounts`, открывает accounts из private storage, поднимает JSON-RPC поверх FFI transport и вызывает `dcAccounts.startIo()`. Sync и transport идут через core.

Главные Android точки:

- `ApplicationContext` - init, accounts, IO, WorkManager, network callbacks;
- `EditRelayActivity` - classic email relay setup;
- `DcContext`/`DcAccounts` - Java wrappers;
- `dc_wrapper.c` - JNI bridge;
- `FetchWorker`, `FetchForegroundService`, `KeepAliveService` - background delivery;
- `DatabaseSecretProvider`, `KeyStoreHelper` - local DB secret handling.

Подробно: `docs/research/ANDROID_ARCHITECTURE_MAP.md`.

## 7. Provider onboarding summary

Android уже использует core provider lookup через `getProviderFromEmailWithDns()`. `EditRelayActivity` показывает provider hints and overview link. Для corporate mode можно добавить явный selector Mail.ru/VK Mail/Yandex и domain allowlist до вызова `rpc.addOrUpdateTransport`.

Подробно: `docs/research/ANDROID_PROVIDER_ONBOARDING_NOTES.md`.

## 8. Background sync summary

Foreground path выглядит зрелым. Background path зависит от режима:

- app alive/open: core IO;
- no push/FOSS: WorkManager periodic fetch, latency around Android minimum interval;
- gplay/chatmail: FCM-triggered foreground/background fetch;
- reliable mode: persistent foreground service.

Для обычных Mail.ru/Yandex/VK IMAP аккаунтов push нельзя считать гарантированным. IDLE/background delivery надо тестировать отдельно.

Подробно: `docs/research/ANDROID_BACKGROUND_SYNC_NOTES.md`.

## 9. Credential/security summary

Database secret на Android M+ sealed через Android Keystore AES/GCM. Credentials передаются в core account configuration. Существующий LogView собирает raw logcat и сам предупреждает, что лог может содержать sensitive information.

Для diagnostics нужен отдельный sanitized JSON exporter.

Подробно: `docs/research/ANDROID_CREDENTIAL_SECURITY_NOTES.md`.

## 10. Android diagnostics design summary

Diagnostics должны проверять DNS, TCP, TLS, IMAP login/select/idle, SMTP login/send, receive correlation, spam/junk placement и latency именно на Android device network.

Лучший MVP-0 путь: standalone diagnostics APK, потом integration into Android fork.

Подробно: `docs/research/ANDROID_DIAGNOSTICS_DESIGN.md`.

## 11. Android MVP-0 recommendation

MVP-0 должен быть transport-first:

- minimal Android APK;
- provider selector;
- email/app password input;
- no secret persistence;
- send/receive diagnostics;
- field-test JSON;
- real SIM testing.

Подробно: `docs/research/ANDROID_MVP0_PLAN.md`.

## 12. Remaining risks

- IMAP/SMTP ports могут быть вне whitelist.
- Webmail availability ничего не доказывает.
- Android Doze может ломать IDLE/background.
- Provider app passwords могут требовать ручной настройки.
- SMTP может быть ограничен provider anti-spam policy.
- FCM path не заменяет IMAP IDLE для обычных провайдеров.
- Windows build setup требует времени.

## 13. Next step

1. Поднять Android build environment.
2. Собрать `assembleFossDebug`.
3. Установить upstream APK на Android phone with SIM.
4. Сделать standalone diagnostics APK.
5. Провести field tests по `docs/research/WHITE_LIST_FIELD_TEST_PROTOCOL.md`.
6. По результатам выбрать: Android fork, wrapper over core, или separate corporate shell.

## 14. Honest verdict

`deltachat-android` подходит как база для будущего Android corporate messenger, но не стоит начинать с глубокого fork/rebrand.

Лучший ближайший путь:

- transport MVP: separate Android diagnostics app;
- product MVP: thin Android fork/custom onboarding over existing core;
- core: не менять до появления точного missing API;
- desktop/RPC: оставить лабораторным инструментом.

Главный блокер сейчас: не кодовая архитектура Delta Chat, а отсутствие Android build окружения и неподтвержденная доступность IMAP/SMTP в mobile whitelist на реальных операторах.
