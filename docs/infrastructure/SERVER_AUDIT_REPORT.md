# Server Audit Report

Date/time of audit: 2026-05-26 13:00-13:05 +03:00

Audit type: read-only infrastructure discovery

Target host: `192.168.7.64`

SSH context: `ssh roman@192.168.7.64`

Related assumptions: [INFRASTRUCTURE_ASSUMPTIONS.md](INFRASTRUCTURE_ASSUMPTIONS.md)

## 1. Executive Summary

A read-only audit was performed for the current deployment host intended for future `messenger-imap` Control Plane, landing page, invite onboarding, APK distribution, and backend/API work.

The server is reachable by key-based SSH as `roman`. Docker is available. Traefik is already running as container `traefik`, attached to Docker network `traefik-net`, and publishing host ports 80 and 443. Multiple existing services and compose stacks are running on the same host, so future `messenger-imap` deployment must be non-destructive and must integrate with existing Traefik routing without replacing existing configuration.

This audit is a point-in-time snapshot. Existing containers are treated as production-like unless the owner explicitly classifies them otherwise. No config files, secret files, `.env` files, ACME storage, container environment variables, private keys, or database contents were inspected.

No deployment actions were performed. No containers were restarted, stopped, removed, edited, or recreated.

## 2. Commands Executed

Only read-only discovery commands were used:

```bash
hostname
whoami
pwd
uname -a
docker ps
docker network ls
docker volume ls
docker compose ls
ls -la /opt
docker inspect traefik
docker inspect traefik-net
```

`docker inspect` output was used only for Traefik/network discovery. Environment variables, secret-bearing file contents, private keys, `.env`, and ACME storage were not inspected.

## 3. Hostname / OS Summary

| Item | Observed value |
| --- | --- |
| Hostname | `ubuntu` |
| SSH user | `roman` |
| Initial working directory | `/home/roman` |
| Kernel / OS summary | `Linux ubuntu 6.8.0-111-generic #111-Ubuntu SMP PREEMPT_DYNAMIC Sat Apr 11 23:16:02 UTC 2026 x86_64 x86_64 x86_64 GNU/Linux` |

The OS appears to be an Ubuntu Linux host based on hostname and kernel string. Distribution release files were not inspected because they were not part of the allowed command list.

## 4. Docker Availability

Docker is available and responding to read-only commands:

- `docker ps` succeeded;
- `docker network ls` succeeded;
- `docker volume ls` succeeded;
- `docker compose ls` succeeded.

Docker Compose is available through `docker compose`.

## 5. Running Containers Summary

Observed running containers:

| Container | Image | Status / notes | Published ports |
| --- | --- | --- | --- |
| `mcp-stage-mcp-1` | `ghcr.io/kwentin3/mcp-bff` | Up, healthy | internal `3001/tcp` |
| `kassa-web` | `kassa-web:demo` | Up | internal `80/tcp` |
| `postgres-dev-postgres-1` | `postgres:16-alpine` | Up, healthy | host `5432` |
| `github-runner-mcp` | `myoung34/github-runner:latest` | Up | none shown |
| `mcp-prod-app-1` | `ghcr.io/kwentin3/mcp-bff` | Up, healthy | internal `3001/tcp` |
| `portainer` | `portainer/portainer-ce:latest` | Up | internal `8000`, `9000`, `9443` |
| `open-notebook` | `lfnovo/open_notebook:latest` | Up | internal `5055`, `8502` |
| `open-notebook-db` | `surrealdb/surrealdb:v2` | Up | host `8000` |
| `traefik` | `traefik:v3.0` | Up | host `80`, `443` |
| `qdrant` | `qdrant/qdrant:latest` | Up | host `6333-6334` |

Security note: some existing services publish host ports directly. This report does not evaluate whether that is intentional or safe. Future `messenger-imap` deployment should avoid direct public port exposure and route through Traefik unless explicitly justified.

## 6. Existing Traefik Container Identification

| Item | Observed value |
| --- | --- |
| Container name | `traefik` |
| Container ID prefix | `06205b697127` |
| Image | `traefik:v3.0` |
| OpenContainers version label | `v3.0.4` |
| Command args | `["traefik"]` |
| Compose project | `traefik` |
| Compose service | `traefik` |
| Compose config file label | `docker-compose.yml` |
| Compose working directory label | `/home/roman/traefik` |
| Published ports | `80`, `443` |
| Docker network | `traefik-net` |
| Container IP on `traefik-net` | `172.18.0.7/16` |

