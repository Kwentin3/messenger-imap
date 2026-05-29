# Android Branding And Onboarding Refactor Audit Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Meta repo: https://github.com/Kwentin3/messenger-imap

Android repo: https://github.com/Kwentin3/messenger-imap-android

Status: audit only, docs-only

## 1. Executive Summary

The old visible workflow is still Delta Chat-first. In the Android fork, the launcher opens `ConversationListActivity`; if the selected profile is not configured, `PassphraseRequiredActionBarActivity` redirects to `WelcomeActivity`. `WelcomeActivity` offers "Create New Profile" and "I Already Have a Profile". "Create New Profile" opens `InstantOnboardingActivity`, which defaults to a Chatmail provider host and exposes "Use Other Server", "List Chatmail Servers", QR login, and classic email/manual relay setup paths.

The provider flow that shows other mail/provider options is not a separate hardcoded provider list in app-layer Java. It comes from a mix of:

- `InstantOnboardingActivity` default Chatmail onboarding and external server list;
- `signup_options_view.xml` alternate signup choices;
- `EditRelayActivity` manual IMAP/SMTP setup;
- native/core/provider-db lookup through `DcContext.getProviderFromEmailWithDns()` and `DcProvider.getBeforeLoginHint()`;
- RPC calls such as `addTransportFromQr()` and `addOrUpdateTransport()`.

The safest immediate refactor is not a package rename or broad rebrand. It is a small app-layer UX slice: make the corporate entry point and wording clear, preserve existing account setup/manual IMAP-SMTP, and defer app identity, icon, package ID, signing, notification channel IDs, and provider-db/core changes.

Important branch finding: Android `origin/main` currently contains the native build guard and README updates for `0.1.1`, but not the corporate placeholder classes. The placeholder layer exists on `origin/feature/release-metadata-warning` and its stacked feature branches. Any implementation must first choose/reconcile the Android baseline branch; otherwise the refactor will be applied to the wrong code surface.

## 2. Source Documents Reviewed

| Source | Status | Notes |
| --- | --- | --- |
| `docs/roadmap/PROJECT_ROADMAP.md` | read | Still contains some older fork-vs-shell blocker wording; later fork decision exists under a different path. |
| `docs/blueprints/ANDROID_FORK_STRATEGY_DECISION.md` | read | Actual available fork decision path in main. It says no rebrand before Blueprint and no chatmail/core, JNI, sync, encryption, or DB migration changes in baseline. |
| `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md` | missing in main | Not available at requested path. |
| `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md` | missing in main | Decision exists as `docs/blueprints/ANDROID_FORK_STRATEGY_DECISION.md`. |
| `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md` | read | Android-first, provider-agnostic, Control Plane-backed product direction. |
| `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md` | read | Branding should be minimal; broad rebrand waits for package/release decisions. |
| `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md` | read | Provider profiles remain provider-agnostic and evidence-based. |
| `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md` | read | Invite activation requires Control Plane in MVP. |
| `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md` | read | Diagnostics do not activate membership. |
| `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md` | read | APK download, invite possession, email verification, and diagnostics are separate trust states. |
| `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md` | read | Defines corporate desired onboarding contract. |
| `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md` | read | Defines DirectoryManifest/Snapshot, internal/external separation, stale behavior. |
| `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md` | read | Control Plane owns invites, verification, membership, external relationship activation, release metadata. |
| `docs/reports/ANDROID_INTERNAL_SMOKE_APK_RELEASE_0_1_1.report.md` | read | Confirms native build, `libnative-utils.so`, `arm64-v8a`, runtime smoke pending at report time. |
| `docs/reports/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_AUDIT.report.md` | missing in main | Not available at requested path. |
| `docs/hand_off/ANDROID_FORK_SAFE_EXTENSION_MAP.md` | missing in main | Not available at requested path. |
| `docs/blueprints/ANDROID_FORK_SAFE_CUSTOMIZATION_BLUEPRINT.md` | missing in main | Not available at requested path. |
| `docs/implementation/ANDROID_CLIENT_MVP_IMPLEMENTATION_PLAN.md` | missing in main | Not available at requested path. |

## 3. Android Repo Baseline State

| Branch / ref | Finding |
| --- | --- |
| `origin/main` | Contains upstream baseline plus README/native build guard changes for `0.1.1`. Diff from upstream baseline touches `README.md`, `BUILDING.md`, `build.gradle`, and `scripts/ndk-make.sh` only. |
| `origin/feature/release-metadata-warning` | Contains stacked corporate placeholder layer from prior Android roadmap execution. It adds `CorporateOnboardingActivity`, invite parser, directory fixtures, external badge policy, release metadata placeholders, manifest deep link, and welcome button. |
| `android-internal-smoke-0.1.1` release target | `61f9c4a8d1f6fc1de2fec8189ac4b16b996ef6a3`; this is the native build fix commit, not the old placeholder feature commit. |
| Runtime owner report | `0.1.1` installs and launches successfully on Huawei; full smoke checklist still needs explicit pass. |

