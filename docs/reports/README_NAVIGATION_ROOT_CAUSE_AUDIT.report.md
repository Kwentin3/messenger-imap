# README Navigation Root-Cause Audit Report

Date: 2026-05-29

Project: Corporate IMAP Messenger / messenger-imap

Scope: root README and documentation navigation visibility audit

## 1. Executive Summary

The APK link was not visible from the user-facing root README because the README/navigation changes were made in open branches and pull requests, not in `main`. In the Android fork, the first APK block also existed on the feature branch `feature/release-metadata-warning`, not on `main`.

There is a second repository-level visibility issue: the current default branch for `Kwentin3/messenger-imap` is `bootstrap/project-import`, not `main`. GitHub shows the default branch on the repository landing page. Therefore, even a merge to `main` will not automatically update the default landing page unless the default branch is changed to `main` or the same README update is landed into the default branch.

## 2. Current README State In `messenger-imap/main`

`messenger-imap/main` root `README.md` is stale:

- it does not contain `Download Android APK`;
- it does not link to the Android internal smoke release;
- it still says the implementation route should be decided;
- it does not reflect that thin fork Delta Chat Android has already been selected;
- it does not describe the current Android fork repository / APK smoke status.

`docs/README.md` exists in `main`, but it does not provide a top-level APK / Android fork quick link section.

`docs/reports/ANDROID_INTERNAL_SMOKE_APK_RELEASE.report.md` is not present in `main`.

`docs/reports/README_NAVIGATION_UPDATE.report.md` is not present in `main`.

## 3. Current README State In `messenger-imap-android/main`

`messenger-imap-android/main` root `README.md` is still the upstream Delta Chat Android README:

- it does not contain the Corporate IMAP Messenger Android Fork block;
- it does not contain `Internal Android APK`;
- it does not link to the Android internal smoke release;
- it preserves upstream Delta Chat description, contribution/building links, screenshots, translations, credits, and license information.

## 4. Branches / PRs Where README Changes Exist

Meta repository:

- [PR #14: Update root README navigation and APK links](https://github.com/Kwentin3/messenger-imap/pull/14)
  - head: `docs/root-readme-navigation`
  - base: `main`
  - status: open draft
  - contains the prior README/navigation update, but it is not merged.

- [PR #13: Document internal Android smoke APK release](https://github.com/Kwentin3/messenger-imap/pull/13)
  - head: `docs/android-internal-smoke-apk-release`
  - base: `android/autonomous-execution`
  - status: open
  - contains the internal smoke release report and Android execution documents, but it is stacked and not merged into `main`.

Android repository:

- [PR #2: Add internal smoke APK link to root README](https://github.com/Kwentin3/messenger-imap-android/pull/2)
  - head: `docs/root-readme-apk-link`
  - base: `main`
  - status: open draft
  - contains the Android README APK block, but it is not merged.

- [PR #1: Add Android corporate MVP placeholders](https://github.com/Kwentin3/messenger-imap-android/pull/1)
  - head: `feature/release-metadata-warning`
  - base: `main`
  - status: open draft
  - contains the first Android README internal APK block, but it is not merged.

## 5. Why The User Does Not See APK Link

The user does not see the APK link because the visible README is read from `main` or from the repository default branch, while the APK-link README changes are only present in open branches / pull requests.

For `messenger-imap`, the situation is compounded by the default branch being `bootstrap/project-import`, not `main`. The root repository page will keep showing that default branch until the branch setting changes or the update is landed there.

For `messenger-imap-android`, `main` still has the upstream README. The project-specific APK block exists in `feature/release-metadata-warning` and `docs/root-readme-apk-link`, but neither branch is merged.

## 6. What Needs To Be Corrected

- Create a focused meta repository fix branch from `main`: `docs/root-readme-navigation-fix`.
- Update root `README.md` in that branch with a prominent `Download Android APK` section near the top.
- Update `docs/README.md` with a `Quick Links` section.
- Add this root-cause audit report and an updated README navigation report.
- Create a focused Android repository fix branch from `main`: `docs/root-readme-apk-link-fix`.
- Add the Corporate IMAP Messenger Android Fork / Internal Android APK block at the top of the Android README without removing upstream attribution, credits, or license content.
- Open pull requests to `main`.
- After review, merge the PRs.
- For the meta repository landing page, either change the default branch to `main` or land the README update into the current default branch `bootstrap/project-import`.

## 7. Safety Checks

Audit constraints:

- no code changes;
- no Android source changes;
- no build file changes;
- no package ID changes;
- no signing config changes;
- no server or Traefik changes;
- no APK/AAB/build artifacts committed;
- no `.env`;
- no secrets;
- no signing keys.

## 8. Recommended Correction Plan

1. Land the meta README/navigation fix into `main`.
2. Land the Android README APK-link fix into `main`.
3. Update the repository default branch for `Kwentin3/messenger-imap` to `main`, or separately land the same root README update into `bootstrap/project-import`.
4. Keep APK binaries only in GitHub Releases.
5. Run the Android runtime smoke checklist on a real device before claiming runtime verification.
