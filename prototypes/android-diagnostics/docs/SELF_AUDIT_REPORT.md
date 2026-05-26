# Self-Audit Report

## 1. Executive Summary

MVP-0a standalone Android diagnostics project was implemented under `prototypes/android-diagnostics`. The implementation follows the refined Blueprint direction: foreground-only transport diagnostics, no Delta Chat fork, no chatmail/core changes, no background MVP creep and no dangerous permissions.

Build validation was completed on 2026-05-13 after installing/configuring the Android toolchain. APK install/runtime validation remains blocked because no Android device is connected and no complete emulator/AVD is available.

## 2. Implementation Scope

Implemented:

- standalone Android project;
- provider selector;
- secure runtime credential input;
- single-account smoke mode;
- two-account canonical mode;
- DNS/TCP/TLS probes;
- IMAP login/select/IDLE;
- SMTP auth/send;
- Message-ID correlation;
- Spam/Junk folder scan;
- timeout policy export;
- sanitized JSON export;
- fieldValidity logic;
- README, schema, redacted samples and QA docs.

## 3. Blueprint Compliance Matrix

| Blueprint requirement | Status | Evidence | Notes |
|---|---|---|---|
| Standalone APK first | implemented | `prototypes/android-diagnostics/settings.gradle` | Separate project. |
| MVP-0a foreground-only | implemented | `MainActivity`, `DiagnosticRunner` | No background service. |
| No Delta Chat fork | implemented | project path only | Upstream repos untouched. |
| No chatmail/core changes | implemented | git status checked | Upstream repos clean. |
| No background MVP creep | implemented | `AndroidManifest.xml` | No service/receiver/notification permission. |
| Provider presets | implemented | `ProviderConfig.java` | Mail.ru, VK Mail, Yandex. |
| Two-account canonical mode | implemented | `DiagnosticConfig`, `MainActivity`, `DiagnosticRunner` | Receiver credentials supported. |
| Self-send only smoke | implemented | `MainActivity`, README | Single-account mode marked smoke. |
| Provider preflight documented | implemented | README | UI checkbox also exists. |
| Timeout defaults implemented/exported | implemented | `TimeoutPolicy.java`, `DiagnosticReport.java` | Defaults match Blueprint. |
| VPN invalidation rule | implemented | `NetworkMetadata.java`, `DiagnosticReport.java` | Best-effort VPN detection. |
| No dangerous permissions | implemented | `AndroidManifest.xml` | Only INTERNET and ACCESS_NETWORK_STATE. |
| Export filename convention | implemented | `DiagnosticReport.exportFileName()` | Uses provider/operator/mode/scenario/result. |
| No raw protocol transcript | implemented | `ImapSession`, `SmtpSession`, report schema | Commands are not exported. |
| No secrets in JSON/logcat | partially implemented | no explicit logging, schema | Static checks pass; logcat check blocked without device/emulator. |
| JSON schema exists | implemented | `schemas/diagnostic-report.schema.json` | Draft schema. |
| Sample reports redacted | implemented | `sample-reports/*.json` | Synthetic only. |
| README exists | implemented | `README.md` | Includes provider/preflight/run/export notes. |
| Manual QA checklist exists | implemented | `docs/MANUAL_QA_CHECKLIST.md` | Includes no-secret checks. |

## 4. MVP-0a Compliance

MVP-0a is foreground-only. The Android manifest does not define services, receivers for background work, notification permissions, boot receivers or foreground service permissions.

## 5. Non-Goals Compliance

No messenger UI, account list, chat storage, push, notifications, background receive, MDM import, cloud dashboard, corporate address book or Delta Chat integration was implemented.

## 6. Security/Privacy Compliance

- Password fields use secure input type.
- Password fields are cleared after run completion and on activity destroy.
- Passwords are not written into report JSON.
- No SharedPreferences credential storage exists.
- No raw logcat export exists.
- Email local-parts are masked in reports and summary.
- Exceptions are redacted before display/export.

Residual risk: Android process memory contains runtime password strings during active run.

## 7. Permissions Compliance

Manifest permissions:

- `android.permission.INTERNET`.
- `android.permission.ACCESS_NETWORK_STATE`.

No dangerous permissions are requested.

## 8. Provider Model Compliance

Provider presets:

- Mail.ru: `imap.mail.ru:993`, `smtp.mail.ru:465`.
- VK Mail: Mail.ru endpoints.
- Yandex: `imap.yandex.com:993`, `smtp.yandex.com:465`.

Debug override is available only in debug build and sets `debugOverrideUsed`.

## 9. Timeout Policy Compliance

`TimeoutPolicy.java` implements the Blueprint defaults and `DiagnosticReport` exports them.

## 10. Error Taxonomy Compliance

Implemented categories include:

