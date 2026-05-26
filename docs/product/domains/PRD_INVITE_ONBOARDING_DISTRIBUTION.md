# Invite Onboarding & Distribution PRD

Date: 2026-05-14

Status: high-level domain PRD.

Root PRD: [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)

## 1. Purpose

Define the product requirements for invite-based onboarding, APK distribution, enrollment, membership activation, and external contact activation for Corporate IMAP Messenger.

Core rule:

```text
APK download is public or semi-public.
Organization membership requires invite/enrollment.
```

Important distinction:

```text
Internal organization invite creates Membership.
External contact invite creates ExternalRelationship.
```

The product should provide assisted one-shot onboarding: as few steps as practical, but honest about Android sideload limitations and fallback paths.

## 2. Problem

Employees need a simple way to join the organization, install the Android client, configure messaging transport, and become active members.

However:

- a browser cannot invisibly or fully seamlessly install an APK on Android;
- sideloading requires explicit user action and device/source permission;
- downloading the APK must not grant organization access;
- not every invite is an employee invite;
- external contacts need a join path that does not expose the internal directory or create membership;
- invite tokens can expire, be revoked, or be restricted;
- provider setup and diagnostics may fail;
- support needs a clear fallback when deep links or app links do not work.

The product must balance convenience with control.

## 3. Goals

- Allow authorized admins to invite employees.
- Provide a join landing page that guides the employee.
- Support APK download with visible release metadata.
- Support deep link/app link token handoff when possible.
- Support fallback invite code entry.
- Support QR code onboarding where useful.
- Require enrollment before membership activation.
- Support external invite flows that create external relationships instead of membership.
- Connect invite, provider setup, diagnostics, and first directory sync into one guided flow.
- Keep distribution separate from membership.
- Avoid promising invisible installation or production-grade distribution in MVP.

## 4. Non-goals

- No invisible APK install from browser.
- No full MDM.
- No full app store distribution strategy in MVP.
- No guarantee that sideload friction disappears.
- No membership activation from APK download alone.
- No external contact membership activation through an employee invite path.
- No internal directory exposure through external invite links.
- No bypass of provider setup or diagnostics where policy requires them.
- No storage of app passwords in invite links.
- No detailed API specification.

## 5. Core Concepts

**Invite**  
An admin-created enrollment artifact that authorizes a specific user, domain, group, or limited use case to attempt joining an organization.

**Internal Organization Invite**  
An invite that can lead to organization membership after invite validation, provider setup, diagnostics, activation, and first directory sync.

**External Contact Invite**  
An invite that can lead to an external relationship with a client, supplier, partner, contractor, or other counterparty. It does not create membership and must not grant full internal directory visibility.

**Invite Token**  
A secret or semi-secret token embedded in a link, QR code, or fallback code. It identifies the invite and must be protected.

**Join Landing Page**  
A browser page that explains the organization join flow, shows the app release, offers APK download, and provides fallback instructions.

**APK Download**  
The Android client binary download. It may be public or semi-public, but it does not create membership.

**Deep Link / App Link**  
A link that can open the installed app and pass the invite token or invite reference into the onboarding flow.

**Fallback Invite Code**  
A manually entered code used when app link handoff fails, the APK was installed separately, or QR scanning is preferred.

**QR Code**  
A scannable representation of the invite link or fallback code for assisted installation and field support.

**Enrollment**  
The process of proving invite validity, completing provider setup, passing required diagnostics, and activating membership.

**Membership Activation**  
The control-plane state change that marks the invited user as active, subject to policy.

**External Relationship Activation**  
The control-plane state change that marks an external contact relationship as active, subject to policy. It grants only scoped guest/contact access.

## 6. Invite Types

**Individual Email Invite**  
Targets one expected email address or person. Recommended MVP default.

**Domain Invite**  
Allows users from an allowed domain to join subject to constraints. Useful later or for controlled pilots.

**Limited Group Invite**  
Activates membership into a limited group, team, or pilot cohort. Useful for field testing and staged rollout.

**One-time Invite**  
Can be used once and then becomes consumed. Useful for high-control onboarding.

**One-to-one External Contact Invite**  
Allows an employee or manager to invite one external contact into a scoped communication relationship. Recommended MVP default for guest access.

