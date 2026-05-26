# Implementation Notes

## Chosen IMAP/SMTP Approach

MVP-0a uses minimal manual protocol handling over `SSLSocket`.

Decision record: `docs/blueprints/ANDROID_MAIL_LIBRARY_DECISION.md`.

## Dependency Licenses

No third-party runtime mail dependency is used in the MVP-0a implementation.

The Android app uses platform APIs:

- `SSLSocket` / `SSLSession`;
- `Socket`;
- `ConnectivityManager`;
- `PowerManager`;
- `org.json`;
- native Android widgets.

## Important Implementation Decisions

- UI is programmatic Java to avoid extra UI dependencies.
- The app uses only `INTERNET` and `ACCESS_NETWORK_STATE`.
- Provider presets are data-driven in `ProviderConfig`.
- Debug endpoint override is available only when `BuildConfig.DEBUG` is true.
- IMAP/SMTP commands are not logged or exported.
- JSON export is created only after explicit user action.
- The report masks email local-parts by default.
- VPN active best-effort invalidates whitelist conclusions.
- Single-account mode is smoke only; two-account mode is canonical for `transport_pass`.

## Known Limitations

- Build could not be executed in the current environment because Java/JDK, Gradle and Android SDK are missing.
- Manual protocol parser is intentionally narrow.
- IMAP folder parsing is basic and may need provider-specific refinement.
- Spam/Junk detection scans folders with names containing spam/junk/bulk/спам.
- No background, locked-screen, notification or foreground-service tests are implemented.
- No STARTTLS 587 fallback is implemented.
- No OAuth/provider-specific auth path is implemented.

## Intentionally Not Implemented

- Messenger UI.
- Persistent account storage.
- Background service.
- Locked-screen delivery.
- Notifications/push.
- Delta Chat Android integration.
- chatmail/core changes.
- Raw logcat export.
- Dangerous permissions.