Implication: before implementing branding/onboarding changes, choose whether to rebase/cherry-pick the placeholder layer onto native-build `main`, or rebuild a new product branch from `origin/feature/release-metadata-warning` plus native build guard. Do not assume the placeholder code is present in `origin/main`.

## 4. Branding Audit

| Area | File/path | Current text/behavior | User-visible? | Safe to change now? | Risk | Recommendation |
| --- | --- | --- | --- | --- | --- | --- |
| App display name / app label | `src/main/res/values/strings.xml:4`, `src/main/AndroidManifest.xml:84` | `app_name` is `Delta Chat`; manifest label uses `@string/app_name`. | yes | later | high_risk_identity | Do not rename until package/release identity decision. App name change affects launcher, task label, notifications, screenshots, support docs. |
| Launcher alias | `src/main/AndroidManifest.xml:159-207` | `.RoutingActivity` alias targets `ConversationListActivity`; launcher icon is `@mipmap/ic_launcher`; app link host includes `i.delta.chat`. | yes | later | high_risk_identity | Do not alter launcher identity in first UX slice. Add corporate entry inside app-layer instead. |
| Launcher icons | `src/main/res/mipmap*/ic_launcher*`, `ic_launcher-web.png` | Upstream Delta Chat icon resources. | yes | later | high_risk_identity | Icon/branding belongs with package/signing/release identity slice. |
| Welcome screen header | `src/main/res/layout/welcome_activity.xml:35-41`, `strings.xml:673` | "Secure Decentralized Chat". | yes | safe_now | low | Safe first wording target if branch baseline is reconciled. Replace with corporate-safe wording without deleting existing account setup. |
| Welcome primary action | `WelcomeActivity.java:89-91`, `strings.xml:648` | "Create New Profile" starts `InstantOnboardingActivity`. | yes | later | medium | Do not remove; can demote after corporate entry is proven. |
| Welcome secondary action | `WelcomeActivity.java:92`, `strings.xml:650`, `login_options_view.xml` | "I Already Have a Profile" opens second-device/backup options. | yes | do_not_change | medium | Preserve. It protects backup and multi-device flows. |
| Corporate placeholder entry | `origin/feature/release-metadata-warning:WelcomeActivity.java:71,96`, `welcome_activity.xml:74` | Adds "Join organization" button to `CorporateOnboardingActivity`. Not present in current `origin/main`. | yes when on feature branch | safe_now after baseline reconciliation | low | Good first product entry point. Reconcile branch first. |
| Corporate placeholder screen | `origin/feature/release-metadata-warning:CorporateOnboardingActivity.java`, `corporate_onboarding_activity.xml` | Shows invite fallback code, provider status, diagnostics, directory fixture, external badge, release metadata. | yes when on feature branch | safe_now after baseline reconciliation | low | Keep as product-layer placeholder; refine wording and flow ordering. |
| About/help labels | `strings.xml:1135`, `strings.xml:1138`, `LocalHelpActivity.java:77-112` | "Delta Chat Homepage", "About Delta Chat", delta.chat links. | yes | later / license_attribution_sensitive | medium | User-facing support links can be replaced later, but preserve upstream attribution/license. |
| Donation/statistics texts | `strings.xml:381`, `strings.xml:872-879`, `ApplicationPreferencesActivity.java:278`, `ConversationListActivity.java:696` | Delta Chat donation/statistics prompts and delta.chat donation link. | yes | later | medium | Suppress/replace user-facing prompts in a dedicated cleanup slice; avoid license/credits removal. |
| Notification text and channels | `NotificationCenter.java:319-320`, `CallCoordinator.java:160-169`, `strings.xml:1108` | Some channel descriptions and permission text mention Delta Chat; channel IDs include legacy delta wording. | yes | later | high_risk_identity | Channel IDs are sticky once created. Defer to identity phase. Text-only permission wording may be safe later. |
| Share target and shortcuts | `src/main/res/xml/shortcuts.xml`, `AndroidManifest.xml:104-147` | Share target routes to `ShareActivity`; labels mostly app label. | yes | later | medium | Do not alter before app identity/release decision. |
| Fastlane metadata | `fastlane/metadata/android/*` | Delta Chat titles/descriptions/screenshots. | yes for store context | later | license_attribution_sensitive | Leave until app store/release strategy exists. |
| Developer README/build docs | `README.md`, `BUILDING.md`, `LICENSE` | Upstream Delta Chat docs plus project block. | no / developer-facing | license_attribution_sensitive | low | Preserve upstream credits and license; project-specific block is fine. |
| Hardcoded support/help URLs | `LocalHelpActivity.java:106-112`, `BUILDING.md`, README | delta.chat homepage/privacy/contribute links. | yes in Help | later / license_attribution_sensitive | medium | Replace only user-facing help/support endpoints; keep attribution and license links. |

