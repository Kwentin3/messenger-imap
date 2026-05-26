# External Contacts & Guest Access PRD

Date: 2026-05-26

Status: high-level domain PRD.

Root PRD: [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)

## 1. Purpose

Define product requirements for communication with external contacts in Corporate IMAP Messenger.

The product must support not only internal employee communication, but also business communication with clients, contractors, suppliers, partners, counterparties, and other non-employee participants. External communication is a normal B2B requirement, but it must not collapse into unmanaged employee membership.

This domain defines how external contacts are invited, represented, displayed, scoped, reassigned, and governed without exposing the internal corporate directory or granting employee rights.

## 2. Background / Context

Corporate communication often includes people outside the organization: clients, suppliers, partners, contractors, auditors, subcontractors, logistics contacts, service providers, and other counterparties.

An external contact is not an employee and must not become an organization member by default. The invite used for an external contact is different from an internal organization invite. Internal invites activate membership; external invites create a limited communication relationship.

External contacts must not receive the internal corporate address book. They should appear in a separate external section of the directory for employees who are allowed to see them. An external contact may be associated with a specific manager, assigned team, department, project, or external organization.

## 3. Problem Statement

If an external client is added as an ordinary employee, they may receive excess access: internal directory visibility, internal group membership, and misleading organization membership status.

If the product ignores external contacts, it will be unsuitable for real B2B communication because employees and managers often need to talk to clients, suppliers, partners, and contractors in the same controlled communication environment.

Managers need a simple way to invite a client into the messenger. The organization must keep control over that relationship even if the manager leaves. The product must separate internal membership from external communication relationships and make that separation visible in the client, directory, control plane, and audit model.

## 4. Goals

- Support the external communication perimeter of the organization.
- Separate `InternalMembership` from `ExternalRelationship`.
- Provide a clear model for external contacts and external organizations.
- Support external invite links and deep links.
- Prevent external contacts from seeing the internal corporate directory.
- Allow employees or managers to invite clients according to organization policy.
- Allow admins to manage external contacts and relationships.
- Support reassignment of an external contact to another employee or team.
- Support visibility scopes: `inviter_only`, `assigned_team`, `department`, `organization`, and later `project_room`.
- Make external contact status and badges visible in the Android client.

## 5. Non-goals

- An external contact does not become an employee.
- An external contact does not receive the full corporate directory.
- An external contact does not appear in "All employees".
- An external contact does not receive internal groups by default.
- This PRD does not design a full CRM.
- This PRD does not design a full helpdesk system.
- This PRD does not design a customer portal.
- This PRD does not design video calls.
- This PRD does not design real-time voice calls.
- MVP does not automatically import all clients without an explicit organization policy.
- MVP does not make external contacts discoverable to all employees by default.

## 6. Core Concepts

**External Contact**  
A non-employee person who can communicate with allowed employees, teams, or rooms under organization policy. Examples: client, supplier, partner, contractor, consultant, auditor, or other counterparty contact.

**External Organization / Counterparty Organization**  
A company or organization outside the tenant, such as a customer company, supplier, agency, partner, or contractor. MVP may capture company name as metadata; full external organization management is later scope.

**External Relationship**  
The scoped relationship between the tenant and an external contact. It defines who invited the contact, who owns or handles the relationship, what visibility scope applies, what conversations are allowed, and the current external status.

**External Invite**  
An invite artifact used to create or activate an external relationship. It does not create employee membership.

**External Invite Token**  
A token embedded in an external invite link, QR code, or fallback code. It must be treated as sensitive and limited by expiry, use count, and policy.

**Visibility Scope**  
The set of employees, teams, departments, or rooms that can see and communicate with the external contact.

**Assigned Employee**  
The employee responsible for the external relationship. Often the inviting manager or account owner.

**Assigned Team**  
The team responsible for the external relationship, such as Sales, Procurement, Support, Legal, or a project team.

**External Section in Directory**  
A separate directory section for external contacts. It is distinct from Internal Members and "All employees".

**External Conversation**  
A one-to-one or limited-scope conversation with an external contact. The UI must mark it as external.

