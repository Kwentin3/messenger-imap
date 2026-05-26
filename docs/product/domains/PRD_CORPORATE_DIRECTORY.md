# Corporate Directory PRD

Date: 2026-05-14

Status: high-level domain PRD. This is intentionally the most detailed domain PRD in the package because the corporate directory is the core B2B product feature.

Root PRD: [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)

## 1. Purpose

Define the product requirements for the centrally managed Corporate Directory used by Corporate IMAP Messenger.

The directory is the source of truth for active internal members, external contacts visible to a user, member statuses, external relationship statuses, managed groups, and client-visible organization identity. The Android client keeps a local cached copy, but authority belongs to the Corporate Control Plane.

The directory must support safe MVP behavior without pretending that automatic address book distribution is already solved. Trust, signing, replay protection, and automated distribution can be expanded later.

## 2. Problem

IMAP/SMTP transport can move messages, but it does not provide corporate identity, membership, or a trusted address book by itself.

Without a controlled corporate directory:

- users may not know who is a valid employee contact;
- users may not know which contacts are employees and which are external clients, suppliers, partners, or contractors;
- inactive or revoked employees may remain visible;
- external contacts may accidentally receive internal directory visibility;
- groups may drift from actual membership;
- onboarding may depend on unsafe manual contact exchange;
- administrators cannot reliably control who appears in the organization;
- silent address book import could create identity and trust risks.

The product needs a directory model that is centrally managed, versioned, hash-verifiable, cacheable on the client, and honest about revocation limits.

## 3. Goals

- Make corporate directory a first-class product feature.
- Use the Corporate Control Plane as source of truth.
- Provide a local client cache for offline and low-connectivity use.
- Represent member lifecycle states explicitly.
- Keep Internal Members and External Contacts separate.
- Support a visible directory per user, filtered by membership and external contact visibility scope.
- Remove revoked employees from active directory views and managed groups.
- Remove or hide revoked/archived external relationships from active external sections.
- Let clients detect directory changes through version/hash.
- Make directory snapshots verifiable through a canonical hash.
- Avoid silent unsafe address book import.
- Support basic managed groups and dynamic "All employees" behavior.
- Defer signed automatic distribution until a trust model exists.

## 4. Non-goals

- No silent import of arbitrary vCards or address books.
- No claim that imported contacts are cryptographically verified unless they are actually verified through the underlying trust mechanism.
- No full directory signing/replay/rollback protocol in MVP.
- No guarantee that previously seen data can be physically erased from user memory, screenshots, backups, logs, or unmanaged copies.
- No exposure of the full internal directory to external contacts.
- No mixing external contacts into "All employees".
- No replacement for IMAP/SMTP message transport.
- No detailed API specification.
- No database schema.
- No full enterprise HR/IdP synchronization in MVP.

## 5. Core Concepts

**Corporate Directory**  
The organization-controlled address book and member index used by the Android client. It contains active corporate contacts, member metadata approved for display, and managed group relationships.

**DirectoryManifest**  
A lightweight control-plane response that tells the client the current directory version, directory hash, snapshot availability, and minimal metadata needed to decide whether to fetch a new snapshot.

**DirectorySnapshot**  
The full directory payload for a specific version. It contains member records, statuses required by the client, and managed group membership rules or outputs.

**DirectoryVersion**  
A monotonically increasing or otherwise ordered version identifier assigned by the control plane when directory content changes.

**DirectoryHash**  
A SHA-256 hash of the canonical directory snapshot payload. It lets the client verify that the downloaded snapshot matches the published manifest.

**Member**  
A person in the organization directory. A member has identity fields, membership status, messaging address where applicable, role/display metadata, and group associations.

**Membership Status**  
The lifecycle state of a member: pending, active, suspended, or revoked.

**Managed Group**  
A control-plane-managed group or team definition based on active directory members. Managed groups may become starter groups or directory groups in the Android client.

