# Android Fork Strategy Decision

Date: 2026-05-13

Status: preliminary engineering decision for the next Blueprint.

## Context

MVP-0a diagnostics confirmed Mail.ru as the first accepted IMAP/SMTP transport baseline. The next stage is an Android-first corporate messenger architecture using Delta Chat / Chatmail as a possible base.

The immediate need is a manageable fork/workspace baseline, not a rebrand or deep transport rewrite.

## Options

### Option A: Fork `deltachat-android` as a separate GitHub fork

Pros:

- preserves upstream history;
- makes upstream pulls and rebases straightforward;
- correct direction for a GPL Android client;
- suitable for a thin fork.

Cons:

- requires managing GPL obligations;
- can drift into a large long-lived fork;
- Android build is heavier than the diagnostics APK build.

### Option B: New product repo with `deltachat-android` as git submodule

Pros:

- keeps product docs, diagnostics and integration notes separate from upstream;
- avoids mixing unrelated docs into the Android client repository;
- can reference a specific fork/commit.

Cons:

- changes to the Android client still need to happen inside the fork/submodule;
- submodule discipline is required;
- review and CI become more complex.

### Option C: New repo with vendor copy

Pros:

- looks simple initially.

Cons:

- loses clean upstream linkage;
- makes upstream updates harder;
- increases license and attribution risk;
- makes future contribution/merge discipline worse.

Recommendation: do not use this option.

### Option D: Do not fork yet; write Blueprint and run a thin integration spike later

Pros:

- avoids premature fork;
- lets architecture choice drive the fork;
- keeps current work focused on requirements, boundaries and compliance.

Cons:

- prevents immediate Android UI spike;
- may delay discovery of Android build/customization friction.

## Recommendation

Use a conservative two-step strategy:

1. Keep upstream discovery clones under `imap-messenger-research/upstream/` unchanged.
2. Use `worktrees/deltachat-android-corporate/` on branch `research/corporate-imap-messenger-baseline` as the local Android baseline until a real product GitHub fork exists.

When GitHub fork access is available:

- create an owned fork of `https://github.com/deltachat/deltachat-android.git`;
- set `origin` in the working clone/worktree route to the owned fork;
- keep `upstream=https://github.com/deltachat/deltachat-android.git`;
- push `research/corporate-imap-messenger-baseline` only after the Blueprint defines the first safe change.

Do not fork `chatmail/core` until there is a concrete need to change core behavior. For now, treat core as upstream dependency and high-risk area.

## Working rule

No vendor copy. No manual file copy. No rebrand before Blueprint. No changes to chatmail/core, JNI, sync, encryption or database migrations in the baseline step.
