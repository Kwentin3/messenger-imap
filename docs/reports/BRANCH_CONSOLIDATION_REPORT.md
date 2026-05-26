# Branch Consolidation Report

Date: 2026-05-26

Status: prepared during documentation refinement and branch consolidation

Repository: `https://github.com/Kwentin3/messenger-imap`

Working branch: `product/prd-refine-control-plane-directory-trust`

## 1. Executive Summary

The repository has three relevant branches:

- `main`;
- `bootstrap/project-import`;
- `product/prd-refine-control-plane-directory-trust`.

`main` was created from the original bootstrap import commit. The latest PRD, infrastructure assumptions, and server audit documentation are in `product/prd-refine-control-plane-directory-trust`.

At the time of audit, `origin/main` was an ancestor of `product/prd-refine-control-plane-directory-trust`, so fast-forward merge into `main` was safe. `bootstrap/project-import` contains a separate detailed bootstrap report commit and is not the latest documentation branch.

No branch deletion is performed by this task.

## 2. Branches Before Work

Observed with `git fetch --all --prune`, `git branch -a`, and `git log --oneline --graph --decorate --all --max-count=30`.

| Branch | Commit before this refine | Notes |
| --- | --- | --- |
| `origin/main` | `eb069a30b9b0edc062d0b85c016cafb701b4cd05` | Bootstrap import baseline |
| `origin/bootstrap/project-import` | `4a0222579e2977793284cd260cf7562629ff8f31` | Bootstrap branch plus detailed bootstrap report |
| `origin/product/prd-refine-control-plane-directory-trust` | `9da7e788050202df90ed476b4a79b6899d22f2f5` | Latest PRD/infrastructure docs before this final refine |
| local `bootstrap/project-import` | `4a0222579e2977793284cd260cf7562629ff8f31` | Tracks bootstrap state locally |
| local `product/prd-refine-control-plane-directory-trust` | `9da7e788050202df90ed476b4a79b6899d22f2f5` | Working branch before this final refine |

Observed `origin/HEAD` before consolidation:

```text
origin/HEAD -> origin/bootstrap/project-import
```

This suggests the GitHub default branch may still point to `bootstrap/project-import` even though `main` exists. Owner should verify in GitHub UI after consolidation.

## 3. Documentation Refinements Performed

Infrastructure documents refined:

- `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md`;
- `docs/infrastructure/SERVER_AUDIT_REPORT.md`.

Added or strengthened rules:

- `/opt/stacks/messenger-imap` is a candidate deployment path, not an approved path.
- `traefik-net` is a candidate Docker network, not automatically approved.
- Deployment Blueprint must explicitly approve path, network, stack name, backup, and rollback plan before deployment.
- Future `messenger-imap` Control Plane should use its own database/container/volume by default.
- Existing `postgres-dev` must not be reused without explicit architecture and data-isolation decision.
- Database must not expose public host ports by default.
- Web/API/database direct host port exposure requires explicit Deployment Blueprint justification.
- APK signing key must not be stored on the deploy host by default.
- APK signing pipeline remains TBD and signing secrets must not be stored in repo, server docs, `.env`, or compose files.
- MVP APK storage recommendation: Control Plane stores release metadata and points to GitHub Releases or controlled backend/object storage.
- Server audit is a point-in-time snapshot.
- Existing containers are treated as production-like unless owner says otherwise.
- Deployment requires separate Deployment Blueprint plus backup/rollback plan.

Link updates verified in:

