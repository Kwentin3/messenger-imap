# Security Policy

This project handles product requirements and prototypes for IMAP/SMTP-based messaging. Credentials and diagnostic evidence must be treated conservatively.

## Do Not Commit

- app passwords;
- OAuth tokens;
- raw AUTH payloads;
- raw IMAP/SMTP protocol transcripts;
- raw logcat dumps;
- real unredacted email local-parts in evidence files;
- APK signing keys;
- keystores;
- local account databases;
- provider credentials.

## Diagnostic Evidence

Diagnostic reports must be sanitized. Evidence JSON may include provider name, network context, masked emails, generated Message-ID values under non-real domains, stage timings, and result status.

Reports must not include raw authentication commands, passwords, raw logs, or message payloads.

## Reporting Issues

For now, report security concerns directly to the repository owner. Before sharing logs or diagnostic files, remove credentials, raw AUTH data, full email addresses, and raw logcat output.
