# Android architecture map

Дата: 2026-05-13

## Главный вывод

`deltachat-android` не реализует IMAP/SMTP самостоятельно. Android app встраивает core как native library через JNI и использует Java wrappers плюс сгенерированный JSON-RPC слой. Поэтому для корпоративного MVP опасно менять sync/core, но можно достаточно безопасно менять Android onboarding, provider selection, diagnostics screen и managed-mode настройки.

## Ключевые файлы

- `src/main/java/org/thoughtcrime/securesms/ApplicationContext.java` - инициализация приложения, `DcAccounts`, event loop, запуск IO, WorkManager, network callbacks.
- `src/main/java/com/b44t/messenger/DcAccounts.java` - Java wrapper над multi-account core API.
- `src/main/java/com/b44t/messenger/DcContext.java` - Java wrapper над account context, chats, contacts, config, provider lookup, send APIs.
- `src/main/java/com/b44t/messenger/DcJsonrpcInstance.java` - native JSON-RPC instance.
- `jni/dc_wrapper.c` - JNI bridge к `dc_accounts_*`, `dc_context_*`, `dc_jsonrpc_*`.
- `src/main/java/chat/delta/rpc/Rpc.java` - generated JSON-RPC Java client.
- `src/main/java/org/thoughtcrime/securesms/relay/EditRelayActivity.java` - classic email relay setup: email, app password, IMAP/SMTP advanced settings.
- `src/main/java/org/thoughtcrime/securesms/connect/FetchWorker.java` - periodic background fetch для FOSS/no-push сценария.
- `src/main/java/org/thoughtcrime/securesms/service/FetchForegroundService.java` - foreground fetch after push/background trigger.
- `src/main/java/org/thoughtcrime/securesms/connect/KeepAliveService.java` - persistent foreground service для надежного получения в фоне.
- `src/main/java/org/thoughtcrime/securesms/notifications/NotificationCenter.java` - Android notifications.
- `src/main/java/org/thoughtcrime/securesms/crypto/DatabaseSecretProvider.java` - secret для открытия локальной базы.
- `src/main/java/org/thoughtcrime/securesms/crypto/KeyStoreHelper.java` - Android Keystore AES/GCM wrapper.
- `src/main/java/org/thoughtcrime/securesms/LogViewFragment.java` - сбор debug log и device/app diagnostics.

## Accounts

`ApplicationContext.onCreate()` создает:

- `DcEventChannel`;
- `DcAccounts(new File(getFilesDir(), "accounts").getAbsolutePath(), eventChannel)`;
- `Rpc(new FFITransport(dcAccounts.getJsonrpcInstance()))`;
- выбранный `DcContext`.

Если аккаунтов нет, создается первый account через `rpc.addAccount()`.

Локальный путь аккаунтов: app private files dir, подпапка `accounts`.

## Core binding

JNI bridge находится в `jni/dc_wrapper.c`.

Важные native вызовы:

- `dc_accounts_new_with_event_channel`;
- `dc_jsonrpc_init`;
- `dc_accounts_start_io`;
- `dc_accounts_maybe_network`;
- `dc_accounts_background_fetch`;
- `dc_context_open`;
- `dc_context_set_config`;
- `dc_context_get_provider_from_email_with_dns`;
- chat/contact/send wrappers.

Android app может работать через:

- старые direct wrappers `DcContext`/`DcAccounts`;
- generated JSON-RPC client `chat.delta.rpc.Rpc`.

Для новых corporate features лучше сначала использовать существующий RPC/wrapper слой, не расширяя JNI.

## Onboarding/account setup

Classic email setup находится в `EditRelayActivity`.

Что там уже есть:

- email field: `R.id.email_text`;
- password/app password field: `R.id.password_text`;
- advanced IMAP/SMTP server/port/login/password fields;
- socket security selectors;
- certificate check selector;
- provider hint block;
- `rpc.listTransports(accId)`;
- `rpc.addOrUpdateTransport(accId, EnteredLoginParam)`.

Это главная безопасная точка для:

- provider selector Mail.ru / VK Mail / Yandex;
- corporate domain allowlist;
- app-password hints;
- diagnostic-mode entry before full account setup.

## Provider detection

Android вызывает:

```java
getContext(this).getProviderFromEmailWithDns(email)
```

Этот вызов идет через JNI в core provider lookup. Значит Android использует те же provider-db/core entries, что и core.

## Sync and message IO

Основной IO стартует в `ApplicationContext`:

```java
dcAccounts.startIo();
```

На смену сети Android вызывает:

```java
dcAccounts.maybeNetwork();
```

Через `ConnectivityManager.NetworkCallback` и fallback `NetworkStateReceiver`.

Фоновый fetch:

- FOSS/no-push: periodic `FetchWorker`, минимум Android WorkManager около 15 минут.
- gplay/chatmail push: FCM token передается в core, push запускает `FetchForegroundService`.
- persistent receive mode: `KeepAliveService`, если включен `Prefs.reliableService()`.

## Notifications

`NotificationCenter` строит notification channels, chat notifications, quick reply, mark-as-read actions. Для корпоративного MVP лучше не менять эту часть до полевого подтверждения транспорта.

## Local database/cache

Core account DB живет в app private storage under `files/accounts`. Android открывает аккаунты через `DcContext.open(passphrase)`, passphrase берется из `DatabaseSecretProvider`.

Изменять migrations и формат core DB на MVP-0 не нужно.

## Credentials

Email/app password вводятся в `EditRelayActivity` и передаются в core через `EnteredLoginParam`/`rpc.addOrUpdateTransport`. После этого учетные данные являются частью core account configuration в локальной базе.

Database secret:

- Android M+ хранится encrypted в SharedPreferences, sealed через Android Keystore AES/GCM.
- Pre-M fallback хранит unencrypted database secret в SharedPreferences.

## Contacts/groups/chats

Direct wrapper exposes:

- `createContact`;
- `addAddressBook`;
- `getChatlist`;
- `createChatByContactId`;
- `createGroupChat`;
- `addContactToChat`;
- `removeContactFromChat`.

Корпоративную адресную книгу можно встроить поверх этих API без изменения core протокола.

## Safe customization points

- `EditRelayActivity` и `activity_edittransport.xml` - provider selector, hints, allowlist, manual presets.
- Новый debug-only Activity/Fragment для diagnostics.
- `Prefs` - флаг corporate managed mode.
- `ApplicationContext` - минимальная инициализация managed defaults, без изменения IO.
- Contacts import flow через existing contact/address-book APIs.
- Log/export UI для sanitized diagnostic JSON.

## Risky areas

- `jni/dc_wrapper.c`, если не нужен новый core API.
- `jni/deltachat-core-rust` sync/IMAP/SMTP/TLS/MIME/encryption.
- Core database migrations.
- Autocrypt/SecureJoin/group protocol.
- Notification delivery internals до полевого теста.

## Где добавлять corporate features

- Provider presets: `EditRelayActivity` UI layer plus existing core provider lookup.
- Domain allowlist: validation before `rpc.addOrUpdateTransport`.
- Managed account mode: app-level flag in `Prefs`, then restrict onboarding options.
- Diagnostics: separate `DiagnosticsActivity` или separate app; не писать секреты в logcat.
- Address book: import after account configured using `createContact`/`addAddressBook`.