- `docs/README.md`;
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`;
- `docs/product/PRODUCT_PRD_PACKAGE_REPORT.md`.

## 4. Commits Created

Existing commits on the product branch before this final refinement:

| Commit | Message |
| --- | --- |
| `6307a370be6aaebb13df35d520294260c09c6190` | `Refine PRD package for control plane stale mode and trust model` |
| `fd31b1a0f463a94a0fbdec556a43e35b33bc7d2b` | `Add infrastructure assumptions for messenger-imap deployment context` |
| `9da7e788050202df90ed476b4a79b6899d22f2f5` | `Add read-only server audit report` |

This report and the final infrastructure refinements are committed in the branch commit whose exact hash is recorded by `git log` and in the task final response.

## 5. Merge Strategy

Required strategy:

```text
git checkout main
git pull origin main
git merge --ff-only product/prd-refine-control-plane-directory-trust
git push origin main
```

Fast-forward check before final commit:

```text
origin/main is ancestor of product/prd-refine-control-plane-directory-trust: yes
```

If fast-forward fails during final consolidation, do not force-push and do not create a merge commit without explicit approval. Use the existing PR path instead.

## 6. Main Branch Status

Before consolidation:

```text
origin/main = eb069a30b9b0edc062d0b85c016cafb701b4cd05
```

Expected after successful fast-forward:

```text
origin/main = product/prd-refine-control-plane-directory-trust HEAD
```

The final main commit hash is recorded in the task final response after push.

## 7. Default Branch Status

Observed before consolidation:

```text
origin/HEAD -> origin/bootstrap/project-import
```

Owner should verify GitHub default branch after main is updated:

```text
Settings -> Branches -> Default branch -> main
```

This task does not change GitHub default branch settings.

## 8. Branches Recommended For Later Deletion

Do not delete branches automatically.

Recommended later cleanup after owner confirmation:

| Branch | Recommendation |
| --- | --- |
| `bootstrap/project-import` | Can be deleted after verifying `main` contains the bootstrap import and all required bootstrap docs. Note: this branch contains `BOOTSTRAP_IMPORT_DETAILED.report.md`; if that report is still needed, import or recreate it on `main` before deletion. |
| `product/prd-refine-control-plane-directory-trust` | Can be deleted after merge into `main`, verification that docs are present, and owner confirmation. |

## 9. Files Changed

Expected changed files for this final refine:

- `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md`;
- `docs/infrastructure/SERVER_AUDIT_REPORT.md`;
- `docs/reports/BRANCH_CONSOLIDATION_REPORT.md`;
- `docs/README.md`, only if link maintenance is needed;
- `docs/product/handoff/PRODUCT_CONTEXT_HANDOFF.md`, only if link/status maintenance is needed;
- `docs/product/PRODUCT_PRD_PACKAGE_REPORT.md`, only if link/status maintenance is needed.

No code, Android prototype, Gradle, upstream, APK, or build artifact files should change.

## 10. Secret / Artifact Scan Result

Required checks:

- `git status`;
- `git diff --name-only`;
- broad secret-pattern scan over `docs/`;
- targeted staged scan for private key markers, raw AUTH payloads, GitHub/OpenAI-style tokens, provider email addresses, and basic-auth hashes;
- `git diff --cached --name-only`.

Expected broad-scan matches are documentation terms only: secret policy, password policy, token terminology, and no-secret guidance.

Forbidden content:

- no private keys;
- no SSH keys;
- no real `.env`;
- no app passwords;
- no mail provider passwords;
- no raw AUTH payloads;
- no raw logcat;
- no APK/build artifacts;
- no upstream/worktree vendor import.

Final staged scan result is recorded in the task final response.

## 11. Remaining Actions For Owner

- Verify the GitHub default branch is `main`.
- Decide whether to close or merge PR #1 if it remains open after direct fast-forward.
- After verifying `main`, decide whether to delete `bootstrap/project-import`.
- After verifying `main`, decide whether to delete `product/prd-refine-control-plane-directory-trust`.
- Decide whether `BOOTSTRAP_IMPORT_DETAILED.report.md` from `bootstrap/project-import` should be copied to `main` before branch cleanup.
- Use `docs/infrastructure/SERVER_AUDIT_REPORT.md` and `docs/infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md` as inputs for `docs/blueprints/DEPLOYMENT_BLUEPRINT.md`.
