# Open Source License Notes

Date: 2026-05-13

Scope: engineering notes for Delta Chat / Chatmail licensing in the Android IMAP/SMTP messenger baseline.

This is an engineering note, not legal advice.

## Observed licenses

`deltachat-android`:

- repository: `https://github.com/deltachat/deltachat-android.git`
- local path: `imap-messenger-research/upstream/deltachat-android`
- README states: `Licensed GPLv3+`
- `LICENSE` file contains GNU General Public License Version 3 text.

`chatmail/core`:

- repository: `https://github.com/chatmail/core.git`
- local path: `imap-messenger-research/upstream/core`
- `LICENSE` file states the files are released under Mozilla Public License Version 2.0.

`provider-db`:

- repository: `https://github.com/deltachat/provider-db.git`
- local path: `imap-messenger-research/upstream/provider-db`
- `LICENSE` file contains Mozilla Public License Version 2.0.

`deltachat-desktop`:

- repository: `https://github.com/deltachat/deltachat-desktop.git`
- local path: `imap-messenger-research/upstream/deltachat-desktop`
- not part of the Android fork baseline, included only as discovery material.

## Practical implications

If the project distributes a modified Android client derived from `deltachat-android`, GPL obligations likely apply. At minimum, expect source distribution, license notice, attribution and modification-marking obligations. Exact distribution model needs legal review.

MPL-2.0 components such as `chatmail/core` and `provider-db` have file-level copyleft obligations. A proprietary wrapper or product layer around MPL components may have a different compliance model than a modified GPL Android client, but this must be reviewed before product commitments.

The current baseline does not change `chatmail/core`, `provider-db` or Delta Chat Android source code. The build baseline only initialized the existing core submodule and compiled the Android app.

## Compliance rules for next stage

- Do not vendor-copy upstream code without preserving license files and history.
- Keep upstream remotes and commit hashes traceable.
- Keep modification boundaries clear.
- Do not remove license headers.
- Do not mix proprietary product assumptions into GPL client work without review.
- Treat any distributed modified APK as requiring source-distribution planning.

## Required legal review points

- whether the product is a modified GPL Android client, a wrapper, or a separate app using MPL core;
- whether app store/internal enterprise distribution changes obligations;
- attribution and source-offer format;
- handling of branding and trademark references;
- compatibility of any new dependencies;
- whether corporate managed configuration or closed provider presets create additional obligations.
