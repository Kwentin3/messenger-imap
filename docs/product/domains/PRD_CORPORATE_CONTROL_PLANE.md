# Corporate Control Plane PRD

Date: 2026-05-14

Status: high-level domain PRD.

Root PRD: [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)

## 1. Purpose

Define the high-level product requirements for the Corporate Control Plane: the admin portal and backend that manage organizations, members, internal invites, external contacts, external invites, directory state, provider profiles, app releases, policies, audit events, and diagnostic status.

The control plane is not the message server. Messages continue to use IMAP/SMTP transport through configured providers.

## 2. Problem

An IMAP/SMTP messenger can move messages, but it does not automatically become a corporate communication product.

Organizations need a trusted place to answer:

- who belongs to the organization;
- who can invite employees;
- who can invite external contacts;
- which external contacts are active, suspended, revoked, archived, or reassigned;
- which members are active, suspended, or revoked;
- what the corporate directory contains;
- which groups are managed;
- which provider profiles are allowed or recommended;
- which APK release should users install;
- which providers have diagnostic evidence;
- what happened in admin operations.

Without a control plane, the product would depend on unmanaged local app state, manual contact exchange, and unsafe assumptions about membership.

## 3. Goals

- Manage organization accounts.
- Manage users, memberships, roles, and lifecycle state.
- Manage invites and enrollment policy.
- Manage external contacts, external invites, external relationships, and reassignment.
- Serve as the source of truth for corporate directory snapshots.
- Manage basic groups or starter groups.
- Manage provider profiles and diagnostic status.
- Manage APK release metadata and download flow.
- Provide admin visibility into diagnostic outcomes.
- Provide audit events for sensitive administrative operations.
- Keep messaging transport outside the control plane.

## 4. Non-goals

- No IMAP/SMTP message hosting.
- No replacement for the email provider.
- No real-time chat server.
- No full MDM in MVP.
- No detailed API specification in this PRD.
- No database schema in this PRD.
- No production-grade compliance suite in MVP.
- No automatic revocation of already delivered IMAP messages.
- No guarantee that revoked employees forget or lose all previously seen information.
- No full CRM or helpdesk in MVP.
- No assumption that external contacts are organization members.

## 5. Admin Roles

**Organization Owner**  
Creates or owns the organization. Can assign administrators and approve high-level policies.

**Admin**  
Manages members, roles, invites, directory, provider profiles, app releases, and diagnostic status.

**Manager**  
May create limited invites, external contact invites, manage selected groups, or update selected directory fields depending on policy.

**Support / IT Administrator**  
Assists users with installation, onboarding, provider setup, and diagnostics. May have read-only or limited operational access.

**Auditor / Read-only Admin**  
Can review members, directory state, app releases, diagnostic status, and audit events without changing state.

## 6. Core Entities

**Organization**  
The corporate tenant. Contains policies, members, directory state, provider defaults, app releases, and audit history.

**User**  
A person identity known to the organization. A user may be invited, active, suspended, or revoked through membership state.

**Membership**  
The relationship between a user and an organization. Controls whether the user is active in the corporate product.

**Role**  
The administrative or employee role within the organization. Roles control admin actions and product access.

**Invite**  
An enrollment artifact created by an authorized admin or manager. It may be individual, domain-based, limited group, or one-time.

**External Contact**  
A non-employee client, supplier, partner, contractor, or other counterparty visible and reachable according to external relationship scope.

**External Organization**  
A client, supplier, partner, contractor, or other counterparty organization. Full external organization management is later scope, but MVP may store company name.

**External Invite**  
An invite that creates or activates an external relationship rather than employee membership.

**External Relationship**  
The organization-owned scoped relationship between the tenant and an external contact. It includes inviting employee, assigned employee/team, visibility scope, status, and audit history.

**Directory**  
The canonical corporate address book and managed group source of truth exposed to clients as versioned snapshots.

**Group**  
A managed collection of active members used for starter groups, teams, announcements, or directory grouping.

**Provider Profile**  
Product-level IMAP/SMTP configuration and guidance for providers such as Mail.ru / VK Mail, Yandex, Rambler, or manual/custom.

**App Release**  
Metadata for an Android APK release: version, channel, size, SHA-256, release date, release notes, and download status.