**Local Directory Cache**  
The client-side cached copy of the latest accepted directory snapshot plus version/hash metadata. It enables directory lookup when connectivity is limited, subject to revocation update limitations.

**Internal Members**  
The employee/member space of the directory. Active internal members may appear in employee search, managed groups, and dynamic "All employees" views according to policy.

**External Contacts**  
The non-employee space of the directory. It contains clients, suppliers, partners, contractors, and other counterparties visible according to external relationship scope.

**Visible Directory**  
The user-specific view of the directory. An employee may see internal members plus allowed external contacts. An external contact sees only allowed organization contacts or teams and never the full internal directory.

**External Section**  
A separate directory area for external contacts, optionally grouped as clients, suppliers, contractors, partners, or other external contacts.

**External Visibility Scope**  
The policy scope that controls which employees can see and communicate with an external contact: inviter-only, assigned team, department, organization, project room, or admin-only.

## 6. Member Statuses

**pending**  
The person has been invited or staged but is not active. Pending members should not appear as normal active contacts unless the product explicitly supports onboarding visibility for admins or support roles.

**active**  
The person is an active organization member. Active members are eligible to appear in the directory, one-to-one chat search, managed groups, and dynamic "All employees" views.

**suspended**  
The person is temporarily disabled or restricted. Suspended member visibility is a product policy decision. The conservative MVP default is to hide suspended members from normal employee search while keeping admin visibility.

**revoked**  
The person is no longer an active member. Revoked members are removed from active directory views and managed groups. A revoked user's client should clear or hide corporate directory data after the next successful sync.

External contacts use the statuses defined in the External Contacts & Guest Access PRD: pending, active, suspended, revoked, archived, and reassigned. Revoked or archived external relationships should be hidden from active external sections. Reassignment should update the visible directory for previous and new assigned employees or teams.

## 7. Directory Sync Model

The MVP sync model is version/hash based:

1. Client stores the last accepted `directoryVersion` and `directoryHash`.
2. Client requests the current DirectoryManifest from the control plane.
3. Client compares manifest version/hash with local cache metadata.
4. If unchanged, client keeps the local cache.
5. If changed, client downloads the DirectorySnapshot.
6. Client computes the hash of the canonical payload.
7. Client verifies computed hash against manifest `directoryHash`.
8. Client applies changes only after successful verification.
9. Client updates local cache metadata.
10. Client refreshes directory search, member visibility, and managed group views.

The PRD does not prescribe the API shape. The Blueprint should define protocol details, authentication, error handling, retry behavior, and storage.

### Internal And External Directory Spaces

The directory must distinguish internal members from external contacts.

Minimum model:

- Internal Members: employee membership records controlled by membership lifecycle.
- External Contacts: external relationship records controlled by guest access policy.

Rules:

- Internal invite creates membership.
- External invite creates external relationship.
- External contacts do not appear in "All employees".
- External contacts do not receive managed internal groups by default.
- External contacts are displayed in external sections with visible badges.
- Employees see external contacts only according to visibility scope.
- External contacts see only allowed employee/team contacts, not the full internal directory.
- Directory snapshots or visible-directory responses must be filtered by the requesting user's role and relationship scope.

The product should treat "directory" as a scoped view, not as one identical full address book sent to every account.

## 8. Hash / Fingerprint Model

The directory hash provides integrity for the snapshot that the control plane publishes.

Minimum product requirements:

- include `directoryVersion` in the manifest;
- include `directoryHash` in the manifest;
- compute `directoryHash` from a canonical snapshot payload;
- use SHA-256 for the hash;
- exclude volatile generated metadata from the hash, such as `generatedAt`, `serverTime`, request ID, or transport headers;
- make the canonical payload rules explicit in the later Blueprint;
- reject or quarantine a snapshot if the computed hash does not match the manifest.

The MVP hash model is not the same as a signed trust model. It protects against accidental mismatch and supports change detection. Later signed updates should add authenticity, issuer identity, replay protection, and rollback policy.

## 9. Revocation Model

