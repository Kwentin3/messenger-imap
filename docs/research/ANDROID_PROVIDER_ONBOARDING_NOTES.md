# Android provider onboarding notes

Дата: 2026-05-13

## Summary

Android client уже получает provider info из core/provider-db через `DcContext.getProviderFromEmailWithDns()`. Это подтверждает, что Mail.ru/VK Mail/Yandex presets лучше не дублировать в Android как отдельную сетевую правду. Android UI может добавить явный selector и инструкции, но финальная конфигурация должна оставаться совместимой с core.

## Текущий flow

Classic email login:

1. Пользователь открывает manual/classic email setup.
2. `EditRelayActivity` показывает email/password.
3. При потере фокуса email или при submit вызывается `updateProviderInfo()`.
4. `updateProviderInfo()` вызывает `getProviderFromEmailWithDns(email)`.
5. Если provider status preparation/broken, UI показывает provider hint and overview link.
6. На submit создается `EnteredLoginParam`.
7. `rpc.addOrUpdateTransport(accId, param)` передает конфигурацию в core.

## Existing provider presets

Из предыдущего discovery остается верным:

- Mail.ru: `imap.mail.ru:993` TLS, `smtp.mail.ru:465` TLS.
- VK Mail: использует Mail.ru IMAP/SMTP endpoints.
- Yandex: `imap.yandex.com:993` TLS, `smtp.yandex.com:465` TLS.

На Android эти данные должны приходить через core provider lookup, а не через отдельный hardcoded Android transport.

## Где показываются hints

`EditRelayActivity`:

- `providerLayout`;
- `providerHint`;
- `providerLink`;
- `provider.getBeforeLoginHint()`;
- `provider.getOverviewPage()`;
- `provider.getStatus()`.

Это место подходит для app-password подсказок.

## Как добавить corporate provider selector

Минимально:

- добавить selector до email/password или над provider hint;
- значения: Mail.ru, VK Mail, Yandex;
- при выборе preset показывать доменные подсказки и expected endpoints;
- email все равно валидировать через выбранный provider/domain mapping;
- advanced IMAP/SMTP оставить доступным только в debug/internal mode.

Не нужно на MVP-0:

- менять provider-db;
- менять core autoconfig;
- ломать manual account setup для обычных пользователей.

## Как скрыть остальные провайдеры

В corporate managed mode:

- ограничить route на `EditRelayActivity`;
- не показывать generic classic email wording;
- запретить submit, если email domain не соответствует выбранному provider или corporate allowlist;
- скрыть/disable advanced server override в production corporate mode.

В debug mode advanced fields нужно оставить, чтобы тестировать provider drift и сетевые проблемы.

## App password copy

Нужные подсказки в UI:

- Mail.ru/VK Mail: использовать пароль приложения, обычный пароль может не пройти.
- Yandex: включить IMAP и использовать app password или разрешенный provider auth path.
- Web availability не равна IMAP/SMTP availability.

Секрет нельзя писать в:

- logcat;
- diagnostic JSON;
- crash report;
- screenshots-ready debug text;
- share intent body.

## Diagnostic mode before account creation

Возможен и желателен:

- separate `DiagnosticsActivity`;
- inputs: provider, email, app password;
- diagnostics запускается без сохранения account config;
- результат export JSON без секретов;
- после успешной диагностики можно предложить перейти к account setup.

Это снижает риск загрязнить Delta Chat account DB тестовыми попытками.
