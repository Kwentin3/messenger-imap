# Upstream License Notes

Date: 2026-05-26

This note summarizes license/compliance considerations from the workspace research. It is not legal advice.

## Summary

| Upstream | Observed license note | Product implication |
|---|---|---|
| Delta Chat Android | GPLv3+ in local research notes / upstream license files | Modified Android distribution likely requires GPL source distribution compliance |
| chatmail/core | MPL-2.0 in local research notes / upstream license files | File-level MPL compliance must be preserved if reused or modified |
| provider-db | MPL-2.0 in local research notes / upstream license files | Preserve license and attribution if reused |
| Delta Chat Desktop | Upstream project reference only | Not imported |
| chatmail-relay | Upstream project reference only | Not imported |

## Bootstrap Decision

The initial repository import does not vendor-copy upstream source. It imports project documentation, sanitized evidence, and the standalone Android Diagnostics prototype source.

## Current Implementation Decision

The MVP Android implementation path is a thin fork of Delta Chat Android in a future separate repository:

```text
Kwentin3/messenger-imap-android
```

The current repository `Kwentin3/messenger-imap` remains product/meta/docs/control-plane coordination and must not vendor-copy the Android fork.

## Required Before Product Distribution

- Review GPL/MPL obligations.
- Define source publication path for modified GPL-covered Android binaries if a fork is distributed.
- Define attribution and notice handling.
- Keep APK signing keys and credentials out of Git.