**Audit Event**  
A record of sensitive admin operations such as member revoke, invite creation, directory publish, provider status change, or app release update.

## 7. Admin Portal Flows

### Create Organization

An owner creates an organization with basic profile, domain policy, initial admin, provider defaults, and distribution settings.

MVP behavior:

- create organization record;
- assign owner/admin role;
- define initial provider profile policy;
- prepare initial directory state;
- create initial APK release entry if available.

### Manage Members

Admins can view, add, update, suspend, and revoke members according to policy.

MVP behavior:

- list members by status;
- view member detail;
- update role and basic directory fields;
- suspend or reactivate where policy allows;
- revoke membership;
- record audit events.

### Revoke Member

Revocation removes a member from active corporate directory state and managed groups.

MVP behavior:

- mark membership as revoked;
- increment directory version and hash;
- remove/hide revoked member from active directory snapshots;
- remove revoked member from managed groups;
- expose state to clients on next sync;
- record audit event.

Limit: revocation does not erase previously delivered IMAP messages or information already seen by people.

### Manage Invites

Admins create, view, revoke, and audit invites.

MVP behavior:

- create individual email invites;
- create domain or limited group invites if allowed by policy;
- set expiry and max use count;
- revoke invites;
- view used-by and status;
- avoid exposing raw invite secrets unnecessarily.

### Manage External Contacts

Admins manage external contacts and guest access independently from employee membership.

MVP behavior:

- list external contacts by status, type, assigned employee/team, and visibility scope;
- view external contact detail;
- create or revoke one-to-one external invites;
- see who invited and activated an external contact;
- change assigned employee/team;
- change visibility scope where policy allows;
- suspend, revoke, archive, or reactivate an external contact;
- record audit events for invite, activation, scope change, reassignment, suspension, revocation, and archive.

Later behavior:

- manage external organizations;
- manage external project rooms;
- integrate with CRM/helpdesk systems;
- support approval workflows for broad external visibility.

### Manage Directory

Admins manage the canonical corporate directory.

MVP behavior:

- view active, pending, suspended, and revoked members;
- edit approved directory fields;
- publish a new directory snapshot;
- expose directory version and hash;
- review changes before publish;
- record audit events.

### Manage Groups

Admins manage controlled groups where selected for MVP.

MVP behavior:

- create managed group;
- add active members;
- remove members;
- remove revoked members automatically on directory publish;
- define an "All employees" dynamic group rather than manually maintaining it.

### Manage APK Releases / Downloads

Admins publish or reference internal APK releases.

MVP behavior:

- record version, channel, size, SHA-256, release date, and release notes;
- expose download link or landing page reference;
- show current recommended release;
- avoid implying that APK download activates membership.

### View Diagnostic Status

Admins view provider diagnostic evidence and status.

MVP behavior:

- show provider profile status such as untested, wifi_verified, normal_mobile_verified, whitelist_verified, failed, or degraded;
- show last successful sanitized diagnostic report ID;
- avoid raw logs, credentials, or sensitive payloads;
- distinguish Mail.ru / VK Mail accepted baseline from unverified candidate providers.

## 8. Functional Requirements

- The control plane must manage organizations.
- The control plane must manage member lifecycle states.
- The control plane must manage invites and enrollment constraints.
- The control plane must distinguish internal organization invites from external contact invites.
- The control plane must manage external contacts and external relationships.
- The control plane must support external contact reassignment.
- The control plane must support external contact revoke/archive/suspend operations.
- The control plane must serve the corporate directory as versioned snapshots.
- The control plane must expose directory version and hash to clients.
- The control plane must manage provider profile definitions and diagnostic statuses.
- The control plane must manage APK release metadata.
- The control plane must record audit events for sensitive operations.
- The control plane must record audit events for external invite, activation, reassignment, scope change, revoke, archive, and suspend operations.
- The control plane must support admin and support roles with scoped permissions.
- The control plane must not route or store user chat messages as the message server.

## 9. Policies

**Who can invite**  
MVP should allow Organization Owner and Admin. Manager-level invite rights are optional and policy-controlled.

**Who can invite external contacts**  
MVP should allow Admin and policy-enabled Manager roles. Organization-wide external visibility or external organization invites may require admin approval.

