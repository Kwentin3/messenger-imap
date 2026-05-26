# Infrastructure Assumptions

Date: 2026-05-26

Status: Draft

Scope: infrastructure assumptions, not deployment runbook

Project: `messenger-imap`

Repository: `https://github.com/Kwentin3/messenger-imap`

## 1. Executive Summary

The `messenger-imap` project already has an initial infrastructure context for future Control Plane, landing page, invite onboarding, APK distribution, backend/API, and admin portal work.

Known current state:

- the public domain is `messenger-imap.speechbattle.com`;
- the domain resolves to public IP `146.19.211.30`;
- the actual deploy host is reachable on the LAN/internal address `192.168.7.64`;
- SSH access is expected through `ssh roman@192.168.7.64`;
- SSH is key-based/passwordless;
- Traefik already exists on the server;
- other services already exist on the server and must not be disrupted.

This document does not deploy anything. It records assumptions, constraints, safety rules, and open questions for future Blueprint and deploy runbook work.

## 2. Known Infrastructure Facts

| Item | Value | Notes |
| --- | --- | --- |
| Public domain | `messenger-imap.speechbattle.com` | Target public hostname |
| Public IP | `146.19.211.30` | DNS resolves here |
| Deploy host | `192.168.7.64` | Internal LAN host |
| SSH user | `roman` | Key-based/passwordless |
| SSH command | `ssh roman@192.168.7.64` | Do not store private keys |
| Reverse proxy | Traefik | Already installed |
| Existing services | present | Must not be disrupted |

## 3. Network Model

Expected high-level network path:

```text
Internet
  -> DNS messenger-imap.speechbattle.com
  -> public IP 146.19.211.30
  -> gateway/router/NAT
  -> internal deploy host 192.168.7.64
  -> Traefik
  -> future messenger-imap services
```

Important distinction:

- `146.19.211.30` is the public entry point / gateway-facing address.
- `192.168.7.64` is the actual deploy host inside the local network.

Future deploy documentation must not confuse public DNS/IP routing with the internal server address.

## 4. Expected Product-Facing Routes

Proposed MVP route map, without implementation commitment:

```text
https://messenger-imap.speechbattle.com/
https://messenger-imap.speechbattle.com/admin
https://messenger-imap.speechbattle.com/join/{inviteToken}
https://messenger-imap.speechbattle.com/download/android/latest
https://messenger-imap.speechbattle.com/api/...
```

MVP recommendation:

- use one domain;
- route by path through Traefik;
- avoid creating multiple subdomains before the product needs them;
- later split `api.` or `download.` only if operational or security needs justify it.

## 5. Logical Service Landscape

Future logical components:

- Control Plane Web / Admin Portal;
- Control Plane API;
- Join Landing Page;
- APK Release / Download endpoint;
- Directory API;
- Invite API;
- Provider Profile API;
- Diagnostics Evidence API;
- Audit Log storage;
- Database;
- object/file storage for APK releases if GitHub Releases is not used.

MVP does not need to become a microservice system. The first implementation may be one backend/web service plus one database, with clear module boundaries and future extraction points only where necessary.

## 6. Traefik Assumptions

Current assumptions:

- Traefik already exists on the server.
- Future deployment must integrate with the existing Traefik setup carefully.
- Existing Traefik config must not be overwritten.
- Existing routers, services, middlewares, certificates, and containers must not be disrupted.
- The project should avoid direct host port exposure unless a later deploy Blueprint explicitly justifies it.
- The service should attach to the correct Docker network used by Traefik.
- The Docker network name must be discovered through read-only server audit.
- TLS/ACME resolver may already be configured, but must be verified through read-only audit.
- New Traefik labels, routers, services, and middlewares must be scoped to `messenger-imap`.

TODO before deployment:

- identify the Traefik container name;
- identify the Docker network used by Traefik;
- identify the ACME resolver name and certificate storage model;
- identify current deployment style: Docker Compose, Portainer, manual Docker, or mixed;
- identify server convention for project stacks, for example `/opt/messenger-imap` if it matches existing practice.

## 7. Deployment Assumptions

Future deployment assumptions:

- deployment may use Docker Compose or a Portainer stack;
- candidate root project path: `/opt/stacks/messenger-imap`;
- container names should be prefixed with `messenger-imap-`;
- Docker network must be shared with Traefik where reverse-proxy routing is needed;
- new `messenger-imap` web/API services should be routed through Traefik;
- no direct host port publishing for web/API/database by default;
- any direct host port exposure requires explicit Deployment Blueprint justification;
- logs should be available through Docker logs;
- later integration with existing observability/logging can be added if available;
- application configuration should be env-based;
- real secrets must not be stored in git.

These assumptions are not a deploy instruction. `/opt/stacks/messenger-imap` is a candidate deployment path, not an approved deployment path. `traefik-net` is a candidate Docker network, not automatically approved. The Deployment Blueprint must explicitly approve path, network, stack name, data layout, backup, and rollback plan before deployment.

## 7.1 Database Isolation Rule

Future `messenger-imap` Control Plane should use its own database/container/volume by default.

Rules:

- do not reuse the existing `postgres-dev` database/container without an explicit architecture and data-isolation decision;
- database storage must be isolated from unrelated services;
- database credentials must be stored outside git;
- database should not expose public host ports by default;
- any direct database host port exposure requires explicit Deployment Blueprint justification;
- backup and restore procedure must be defined before production use.

## 8. Environment Model

Minimal environment model:

| Environment | Meaning | Current status |
| --- | --- | --- |
| local | Developer machine | Exists by project workspace/repo |
| stage/server | Host `192.168.7.64` | Candidate early deploy host |
| production | TBD / possibly same host in early MVP | Not decided |