Revocation is a membership lifecycle action controlled by admins through the control plane.

MVP behavior:

1. Admin marks an employee as revoked.
2. Control plane removes the member from active directory output.
3. Control plane removes the member from managed groups.
4. Directory version changes.
5. Directory hash changes.
6. Active clients detect the changed manifest.
7. Active clients download and verify the new snapshot.
8. Active clients remove or hide the revoked member from directory search and managed groups.
9. A revoked user's client, after next successful sync, clears or hides corporate directory data and stops presenting active organization membership.
10. If the revoked user was assigned to external contacts, those external relationships remain owned by the organization and must be reassigned, suspended, or hidden from that revoked user's client after sync.

Important limitation:

- The product cannot guarantee physical erasure of previously seen data.
- A user may remember names, keep screenshots, have backups, or retain old messages delivered through IMAP/SMTP.
- Revocation updates current product state; it does not retroactively erase all historical knowledge or messages.

This limitation must be documented honestly in product, admin, and support materials.

## 10. Address Book Import / Distribution

MVP behavior:

- sync corporate directory from the control plane;
- render Internal Members and External Contacts as separate spaces;
- show external contacts only in allowed visible-directory scopes;
- optionally support manual vCard import with explicit preview and confirmation;
- avoid Android system contacts permission unless product policy requires it;
- do not silently import arbitrary address books;
- do not automatically trust contacts received through arbitrary messages or attachments;
- do not mark imported contacts as verified unless the underlying verification flow actually verifies them.

Manual vCard import, if included, should show:

- source;
- count of contacts;
- names and addresses to be added or changed;
- warning that import does not prove cryptographic trust;
- confirmation before applying changes.

Later behavior:

- signed directory updates;
- trusted issuer identity;
- versioning and replay protection;
- rollback and audit;
- optional email/system-account distribution only after trust design;
- automated client update only after signature, version, and policy checks.

Automatic address book distribution requires trust/signing/versioning and is later scope.

## 11. Groups and Directory

Managed groups must be based on active directory members.

MVP behavior:

- managed groups use active members only;
- revoked members are removed from managed groups when directory is published;
- suspended members are included or excluded according to explicit policy;
- "All employees" is dynamic, not manually maintained;
- group membership should not drift from member status;
- starter groups may be provisioned from managed groups if the Android implementation path supports it safely.

The directory PRD does not redesign underlying chat group protocol. It defines product authority for which members should appear in managed group sources.

## 12. Functional Requirements

- The control plane must be the source of truth for the corporate directory.
- The Android client must maintain a local cached directory snapshot.
- Every published directory snapshot must have a directory version.
- Every published directory snapshot must have a SHA-256 directory hash over canonical payload.
- The client must compare local version/hash against the manifest.
- The client must verify snapshot hash before applying updates.
- The directory must represent pending, active, suspended, and revoked statuses.
- Active members must be eligible for normal directory search.
- Revoked members must be removed from active directory search after sync.
- Revoked members must be removed from managed groups.
- A revoked client must clear or hide corporate directory data after next successful sync.
- The product must not promise erasure of previously seen information.
- The MVP must not silently import arbitrary address books.
- The MVP should support manual import only with preview and confirmation if import is included.
- Managed groups must derive from active membership and policy.
- Directory changes must be auditable in the control plane.
- External contacts must be separated from internal members in directory output.
- External contacts must have external badges or type labels.
- External contacts must not appear in dynamic "All employees".
- External contact visibility must follow external relationship scope.
- External contact revocation, archive, and reassignment must affect visible directory output.

## 13. Security / Trust

Directory data affects user trust. A wrong or malicious directory can cause users to contact the wrong person, trust a stale employee, or accept a misleading identity.

MVP security requirements:

- authenticate client access to organization directory according to enrollment state;
- restrict directory administration to authorized roles;
- audit directory publish and revocation actions;
- verify snapshot hash before applying;
- avoid silent imports;
- avoid exposing internal directory data to external contacts;
- mark external contacts distinctly in any directory or contact picker;
- avoid marking directory contacts as cryptographically verified without real verification;
- sanitize support exports;
- make revocation limitations explicit.

