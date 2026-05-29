# Implementation Fork Strategy Decision

Date: 2026-05-29

Status: Accepted

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Decision owner: project owner

Scope: implementation strategy decision

## 1. Decision Summary

The MVP Android implementation path is a **thin fork of Delta Chat Android**.

Rejected for MVP:

- custom Android shell over `chatmail/core`;
- writing an Android messenger from scratch;
- modifying `chatmail/core` as the first implementation step.

This decision does not approve a hard fork, deep upstream rewrite, rebrand, release build, APK distribution, server deployment, or `chatmail/core` change.

## 2. Rationale

Thin fork is the fastest credible path to MVP because Delta Chat Android already provides:

- an existing Android client;
- IMAP/SMTP account setup and transport behavior through the existing Delta Chat / Chatmail stack;
- one-to-one chat primitives;
- contact primitives;
- group and broadcast primitives;
- attachment handling;
- voice-message/audio-message primitives;
- Android build, local storage, notification, and connectivity surfaces.

Using the existing Android app reduces product risk and lets the project focus on the corporate product layer:

- Corporate Directory;
- invite onboarding;
- provider profiles;
- diagnostics/check-transport flow;
- Control Plane integration;
- external contact distinctions;
- release metadata and APK distribution policy.

Custom shell over `chatmail/core` remains a possible later route only if the thin fork becomes technically or compliance-wise unworkable.

## 3. Architecture Implications

- Android fork work belongs in a separate Android repository.
- Control Plane remains a separate backend/web/admin layer.
- The current `messenger-imap` repository remains the product/meta/docs/control-plane coordination repository.
- Corporate features should be implemented as thin, isolated Android app/product-layer modules, adapters, and integration boundaries where possible.
- Avoid deep upstream rewrites.
- Avoid modifying `chatmail/core`, JNI, sync, encryption, MIME, queueing, or database migrations unless a later Blueprint explicitly justifies the change.
- Keep provider-agnostic architecture.
- Do not hardcode Mail.ru-only flows. Mail.ru / VK Mail remains the first accepted baseline, not the product boundary.

## 4. Repository Strategy

Future Android fork repository:

```text
Kwentin3/messenger-imap-android
```

Current product/meta repository:

```text
Kwentin3/messenger-imap
```

Repository rules:

- Do not vendor-copy Delta Chat Android into `Kwentin3/messenger-imap`.
- Use a proper fork/upstream relationship for the Android client.
- Track upstream Delta Chat Android remotes and commit references.
- Preserve upstream license notices and attribution.
- Keep product documentation, Control Plane planning, and coordination in `Kwentin3/messenger-imap`.
- Keep Android fork source, Android build configuration, and Android client changes in `Kwentin3/messenger-imap-android`.

Fork repository visibility:

- Public repository is acceptable if the owner confirms.
- Private until first release is acceptable if the owner chooses.
- Regardless of visibility, GPL compliance must be planned before APK distribution.

## 5. Compliance Implications

Project notes identify Delta Chat Android as GPL-lineage / GPLv3+ and `chatmail/core` / `provider-db` as MPL-2.0.

Working compliance decision:

- If the project distributes a modified APK derived from Delta Chat Android, the project is prepared to satisfy GPL obligations.
- Modified source distribution path must be planned before release.
- License notices and upstream attribution must be preserved.
- Corresponding source must be published or offered when required.
- Legal review remains recommended before public or commercial distribution.
- APK signing, release, and distribution flow must not proceed without a compliance path.

This is an engineering decision record, not legal advice.

## 6. Initial Android Fork Intake Sequence

First safe Android fork intake steps:

1. Create or fork the Android repository as `Kwentin3/messenger-imap-android`.
2. Record the official Delta Chat Android upstream remote.
3. Build upstream/fork cleanly from a known commit.
4. Document Gradle, Android SDK, JDK, NDK, Rust, and path requirements.
5. Produce local debug build only.
6. Record build command, commit, environment, warnings, and generated artifact path in a report.

Explicit non-goals for the first intake slice:

- no package ID rename;
- no app name rename;
- no branding/icon changes;
- no Control Plane integration;
- no invite/deep-link integration;
- no provider profile integration;
- no diagnostics integration;
- no `chatmail/core` changes;
- no release APK distribution;
- no APK signing pipeline;
- no APK committed to git.

## 7. Later Android Fork Slices

Later slices may include:

- package ID, app name, and branding;
- invite deep link / app link handling;
- fallback invite code entry;
- provider profile policy integration;
- read-only Corporate Directory sync;
- stale directory UX;
- diagnostics / Check Connection integration;
- release metadata / update warning flow;
- external contact badges and warnings;
- managed group roster warnings;
- support-safe diagnostics export;
- CI/build pipeline.

Each slice should have a narrow implementation plan, tests, and rollback/containment notes.

## 8. Backend Stack Working Assumption

Control Plane working hypothesis for future implementation planning:

```text
Node.js / TypeScript + PostgreSQL
```

This is a working hypothesis, not a final implementation lock.

Rationale:

- fast MVP development;
- good web/admin/API fit;
- good JSON/domain API ergonomics;
- good Docker/Traefik fit;
- straightforward PostgreSQL integration.

Database rule remains:

- Control Plane should use its own database/container/volume by default;
- do not reuse existing `postgres-dev` without an explicit architecture and data-isolation decision.

## 9. Open Questions

- Public vs private Android fork repository.
- Exact package ID.
- App name and branding.
- Signing key custody.
- GPL source publication workflow.
- Upstream merge strategy.
- How to isolate corporate modules inside the Android fork.
- How much existing Delta Chat UX to keep.
- Whether to use Android system contacts permission in MVP.
- CI/build pipeline.
- Exact Control Plane backend stack confirmation.

## 10. Acceptance Criteria

This decision is accepted because:

- docs clearly say MVP uses a thin fork of Delta Chat Android;
- custom shell over `chatmail/core` is rejected for MVP;
- `chatmail/core` changes are not the first step;
- Android fork repository strategy is clear;
- current repository role is clear;
- GPL/source distribution implications are not ignored;
- first Android fork intake is build-only and reproducibility-focused.