**Domain restrictions**  
Invites may be restricted to allowed email domains. Domain invite does not remove the need for enrollment and activation.

**Invite expiry**  
Every invite should support an expiry date/time. Expired invites cannot activate membership.

**Invite use count**  
Invites should support maxUses. One-time invites should expire after first successful activation.

**External invite constraints**  
External invites should support expiry, maxUses, allowed email domain if needed, contact type, assigned employee/team, visibility scope, and approval policy.

**Revocation**  
Revoked members are removed from active directory snapshots and managed groups. Revocation is reflected to active clients through directory sync.

**External contact revocation and reassignment**  
External contacts belong to the organization, not personally to the inviting employee. Admins must be able to revoke, archive, suspend, and reassign external contacts when an employee leaves or responsibility changes.

**Directory sync**  
Clients receive a manifest with version/hash and download a snapshot when changed. Control plane remains source of truth.

**Provider profile policy**  
Organization can recommend or restrict provider profiles, but whitelist-ready status must be evidence-based.

## 10. Security / Privacy

- Separate admin identity, employee membership, and IMAP/SMTP credentials.
- Do not collect or store app passwords in the control plane unless a later security design explicitly allows a managed credential model.
- Protect invite tokens and avoid unnecessary display of full token values.
- Protect external invite tokens and make clear that external invites do not create membership.
- Prevent internal directory exposure to external contacts.
- Record audit events for sensitive admin actions.
- Keep diagnostic reports sanitized.
- Do not expose full raw email addresses or personal data in broad diagnostic views unless required for support and authorized by role.
- Make revocation limitations clear: control-plane state can change, but external copies and previously delivered messages cannot be guaranteed erased.

## 11. MVP Scope

- Organization management.
- Admin roles.
- Member list and lifecycle state.
- Invite creation, expiry, revocation, and use tracking.
- External contact list.
- One-to-one external invite creation and revocation.
- External contact revoke/archive/suspend/reassign.
- External visibility scope management for MVP-supported scopes.
- Directory snapshot source of truth with version/hash.
- Basic managed groups.
- Provider profile list and diagnostic status.
- APK release metadata and download reference.
- Basic audit events.
- Admin diagnostic status view.

## 12. Later Scope

- Fine-grained RBAC.
- Advanced audit search and export.
- Signed directory snapshots and rollback.
- Advanced policy engine.
- Managed configuration delivery.
- Rich support case workflow.
- Automated diagnostic campaign tracking.
- Full distribution channel management.
- Integration with enterprise identity providers.
- External organization management.
- External project room management.
- CRM/helpdesk integrations.

## 13. Acceptance Criteria

- An admin can create or manage an organization.
- An admin can create, revoke, and inspect invites.
- An admin can create, revoke, and inspect external invites.
- An admin can list external contacts and see assigned employee/team.
- An admin can revoke/archive/suspend/reassign an external contact.
- External invite activation does not create membership.
- APK download remains separate from membership activation.
- An admin can add, suspend, or revoke members.
- Revocation changes the directory version/hash and active directory output.
- An admin can publish or expose a directory snapshot.
- An admin can manage basic groups using active members.
- An admin can configure or view provider profiles and diagnostic status.
- An admin can manage APK release metadata with version, size, SHA-256, channel, and release date.
- The control plane does not act as an IMAP/SMTP message server.
- The control plane prevents external contacts from receiving the internal directory by default.

## 14. Open Questions

- What identity/auth mechanism should admins use in MVP?
- How much role granularity is needed before first field trial?
- Are domain invites allowed in MVP or deferred?
- Which roles can invite external contacts in MVP?
- Is admin approval required for external contacts or only for broad visibility scopes?
- Which external contact statuses are exposed to managers versus admins?
- What audit retention is required for external relationship events?
- Who can publish directory changes?
- Should provider profiles be organization-specific, global, or both?
- What diagnostic evidence is required before an admin can mark a provider as recommended?
- What APK hosting path is acceptable for internal testing?
- How much audit retention is required?

## 15. Product Review Refinements

These refinements are product requirements from [Product PRD Review Addendum](../PRODUCT_PRD_REVIEW_ADDENDUM.md).

### Control Plane Availability

The Control Plane is required for organization authority, but it may be unreachable in mobile whitelist or restricted-network mode.

