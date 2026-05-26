# Contributing

This repository is in bootstrap/product-definition stage.

## Ground Rules

- Keep product docs, reports, and prototype source separate from local upstream clones.
- Do not vendor-copy Delta Chat / Chatmail repositories without an explicit architecture decision.
- Do not commit generated build artifacts, APKs, `.gradle`, `build`, `app/build`, or `node_modules`.
- Do not commit secrets, app passwords, raw AUTH payloads, raw logcat, or unredacted runtime evidence.
- Keep provider-specific evidence separate from product architecture; Mail.ru / VK Mail is a baseline, not the product boundary.

## Documentation

Product PRDs live under `docs/product/`.

Reports should be placed under dated folders in `docs/reports/YYYY-MM-DD/` unless an explicit compatibility path is required.

## Prototype

The Android Diagnostics prototype lives under `prototypes/android-diagnostics/`. Commit source, docs, schema, Gradle wrapper, and redacted sample reports only.
