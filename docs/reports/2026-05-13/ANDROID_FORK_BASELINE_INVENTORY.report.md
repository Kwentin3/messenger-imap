# Android Fork Baseline Inventory

Date: 2026-05-13

Scope: inventory of the local workspace before Android IMAP/SMTP messenger fork planning.

## Executive summary

The workspace contains the standalone Android diagnostics prototype, research/blueprint/report documents, and local upstream discovery clones for Delta Chat / Chatmail repositories.

The root workspace is not a Git repository. The upstream repositories under `imap-messenger-research/upstream/` are separate Git repositories. They are clean at inventory time.

## Workspace directories

- Diagnostics APK: `prototypes/android-diagnostics/`
- Reports: `docs/reports/`
- Research docs and JSON evidence: `docs/research/`
- Blueprints: `docs/blueprints/`
- Handoff docs: `docs/hand_off/`
- Upstream discovery: `imap-messenger-research/upstream/`
- Android worktree baseline: `worktrees/deltachat-android-corporate/`

Closure report:

- `docs/reports/2026-05-13/ANDROID_DIAGNOSTICS_MVP0A_CLOSURE.report.md`

## Local upstream repositories

| Path | Branch | HEAD | Remote(s) | Local changes |
|---|---:|---:|---|---|
| `imap-messenger-research/upstream/deltachat-android` | `main` | `be07043b474c8a73679a696f149658b5b904f217` | `origin=https://github.com/deltachat/deltachat-android.git`; `upstream=https://github.com/deltachat/deltachat-android.git` | clean |
| `imap-messenger-research/upstream/core` | `main` | `0bb4c3d073e51f48d93ac041f72c313f4624bfc8` | `origin=https://github.com/chatmail/core.git` | clean |
| `imap-messenger-research/upstream/provider-db` | `master` | `2cba4b72f4c6e6417b83ba549aff7781be5f166c` | `origin=https://github.com/deltachat/provider-db.git` | clean |
| `imap-messenger-research/upstream/deltachat-desktop` | `main` | `90f41321e98dd3c3948ae403fb25e92ad970d960` | `origin=https://github.com/deltachat/deltachat-desktop.git` | clean |
| `imap-messenger-research/upstream/chatmail-relay` | `main` | `ed664cd9cd3e` | `origin=https://github.com/chatmail/relay.git` | clean |

## Android worktree baseline

Created worktree:

- path: `worktrees/deltachat-android-corporate/`
- source repository: `imap-messenger-research/upstream/deltachat-android`
- branch: `research/corporate-imap-messenger-baseline`
- HEAD: `be07043b474c8a73679a696f149658b5b904f217`
- submodule: `jni/deltachat-core-rust` at `dab7ca19fec91fe2462cb70eb52ec407343b4b2d`

Current remotes in the worktree:

- `origin=https://github.com/deltachat/deltachat-android.git`
- `upstream=https://github.com/deltachat/deltachat-android.git`

No product GitHub fork remote is configured yet. `origin` still points to the official repository because no owned GitHub fork was available in this environment.

## Notes

- No manual vendor copy was created.
- `chatmail/core` upstream was not changed.
- Delta Chat Android source files were not customized.
- Build artifacts may exist under ignored `build/` directories after the build baseline run.
