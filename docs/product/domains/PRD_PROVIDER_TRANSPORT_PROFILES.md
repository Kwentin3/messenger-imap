# Provider Transport Profiles PRD

Date: 2026-05-14

Status: high-level domain PRD.

Root PRD: [Corporate IMAP Messenger Root PRD](../PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)

## 1. Purpose

Define the product model for IMAP/SMTP provider profiles used by Corporate IMAP Messenger.

Provider profiles keep the product provider-agnostic while allowing the MVP to use Mail.ru / VK Mail as the first accepted transport baseline. Profiles also attach diagnostic status to provider/network evidence instead of assuming that a provider works because its website is reachable.

## 2. Problem

The product needs practical defaults for known providers, but hardcoding one provider would create a fragile and strategically wrong architecture.

Risks without provider profiles:

- Mail.ru-only product behavior;
- inability to support customer-specific IMAP/SMTP servers;
- unclear distinction between preset and verified provider;
- unsafe assumptions about whitelist readiness;
- repeated manual configuration errors;
- no place to attach diagnostic evidence.

Provider profiles are the product layer that separates provider-specific settings from the rest of the messenger.

## 3. Goals

- Support provider-agnostic IMAP/SMTP configuration.
- Treat Mail.ru / VK Mail as the first accepted baseline.
- Include Yandex and Rambler as candidate profiles, not automatically trusted profiles.
- Require manual/custom profile support.
- Define provider profile fields needed for onboarding and diagnostics.
- Track diagnostic status by evidence.
- Avoid using website availability as proof of IMAP/SMTP transport availability.
- Let the control plane recommend or restrict provider profiles per organization.

## 4. Non-goals

- No Mail.ru-only architecture.
- No claim that all listed providers are whitelist-ready.
- No final provider compatibility matrix in this PRD.
- No provider-specific code design.
- No detailed API specification.
- No storage of user credentials inside provider profile definitions.
- No bypass of diagnostics for whitelist-ready claims.
- No rewrite of IMAP/SMTP transport.

## 5. Core Concepts

**ProviderProfile**  
Product-level definition of a provider or provider family. Includes display name, domain hints, guidance, diagnostic status, and one or more transport profiles.

**TransportProfile**  
Concrete IMAP/SMTP connection settings: hosts, ports, encryption, username mode, auth method, and related hints.

**ProviderFamily**  
A grouping of providers that share infrastructure or endpoints. Mail.ru / VK Mail is treated as one initial transport family for baseline purposes.

**DiagnosticStatus**  
Evidence-based status assigned after diagnostics in a known context. It is not inferred from provider brand.

**Manual / Custom Profile**  
A user/admin-defined IMAP/SMTP profile for providers outside preset profiles or customer-owned infrastructure.

## 6. Initial Profiles

**Mail.ru / VK Mail family**  
First accepted baseline. Known baseline endpoints:

- IMAP: `imap.mail.ru:993`
- SMTP: `smtp.mail.ru:465`

This baseline does not justify a Mail.ru-only architecture.

**Yandex**  
Candidate preset. Requires diagnostics for target network context before being marked verified or whitelist-ready.

**Rambler**  
Candidate preset. Requires diagnostics for target network context before being marked verified or whitelist-ready.

**Manual / Custom**  
Required for provider-agnostic architecture. Supports customer-specific IMAP/SMTP servers and providers not covered by presets.

## 7. Provider Profile Fields

Minimum provider profile fields:

- `providerId`
- `displayName`
- domain hints
- IMAP host
- IMAP port
- IMAP encryption: SSL/TLS or STARTTLS
- IMAP username mode
- SMTP host
- SMTP port
- SMTP encryption: SSL/TLS or STARTTLS
- SMTP username mode
- auth method
- app password hint
- diagnostic status
- last successful diagnostic report ID

Important product rule: provider profiles do not contain user passwords or app passwords.

## 8. Diagnostic Statuses

**untested**  
No accepted diagnostics for the relevant context.

**wifi_verified**  
Diagnostics passed in a Wi-Fi context.

**normal_mobile_verified**  
Diagnostics passed in a normal mobile network context.

**whitelist_verified**  
Diagnostics passed in a documented whitelist or restricted-network context.

**failed**  
Diagnostics failed in a known context.

**degraded**  
Diagnostics partially passed or passed with limitations, such as send success but unreliable receive correlation, IDLE limitations, or placement issues.

Diagnostic status should include the relevant network context and report reference. A provider can be verified in one context and untested or failed in another.

## 9. Product Rules

