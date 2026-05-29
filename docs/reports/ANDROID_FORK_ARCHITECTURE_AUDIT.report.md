# Android Fork Architecture Audit Report

Date: 2026-05-29

Phase: 5 - Delta Chat Android architecture audit

Meta repository: `Kwentin3/messenger-imap`

Android repository: `Kwentin3/messenger-imap-android`

Android branch: `intake/upstream-build-baseline`

Android commit: `a3a8b3581f82456bb7fe3342485cef4593c31315`

Core submodule: `jni/deltachat-core-rust` at `784a6abb3bae6d027062cb9dbc1bf9829905b013`

## Source Docs Used

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`
- `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md` from PR #9 / `origin/docs/fork-strategy-decision`
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`
- `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md`
- `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md`
- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`
- `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md`
- Android upstream files in `C:\work\messenger-imap-android`

## What Was Done

- Audited the clean Delta Chat Android fork structure after a successful Gradle debug build.
- Identified onboarding, invite/deeplink, provider setup, connectivity diagnostics, contacts, chat list, logs, build flavor, license, RPC and JNI/core boundaries.
- Confirmed the Android fork working tree remained clean.
- Created `docs/hand_off/ANDROID_FORK_SAFE_EXTENSION_MAP.md`.

## What Was Not Done

- No Android source code was changed.
- No corporate feature was implemented.
- No package ID, app name, branding, icon, signing or release flow was changed.
- No `chatmail/core`, JNI, sync, encryption, MIME, or database migration changes were made.
- No server, deployment, Traefik, Control Plane backend or API work was done.

## Files Changed

- `docs/reports/ANDROID_FORK_ARCHITECTURE_AUDIT.report.md`
- `docs/hand_off/ANDROID_FORK_SAFE_EXTENSION_MAP.md`

## Commands / Checks Run

- `rg --files`
- `rg -n "WelcomeActivity|InstantOnboardingActivity|ConversationListActivity|ConnectivityActivity|RelayListActivity|EditRelayActivity|ShareActivity|LogViewActivity" src/main/AndroidManifest.xml`
- `rg -n "ACTION_VIEW|providerQrData|setConfigFromQr|createAccount|configure|DCACCOUNT|DCLOGIN|handle|getIntent()" ...`
- `rg -n "ConnectivityActivity|check_connection|getConnectivityHtml|connectivity|ConnectivityChanged|LogViewActivity|share|FileProviderUtil" ...`
- `rg -n "listTransports|addOrUpdateTransport|EnteredLoginParam|getProviderFromEmailWithDns|imap|smtp|password" ...`
- `rg -n "class QrCodeHandler|handleQrData|handleOnlySecureJoinQr|checkQr|Securejoin|DC_QR|addTransportFromQr|setConfigFromQr" ...`
- `rg -n "sourceSets|productFlavors|applicationId|namespace|versionName|versionCode|compileSdk|jni" build.gradle`
- `rg -n "deltachat-core-rust|chatmail/core|GPL|source|build" README.md LICENSE .gitmodules build.gradle scripts/ndk-make.sh`
- `git status --short`
- `git rev-parse --abbrev-ref HEAD`
- `git rev-parse HEAD`
- `git submodule status`

## Tests Run

No new tests were run in this phase. Phase 4 already proved `.\gradlew.bat assembleDebug --stacktrace` passes in the ASCII checkout. A test-source search did not find an existing Java/Kotlin `src/test` or `src/androidTest` tree in the checked source list.

## Architecture Findings

### App Module and Build

- Main Android app namespace: `org.thoughtcrime.securesms`.
- Default application ID: `com.b44t.messenger`.
- `gplay` flavor application ID: `chat.delta`.
- Version observed: `versionName "2.50.0"`, `versionCode 748`.
- Product flavors: `foss` and `gplay`.
- Debug build adds application ID suffix `.beta`.
- Main JNI libraries are loaded from `libs` via `sourceSets.main.jniLibs.srcDirs = ['libs']`.
- Build file declares `compileSdk 36`, `minSdkVersion 21`, `targetSdkVersion 36`.

### Onboarding and Account Setup

- Primary welcome surface: `src/main/java/org/thoughtcrime/securesms/WelcomeActivity.java`.
- Instant account / chatmail onboarding surface: `src/main/java/org/thoughtcrime/securesms/InstantOnboardingActivity.java`.
- `InstantOnboardingActivity` handles `DCACCOUNT:` / `DCLOGIN:` URI schemes and provider QR data.
- Configured-account path for `DCACCOUNT:` links routes to `RelayListActivity` instead of re-running full onboarding.
- Profile setup surface exists in `CreateProfileActivity.java`.

### Deep Link and QR Handling

- Manifest declares existing deep links for `mailto`, `openpgp4fpr`, `https://i.delta.chat`, `DCACCOUNT`, and `DCLOGIN`.
- `ConversationListActivity.handleOpenpgp4fpr()` handles `openpgp4fpr` and `Util.isInviteURL(uri)`.
- `QrCodeHandler` centralizes QR handling for SecureJoin, relay/profile QR, proxy QR and backup QR.
- `DcContext.checkQr()` and `Rpc.checkQr()` cross the core boundary; corporate invite parsing should not be mixed into SecureJoin/core QR semantics unless a later Blueprint authorizes that.

