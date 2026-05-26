# Provider compatibility notes

Discovery date: 2026-05-13.

## Delta Chat provider database status

Core already contains presets for the three target providers:

| Provider | Core provider id | IMAP | SMTP | Notes |
|---|---|---|---|---|
| Mail.ru | `mail.ru` | `imap.mail.ru:993` TLS | `smtp.mail.ru:465` TLS | Status in core is `Preparation`; before-login hint says an app password for external apps is required. |
| VK Mail | `vk.com` | `imap.mail.ru:993` TLS | `smtp.mail.ru:465` TLS | Uses Mail.ru endpoints; app password hint points to Mail.ru account password page. |
| Yandex Mail | `yandex.ru` | `imap.yandex.com:993` TLS | `smtp.yandex.com:465` TLS | Status in core is `Preparation`; hint says IMAP protocol option must be enabled. |

Observed in `upstream/core/src/provider/data.rs`.

## Local no-secret endpoint probe

Command:

```powershell
cd imap-messenger-research/prototypes/imap-diagnostics
npm run diagnose
$env:PROVIDER="vk"; npm run diagnose
$env:PROVIDER="yandex"; npm run diagnose
```

Result on the current network, without credentials:

| Provider | DNS | TCP | TLS | Login/send/receive |
|---|---|---|---|---|
| Mail.ru | ok | IMAP 993 ok, SMTP 465 ok | ok, authorized, TLSv1.2 | skipped, no app password provided |
| VK Mail | ok | IMAP 993 ok, SMTP 465 ok | ok, authorized, TLSv1.2 | skipped, no app password provided |
| Yandex | ok | IMAP 993 ok, SMTP 465 ok | ok, authorized, TLSv1.3 | skipped, no app password provided |

This proves only that endpoints were reachable from this machine/network at test time.
It does not prove reachability in mobile whitelist mode.

## Provider setup expectations

- Mail.ru and VK Mail: user must create an app password / external application password. Use the full email address as IMAP/SMTP username.
- Yandex: user must enable IMAP access and use an app password or provider-approved auth path. Delta Chat provider entry also has a Yandex OAuth authorizer, but corporate MVP should prefer app password first if the provider account policy allows it.
- Availability of `mail.ru`, `vk.com` or `yandex.ru` web UI does not imply availability of `imap.*:993` or `smtp.*:465`.