## 5. Current Onboarding Workflow Audit

### Current Flow

```text
App launch
  -> Android launcher alias .RoutingActivity
  -> ConversationListActivity
  -> PassphraseRequiredActionBarActivity checks DcHelper.isConfigured()
  -> if unconfigured: WelcomeActivity
  -> Create New Profile
      -> InstantOnboardingActivity
      -> default Chatmail host nine.testrun.org
      -> optional Use Other Server / List Chatmail Servers
      -> optional scan dcaccount/dclogin QR
      -> rpc.addTransportFromQr(...)
  -> I Already Have a Profile
      -> add second device QR
      -> restore backup
  -> Use Classic Email as Relay
      -> EditRelayActivity
      -> email/password
      -> provider hint from provider-db/native lookup
      -> advanced IMAP/SMTP fields
      -> rpc.addOrUpdateTransport(...)
```

### Corporate Desired Flow

```text
App launch
  -> corporate welcome
  -> join organization / enter invite
  -> resolve invite through Control Plane later
  -> configure mail transport
  -> check transport
  -> email verification / activation placeholder until backend exists
  -> first directory sync placeholder until backend exists
  -> preserve manual IMAP/SMTP and standard Delta Chat setup as fallback paths
```

| Step | Current implementation | Desired corporate behavior | Gap | Risk | Recommendation |
| --- | --- | --- | --- | --- | --- |
| Launcher | `AndroidManifest.xml:159-207` opens `ConversationListActivity`. | Launcher can remain unchanged initially. | Corporate UX starts only after unconfigured redirect. | low | Keep launcher stable. |
| Unconfigured redirect | `PassphraseRequiredActionBarActivity.java:31-33` starts `WelcomeActivity`. | Same redirect can host corporate welcome. | No corporate-first wording in `origin/main`. | low | Use `WelcomeActivity` as safe entry point. |
| Welcome screen | `WelcomeActivity.java:63-92`, `welcome_activity.xml` | "Join organization" should be first or prominent. | Current main only shows Delta Chat profile actions. | medium | Add or restore corporate entry after branch reconciliation. |
| Instant account | `InstantOnboardingActivity.java:69-70,127,407,570` | Provider setup should be after invite context or explicitly optional. | Current flow starts with Chatmail profile creation. | medium | Do not remove; demote behind corporate flow. |
| Other servers | `InstantOnboardingActivity.java:377`, `strings.xml:662-663` | Organization-recommended provider first; custom provider remains. | "List Chatmail Servers" is non-corporate. | low | Hide behind advanced/non-corporate option later. |
| Manual IMAP/SMTP | `EditRelayActivity.java`, `activity_edittransport.xml`, `strings.xml:652` | Required fallback for provider-agnostic architecture. | Text says "Classic Email as Relay"; no organization policy context. | low | Preserve path; wrap with provider profile policy text later. |
| Connectivity/transport check | `ConnectivityActivity`, `corporate placeholder opens it when configured` | Check Transport after provider setup. | In current main it is generic connectivity, not corporate diagnostics gate. | medium | Use as handoff first; do not claim diagnostics activation. |
| Invite deep link | Delta Chat handles `i.delta.chat`, `openpgp4fpr`, `dcaccount`, `dclogin`; feature branch adds `messenger-imap://invite`. | Corporate app link/fallback code. | Custom scheme exists only in feature branch and is not verified app link. | medium | Keep placeholder; later add verified app link after domain decision. |

## 6. Provider Flow Audit