**External Project Room**  
A later-scope room that includes internal employees and one or more external contacts for a project, deal, support case, delivery, or partnership.

**Guest User**  
A user identity that has access only through external relationships and allowed external conversations. A guest user is not an employee member.

**External Status**  
The lifecycle status of an external relationship: pending, active, suspended, revoked, archived, or reassigned.

## 7. Internal vs External Model

Core principle:

```text
Internal invite creates Membership.
External invite creates ExternalRelationship.
```

| Aspect | Internal Employee | External Contact |
|---|---|---|
| Created by | Internal organization invite and enrollment | External invite and external enrollment |
| Product relationship | `Membership` | `ExternalRelationship` |
| Receives corporate directory | Yes, according to member role and policy | No full internal directory; only allowed contacts/teams |
| Appears in | Internal Members, All employees, managed groups where applicable | External Contacts section only |
| Default groups | May receive managed internal groups | No internal groups by default |
| Managed by | Admins/managers through member lifecycle | Admins/managers through external relationship lifecycle |
| Rights | Employee product rights according to role | Limited guest/contact rights according to scope |
| Revocation effect | Removes membership and active internal directory visibility | Disables or archives external relationship and allowed external conversations |
| Control-plane object | Membership/user/member record | External contact/relationship record |

## 8. External Contact Statuses

**pending**  
The invite or relationship has been created, but the external contact has not completed enrollment or activation. The contact may be visible to the inviter/admin as pending, but should not be treated as an active contact.

**active**  
The external contact completed the required flow and can communicate within the allowed scope. Active external contacts appear in the external section for allowed employees.

**suspended**  
The relationship is temporarily disabled. The external contact should not be able to initiate or continue allowed conversations until reactivated. Admins retain visibility.

**revoked**  
The relationship is terminated. The contact should no longer be visible as an active external contact and should lose access to allowed organization contacts after the next sync/enforcement point.

**archived**  
The relationship is no longer active but is retained for history, audit, or future reference. Archived contacts should be hidden from active lists by default.

**reassigned**  
The external contact remains active, but responsibility moved to another employee or team. The status may be represented as a lifecycle event rather than a permanent state. The previous owner loses owner-level access according to policy.

## 9. External Invite Types

**One-to-one external invite**  
Creates an external relationship between one employee/manager and one external contact. This is the MVP default.

**Team external invite**  
Creates an external relationship visible to an assigned team, such as Sales or Procurement.

**Project / room external invite**  
Allows an external contact to join a specific external project room. Later scope.

**External organization invite**  
Invites a contact in the context of an external organization, such as a supplier or client company. Later scope.

**Limited-use invite**  
An invite with `maxUses`, often one-time.

**Expiring invite**  
An invite with a fixed expiration time.

MVP priority:

- MVP: one-to-one external invite from employee/manager to external contact.
- Later: team invite, project room invite, external organization invite.

## 10. External Invite Flow

1. Manager selects "Invite external contact".
2. Manager selects contact type: client, supplier, partner, contractor, or other.
3. Manager enters name, company, and email if known.
4. Manager selects visibility scope, defaulting to `inviter_only` or assigned employee.
5. Control plane creates an external invite link.
6. Manager sends the link to the client or counterparty through an allowed channel.
7. External contact opens the link.
8. Join landing page explains that this is external access, not employee membership.
9. External contact downloads the APK if needed.
10. App receives the external invite through deep link/app link or fallback code.
11. External contact completes external enrollment.
12. External contact configures IMAP/SMTP transport according to allowed provider policy.
13. Control plane creates or activates the external relationship.
14. Contact appears in the manager's external contacts section.
15. External contact sees only the allowed employee/team/organization contact, not the internal directory.

## 11. Directory Behavior

The Corporate Directory must have at least two spaces:

- Internal Members.
- External Contacts.

Possible external sections:

- Clients.
- Suppliers.
- Contractors.
- Partners.
- Other external contacts.

Rules:

- Employees see external contacts according to visibility scope.
- External contacts see only allowed organization contacts or teams.
- External contacts do not mix with employees in "All employees".
- External contacts have visible badges such as "External contact", "Client", "Supplier", "Partner", or "Contractor".
- An external contact may be part of a user's visible directory, but not part of the full internal directory.
- A user's visible directory is a policy-filtered view, not a simple copy of all organization people.

