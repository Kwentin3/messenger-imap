# Android Fork Baseline Preparation Report

Date: 2026-05-13

Scope: preparation of a controlled fork/workspace baseline for an Android IMAP/SMTP-backed messenger based on Delta Chat / Chatmail.

## 1. Executive summary

The baseline workspace was prepared without vendor-copying the Android client and without changing `chatmail/core`.

A Delta Chat Android worktree was created at `worktrees/deltachat-android-corporate/` on branch `research/corporate-imap-messenger-baseline`. Upstream history is preserved. The local Android baseline builds successfully when run through an ASCII path workaround.

The next step is the Android IMAP Messenger MVP Blueprint, not rebrand or UI development.

## 2. What was prepared

Created documents:

- `docs/reports/2026-05-13/ANDROID_FORK_BASELINE_INVENTORY.report.md`
- `docs/blueprints/ANDROID_FORK_STRATEGY_DECISION.md`
- `docs/reports/2026-05-13/DELTACHAT_ANDROID_FORK_BUILD_BASELINE.report.md`
- `docs/reports/2026-05-13/OPEN_SOURCE_LICENSE_NOTES.report.md`
- `docs/hand_off/DELTACHAT_ANDROID_SAFE_CUSTOMIZATION_MAP.md`
- `docs/hand_off/ANDROID_IMAP_MESSENGER_MVP_BLUEPRINT_PROMPT_DRAFT.md`

Prepared local Android baseline:

- `worktrees/deltachat-android-corporate/`
- branch: `research/corporate-imap-messenger-baseline`

## 3. Fork/workspace strategy recommendation

Recommended strategy:

- do not use vendor copy;
- keep upstream discovery clones under `imap-messenger-research/upstream/`;
- use the local worktree for baseline and Blueprint preparation;
- create a real GitHub fork of `deltachat-android` when credentials/access are available;
- then set product `origin` to the owned fork and keep `upstream` as official Delta Chat Android;
- do not fork `chatmail/core` until there is a concrete need to change core behavior.

## 4. Local repository status

The root workspace is not a Git repository.

Local upstream repositories are clean:

- `imap-messenger-research/upstream/deltachat-android` at `be07043b474c8a73679a696f149658b5b904f217`
- `imap-messenger-research/upstream/core` at `0bb4c3d073e51f48d93ac041f72c313f4624bfc8`
- `imap-messenger-research/upstream/provider-db` at `2cba4b72f4c6e6417b83ba549aff7781be5f166c`
- `imap-messenger-research/upstream/deltachat-desktop` at `90f41321e98dd3c3948ae403fb25e92ad970d960`

Diagnostics APK remains separate at:

- `prototypes/android-diagnostics/`

## 5. Remote/upstream setup

Current worktree remotes:

- `origin=https://github.com/deltachat/deltachat-android.git`
- `upstream=https://github.com/deltachat/deltachat-android.git`

Important caveat:

- `origin` is not an owned product fork yet;
- no GitHub fork credentials were available in this environment;
- this is documented and must be corrected when the owned fork is created.

## 6. Branches created

Created branch:

- `research/corporate-imap-messenger-baseline`

The branch currently points to upstream Delta Chat Android HEAD:

- `be07043b474c8a73679a696f149658b5b904f217`

No rebrand, UI or transport changes were added to the branch.

## 7. Build baseline result

Build baseline:

- direct Windows build from the path with Cyrillic characters failed because Android Gradle Plugin rejects non-ASCII project paths;
- the same build succeeded through temporary `subst M:` ASCII path mapping;
- command: `.\gradlew.bat assembleDebug --no-daemon`;
- result: `BUILD SUCCESSFUL`;
- generated FOSS and Gplay debug APKs under `worktrees/deltachat-android-corporate/build/outputs/apk/`.

This is sufficient for the next Blueprint. The non-ASCII Windows path issue must be handled in developer setup instructions.

## 8. License notes summary

Observed:

- `deltachat-android`: README says GPLv3+; LICENSE contains GPLv3 text;
- `chatmail/core`: MPL-2.0;
- `provider-db`: MPL-2.0.

Engineering implication:

- a distributed modified Android client likely needs GPL source-distribution compliance;
- MPL components need file-level compliance;
- legal review is required before product distribution decisions.

## 9. Safe customization summary

Safer areas after Blueprint:

- onboarding;
- provider selection;
- provider hints;
- managed provider profiles;
- corporate domain allowlist;
- diagnostics entry point;
- address book import through existing APIs;
- UI-level restrictions;
- documentation.

High-risk areas:

- `chatmail/core`;
- JNI bridge;
- IMAP sync;
- SMTP queue/retry;
- database migrations;
- Autocrypt/SecureJoin/encryption;
- group protocol;
- notification internals;
- background service behavior before a separate background Blueprint.

## 10. Provider-agnostic decision

The product must not be Mail.ru-only.

Mail.ru is the first accepted transport baseline. The architecture must support:

- Mail.ru preset;
- VK Mail preset;
- Yandex preset;
- later Rambler/other presets;
- manual/custom IMAP/SMTP profiles;
- diagnostic status per provider/profile.

Whitelist-ready status must be evidence-based.

## 11. What was intentionally not done

Intentionally not done:

- no manual vendor copy;
- no full rebrand;
- no messenger UI development;
- no changes to `chatmail/core`;
- no JNI/sync/encryption/database migration changes;
- no IMAP/SMTP transport rewrite;
- no diagnostics APK migration into Delta Chat Android;
- no secrets, app passwords, real test emails or raw logs added;
- no production-ready claim.

## 12. Remaining risks

Remaining risks:

- no owned GitHub fork remote is configured yet;
- local `origin` still points to official Delta Chat Android;
- Windows non-ASCII workspace path breaks direct Gradle build;
- GPL/MPL obligations need legal review;
- thin fork vs wrapper vs custom shell is not decided yet;
- background reliability is unresolved and intentionally deferred.

## 13. Recommended next step

Create the next Blueprint:

```text
docs/blueprints/ANDROID_IMAP_MESSENGER_MVP_BLUEPRINT.md
```

Use the prompt draft:

```text
docs/hand_off/ANDROID_IMAP_MESSENGER_MVP_BLUEPRINT_PROMPT_DRAFT.md
```

The Blueprint should choose the product architecture route before any Delta Chat Android UI, branding or transport changes begin.