Product rules:

- IMAP/SMTP messaging may continue when provider transport is reachable;
- directory, policy, invite, revoke, release metadata, external relationship, audit upload, and diagnostic upload sync may be delayed;
- clients must cache the last known directory and policy state;
- admin and client UX must expose stale/expired state where relevant;
- activation of internal invites and external invites requires Control Plane availability in MVP;
- signed IMAP/system-account distribution of directory/policy updates is later fallback scope.

### RBAC Matrix

This is the product-level permission baseline. Exact permission names and enforcement points belong to later Blueprints.

| Action | Owner | Admin | Manager | Support/IT | Auditor |
| --- | --- | --- | --- | --- | --- |
| Create organization | yes | no | no | no | no |
| Manage admins | yes | policy | no | no | read-only |
| Create internal invite | yes | yes | policy | policy | no |
| Create external invite | yes | yes | policy | policy | no |
| Approve member | yes | yes | policy | policy | no |
| Suspend/revoke member | yes | yes | policy | policy | no |
| Publish directory | yes | yes | no | policy | read-only |
| Edit directory fields | yes | yes | policy | policy | read-only |
| Manage managed groups | yes | yes | policy | policy | read-only |
| Reassign external contact | yes | yes | policy | policy | read-only |
| Revoke/archive external contact | yes | yes | policy | policy | read-only |
| Change provider profiles | yes | yes | no | policy | read-only |
| Publish APK release | yes | yes | no | policy | read-only |
| View diagnostics | yes | yes | policy | yes | read-only |
| Export diagnostic report | yes | yes | policy | policy | read-only |
| View audit log | yes | yes | no | policy | read-only |

Legend: `policy` means the organization can allow or restrict the action; `read-only` means view without mutation.

### Email Verification Challenge

Membership activation and external relationship activation require proof of mailbox ownership.

The preferred product flow is:

1. Control Plane sends a verification code or challenge to the expected email.
2. User enters the code in the app.
3. Control Plane validates the code and checks `allowedEmail` / `allowedDomain` constraints against the verified email.
4. Activation proceeds only if invite, policy, membership/external relationship, and provider constraints pass.

IMAP/SMTP login success can support transport readiness but does not replace product-level email ownership proof.

### Invite Abuse, Rate Limit, And Audit

Control Plane must support:

- invite expiry and `maxUses`;
- expected email/domain constraints where applicable;
- failed attempt audit;
- rate limits for repeated failed code/invite attempts;
- invite revocation;
- revoke-all-active-invites-by-issuer admin action;
- admin/support visibility into suspicious invite activity;
- later alerting for suspicious invite activity.

Audit events should include failed invite attempts, failed email verification attempts, successful verification, activation, invite revoke, invite expiry, member revoke/suspend, external contact revoke/archive/reassign, and release policy publication.

### App Release Lifecycle

APK release management must include:

- `appReleaseVersion`;
- `minSupportedVersion`;
- `forceUpgradeBelowVersion`;
- `deprecatedVersion`;
- `blockedVersion`;
- release channel: `internal`, `beta`, `stable`;
- APK SHA-256;
- release date;
- signing info or signature note;
- rollback status;
- update warning/force-upgrade policy.

APK-by-email is allowed only as an Android emergency fallback and must not become the primary distribution path. iOS support and iOS distribution are out of current scope.

### External Contact Reassignment Support

When an assigned manager leaves or loses access:

- the external relationship remains organization-owned;
- admin can reassign the contact to another employee or team;
- the old manager loses access after sync/enforcement;
- the external contact sees the new assigned contact/team where policy allows;
- no internal HR reason should be exposed to the external contact;
- exact chat/history behavior must be defined in Blueprint.

## 16. MVP / Later / Non-goals Summary

MVP covers organization, members, internal invites, external contacts, one-to-one external invites, external contact reassignment/revocation, directory authority, basic groups, provider profiles, APK metadata, diagnostic status, and audit events.

Later covers advanced RBAC, signed directory trust, richer policy, enterprise identity, external organizations, external project rooms, CRM/helpdesk integrations, distribution management, and diagnostic campaigns.

Non-goals exclude message hosting, real-time chat server behavior, full MDM, full CRM/helpdesk, database schema, API specs, and production-grade compliance claims.
