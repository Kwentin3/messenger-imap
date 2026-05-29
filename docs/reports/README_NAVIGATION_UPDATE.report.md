# README Navigation Update Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Status: Prepared as docs-only fix

## 1. Root-Cause Summary

The APK link was not visible in the user-facing root README because the README changes existed only in open branches / PRs, not in `main`. The Android fork README APK block also existed only in non-main branches.

The meta repository has an additional landing-page issue: its current default branch is `bootstrap/project-import`, not `main`. GitHub repository visitors see the default branch by default, so a merge to `main` must be paired with a default-branch change or an equivalent update to the default branch if the root page must update immediately.

Local workspace correction: the README fix was prepared in temporary worktrees and pushed correctly, but the main local checkout was left on `docs/android-internal-smoke-apk-release`, so its local README still looked stale. The active meta checkout is now `d:\Users\Roman\Desktop\Проекты\messenger-imap` on `docs/root-readme-navigation-fix`; the active Android checkout is `C:\work\messenger-imap-android` on `docs/root-readme-apk-link-fix`.

## 2. What Was Changed In `messenger-imap`

- Root `README.md` was rewritten as the primary project entry point.
- A prominent `Download Android APK` section was added near the top.
- Repository roles were clarified.
- Current status was updated so it no longer says the fork/shell route is still open.
- Runtime smoke checklist was added.
- Safety / do-not-commit rules were added.
- `docs/README.md` received a `Quick Links` section.
- Root-cause and update reports were added under `docs/reports/`.

## 3. What Was Changed In `messenger-imap-android`

The Android repository fix branch adds a project-specific block at the top of root `README.md`:

- Corporate IMAP Messenger Android Fork title;
- Internal Android APK section;
- Android internal smoke release link;
- recommended FOSS debug APK name;
- SHA-256;
- runtime smoke status;
- build commit;
- related repository links.

The original upstream Delta Chat README content remains below the project block, including description, contribution/building links, screenshots, translations, credits, and license information.

## 4. APK Release Link

- [Android Internal Smoke APK 0.1.0](https://github.com/Kwentin3/messenger-imap-android/releases/tag/android-internal-smoke-0.1.0)
- Recommended APK: `messenger-imap-android-foss-debug-2.50.0.apk`
- SHA-256: `E50768D6DB2D1B26A76FB53A37D16ADD374E76DA544B9D2C8408B500AB320410`
- Status: internal debug build, runtime smoke pending

## 5. README Links Added

Meta root README:

- Android internal smoke release;
- main project / documentation repo;
- Android fork repo;
- Project Roadmap;
- Corporate Control Plane MVP Blueprint;
- Corporate Directory MVP Blueprint;
- Invite Onboarding & Distribution MVP Blueprint;
- Infrastructure Assumptions;
- Server Audit Report;
- Product Decisions Log.

Meta `docs/README.md`:

- Android internal smoke release;
- Android fork repository;
- Project Roadmap;
- Control Plane Blueprint;
- Directory Blueprint;
- Invite Blueprint;
- Infrastructure Assumptions;
- Server Audit Report;
- Product Decisions Log.

Android root README:

- Android internal smoke release;
- main project / documentation repo;
- Android fork repo;
- recommended APK name, SHA-256, runtime status, and build commit.

## 6. Links Verified

Relative markdown links were checked locally in:

- `README.md` in `d:\Users\Roman\Desktop\Проекты\messenger-imap` on branch `docs/root-readme-navigation-fix`;
- `docs/README.md` in `d:\Users\Roman\Desktop\Проекты\messenger-imap` on branch `docs/root-readme-navigation-fix`;
- `README.md` in `C:\work\messenger-imap-android` on branch `docs/root-readme-apk-link-fix`.

The GitHub release link was also checked through GitHub release metadata.

## 7. Documents Not Linked Because Missing From Main

These documents were not linked from root README because they are not present in the current `origin/main` baseline of the meta repository:

- `docs/roadmap/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_ROADMAP.md`
- `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md`
- `docs/blueprints/ANDROID_FORK_INTAKE_BLUEPRINT.md`
- `docs/blueprints/ANDROID_CLIENT_MVP_BLUEPRINT.md`
- `docs/reports/ANDROID_INTERNAL_SMOKE_APK_RELEASE.report.md`
- `docs/reports/ANDROID_MESSENGER_AUTONOMOUS_EXECUTION_AUDIT.report.md`
- `docs/reports/PROJECT_PRE_IMPLEMENTATION_ANAMNESIS_AND_READINESS_AUDIT.report.md`

## 8. PRs Created

- Meta repository PR: [Fix root README navigation and APK links](https://github.com/Kwentin3/messenger-imap/pull/15)
- Android repository PR: [Add internal smoke APK link to root README](https://github.com/Kwentin3/messenger-imap-android/pull/3)

## 9. Confirmation: APK Not Committed To Git

No APK, AAB, build output, `.env`, secret, signing key, or keystore was added to either repository. APK binaries remain GitHub Release assets only.

## 10. Remaining Actions

- Merge the meta README/navigation fix PR.
- Merge the Android README APK-link fix PR.
- Change the meta repository default branch to `main` or land the same README update into `bootstrap/project-import`.
- Run runtime smoke on a real Android device.
