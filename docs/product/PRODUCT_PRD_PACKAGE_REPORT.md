# Product PRD Package Report

Date: 2026-05-14

## 1. Какие документы созданы

Создан пакет PRD для Corporate IMAP Messenger в `docs/product/`:

- [Root PRD](PRD_ROOT_CORPORATE_IMAP_MESSENGER.md)
- [Domain PRD Index](DOMAIN_PRD_INDEX.md)
- [Android Messenger Client PRD](domains/PRD_ANDROID_MESSENGER_CLIENT.md)
- [Corporate Control Plane PRD](domains/PRD_CORPORATE_CONTROL_PLANE.md)
- [Corporate Directory PRD](domains/PRD_CORPORATE_DIRECTORY.md)
- [Invite Onboarding & Distribution PRD](domains/PRD_INVITE_ONBOARDING_DISTRIBUTION.md)
- [Provider Transport Profiles PRD](domains/PRD_PROVIDER_TRANSPORT_PROFILES.md)
- [Diagnostics & Transport Verification PRD](domains/PRD_DIAGNOSTICS_AND_TRANSPORT_VERIFICATION.md)
- [External Contacts & Guest Access PRD](domains/PRD_EXTERNAL_CONTACTS_AND_GUEST_ACCESS.md)
- [Product PRD Review Addendum](PRODUCT_PRD_REVIEW_ADDENDUM.md)
- [Product PRD Refinement Report](PRODUCT_PRD_REFINEMENT_REPORT.md)
- [Infrastructure Assumptions](../infrastructure/INFRASTRUCTURE_ASSUMPTIONS.md)
- [Server Audit Report](../infrastructure/SERVER_AUDIT_REPORT.md)
- [Product Decisions Log](decisions/PRODUCT_DECISIONS_LOG.md)
- [Product Context Handoff](handoff/PRODUCT_CONTEXT_HANDOFF.md)

## 2. Какие домены покрыты

Покрыты семь продуктовых доменов:

- Android Messenger Client;
- Corporate Control Plane;
- Corporate Directory;
- Invite Onboarding & Distribution;
- Provider Transport Profiles;
- Diagnostics & Transport Verification;
- External Contacts & Guest Access.

Review addendum created:

- Control Plane stale/offline behavior;
- email verification;
- multi-workspace scoping;
- managed group enforcement;
- trust/identity states;
- RBAC matrix;
- canonical directory hash rules;
- invite abuse controls;
- external contact reassignment UX;
- app release lifecycle.
- infrastructure assumptions for `messenger-imap.speechbattle.com`, public IP `146.19.211.30`, internal deploy host `192.168.7.64`, existing Traefik, and future non-destructive server audit.
- read-only server audit identifying Traefik container `traefik`, shared Docker network `traefik-net`, existing compose stacks, and `/opt/stacks` as likely deployment convention.

## 3. Какие документы наиболее детализированы

Наиболее детализированы:

- Root PRD, потому что фиксирует общую продуктовую рамку, принципы, MVP, риски, roadmap и открытые решения.
- Corporate Directory PRD, потому что корпоративная адресная книга является ключевой B2B-функцией и требует явных правил по source of truth, version/hash, member statuses и revoked employee behavior.
- External Contacts & Guest Access PRD, потому что реальная B2B-коммуникация требует клиентов/поставщиков/партнеров/подрядчиков без превращения их в сотрудников и без раскрытия внутренней адресной книги.
- Product PRD Review Addendum, потому что перед Blueprint фиксирует cross-domain уточнения: Control Plane может быть недоступен в whitelist mode, stale cache обязателен, email verification отделена от transport diagnostics, RBAC и trust states должны быть явными.

Остальные доменные PRD достаточно детализированы для дальнейшего обсуждения и подготовки Blueprint, но намеренно не превращены в технические спецификации.

## 4. Какие открытые решения повторяются чаще всего

Чаще всего повторяются:

- thin Delta Chat Android fork vs custom shell over chatmail/core;
- GPL/MPL compliance и модель распространения;
- первый MVP provider set после Mail.ru / VK Mail baseline;
- directory authority model и canonical payload для hash;
- stale/expired directory thresholds и поведение при недоступном Control Plane;
- email verification UX и возможность later IMAP challenge reading;
- one active workspace UI vs multi-workspace UI;
- trust/identity state model и SecureJoin-equivalent indicators;
- Control Plane RBAC-to-permission mapping;
- app release lifecycle policy;
- invite policy и правила activation;
- external invite policy, visibility scopes и правила reassignment;
- background / locked-screen reliability target;
- branding, package identity и distribution channel;
- граница между standalone diagnostics и in-client Check Transport.