Current constraints:

- stage/production separation is not decided;
- this document must not pretend a full production landscape exists;
- future Blueprint must decide whether MVP uses one environment or separate stage/prod environments.

## 9. Configuration And Environment Variables

Future implementation should provide:

```text
.env.example
```

Only placeholders belong in `.env.example`. Real `.env` files must stay outside git.

Expected configuration categories:

- public app settings;
- domain/base URL;
- database URL;
- session/JWT secret;
- invite token secret or signing key;
- mail verification sender settings;
- APK release storage path or bucket;
- Traefik-related service metadata if needed;
- diagnostics upload settings;
- logging level.

Do not write real secrets into documentation, examples, commits, logs, or issue/PR comments.

## 10. Secrets Policy

Hard rules:

- no SSH private keys in repo;
- no passwords in docs;
- no real `.env`;
- no app passwords;
- no mail provider passwords;
- no JWT/session secrets;
- no invite signing secrets;
- no APK signing keys;
- no database passwords;
- no raw auth payloads;
- no raw logs containing sensitive data.

Future secret storage options:

- Docker secrets;
- server-side `.env` outside git;
- CI/CD secrets;
- password manager;
- later centralized secret storage.

The exact option is a deployment/security decision and must be documented before production use.

APK signing key policy:

- APK signing key must not be stored on the deploy host by default;
- build/signing pipeline is TBD;
- APK signing secrets must not be stored in repo, server docs, `.env`, compose files, or Traefik labels;
- future release process must document where signing occurs and how hashes/signing info are published.

## 11. APK Distribution Assumptions

APK binaries must not be committed to git.

Possible APK distribution channels:

- GitHub Releases;
- backend download endpoint;
- object storage;
- emergency email attachment for Android only.

MVP recommendation:

- Control Plane stores release metadata;
- release metadata may point to GitHub Releases or controlled backend/object storage;
- if GitHub Releases are used, Control Plane should store version, URL, SHA-256, size, channel, release date, and signing info;
- backend download endpoint can proxy or redirect to release storage later;
- APK binary must not be committed to git.

Each APK release should record:

- `versionName`;
- `versionCode`;
- channel;
- SHA-256;
- size;
- build date;
- signing info;
- release notes.

Important product constraints:

- iOS is out of current scope;
- APK-by-email is emergency fallback only;
- APK-by-email does not imply trust, membership, or completed enrollment;
- normal install/download flow needs normal internet or at least access to the chosen download channel;
- invite activation, email ownership verification, provider setup, policy checks, and directory sync remain required.

## 12. Control Plane Availability In Whitelist Mode

Product assumption:

- `messenger-imap.speechbattle.com` may be unavailable in mobile whitelist or restricted-network mode.

Expected behavior:

- messages through Mail.ru / VK Mail IMAP/SMTP may continue if provider transport is reachable;
- directory sync may be delayed;
- invite activation may be delayed;
- release metadata sync may be delayed;
- policy sync may be delayed;
- diagnostics upload may be delayed;
- the client must support stale directory / stale policy mode.

Future fallback:

- signed directory/policy updates through an IMAP/SMTP system account may be designed later;
- IMAP-based control updates are not the MVP default unless explicitly selected;
- any automatic fallback update must be signed, versioned, replay-protected, and scoped by organization/workspace.

## 13. Safety Rules For Server Audit

Future server audit must start as read-only discovery.

Allowed read-only discovery commands:

```bash
hostname
whoami
pwd
docker ps
docker network ls
docker compose ls
docker volume ls
docker inspect <traefik-container>
ls -la /opt
```

Use care with commands that may reveal secrets. Do not paste sensitive environment variables, private keys, auth tokens, raw logs, or database credentials into repository docs.

Forbidden without a separate explicit command:

```bash
docker stop
docker restart
docker rm
docker compose down
docker system prune
editing Traefik config
changing firewall rules
changing DNS
deleting files
moving production files
changing existing containers
changing existing volumes
```

Existing services and existing Traefik routing must not be disrupted during discovery.

## 14. Open Infrastructure Questions

- What is the Traefik container name?
- Which Docker network does Traefik use?
- Which ACME resolver is configured?
- Where should the project stack live?
- Is Portainer used, or plain Docker Compose?
- Which database should the Control Plane use?
- Is separate object storage needed for APK releases?
- How will secrets be stored?
- Is there existing monitoring/logging to integrate with?
- Will MVP have stage/prod separation?
- How will database backup work?
- How will release rollback work?
- Where will the APK signing key be stored?
- Who should have SSH access?
- What is the server convention for Docker Compose project names and container prefixes?
- Are there existing backup, firewall, or certificate renewal policies that must be followed?

## 15. Recommended Next Step

Before writing a technical deployment Blueprint, perform a read-only server audit and create:

```text
docs/infrastructure/SERVER_AUDIT_REPORT.md
```

Then prepare:

```text
docs/blueprints/CORPORATE_CONTROL_PLANE_MVP_BLUEPRINT.md
docs/blueprints/DEPLOYMENT_BLUEPRINT.md
```

The deployment Blueprint is still premature until the Control Plane stack is selected.

## 16. Non-Goals

This document is not:

- a deployment runbook;
- a production launch guide;
- a Docker Compose file;
- a Traefik config;
- a security hardening guide;
- a backup plan;
- a CI/CD specification;
- a monitoring specification;
- a server audit report;
- proof that the Control Plane is reachable in whitelist mode.

It is an assumptions document for future infrastructure design.
