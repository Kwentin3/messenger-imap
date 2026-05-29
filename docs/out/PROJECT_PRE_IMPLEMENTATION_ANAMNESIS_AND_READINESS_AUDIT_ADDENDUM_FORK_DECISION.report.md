# Pre-Implementation Readiness Audit Addendum: Fork Strategy Decision

Date: 2026-05-29

Status: Addendum

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

Related decision: `docs/decisions/IMPLEMENTATION_FORK_STRATEGY_DECISION.md`

## 1. Context

The pre-implementation anamnesis/readiness audit was created in PR #8 and is not part of `main` at the time of this addendum.

This addendum records the management decision made after that audit:

```text
MVP Android implementation path: thin fork of Delta Chat Android.
```

## 2. Readiness Impact

The fork-vs-shell blocker is resolved for MVP.

Remaining blockers still apply:

- GPL/MPL compliance and source distribution path;
- Android fork repository creation and intake plan;
- package ID, app name, branding, signing, and release identity;
- Control Plane backend stack confirmation;
- database choice and isolation plan;
- Deployment Blueprint;
- APK signing and release storage;
- stale/expired thresholds;
- email verification provider;
- Provider / Diagnostics Blueprint;
- implementation slices and test plans.

## 3. Updated Matrix Notes

| Domain | Updated status |
| --- | --- |
| Android Client | Fork path selected: thin Delta Chat Android fork. Android Client Blueprint PR #6 still requires review/update under this assumption. |
| Licensing / Fork strategy | Fork strategy resolved for MVP; GPL/MPL compliance remains a blocker before APK distribution. |
| Release / APK signing | Still blocked on signing key custody, release storage, and source distribution workflow. |
| Control Plane | Working backend hypothesis is Node.js / TypeScript + PostgreSQL; final implementation lock remains for implementation planning. |

## 4. Recommended Next Steps

1. Review/update Android Client MVP Blueprint under the thin-fork assumption.
2. Create Android fork intake plan for `Kwentin3/messenger-imap-android`.
3. Keep first Android fork slice build-only and reproducibility-focused.
4. Create Provider / Diagnostics Blueprint after Android Client Blueprint is aligned.
5. Keep deployment, release APK distribution, and signing pipeline blocked until their Blueprints/decisions exist.

## 5. Updated Verdict

The readiness verdict remains:

```text
READY_WITH_BLOCKERS
```

Reason: the largest Android architecture fork-choice blocker is resolved, but implementation planning still depends on compliance, fork intake, Control Plane stack confirmation, diagnostics policy, deployment, release/signing, and test plans.