Later trust requirements:

- signed directory snapshots;
- trusted issuer/key management;
- replay protection;
- rollback policy;
- effective dates;
- multi-admin approval for sensitive updates if needed;
- client warning on stale directory state;
- conflict rules for user-edited local contacts.

## 14. MVP Scope

- Control-plane managed corporate directory.
- Member statuses: pending, active, suspended, revoked.
- Directory manifest with version and hash.
- Directory snapshot download and client cache.
- Client-side hash verification before apply.
- Active member lookup.
- Revoked member removal/hiding after sync.
- Revoked client directory clear/hide after sync.
- Basic managed groups from active members.
- Dynamic "All employees" concept.
- Separate External Contacts section.
- Visible Directory per user, with external contacts filtered by scope.
- External badges/type labels for clients, suppliers, partners, contractors, or other.
- External contact revocation/archive/reassignment reflected after sync.
- Optional manual vCard import with preview and confirmation.
- No silent address book import.

## 15. Later Scope

- Signed directory snapshots.
- Trusted issuer model.
- Replay protection and rollback.
- Directory diff previews.
- Admin approval workflow for sensitive changes.
- Automated signed updates via email/system account.
- Advanced conflict handling for local edits.
- HR/IdP integration.
- Stale-directory risk indicators.
- Cross-device directory state consistency.
- External organization directory sections.
- External project-room directory views.
- External directory snapshots for advanced guest access.

## 16. Acceptance Criteria

- Corporate directory is documented as the core B2B feature.
- Control plane is documented as the source of truth.
- Android client is documented as holding a local cached copy.
- Directory manifest includes version and hash.
- Directory snapshot hash uses SHA-256 over canonical payload.
- Volatile fields such as generatedAt/serverTime are excluded from hash.
- Client sync model compares version/hash and verifies snapshot before apply.
- Member statuses include pending, active, suspended, revoked.
- Revoked employees are removed from active directory and managed groups.
- Revoked employee client clears or hides corporate directory after next sync.
- PRD explicitly states that old knowledge cannot be physically erased.
- MVP excludes silent arbitrary address book import.
- Later scope includes signed updates and trust model.
- Directory model separates Internal Members and External Contacts.
- External contacts are excluded from "All employees".
- External contacts are visible only according to visibility scope.
- External contact revocation/reassignment changes visible directory output.
- External contacts do not receive the internal corporate directory.

## 17. Open Questions

- What exact fields are required in the MVP member record?
- Should suspended members be visible to ordinary employees?
- What is the first authoritative source of directory records: manual admin entry, CSV import, HR system, or identity provider?
- What is the canonical payload format for hashing?
- How should the client behave if it cannot sync for a long period?
- Should users be allowed to edit local display names for corporate contacts?
- Should directory snapshots support diffs in MVP or only full snapshots?
- What admin approval policy is required before publishing directory changes?
- When should signed directory updates enter the roadmap?
- What external visibility scopes are required in MVP beyond inviter-only / assigned employee?
- Should external contacts receive a minimal directory snapshot or only explicit allowed contact cards?
- How should directory stale-state warnings differ for external users?
- Which external contact types should be first-class labels in MVP?

## 18. MVP / Later / Non-goals Summary

MVP covers centrally managed directory, version/hash manifests, client cache, member statuses, revocation behavior, managed groups, separate external contact section, visible directory filtering, external badges, and no silent unsafe import.

Later covers signed updates, trusted issuers, replay protection, rollback, diff previews, HR/IdP integration, external organizations, external project-room directory views, and advanced stale-state handling.

Non-goals exclude full signing protocol in MVP, API specs, database schemas, silent arbitrary import, exposing internal directory to external contacts, mixing external contacts into "All employees", and guarantees of erasing information already seen outside current product state.