**Team / Project External Invite**  
Allows an external contact to communicate with an assigned team or project room. Later scope unless explicitly selected for MVP.

## 7. Invite Constraints

Each invite should support these constraints where applicable:

- `expiresAt`: time after which the invite cannot be used;
- `maxUses`: maximum number of successful activations;
- `allowedEmail`: specific email identity allowed to use the invite;
- `allowedDomain`: domain restriction for accepted account identity;
- `createdBy`: admin or manager who created the invite;
- `revokedAt`: time at which an invite was manually revoked;
- `usedBy`: member or account identity that consumed the invite.

MVP can prioritize individual internal email invites, one-time invites, and one-to-one external contact invites while keeping the model open for domain, group, team, and project invites.

## 8. User Flow

1. Employee receives an invite email or controlled invite message.
2. Employee opens invite link.
3. Join landing page explains the organization and required steps.
4. Employee downloads the APK using an explicit button.
5. Employee manually installs the APK through Android installation flow.
6. Employee returns to browser or opens the app.
7. App receives invite token through deep link/app link where available.
8. If token handoff fails, employee enters fallback invite code or scans QR code.
9. App resolves invite through the control plane.
10. Employee completes provider setup.
11. Employee runs required transport diagnostics or check.
12. Control plane activates membership if policy requirements are met.
13. App performs first directory sync.
14. Employee enters the messenger experience.

This is assisted one-shot onboarding: one guided flow with explicit installation steps and fallback, not invisible installation.

## 8.1 External Contact Invite Flow

1. Manager selects "Invite external contact".
2. Manager enters external contact metadata such as name, company, email if known, and contact type.
3. Manager selects visibility scope according to policy.
4. Control plane creates an external invite.
5. External contact opens the invite link.
6. Landing page explains that this is external access, not employee membership.
7. External contact downloads the APK if needed.
8. App receives the external invite through deep link/app link or fallback code.
9. External contact completes external enrollment and provider setup.
10. Control plane activates the external relationship if policy requirements pass.
11. Employee sees the contact in the External Contacts section.
12. External contact sees only the allowed employee, team, or room.
13. External contact does not receive the internal corporate directory.

## 9. Landing Page Requirements

The join landing page should:

- identify the organization;
- explain that membership requires invite/enrollment;
- show the current recommended Android release;
- provide explicit APK download button;
- show fallback invite code or QR code when policy allows;
- explain that Android may require manual install permission;
- provide "open app" action after install where feasible;
- avoid exposing unnecessary invite secret details;
- show expired/revoked/invalid invite states;
- clearly distinguish employee join from external contact join;
- for external invites, state that the invite grants only scoped guest/contact access;
- provide support contact or support instructions.

The page must not imply that opening the page or downloading the APK grants membership.

## 10. APK Release / Download Requirements

Every app release exposed for download should include:

- version;
- size;
- SHA-256;
- channel;
- release date;
- recommended/current marker;
- optional release notes;
- minimum Android version if known;
- status such as active, deprecated, or blocked.

The SHA-256 is for release integrity communication and support. The later Blueprint should define how the hash is displayed, verified, and audited.

## 11. Security

- Invite tokens must be treated as sensitive.
- Invites must expire or be revocable.
- Invite use should be auditable.
- Invite constraints must be enforced by the control plane.
- APK download must not imply membership.
- External invite use must not imply membership.
- External invites must not expose internal directory data.
- Fallback codes should have limited lifetime or use count.
- Invite links must not contain credentials or app passwords.
- Diagnostic reports must remain sanitized.
- The app should avoid accepting stale or already consumed invite tokens.
- Support flows should not require users to share app passwords or raw logs.

## 12. MVP Scope

- Individual email invite.
- One-time invite support if feasible.
- One-to-one external contact invite.
- Invite expiry and revocation.
- Join landing page.
- Explicit APK download button.
- App link/deep link token handoff where feasible.
- Fallback invite code.
- Optional QR code.
- Provider setup after invite resolution.
- Diagnostic check before activation where policy requires it.
- Membership activation.
- External relationship activation.
- First directory sync.
- APK release metadata: version, size, SHA-256, channel, release date.

## 13. Later Scope