## 12. Visibility Scopes

**inviter_only**  
Only the inviting employee or assigned employee sees and communicates with the external contact.

**assigned_team**  
Members of the assigned team can see and communicate with the external contact.

**department**  
A department can see and communicate with the external contact.

**organization**  
All active employees can see the external contact. This should be used cautiously and may require admin approval.

**project_room**  
The external contact is visible only inside a specific external project room. Later scope.

**admin_only**  
The contact is visible to admins for review, audit, suspension, or reassignment, but not generally visible to employees.

Examples:

- A client is visible only to the assigned account manager.
- A client is visible to the entire sales team.
- A supplier is visible to the procurement department.
- A key partner is visible to the whole organization after admin approval.

## 13. Ownership And Reassignment

An external contact belongs to the organization, not personally to the manager who invited them.

The manager may be the assigned owner/handler, but the control plane remains the source of authority. If the manager leaves, their external contacts must not be lost. Admins must be able to reassign external contacts to another employee or team.

Rules:

- External relationship ownership is organizational.
- Assigned employee/team is operational responsibility, not personal ownership.
- Revoked employees lose access to assigned external contacts after sync/enforcement.
- Admins can reassign external contacts.
- Reassignment should produce an audit event.
- Later, the external contact may receive a notification or updated contact card for the new manager.

## 14. External Communication Rules

- External contacts can write only to allowed chats, employees, teams, or rooms.
- Employees see a clear warning or badge when communicating with an external contact.
- The app may warn: "This is an external contact."
- Internal groups are not available to external contacts by default.
- External project or team rooms are possible later.
- Forwarding internal information to external contacts remains a user responsibility, but UI should reduce mistakes through badges and warnings.
- Search, contact picking, and group creation must distinguish internal members from external contacts.

## 15. Corporate Control Plane Requirements

The admin/control plane must support:

- external contact list;
- external organization list, later if not MVP;
- external invite creation and revocation;
- visibility into who invited an external contact;
- assigned employee/team display;
- visibility scope changes;
- suspend, revoke, archive, and reactivate operations;
- external contact reassignment;
- audit log for invite, activation, reassignment, suspension, revocation, archive, and scope change;
- policies controlling who may invite external contacts;
- external relationship search and filtering by status, assigned owner, team, type, and organization.

## 16. Policy Requirements

Policies should define:

- who can invite external contacts;
- which external contact types are allowed;
- whether admin approval is required;
- external invite lifetime;
- `maxUses`;
- allowed external email domains if needed;
- whether external contacts can invite others from their side;
- whether project rooms with multiple external participants are allowed;
- whether organization-wide external visibility requires approval;
- retention and audit requirements for external relationship history.

MVP policy recommendation:

- external invites allowed only for admin/manager roles, or configurable by organization;
- one-to-one external invite only;
- admin can revoke and reassign;
- external contact does not see internal directory;
- default visibility is `inviter_only` or assigned employee;
- external contacts cannot invite other external users in MVP.

## 17. Security / Privacy Risks

- Accidental exposure of the internal corporate directory to an external person.
- Forwarded external invite links used by the wrong person.
- Former employee retaining access to old external contacts.
- Client assigned to the wrong manager.
- Spoofed external identity or wrong email ownership.
- Insufficient email ownership validation.
- Missing audit trail for invite, activation, reassignment, or revocation.
- Leakage through screenshots, exports, support reports, raw logs, or credentials.
- Confusion between employee membership and guest access.
- External contacts appearing in "All employees" or internal groups by mistake.

Risk controls:

- separate internal and external invite types;
- visible external badges;
- scoped visible directory;
- admin reassignment and revocation;
- audit events;
- sanitized exports;
- no raw logcat, raw AUTH, app passwords, or unnecessary personal data in reports.

## 18. MVP Scope

- External contact entity.
- External invite link type.
- One-to-one external relationship.
- External contacts section in directory.
- External badge in directory and chat.
- Visibility scope: `inviter_only` or assigned employee.
- Admin list of external contacts.
- Revoke/archive external contact.
- Reassign external contact to another employee or team.
- External contact does not receive internal directory.
- External invite creates `ExternalRelationship`, not `Membership`.

