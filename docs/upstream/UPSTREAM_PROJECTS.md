# Upstream Projects

Date: 2026-05-26

This repository documents upstream projects used for research and planning. It does not vendor-copy these projects.

| Project | URL | Local workspace role | Current handling |
|---|---|---|---|
| Delta Chat Android | `https://github.com/deltachat/deltachat-android.git` | Selected MVP Android thin-fork baseline | Not copied into this repo; future fork repo is `Kwentin3/messenger-imap-android` |
| chatmail/core | `https://github.com/chatmail/core.git` | Core transport and chat capability reference | Not copied; do not modify unless justified by Blueprint |
| provider-db | `https://github.com/deltachat/provider-db.git` | Provider profile and compatibility reference | Not copied; document relevant provider facts only |
| Delta Chat Desktop | `https://github.com/deltachat/deltachat-desktop.git` | Desktop/client capability reference | Not copied |
| chatmail-relay | `https://github.com/chatmail/relay.git` | Chatmail relay/backend reference | Not copied |

## Policy

- Do not import local upstream clones into this repository.
- Do not commit upstream `.git` directories.
- Do not commit local git worktrees.
- If source reuse becomes necessary, make an explicit architecture and license decision first.
- MVP Android source reuse decision is now explicit: thin Delta Chat Android fork in a separate Android repo.
- Prefer upstream URLs, commit references, and concise research notes over vendor copies.

## Current Fork Status

The workspace contained a local Delta Chat Android worktree for baseline planning, but no product changes were observed there during bootstrap. The project repository import is documentation and diagnostics-prototype focused, not an Android fork import.

Current decision: MVP uses a thin Delta Chat Android fork. The future Android fork should live in `Kwentin3/messenger-imap-android` with the official Delta Chat Android repository preserved as upstream. Do not vendor-copy Delta Chat Android into `Kwentin3/messenger-imap`.