- Domain invites.
- Limited group invites.
- Team external invites.
- Project/room external invites.
- External organization invites.
- Rich QR enrollment flows.
- Managed distribution or MDM integration.
- App store, enterprise store, or F-Droid strategy.
- Automatic update policy.
- Device posture checks.
- More advanced fraud/abuse controls.
- Multi-step admin approval for sensitive enrollments.

## 14. Acceptance Criteria

- The product rule states that APK download is not membership.
- An admin can create an invite with expiry and constraints.
- An employee can open a join landing page.
- The landing page provides explicit APK download and fallback instructions.
- The flow supports deep link/app link token handoff where available.
- The flow supports manual fallback invite code.
- The flow acknowledges Android manual install limitations.
- The app requires invite/enrollment before activating membership.
- The app distinguishes internal organization invites from external contact invites.
- External invite activation creates an external relationship, not membership.
- External contact onboarding does not expose the internal corporate directory.
- Provider setup and diagnostics can occur before activation according to policy.
- First directory sync happens after activation.
- APK release metadata includes version, size, SHA-256, channel, and release date.

## 15. Open Questions

- Are one-time invites mandatory in MVP or optional?
- Are domain invites allowed in MVP or deferred?
- Who can create external contact invites in MVP?
- Is admin approval required for external contact activation?
- What is the default external invite visibility scope?
- Should invite email be sent by control plane or by admins manually?
- What is the acceptable invite token lifetime?
- How should app link verification be handled for internal distribution?
- What support path is needed for users who cannot sideload APKs?
- Should APK SHA-256 be user-visible, support-visible, or both?
- What is the first acceptable distribution channel for field testing?

## 16. Product Review Refinements

These refinements are product requirements from [Product PRD Review Addendum](../PRODUCT_PRD_REVIEW_ADDENDUM.md).

### Email Verification Code Flow

Invite enrollment must prove mailbox ownership before activating membership or an external relationship.

Product flow:

1. User opens an internal organization invite or external contact invite.
2. App validates invite format locally and asks Control Plane to resolve the invite.
3. Control Plane sends a verification code/challenge to the expected email address.
4. User enters the verification code in the app.
5. Control Plane verifies the code and checks `allowedEmail` / `allowedDomain` constraints against the verified email.
6. Activation proceeds only after invite validity, email ownership, and policy checks pass.

IMAP/SMTP login success proves transport readiness for a configured mailbox but does not replace product-level ownership proof.

Later, the app may read a verification challenge through IMAP if a secure design is approved. That is not assumed for MVP.

### Control Plane Required For Activation

Invite activation requires Control Plane availability in MVP.

If the Control Plane is unavailable in whitelist or restricted-network mode:

- APK installation can still happen if the artifact is available;
- provider setup or local diagnostics may proceed where useful;
- internal membership activation is delayed;
- external relationship activation is delayed;
- directory sync is delayed;
- app should show clear pending/stale state instead of implying active membership.

### Invite Abuse Controls

Invite flows must account for:

- invite forwarding;
- external invite forwarding;
- screenshot leak;
- expired invite replay;
- wrong email using invite;
- repeated failed code/invite attempts;
- external invite sent to the wrong person.

Requirements:

- expiry;
- `maxUses`;
- expected email/domain binding where possible;
- rate limits;
- failed attempt audit;
- admin revoke invite;
- admin revoke all active invites by issuer when needed;
- suspicious activity visibility for admin/support, with alerting later.

### Installation Under Limited Internet

Normal internet is usually required for the Android APK download/install flow.

APK-by-email is acceptable only as an Android emergency fallback. It is not the primary distribution path and does not imply organization membership. The app must still complete invite resolution, email verification, provider setup, policy checks, and directory sync when Control Plane is reachable.

iOS support is out of current scope. iOS would need a separate App Store, TestFlight, MDM-like, or enterprise distribution path later.

## 17. MVP / Later / Non-goals Summary

MVP covers individual internal invite, one-to-one external contact invite, landing page, explicit APK download, deep link or fallback code, provider setup, diagnostics, membership activation, external relationship activation, and first directory sync.

Later covers domain/group invites, team/project external invites, external organization invites, managed distribution, app stores, automatic updates, and advanced device/posture controls.

Non-goals exclude invisible install, full MDM, membership by download, guest membership by external invite, internal directory exposure to external contacts, credential-in-link flows, and a final production distribution strategy.