| Provider flow area | File/path | Current behavior | Corporate target | Safe change? | Risk | Recommendation |
| --- | --- | --- | --- | --- | --- | --- |
| Default instant provider | `InstantOnboardingActivity.java:69-70,127` | Defaults to `nine.testrun.org` Chatmail QR account. | Invite-resolved organization profile or cached recommended provider. | later | medium | Do not change default until corporate entry decides when instant onboarding is allowed. |
| Other provider/server list | `InstantOnboardingActivity.java:69,377`, `strings.xml:662-663` | Opens `https://chatmail.at/relays`, labelled "List Chatmail Servers". | Organization recommended provider first; non-baseline providers marked unverified/candidate. | later | medium | Suppress from primary corporate onboarding; keep accessible in advanced path if needed. |
| Manual account setup | `signup_options_view.xml:44`, `EditRelayActivity.java:147`, `activity_edittransport.xml` | "Use Classic Email as Relay" exposes email/password and advanced IMAP/SMTP. | Custom IMAP/SMTP path remains available where policy allows. | safe_now for wording only | low | Preserve. Add corporate provider profile hint around it later. |
| Provider database lookup | `EditRelayActivity.java:316-358`, `DcContext.java:326-327`, `DcProvider.java:22-24` | On email blur, native lookup returns provider status, hint, and overview URL. | Evidence-based provider profile/diagnostics status. | do_not_change | high | Do not alter provider-db/native lookup in this refactor. Add app-layer wrapper/labels only. |
| Transport save | `EditRelayActivity.java:530`, `Rpc.java:294` | Sends `EnteredLoginParam` to core via `addOrUpdateTransport`. | Same transport authority, no Control Plane credential storage. | do_not_change | high | Keep unchanged. |
| QR account/login | `InstantOnboardingActivity.java:570`, `Rpc.java:308` | Adds transport from `dcaccount:`/`dclogin:` QR. | Corporate invite route separate from Delta Chat transport QR. | later | medium | Keep QR support. Avoid conflating corporate invite with transport QR. |
| Connectivity check | `ConnectivityActivity`; corporate feature branch button opens it | Generic Delta Chat connectivity view. | Transport check handoff, no membership activation. | safe_now for navigation/wording | low | Use as placeholder; future diagnostics adapter must redact secrets. |

## 7. App Identity Audit

| Identity point | File/path | Current value | Classification | Recommendation |
| --- | --- | --- | --- | --- |
| Namespace | `build.gradle:17` | `org.thoughtcrime.securesms` | high_risk_identity | Do not change in this slice. |
| FOSS applicationId | `build.gradle:34` + debug suffix `build.gradle:112` | `com.b44t.messenger.beta` for debug | high_risk_identity | Do not change until package identity decision. |
| GPlay applicationId | `build.gradle:138` + debug suffix | `chat.delta.beta` for debug | high_risk_identity | Do not change now. |
| Signing configs | `build.gradle:89-127` | Upstream debug/release config with external properties. | high_risk_identity | Do not touch; no signing keys in repo. |
| File provider authorities | `AndroidManifest.xml:515-536`, `FileProviderUtil.java:12` | `${applicationId}.fileprovider`, `${applicationId}.attachments` | high_risk_identity | Package rename changes these; defer. |
| Launcher activity | `AndroidManifest.xml:159-207` | Alias `.RoutingActivity` to `ConversationListActivity`. | later | Keep. |
| Deep link schemes | `AndroidManifest.xml:175-196,381-390`; feature branch adds `messenger-imap://invite`. | `openpgp4fpr`, `i.delta.chat`, `dcaccount`, `dclogin`; feature branch custom invite scheme. | later | Preserve upstream schemes; keep corporate scheme placeholder until verified app link design. |
| Notification channel IDs | `NotificationCenter.java`, `CallCoordinator.java` | Existing Delta/VOIP channel IDs. | high_risk_identity | Defer; Android channels are sticky for installed users. |

Externally brandable without package rename: safe app-layer text, welcome copy, corporate screen title, non-license user-facing links, internal README/project block.

Requires later package identity decision: app label, icon, applicationId/package name, authorities, signing, release metadata identity, store metadata, notification channel IDs.

Unsafe now: package ID/signing config, JNI/core/provider-db changes, database migrations, sync/encryption/MIME, shortcut/share authorities.

## 8. External Links And Attribution Audit

| Link / reference | File/path | Classification | Recommendation |
| --- | --- | --- | --- |
| `https://delta.chat`, `https://delta.chat/gdpr`, `https://delta.chat/contribute` | `LocalHelpActivity.java:106-112`, `strings.xml:1135-1138` | user-facing onboarding/help link plus attribution-sensitive | Replace user help/support later; preserve upstream attribution/license. |
| `https://get.delta.chat` | `strings.xml:605`, `strings.xml:1017` | user-facing distribution/update link | Replace in corporate release metadata/update warning slice. |
| `https://delta.chat/donate` | `strings.xml:381`, `ConversationListActivity.java:696`, `ApplicationPreferencesActivity.java:278` | user-facing upstream donation prompt | Suppress/replace in dedicated cleanup slice; preserve credits elsewhere. |
| Google Play / F-Droid badges | Android `README.md`, fastlane metadata | developer/store-facing | Keep developer attribution; do not present as corporate distribution path. |
| Fastlane metadata | `fastlane/metadata/android/*` | store-facing / attribution-sensitive | Leave until app store strategy and branding decision. |
| `chatmail.at/relays` | `InstantOnboardingActivity.java:69,377` | user-facing provider/server list | Hide from primary corporate flow; keep only advanced or replace with organization provider profile later. |

