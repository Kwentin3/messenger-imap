# Android-first whitelist field test protocol

Дата: 2026-05-13

## Purpose

Проверить, доступны ли IMAP/SMTP endpoints российских почтовых провайдеров с Android-устройств в обычной мобильной сети и в режиме whitelist/restricted mobile internet.

Доступность web-интерфейса `mail.ru` или `yandex.ru` не доказывает доступность IMAP/SMTP. Проверять нужно именно DNS, TCP, TLS, IMAP, SMTP и delivery.

## Test environments

Каждый тест выполняется на Android device with SIM.

Desktop/Wi-Fi используется только как контрольная группа и не доказывает mobile whitelist compatibility.

Network modes:

- Wi-Fi control;
- normal mobile;
- whitelist/restricted mobile.

Operators:

- МТС;
- Билайн;
- МегаФон;
- T2.

Providers:

- Mail.ru;
- VK Mail;
- Yandex.

## Metadata per run

Фиксировать для каждого запуска:

- timestamp;
- device manufacturer/model;
- Android version / SDK;
- app version/build number;
- provider;
- email domain, not full email by default;
- operator;
- MCC/MNC, если доступно без dangerous permission;
- region/city;
- network type;
- mode: normal / restricted / whitelist;
- battery optimization state;
- reliable foreground service state;
- app state: foreground / background / locked screen;
- SIM slot, если важно и безопасно;
- test repetition number.

## Test repetitions

Каждый scenario повторять минимум 3 раза.

Не делать вывод по одному оператору, одному провайдеру или одному времени суток.

## Foreground test

1. Открыть diagnostics app.
2. Выбрать provider.
3. Ввести email + app password.
4. Запустить diagnostics.
5. Проверить:
   - DNS;
   - IMAP TCP/TLS/login/select;
   - IMAP IDLE;
   - SMTP TCP/TLS/login/send;
   - IMAP receive by Message-ID;
   - spam/junk folder placement.

Foreground pass: send/receive успешны, latency зафиксирована, JSON exported.

## Background test

1. Запустить diagnostics или Delta Chat test account.
2. Перевести приложение в background.
3. Отправить test message со второго controlled account.
4. Ждать configured window.
5. Зафиксировать notification/receive latency.

Background pass: сообщение получено без ручного открытия приложения в пределах заданного окна.

## Locked-screen test

1. Запустить test.
2. Заблокировать экран.
3. Отправить test message со второго account.
4. Зафиксировать delivery/notification latency.

Locked-screen pass: сообщение получено на locked screen, notification сработала, секреты не показаны.

## Network transition tests

Wi-Fi -> mobile:

1. Start on Wi-Fi.
2. Establish IMAP IDLE or polling state.
3. Disable Wi-Fi.
4. Confirm mobile network active.
5. Send test message.
6. Record reconnect and receive latency.

Airplane mode on/off:

1. Start diagnostics.
2. Turn airplane mode on.
3. Wait 30-60 seconds.
4. Turn airplane mode off.
5. Confirm reconnect.
6. Send/receive test.

Pass: app classifies network interruption and recovers without credentials reset.

## Pass/fail categories

- `foreground_pass`
- `background_pass`
- `locked_screen_pass`
- `idle_pass`
- `fallback_polling_pass`
- `provider_fail`
- `network_whitelist_fail`
- `auth_fail`
- `spam_junk_fail`
- `inconclusive`

## Failure interpretation

- DNS fail: likely resolver or whitelist issue.
- TCP timeout: likely port/IP not allowed or network filtering.
- TLS fail: TLS interception, SNI/cert issue, or provider/network incompatibility.
- Auth fail: app password/provider setup issue.
- SMTP rejected: provider policy/rate/spam issue.
- IMAP receive fail after SMTP send: delivery/spam/folder/sync issue.
- IDLE fail but polling pass: usable only with degraded latency.

## Required artifacts

- sanitized JSON report per run;
- screenshot of non-secret result screen if needed;
- manual notes for operator/provider/device anomalies;
- no app passwords in exported files.