- `dns_fail`;
- `tcp_timeout`;
- `tcp_refused`;
- `tls_fail`;
- `imap_greeting_fail`;
- `auth_fail`;
- `imap_select_fail`;
- `idle_unavailable`;
- `smtp_greeting_fail`;
- `smtp_auth_fail`;
- `smtp_rejected`;
- `message_not_received`;
- `spam_or_junk_placement`;
- `network_changed`;
- `network_whitelist_fail` as top-level result;
- `diagnostic_only`.

Partial: `cert_fail`, `provider_preflight_missing`, `android_background_restricted` are schema/audit concepts but not separately emitted by current runner.

## 11. JSON Schema/Export Compliance

Schema exists and sample reports follow the intended shape. Export uses Android document picker. Runtime export validation is blocked by no device/emulator.

## 12. Field Validity Rules Compliance

`fieldValidity` includes:

- whitelist mode check;
- VPN active invalidation;
- provider preflight checkbox;
- network change invalidation;
- export action flag.

The app does not manage test series/repetition UI; repetition remains tester-managed as allowed by Blueprint.

## 13. No-Secret QA Result

Static checks found no obvious token/app-password literals in project files. Matches for password/auth terms are expected source-code variables, README/security guidance and synthetic/redacted sample data. Runtime logcat validation is blocked because no Android device/emulator is available.

## 14. Deviations from Blueprint

- Runtime install/launch was not validated because no Android device/emulator is available.
- No runtime schema validation is implemented inside APK.
- Spam/Junk folder detection is heuristic.
- IMAP parser is intentionally narrow.
- FieldValidity exported-success signal is based on export-generation action before document write completes.

## 15. Known Limitations

- No field results.
- No device/SIM validation.
- No emulator validation.
- No STARTTLS 587 fallback.
- No OAuth.
- No background/locked-screen checks.

## 16. Risks Remaining

- Provider-specific IMAP folder names may require refinement.
- Some providers may reject `AUTH PLAIN` and require another SMTP auth mechanism.
- Some IMAP servers may require modified folder encoding support.
- Manual protocol implementation needs real-device validation.

## 17. Recommended Next Steps

1. Connect a real Android phone with SIM.
2. Install `app/build/outputs/apk/debug/app-debug.apk`.
3. Run manual QA checklist on Wi-Fi.
4. Validate exported JSON against schema.
5. Run real SIM tests only after Wi-Fi preflight.

## 18. Build/Runtime Validation Update

Validation date: 2026-05-13.

| Item | Status | Evidence | Notes |
|---|---|---|---|
| Build status | passed | `.\gradlew.bat clean assembleDebug --no-daemon` | Build completed successfully. |
| APK path | produced | `app/build/outputs/apk/debug/app-debug.apk` | Size observed: 36052 bytes. |
| Gradle wrapper | added | `gradlew.bat`, `gradle/wrapper/gradle-wrapper.properties` | Gradle 8.13. |
| JDK | configured | `java -version` | Microsoft OpenJDK 17.0.19. |
| Android SDK | configured | `C:\Android\android-sdk` | Platform `android-36`, build-tools `36.0.0`. |
| Install status | blocked | `adb devices -l` | No connected devices. |
| Launch status | blocked | no device/emulator | No runtime launch possible. |
| Runtime test status | blocked | no device/emulator | No UI/provider execution. |
| JSON export status | blocked | no runtime | Export flow could not be exercised. |
| Logcat no-secret status | blocked | no runtime | Static no-secret scan completed. |
| Provider Wi-Fi control status | blocked | no device/credentials | No provider result claimed. |
| Manifest permissions | passed | `aapt dump permissions` | Only `INTERNET` and `ACCESS_NETWORK_STATE`. |
| Remaining blocker | open | environment | Need physical Android device with SIM or complete emulator/AVD. |

## 19. Verification Commands Run

Executed:

- `rg -n "<uses-permission" prototypes/android-diagnostics/app/src/main/AndroidManifest.xml`
- `rg -n "READ_PHONE_STATE|ACCESS_FINE_LOCATION|READ_CONTACTS|READ_SMS|READ_CALL_LOG|POST_NOTIFICATIONS|FOREGROUND_SERVICE|REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" prototypes/android-diagnostics`
- `ConvertFrom-Json` over all sample reports and schema.
- secret-pattern grep over `prototypes/android-diagnostics` and the mail decision doc.
- `gradle assembleDebug`
- `java -version`
- `adb version`
- `.\gradlew.bat clean assembleDebug --no-daemon`
- `aapt dump permissions app/build/outputs/apk/debug/app-debug.apk`
- `adb devices -l`

Results:

- Manifest contains only `INTERNET` and `ACCESS_NETWORK_STATE`.
- Dangerous/background permissions were not found.
- Sample JSON files and schema parse as JSON.
- Secret-pattern grep found only expected masked synthetic emails in sample reports.
- Build now passes after toolchain setup.
- Install/runtime checks are blocked because no Android device/emulator is available.