## 9. Safe Change Classification

Safe now after Android baseline reconciliation:

- corporate welcome wording and button placement;
- corporate placeholder copy;
- explicit "Join organization" entry;
- clear note that APK install does not equal membership;
- app-layer provider profile explanatory text;
- route to existing Connectivity screen as transport check handoff;
- docs/README status updates.

Later:

- full welcome/onboarding UX flow;
- provider profile selection UI;
- replacing generic Help/support/donation links;
- notification permission wording;
- verified app links;
- store/fastlane metadata.

Do not change in this refactor:

- `applicationId`, package namespace, signing config, file provider authorities;
- launcher icon/app name until identity decision;
- chatmail/core, JNI/FFI, sync, encryption, Autocrypt/SecureJoin, MIME, database migrations;
- provider-db/native lookup;
- existing manual IMAP/SMTP path;
- upstream license/credits/attribution.

## 10. First Safe Slice

Prerequisite: reconcile Android baseline. Current `origin/main` does not contain the corporate placeholder app-layer files, while `origin/feature/release-metadata-warning` does. The next implementation should first create a branch from current `main` and bring in the placeholder layer intentionally, or rebase the placeholder branch onto current `main`.

First safe implementation slice:

1. Preserve package ID, signing, launcher icon, manual IMAP/SMTP, and existing account setup.
2. Add or restore `CorporateOnboardingActivity` as an app-layer Activity.
3. Add a visible "Join organization" entry on `WelcomeActivity`.
4. Keep "Create New Profile" and "I Already Have a Profile" available.
5. Adjust only safe strings/copy so users understand corporate onboarding is the intended path.
6. Build `assembleFossDebug -PABI_FILTER=arm64-v8a`.
7. Run install/launch smoke and verify old account setup still opens.

## 11. High-Risk Files / Areas

- `jni/` and `jni/deltachat-core-rust/`
- `src/main/java/com/b44t/messenger/*` native wrapper classes
- `src/main/java/chat/delta/rpc/*` generated RPC layer
- `build.gradle` package/applicationId/signing sections
- `AndroidManifest.xml` authorities, launcher alias, existing Delta Chat deep links
- provider-db/native provider lookup
- `EditRelayActivity` save path and RPC transport calls
- notification channel IDs
- database/account migration paths in `ApplicationContext` and core
- SecureJoin, Autocrypt, encryption, sync, MIME, and DB migrations

## 12. Acceptance Criteria Result

| Criterion | Result |
| --- | --- |
| Branding points found | yes |
| Onboarding points found | yes |
| Provider flow offering mail/services found | yes |
| Manual IMAP/SMTP path identified | yes |
| Safe changes identified | yes |
| Do-not-change areas identified | yes |
| Package ID/signing/applicationId unchanged | yes, audit only |
| Old account setup preserved | yes, audit recommends preserving |
| Mail.ru-only refactor avoided | yes |
| Phase refactor plan created | yes, see `docs/blueprints/ANDROID_BRANDING_AND_ONBOARDING_REFACTOR_PLAN.md` |
| First safe slice listed | yes |
| High-risk files/areas listed | yes |
| Runtime confirmation report created | yes |
| Code changes made | no |

## 13. Findings

1. `origin/main` and the placeholder feature branch are not aligned. This is the main process risk for the next implementation task.
2. The old Delta Chat onboarding is concentrated in `WelcomeActivity`, `InstantOnboardingActivity`, `signup_options_view.xml`, and `EditRelayActivity`.
3. The visible "other provider/server" UX comes mainly from Chatmail instant onboarding and manual relay setup, not from a simple app-layer provider list.
4. Provider hints and provider status are native/provider-db-backed; this should be wrapped at app-layer, not rewritten.
5. The corporate product can safely start with a thin app-layer entry and wording pass, but app identity changes should wait.
6. Runtime owner report confirms `0.1.1` install and launch, but full smoke remains to be completed.

## 14. Next Recommended Implementation Task

Create a docs-backed implementation branch in `messenger-imap-android` that reconciles the corporate placeholder branch with current native-build `main`, then implement Phase 1 from the refactor plan: minimal safe product wording and visible corporate entry, with no package/signing/core changes.