### Provider Setup and Transport Checks

- Provider/manual transport setup is centered on `src/main/java/org/thoughtcrime/securesms/relay/EditRelayActivity.java`.
- `EnteredLoginParam` carries IMAP/SMTP host, port, security, username and password fields.
- `EditRelayActivity.setupConfig()` calls `rpc.addOrUpdateTransport(accId, param)`.
- `RelayListActivity` reads configured transports via `rpc.listTransportsEx(accId)`.
- Provider hints are obtained through `DcContext.getProviderFromEmailWithDns(...)`.
- Connectivity diagnostics surface: `ConnectivityActivity`, backed by `DcContext.getConnectivityHtml()`.
- Settings route into connectivity via `ApplicationPreferencesActivity`.

### Contacts, Directory and External Marking Surfaces

- Main contact list surface: `ContactSelectionListFragment`.
- Adapter/item surfaces: `contacts/ContactSelectionListAdapter.java` and `contacts/ContactSelectionListItem.java`.
- Recipient state is represented through `recipients/Recipient.java`.
- System address book access is isolated in `contacts/ContactAccessor.java`; it reads `ContactsContract`.
- Corporate directory MVP should avoid silent system contact import and should use a separate read-only corporate directory adapter before touching system contact permissions or writes.

### Logs and Diagnostics Export

- `LogViewActivity.shareLog()` exports current log content through a file provider and Android share intent.
- Existing log export is useful as a diagnostic surface but is high-risk for corporate support because logs can contain sensitive data. Any support-safe export needs redaction and explicit user action.

### Core / JNI / RPC Boundary

- Java RPC wrapper: `src/main/java/chat/delta/rpc/Rpc.java`.
- Native FFI transport: `src/main/java/com/b44t/messenger/FFITransport.java`.
- Native context wrapper: `src/main/java/com/b44t/messenger/DcContext.java`.
- Core submodule: `jni/deltachat-core-rust`.
- Early corporate implementation must treat these as high-risk boundaries and stay above them.

### Notifications and Background Work

- Relevant packages exist under `notifications`, `service`, `jobmanager`, `connect`, and `ApplicationContext`.
- Background reliability promises are not safe early-slice scope. They require separate investigation after app-layer corporate flows are stable.

### Licensing

- Android upstream repository is GPLv3+ per `README.md` and `LICENSE`.
- Modified APK distribution requires corresponding source and notice preservation planning before release.
- GPL compliance remains a release blocker, not a local debug-build blocker.

## Safe Areas Identified

- New isolated corporate package under the Android app layer.
- Manifest-level corporate invite deep link Activity, without touching existing SecureJoin handlers.
- Welcome/settings entry points that launch corporate placeholder flows.
- Read-only local corporate directory models/adapters that do not write to Delta Chat core DB.
- Provider profile policy wrapper around existing transport setup, without storing credentials in Control Plane.
- Connectivity status wrappers that call existing app/core diagnostics, without raw log export.
- UI badges/warnings in contact/chat surfaces using corporate metadata fixtures first.
- Release metadata warning screen with mocked metadata and no auto-update/signing.

## High-Risk Areas Identified

- `jni/deltachat-core-rust` / chatmail core.
- JNI/FFI classes and native method contracts.
- Sync engine, encryption, SecureJoin protocol, MIME pipeline and message database migrations.
- Existing account DB ownership and direct core database writes.
- Background services and notification internals.
- Existing system contact import/export semantics.
- Existing log export without redaction.
- Package rename, app rename, branding, release signing and release APK publication.

## Acceptance Criteria Result

Accepted.

- Safe areas identified: yes.
- High-risk areas identified: yes.
- Relevant files/classes listed: yes.
- No code changes: yes.
- No core changes: yes.
- No implementation started: yes.

## Gate Result

Gate to Phase 6 is open. The safe extension map exists and supports a conservative app-layer customization Blueprint.

## Blockers

No Phase 5 blocker.

Known caveat from Phase 4 remains: native/core rebuild is not validated on this Windows machine because Android NDK and Rust are not installed/configured. This blocks native/core work, which is out of early roadmap scope.

## Next Phase Decision

Proceed to Phase 6: Safe customization map and corporate extension points.
