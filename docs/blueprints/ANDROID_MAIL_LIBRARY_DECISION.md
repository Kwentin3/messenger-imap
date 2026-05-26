# Android Mail Protocol Implementation Decision

Date: 2026-05-13  
Status: Accepted for MVP-0a  
Applies to: `prototypes/android-diagnostics`

## Decision

Use a minimal manual protocol implementation over `SSLSocket` for MVP-0a.

The diagnostics APK is not a mail client. It needs controlled foreground probes, explicit timeout handling, TLS metadata, redacted errors, no secret logging, and a narrow command set. A manual implementation gives the most direct control over DNS/TCP/TLS/IMAP/SMTP steps and keeps the APK dependency-free.

## Options Compared

| Option | Android compatibility | License | Maintained status | IMAP 993 | SMTP 465 | IMAP IDLE | Message-ID search | Timeout control | TLS metadata | Secret logging risk | Dependency size | Complexity | Verdict |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| Jakarta Mail / Angus Mail | Official Angus Android notes say API 19+ is supported | EPL 2.0 / mixed source headers | Active Eclipse EE4J project | Yes | Yes | Yes via IMAP provider APIs | Yes | Good at mail API level | Possible but less direct | Usually not by default, but debug/session logging must be controlled | Medium | Low/medium | Good later, but larger abstraction than MVP-0a needs |
| Apache Commons Net | Java library, Android use needs verification per build | Apache-2.0 | Active Apache project | Low-level IMAP client exists | SMTP(S) exists | Not as ergonomic as mail-specific API | Manual commands possible | Good | Direct with socket factory work | Low if caller controls logging | Small/medium | Medium | Viable, but still leaves much protocol work manual |
| Manual protocol over `SSLSocket` | Native Java/Android APIs | No new dependency | App-owned code | Yes, narrow command subset | Yes, narrow command subset | Yes, send `IDLE` / `DONE` | Yes, `SEARCH HEADER Message-ID` | Full control | Full control via `SSLSession` | Lowest if no transcript logging is implemented | None | Medium/high | Chosen for MVP-0a |
| K-9/Thunderbird Android mail stack | Android proven | Open-source, license review required | Active ecosystem | Yes | Yes | Yes | Yes | Good | likely possible | Larger integration risk | Large | High | Too broad for diagnostics APK |

## Rationale

MVP-0a must classify each transport stage separately: DNS, TCP, TLS, IMAP greeting, login, `SELECT INBOX`, IDLE enter/exit, SMTP greeting, EHLO, AUTH, send, receive correlation and Spam/Junk placement. General mail libraries are optimized for mail-client workflows, not step-by-step whitelist diagnostics.

Manual protocol implementation keeps these properties explicit:

- no raw protocol transcript persistence;
- no dependency debug logging;
- direct timeout boundaries for each step;
- direct `SSLSession` certificate/protocol/cipher extraction;
- simple redaction model;
- smaller APK and review surface.

## Implementation Constraints

- Use `SSLSocket` only for TLS endpoints in MVP-0a.
- Do not log command payloads.
- Never log or export `AUTH`, base64 auth strings, raw `LOGIN`, app passwords or full protocol transcript.
- Store only step name, status, latency, endpoint, TLS metadata and redacted error category/message.
- Keep the command set narrow:
  - IMAP: greeting, `LOGIN`, `SELECT`, `CAPABILITY`, `IDLE`/`DONE`, `SEARCH HEADER Message-ID`, `LIST`, `LOGOUT`.
  - SMTP: greeting, EHLO, `AUTH PLAIN`, MAIL FROM, RCPT TO, DATA, QUIT.

## Sources Checked

- Eclipse Angus Mail Android page: `https://eclipse-ee4j.github.io/angus-mail/Android`
- Eclipse Angus Mail project: `https://github.com/eclipse-ee4j/angus-mail`
- Apache Commons Net project: `https://github.com/apache/commons-net`
- Apache Commons Net IMAPClient API docs: `https://commons.apache.org/proper/commons-net/javadocs/api-3.6/org/apache/commons/net/imap/IMAPClient.html`

## Revisit Criteria

Reconsider Angus Mail or another maintained mail library if:

- manual protocol handling becomes broader than MVP-0a;
- provider-specific IMAP parsing becomes unreliable;
- MIME handling becomes necessary;
- OAuth/provider-specific auth paths are required;
- background sync/messenger functionality enters scope.
