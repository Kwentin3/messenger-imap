# Android credential and security notes

Дата: 2026-05-13

## Summary

Android client использует core account DB и отдельный database secret. На Android M+ database secret sealed через Android Keystore AES/GCM и хранится в SharedPreferences. Email/app password передаются в core через account transport configuration. Для corporate diagnostics нельзя использовать raw log export как отчет: существующий LogView сам предупреждает, что лог может содержать sensitive information.

## Где вводятся credentials

`EditRelayActivity`:

- `email_text`;
- `password_text`;
- optional `imap_login_text`;
- optional `smtp_login_text`;
- optional `smtp_password_text`;
- IMAP/SMTP host/port/security.

На submit создается `EnteredLoginParam` и вызывается:

```java
rpc.addOrUpdateTransport(accId, param)
```

## Где хранится local account data

`ApplicationContext` создает accounts directory:

```java
new File(getFilesDir(), "accounts").getAbsolutePath()
```

Core открывает account DB через:

```java
ac.open(secret.asString())
```

## Database secret

`DatabaseSecretProvider`:

- генерирует 32-byte secret;
- на Android M+ вызывает `KeyStoreHelper.seal()`;
- хранит encrypted serialized secret in SharedPreferences;
- на pre-M fallback хранит unencrypted secret.

`KeyStoreHelper`:

- Android Keystore alias: `DeltaSecret`;
- AES/GCM/NoPadding;
- IV + encrypted data сериализуются Base64 JSON.

## Что попадает в logs

`LogViewFragment` собирает:

```text
logcat -v threadtime -d -t 10000 *:I
```

И добавляет предупреждение, что лог может содержать sensitive information. Также diagnostic description включает device, app, battery optimization, reliable service, push state/token marker.

Вывод: raw log export нельзя использовать как corporate diagnostic JSON. Нужен отдельный sanitized exporter.

## Crash reports

В явном виде полноценная crash-report SaaS интеграция не подтверждена в рамках этого прохода. Есть helper для отправки crash report через email client. Для corporate fork нужно отдельно проверить все crash/log flows перед включением в field build.

## Security requirements для MVP-0 diagnostics

- Не persist app password.
- Не писать app password в SharedPreferences.
- Не писать password в logcat.
- Не включать raw SMTP/IMAP transcript в report.
- Не включать full email by default; достаточно domain или masked email.
- Не делать screenshots с видимым паролем.
- Не экспортировать raw logcat вместе с JSON.
- Все exceptions redacted before UI/export.

## Reset credentials

Для полного product flow reset лучше делать через existing account removal / transport update flows. Для diagnostics app проще: не сохранять credentials вообще, очищать поля после test run или on screen close.

## Corporate managed mode

Нужно добавить:

- запрет raw log export in managed builds or strong warning;
- forced provider/domain policy;
- optional MDM-provided config без секретов;
- separate secure secret entry flow;
- redaction utility shared by diagnostics and crash/log screens.

## Risk verdict

Текущая схема достаточно зрелая для обычного клиента, но corporate field diagnostics требует отдельной дисциплины redaction. Самый опасный leakage vector на MVP-0 - не база, а debug logs, screenshots и exported diagnostic artifacts.