## 5. Что рекомендуется сделать следующим шагом

Рекомендуемые следующие шаги:

- провести review PRD-пакета;
- принять решение по fork vs custom shell;
- принять решение по GPL/MPL distribution acceptability;
- подготовить Android IMAP Messenger MVP Blueprint;
- подготовить Corporate Control Plane Blueprint;
- учесть в Blueprints stale Control Plane mode, email verification, workspace scoping, trust states, RBAC, invite abuse controls и app release lifecycle;
- использовать `docs/infrastructure/SERVER_AUDIT_REPORT.md` как input для Deployment Blueprint;
- после выбора Control Plane stack подготовить `docs/blueprints/DEPLOYMENT_BLUEPRINT.md`;
- включить External Contacts & Guest Access в будущие Blueprint;
- при необходимости подготовить отдельный Directory Blueprint;
- определить MVP-объём in-client diagnostics;
- спланировать полевую проверку дополнительных провайдеров и сетевых контекстов.

## 6. Что намеренно не делалось

Намеренно не делалось:

- не писался код;
- не форкался Delta Chat Android;
- не менялся chatmail/core;
- не создавались UI-макеты;
- не писались детальные API-specs;
- не создавалась Mail.ru-only архитектура;
- не делался вывод, что все провайдеры работают в whitelist-контекстах;
- не предполагалось, что Control Plane доступен в whitelist-контекстах;
- не смешивались transport diagnostics и email ownership proof;
- не включался iOS в текущий scope;
- не делался APK-by-email primary distribution flow;
- не обещался production-ready продукт;
- не смешивались internal membership и external relationship;
- не предполагалось, что external contacts получают internal corporate directory;
- не добавлялись секреты, реальные email, app passwords или raw logs.
- не выполнялся deploy;
- не менялся Traefik;
- не менялись существующие сервисы/контейнеры;
- не добавлялись `.env`, SSH keys, APK signing keys или server credentials.

## 7. MVP / Later / Non-goals Summary

MVP зафиксирован как Android-first корпоративный мессенджер с invite onboarding, email verification, external contact invite handling, provider profiles, Mail.ru / VK Mail baseline, manual/custom profiles, базовой диагностикой, one-to-one chats, basic groups, corporate directory sync with stale cache behavior, external contacts section и control-plane администрированием.

Later scope включает background reliability, signed IMAP/system-account directory/policy fallback, external organizations/project rooms, multi-workspace UI if deferred, расширенную provider/operator validation matrix, advanced policies, distribution strategy, iOS strategy, audio transcription и дополнительные платформы.

Non-goals включают video calls, real-time voice calls, production-grade background guarantees, full MDM, silent unsafe address book import, all-provider whitelist proof, iOS support in current scope, превращение external contacts в employees, раскрытие internal directory внешним контактам и IMAP/SMTP transport rewrite.

## 8. External Contacts open questions

Оставшиеся вопросы по новому домену:

- кто может приглашать внешних контактов в MVP;
- нужен ли admin approval для external invite или только для широких visibility scopes;
- какой default visibility scope: inviter_only или assigned_employee;
- нужна ли email ownership verification для внешнего контакта;
- нужны ли external project rooms в MVP или later;
- как именно уведомлять клиента при reassignment менеджера;
- требуется ли CRM/helpdesk integration в ранних пилотах;
- используют ли external contacts тот же APK и тот же app mode, что сотрудники.

## 9. PRD Review Refinement open questions

Оставшиеся вопросы после review refine:

- какие значения `directoryStaleAfter` и `directoryExpiredAfter` выбрать для MVP;
- какие действия блокировать при stale/expired Control Plane state;
- использовать ли one active workspace UI в MVP или сразу multi-workspace UI;
- разрешать ли later IMAP challenge reading для email verification;
- какие роли получают право создавать external invites по умолчанию;
- нужен ли admin approval для broad external visibility scopes;
- какие exact permission keys соответствуют RBAC matrix;
- какие версии считать deprecated/blocked/minSupported в app release lifecycle;
- как показывать SecureJoin-equivalent verification, если она входит в MVP.