## 19. Later Scope

- External organizations.
- Team/project rooms with external contacts.
- Department-level and organization-level visibility.
- External contact self-profile.
- External organization admin or delegated contact management.
- Signed external invites.
- External directory snapshots.
- CRM integration.
- Support/helpdesk workflows.
- External contact lifecycle automation.
- Project-room specific guest policies.
- Multiple external participants in one scoped room.

## 20. Acceptance Criteria

- External invite does not create internal membership.
- External contact does not see internal directory.
- Employee sees external contact in external section.
- External contact can communicate only with allowed employee/team/room.
- Revoked employee loses access to assigned external contacts after sync/enforcement.
- Admin can revoke or reassign external contact.
- External contact is visibly marked in the UI.
- Audit event is created for invite, activation, revoke, archive, reassignment, and scope change.
- External contacts do not appear in "All employees".
- External contact visibility follows the selected scope.

## 21. Open Questions

- Who can invite external contacts in MVP?
- Is admin approval required before activation?
- Is email ownership verification required for external contacts?
- Can external contacts communicate with each other?
- Are external project rooms needed in MVP or later?
- How exactly should clients be transferred when a manager leaves?
- Is CRM integration required in early field trials?
- Should external contacts use the same APK and app identity as employees?
- What is the default visibility scope for external contacts?
- Should organization-wide external contacts require approval?
- What audit retention period is required for external relationship events?

## 22. Product Review Refinements

These refinements are product requirements from [Product PRD Review Addendum](../PRODUCT_PRD_REVIEW_ADDENDUM.md).

### External Email Ownership Verification

External relationship activation requires proof that the external contact controls the target mailbox.

Preferred flow:

1. Control Plane sends a verification code/challenge to the external email.
2. External contact enters the code in the app.
3. Control Plane validates the code against the external invite.
4. Activation proceeds only if invite, email, policy, and external relationship checks pass.

IMAP/SMTP login can support transport readiness but does not replace product-level ownership proof.

### Control Plane Unavailable

External contact activation requires Control Plane availability in MVP.

If Control Plane is unavailable:

- external invite opening may show pending state;
- APK installation or provider setup may happen if possible;
- external relationship activation is delayed;
- external visible directory sync is delayed;
- external contact must not receive internal directory data from cache or fallback state.

### Stale External Directory Behavior

External contacts can receive only their scoped visible directory, never the internal corporate directory.

If external visible directory state is stale:

- the app should show a stale warning where appropriate;
- communication with previously allowed employee/team may continue if policy and transport allow it;
- new external relationship activation, reassignment, revoke, and visibility changes are delayed until sync;
- stale cache must not expand external visibility.

### Reassignment UX

When an assigned manager leaves or loses access:

- the external relationship remains organization-owned;
- admin can reassign the external contact to another employee or team;
- old manager loses access after sync/enforcement;
- external contact sees updated assigned person/team where policy allows;
- external contact must not see internal HR reason;
- old chat may become historical, unavailable, or show a transfer notice; exact behavior is Blueprint scope.

### External Invite Abuse

External invite safety must cover forwarding, screenshot leak, wrong recipient, expired replay, and repeated failed attempts.

Requirements:

- expiry;
- `maxUses`;
- expected email binding where possible;
- rate limits;
- failed attempt audit;
- invite revoke;
- revoke all active invites by issuer/admin action;
- admin/support visibility into suspicious external invite activity.

## 23. MVP / Later / Non-goals Summary

MVP covers external contact entity, one-to-one external invite, external relationship, external directory section, visible external badges, assigned employee visibility, admin revoke/archive/reassign, and strict no internal-directory exposure.

Later covers external organizations, project rooms, broader visibility scopes, external self-profile, signed external invites, CRM/helpdesk integrations, and lifecycle automation.

Non-goals exclude turning external contacts into employees, exposing full internal directory, full CRM, full helpdesk, customer portal, video calls, real-time voice calls, and automatic client import without policy.
