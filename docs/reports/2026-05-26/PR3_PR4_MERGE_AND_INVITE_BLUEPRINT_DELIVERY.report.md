# PR #3 / PR #4 Merge And Invite Blueprint Delivery Report

Date: 2026-05-26

Status: Draft delivery report

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Branch: `blueprint/invite-onboarding-distribution-mvp`

Invite Blueprint content commit: `738e0404bb2420a117f2b89f4415d4189c040228`

Invite Blueprint PR: `https://github.com/Kwentin3/messenger-imap/pull/5`

## 1. Executive Summary

PR #3 and PR #4 were reviewed for docs-only scope, artifact safety, and secret safety.

PR #3, Corporate Control Plane MVP Blueprint, was merged into `main`.

PR #4, Corporate Directory MVP Blueprint, was a stacked PR on top of PR #3. Its content was fast-forwarded into `main` after PR #3. GitHub could not retarget PR #4 to `main` because there were no remaining commits between `main` and the PR head. PR #4 was therefore closed with an explanatory comment; its content is present in `main`.

After the merge, the Invite Onboarding & Distribution MVP Blueprint was drafted on a new branch from updated `main`.

PR #5 was opened from `blueprint/invite-onboarding-distribution-mvp` to `main` and is mergeable at the time of this report update.

## 2. PR Review And Merge Summary

| Item | Result |
| --- | --- |
| PR #3 | Merged |
| PR #3 URL | `https://github.com/Kwentin3/messenger-imap/pull/3` |
| PR #3 branch | `blueprint/control-plane-mvp` |
| PR #3 merge commit | `417006eb794c129069b0b80c8ab0fef8515e6c5a` |
| PR #4 | Closed after content was fast-forwarded into `main` |
| PR #4 URL | `https://github.com/Kwentin3/messenger-imap/pull/4` |
| PR #4 branch | `blueprint/corporate-directory-mvp` |
| Main after PR #3/#4 consolidation | `937edaf00aee096411b977cba9e4a4095fab58e5` |
| Invite Blueprint branch | `blueprint/invite-onboarding-distribution-mvp` |
| Invite Blueprint commit | `738e0404bb2420a117f2b89f4415d4189c040228` |
| Invite Blueprint PR | `https://github.com/Kwentin3/messenger-imap/pull/5` |

## 3. Review Checks Performed

Reviewed PR diffs for:

- docs-only scope;
- absence of APK binaries;
- absence of build artifacts;
- absence of `.env`, private keys, keystores, raw logs, or upstream vendor copies;
- source-document consistency between Roadmap, Control Plane Blueprint, and Directory Blueprint;
- stacked PR dependency correctness.

The detected matches for words such as `token`, `secret`, `password`, and `app password` were documentation terms and security requirements, not real secrets.

## 4. Merge Handling

The merge path was:

```text
main
  -> blueprint/control-plane-mvp
      -> blueprint/corporate-directory-mvp
```

The local repository was updated and `main` was fast-forwarded through the two Blueprint branches.

After `main` reached the Directory Blueprint head, PR #4 had no remaining diff against `main`. Because GitHub does not mark this stacked case as merged when the base branch remains `blueprint/control-plane-mvp`, PR #4 was closed with a note that the content is already included in `main`.

No force push was used. No branch was deleted.

## 5. Invite Blueprint Created

Created:

- `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT.md`
- `docs/blueprints/INVITE_ONBOARDING_DISTRIBUTION_MVP_BLUEPRINT_REPORT.md`

The Blueprint covers:

- internal invite vs external invite;
- invite token and fallback code handling;
- join landing page contract;
- Android APK download and app handoff;
- email verification code flow;
- provider setup;
- diagnostics gate;
- membership activation;
- external relationship activation;
- first directory sync;
- Control Plane unavailable behavior;
- invite abuse controls;
- audit events;
- security and privacy requirements;
- MVP scope, later scope, boundaries, open questions, and acceptance criteria.

## 6. Source Documents Used

The Invite Blueprint uses these current project documents:

- `docs/roadmap/PROJECT_ROADMAP.md`
- `docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/product/domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md`
- `docs/product/PRD_ROOT_CORPORATE_IMAP_MESSENGER.md`
- `docs/product/PRODUCT_PRD_REVIEW_ADDENDUM.md`
- `docs/product/domains/PRD_CORPORATE_CONTROL_PLANE.md`
- `docs/product/domains/PRD_CORPORATE_DIRECTORY.md`
- `docs/product/domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md`
- `docs/product/domains/PRD_ANDROID_MESSENGER_CLIENT.md`
- `docs/product/domains/PRD_PROVIDER_TRANSPORT_PROFILES.md`
- `docs/product/domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md`
- `docs/product/decisions/PRODUCT_DECISIONS_LOG.md`
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`
- `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md`
- `docs/infrastructure/SERVER_AUDIT_REPORT.md`
- `docs/research/DELTACHAT_CAPABILITIES_FOR_CORPORATE_MESSENGER.report.md`
- `docs/hand_off/DELTACHAT_CORPORATE_FEATURE_MAP.md`

## 7. Decisions Preserved

The Blueprint preserves these accepted decisions:

- APK download does not equal membership.
- Invite token possession does not equal membership.
- Internal invite creates `Membership`.
- External invite creates `ExternalRelationship`.
- Email ownership verification is required.
- Provider diagnostics do not replace email verification.
- Control Plane is required for activation in MVP.
- Control Plane may be unavailable in whitelist/restricted mode.
- External contacts do not receive internal directory.
- APK-by-email is Android emergency fallback only.
- iOS is out of current scope.
- Signed IMAP/system-account control updates are later scope, not MVP default.

## 8. Files Updated

Updated:

- `docs/README.md`
- `docs/roadmap/PROJECT_ROADMAP.md`
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT.md`
- `docs/blueprints/CORPORATE_DIRECTORY_MVP_BLUEPRINT_REPORT.md`

The Roadmap now marks Control Plane and Directory as merged baselines and Invite Onboarding & Distribution as drafted.

The Directory Blueprint and report no longer state that PR #3 is still open.

The handoff now points the next work toward Invite review and Android Client Blueprint.

## 9. Safety Result

No code changes were made.

No deployment actions were performed.

No server, Traefik, Docker, Android prototype, Delta Chat, or chatmail/core files were changed.

No APK binaries, build artifacts, raw logs, `.env` files, SSH keys, private keys, keystores, or credentials were added.

## 10. Remaining Actions

Recommended next steps:

1. Review and accept the Invite Onboarding & Distribution MVP Blueprint.
2. Prepare Android Client MVP Blueprint.
3. Historical next step: decide or scope the thin Delta Chat Android fork vs custom shell over chatmail/core spike. Resolved for MVP on 2026-05-29: thin Delta Chat Android fork.
4. Keep Deployment Blueprint blocked until Control Plane stack assumptions are concrete.
5. Leave old branches in place until the owner explicitly approves cleanup.
