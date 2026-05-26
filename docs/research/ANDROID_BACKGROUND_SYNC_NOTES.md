# Android background sync notes

Дата: 2026-05-13

## Summary

Для корпоративного IMAP-backed мессенджера background behavior является одним из главных рисков. Delta Chat Android уже имеет несколько механизмов: foreground IO while app alive, WorkManager periodic fetch, FCM-triggered fetch for gplay/chatmail, и optional persistent foreground service. Но для обычных IMAP провайдеров Mail.ru/Yandex/VK Mail нельзя считать push гарантированным. Доставку в фоне нужно проверять на реальных Android устройствах и SIM.

## Foreground

При старте приложения `ApplicationContext` создает `DcAccounts`, открывает accounts и вызывает:

```java
dcAccounts.startIo();
```

Это запускает core IO/sync. При открытом приложении это основной путь получения/отправки.

## Network changes

Для Android N+ регистрируется `ConnectivityManager.NetworkCallback`. На `onAvailable()` вызывается:

```java
getDcAccounts().maybeNetwork();
```

Также регистрируется legacy `NetworkStateReceiver` для `CONNECTIVITY_ACTION`. На старых Android он вызывает `maybeNetwork()` в worker thread.

Для field test обязательно проверить Wi-Fi -> mobile, mobile -> Wi-Fi и airplane mode on/off.

## Background without push

Если push disabled/unavailable, `ApplicationContext` планирует `FetchWorker` через WorkManager:

- network constraint: connected;
- interval: `PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS`, обычно 15 минут;
- worker вызывает `DcHelper.getAccounts(context).startIo()`;
- затем ждет 60 секунд.

Это полезно как fallback, но не дает instant messenger latency.

## Background with gplay/chatmail push

Flavor `gplay` содержит `FcmReceiveService`.

- получает FCM token;
- передает token в core через `setPushDeviceToken`;
- high-priority push запускает `FetchForegroundService`;
- non-high priority push делает synchronous background fetch с коротким timeout.

Для обычных Mail.ru/Yandex IMAP аккаунтов нельзя предполагать, что FCM push будет работать как chatmail push.

## Foreground service / reliable service

`KeepAliveService` может быть включен через `Prefs.reliableService()`.

Код прямо фиксирует продуктовую реальность Android: чтобы приложение надежнее получало сообщения в фоне, persistent foreground notification часто надежнее, чем только Doze exemption.

Corporate MVP должен явно тестировать:

- reliable service off;
- reliable service on;
- battery optimization allowed;
- battery optimization ignored.

## Doze and battery optimization

Есть UI и reminder для battery optimization:

- `NotificationsPreferenceFragment`;
- `DozeReminder`;
- `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`;
- `PowerManager.isIgnoringBatteryOptimizations()`.

Для корпоративного режима нельзя автоматически обещать reliable background delivery. Нужно показать field-test guidance и, возможно, управляемую инструкцию для отключения оптимизаций.

## Notifications

`NotificationCenter` отвечает за Android notification channels, chat notifications, quick reply. На Android 13+ требуется `POST_NOTIFICATIONS`.

Для MVP-0 transport test notification UX вторичен. Главное - замерить delivery latency in foreground/background/locked screen.

## Field-test cases

Обязательные сценарии:

- app foreground;
- app background;
- locked screen;
- reliable service off/on;
- battery optimization normal/ignored;
- Wi-Fi -> mobile;
- mobile -> Wi-Fi;
- airplane mode on/off;
- normal mobile;
- whitelist/restricted mobile.

## Risk verdict

Для open app / foreground Delta Chat Android должен быть хорошей базой. Для locked-screen/background на обычном IMAP через российские провайдеры риск высокий до полевых тестов: Android power management и provider/network whitelist могут ломать long-lived IMAP IDLE и TCP/TLS.