Observed Traefik-related labels:

- `traefik.enable=true`;
- router for `Host(`traefik.speechbattle.com`)`;
- entrypoint `websecure`;
- service `api@internal`;
- TLS certresolver `letsencrypt`;
- middleware `traefik-auth`.

Redaction note: Traefik labels include a basic-auth users value for the dashboard middleware. The credential hash was observed during inspect but is intentionally omitted from this report.

## 7. Docker Networks

Observed Docker networks:

| Network | Driver | Scope | Notes |
| --- | --- | --- | --- |
| `bridge` | bridge | local | Docker default |
| `host` | host | local | Docker default |
| `none` | null | local | Docker default |
| `postgres-dev_default` | bridge | local | Compose-created network |
| `traefik-net` | bridge | local | Shared reverse-proxy network |

## 8. Which Network Traefik Uses

Traefik uses Docker network:

```text
traefik-net
```

Observed containers attached to `traefik-net`:

| Container | Network IP |
| --- | --- |
| `qdrant` | `172.18.0.2/16` |
| `portainer` | `172.18.0.3/16` |
| `kassa-web` | `172.18.0.4/16` |
| `open-notebook` | `172.18.0.5/16` |
| `open-notebook-db` | `172.18.0.6/16` |
| `traefik` | `172.18.0.7/16` |
| `mcp-prod-app-1` | `172.18.0.8/16` |
| `mcp-stage-mcp-1` | `172.18.0.9/16` |

Candidate network for future `messenger-imap` services:

```text
traefik-net
```

This should be confirmed in the deployment Blueprint before use.

## 9. Existing Compose Stacks

Observed `docker compose ls` output:

| Stack / project | Status | Config files |
| --- | --- | --- |
| `kassa-web` | `running(1)` | `/opt/stacks/kassa-web/docker-compose.yml` |
| `mcp-prod` | `running(1)` | `/opt/stacks/mcp-prod/docker-compose.yml` |
| `mcp-stage` | `running(1)` | `/opt/stacks/mcp-stage/docker-compose.yml`, `/opt/stacks/mcp-stage/deploy/compose/compose.lock.yml` |
| `open-notebook` | `running(2)` | `/data/compose/1/docker-compose.yml` |
| `postgres-dev` | `running(1)` | `/opt/stacks/postgres-dev/docker-compose.yml` |
| `qdrant` | `running(1)` | `/home/roman/qdrant/docker-compose.yml` |
| `traefik` | `running(2)` | `docker-compose.yml` |

Observed conventions are mixed:

- several stacks live under `/opt/stacks/<name>`;
- at least one stack appears under `/data/compose/1`, consistent with Portainer-managed compose storage;
- Traefik appears to have compose working directory `/home/roman/traefik`;
- Qdrant appears under `/home/roman/qdrant`.

## 10. Existing `/opt` Layout

Observed `/opt` entries:

| Path | Owner | Notes |
| --- | --- | --- |
| `/opt/containerd` | `root:root` | system/container runtime path |
| `/opt/paddleocr-vl` | `roman:roman` | existing project/service directory |
| `/opt/stacks` | `roman:roman` | compose stack convention for several services |
| `/opt/supabase` | `roman:roman` | existing project/service directory |

The visible convention suggests `/opt/stacks/<project-name>` is a strong candidate for future compose-managed services.

## 11. Known Existing Services / Domains

Known existing services from container names and compose projects:

- Traefik reverse proxy;
- Portainer;
- MCP production and stage services;
- Kassa web demo;
- Postgres dev;
- Open Notebook and SurrealDB;
- Qdrant;
- GitHub runner.

Observed domain label:

- `traefik.speechbattle.com` for the Traefik dashboard/router.

Other service domains were not enumerated because that would require inspecting other service labels/configs and might expose sensitive values. This should be done later through a deliberate, redacted read-only audit if needed.

## 12. Observed Deployment Conventions

Observed conventions:

- Docker Compose is used.
- Portainer is installed and running.
- Multiple project stacks use `/opt/stacks/<project-name>`.
- Traefik shared network is named `traefik-net`.
- Traefik routing is likely label-driven for at least the dashboard route.
- Existing containers use a mix of compose-generated names and manually named services.
- Existing services must be treated as production-like unless explicitly classified otherwise.

Recommendations for future `messenger-imap` deployment:

- prefer `/opt/stacks/messenger-imap` as candidate project path;
- prefix containers with `messenger-imap-`;
- attach web/API service to `traefik-net`;
- do not publish direct host ports for web/API/database containers by default;
- any direct host port exposure requires explicit Deployment Blueprint justification;
- scope Traefik routers/services/middlewares to `messenger-imap`;
- do not modify existing Traefik config without a separate Deployment Blueprint, backup, and rollback plan.

## 13. Candidate Deployment Path

Preferred candidate:

```text
/opt/stacks/messenger-imap
```

Rationale:

- `/opt/stacks` exists;
- multiple current compose projects use `/opt/stacks/<name>`;
- owner is `roman:roman`, matching SSH user context observed in this audit.

Alternative:

```text
/opt/messenger-imap
```

This alternative is less aligned with the visible compose-stack convention, but may still be acceptable if the server owner prefers it.

Important: `/opt/stacks/messenger-imap` is a candidate deployment path, not an approved deployment path. Deployment Blueprint must explicitly approve the path, stack name, ownership, backup location, and rollback plan before deployment.

## 14. Candidate Docker Network

Candidate network:

```text
traefik-net
```

Rationale:

- Traefik is attached to this network;
- existing routed services are attached to this network;
- future `messenger-imap` web/API service likely needs Traefik access.

The database should not be exposed publicly. It may use an internal app-specific network plus a Traefik-facing network for the web/API service, depending on the final compose design.

Important: `traefik-net` is a candidate Docker network for reverse-proxy access, not automatically approved. Deployment Blueprint must explicitly approve the network plan, including whether the app uses a separate internal database network.

Database isolation rule:

- future `messenger-imap` Control Plane should use its own database/container/volume by default;
- do not reuse existing `postgres-dev` without explicit architecture and data-isolation decision;
- database credentials must stay outside git;
- database must not expose public host ports unless explicitly justified.

APK signing and storage rule:

- APK signing key must not be stored on the deploy host by default;
- build/signing pipeline is TBD;
- signing secrets must not be stored in repo, server docs, `.env`, or compose files;
- MVP Control Plane can store release metadata and point to GitHub Releases or controlled backend/object storage;
- if GitHub Releases are used, metadata should include version, URL, SHA-256, size, channel, release date, and signing info;
- backend download endpoint can proxy or redirect to release storage later.

## 15. Unknowns And Follow-Up Questions

- Where is the active Traefik static configuration stored?
- Is `letsencrypt` the only ACME resolver?
- Where is ACME storage located?
- What is the certificate renewal and backup policy?
- Which existing domains route through Traefik besides `traefik.speechbattle.com`?
- Which services are production-critical?
- Is Portainer the preferred way to deploy new stacks?
- Should `messenger-imap` use Postgres, another existing database, or its own database container?
- Should APK files use GitHub Releases, backend storage, or object storage?
- How should server-side secrets be stored?
- Is there an existing backup policy for `/opt/stacks` and volumes?
- Is there existing observability/log aggregation?
- Should MVP use one host for stage/prod or separate environments?
- Who should have SSH access for deployment and support?
- What rollback policy is expected for failed app/backend releases?
- Where should APK signing keys be stored?
- Where does APK signing happen, and how is signing metadata published?
- Should the Control Plane use GitHub Releases URLs, backend storage, object storage, or a redirect/proxy model for APK downloads?

## 16. Safety Notes: What Was Not Inspected

To avoid exposing secrets, this audit did not inspect:

- `.env` files;
- `acme.json`;
- private keys;
- SSH keys;
- container environment variables;
- volume contents;
- database contents;
- application logs;
- raw Traefik config files;
- compose file contents;
- other service labels beyond the Traefik/network discovery needed for this report.

Traefik dashboard basic-auth credential hash was observed in a label during safe inspect and is deliberately redacted from this report.

## 17. Explicit Confirmation: No Changes Were Made

No changes were made on the server.

Specifically, this audit did not:

- stop containers;
- restart containers;
- remove containers;
- run `docker compose up` or `docker compose down`;
- run `docker system prune`;
- edit files;
- edit Traefik configuration;
- change firewall rules;
- change DNS;
- delete files;
- inspect secret files;
- deploy `messenger-imap`.

This report is read-only discovery evidence for future infrastructure Blueprint work.

Deployment must start with a separate Deployment Blueprint and backup/rollback plan. This audit does not approve any deployment path, Docker network, stack name, database reuse, host port exposure, signing flow, or storage layout.