- Mail.ru / VK Mail is accepted as the first transport baseline.
- Product architecture must remain provider-agnostic.
- Manual/custom profile support is required.
- Whitelist-ready status must be evidence-based.
- Provider website availability is not proof of IMAP/SMTP transport availability.
- Yandex and Rambler are candidate profiles until diagnostics confirm status.
- Diagnostic reports must be sanitized.
- Provider profiles must not embed credentials.
- Organization admins may recommend or restrict provider profiles through the control plane.
- Failed or degraded profiles should show actionable diagnostic stage where available.

## 10. Functional Requirements

- The product must represent provider profiles separately from credentials.
- The product must support Mail.ru / VK Mail family profile.
- The product must support manual/custom profile creation or entry.
- The product should include Yandex and Rambler as candidate profiles if UX wording avoids unverified claims.
- The product must expose diagnostic status for each profile.
- The product must store or reference last successful diagnostic report ID.
- The product must support provider profile use in Android onboarding.
- The control plane should be able to define organization-level recommended profiles.
- The product must prevent untested provider profiles from being labeled whitelist-ready.
- The product must support failure/degraded states.

## 11. MVP Scope

- Mail.ru / VK Mail family baseline profile.
- Manual/custom IMAP/SMTP profile.
- Candidate Yandex and Rambler profile definitions or placeholders with untested status.
- Basic provider selection in Android onboarding.
- Provider guidance such as app-password hints.
- Diagnostic status model.
- Last successful sanitized diagnostic report reference.
- Admin visibility into profile status.

## 12. Later Scope

- More provider presets.
- Provider-specific folder mapping.
- Provider-specific rate limit guidance.
- OAuth or alternative auth methods where applicable.
- Organization-level managed provider restrictions.
- Field diagnostic campaign dashboards.
- Automatic provider profile updates.
- Provider deprecation and migration flows.

## 13. Acceptance Criteria

- Mail.ru / VK Mail family is documented as first accepted baseline.
- Mail.ru / VK Mail endpoints are documented.
- Architecture remains provider-agnostic.
- Manual/custom profile is required.
- Yandex and Rambler are documented as candidate profiles requiring diagnostics.
- Provider profile fields include IMAP/SMTP hosts, ports, encryption, username modes, auth method, app-password hint, diagnostic status, and last report ID.
- Diagnostic statuses include untested, wifi_verified, normal_mobile_verified, whitelist_verified, failed, degraded.
- PRD states that website availability is not proof of IMAP/SMTP availability.
- PRD states that whitelist-ready status is evidence-based.
- PRD excludes credentials from provider profile definitions.

## 14. Open Questions

- Should Yandex and Rambler appear in MVP UI or only admin configuration?
- What exact app-password guidance is acceptable per provider?
- Should custom profiles be employee-entered, admin-defined, or both?
- How should provider profile changes affect already enrolled clients?
- What evidence threshold is required for admins to mark a provider recommended?
- How should failed provider statuses be shown without creating support noise?
- Should provider profile data be global, organization-specific, or layered?

## 15. Product Review Refinements

These refinements are product requirements from [Product PRD Review Addendum](../PRODUCT_PRD_REVIEW_ADDENDUM.md).

### Organization / Workspace Scope

Provider profiles and provider diagnostic status are scoped by organization/workspace.

Required scoping:

- `organizationId` and/or `workspaceId`;
- provider family/profile ID;
- network context such as Wi-Fi, normal mobile, operator, region, and whitelist context where evidence exists;
- diagnostic report reference;
- last verified time and evidence status.

Manual/custom profiles must also be organization-scoped. A custom profile accepted for one workspace is not automatically accepted globally.

### Control Plane Stale Mode

The Control Plane may be unreachable in whitelist or restricted-network mode while provider IMAP/SMTP transport still works.

Product behavior:

- cached provider profiles may be used while stale if policy allows;
- provider profile changes are delayed until Control Plane sync;
- new organization policy changes do not apply until sync;
- diagnostics may run locally and upload later;
- UI should distinguish provider transport availability from Control Plane availability.

### Email Verification Separation

Provider login and diagnostics prove transport readiness. They do not prove product-level mailbox ownership for enrollment.

Email ownership verification uses the invite verification code/challenge flow. Provider transport diagnostics can support troubleshooting and onboarding readiness, but must not replace invite activation or membership/external relationship verification.

## 16. MVP / Later / Non-goals Summary

MVP covers Mail.ru / VK Mail baseline, manual/custom profiles, candidate Yandex/Rambler handling, diagnostic status, and admin/client provider profile use.

Later covers expanded provider catalog, OAuth, managed restrictions, dashboards, automatic updates, and migration/deprecation flows.

Non-goals exclude Mail.ru-only architecture, all-provider whitelist claims, API specs, credential storage in profiles, and transport rewrites.
