package com.example.imapdiag;

final class ProviderConfig {
  final String id;
  final String displayName;
  final String[] domains;
  final String imapHost;
  final int imapPort;
  final String smtpHost;
  final int smtpPort;
  final boolean requiresAppPassword;
  final String note;
  final boolean debugOverrideUsed;

  ProviderConfig(
      String id,
      String displayName,
      String[] domains,
      String imapHost,
      int imapPort,
      String smtpHost,
      int smtpPort,
      boolean requiresAppPassword,
      String note,
      boolean debugOverrideUsed) {
    this.id = id;
    this.displayName = displayName;
    this.domains = domains;
    this.imapHost = imapHost;
    this.imapPort = imapPort;
    this.smtpHost = smtpHost;
    this.smtpPort = smtpPort;
    this.requiresAppPassword = requiresAppPassword;
    this.note = note;
    this.debugOverrideUsed = debugOverrideUsed;
  }

  static ProviderConfig mailRu() {
    return new ProviderConfig(
        "mailru",
        "Mail.ru",
        new String[] {"mail.ru", "internet.ru", "bk.ru", "inbox.ru", "list.ru"},
        "imap.mail.ru",
        993,
        "smtp.mail.ru",
        465,
        true,
        "Requires app password for external apps.",
        false);
  }

  static ProviderConfig vkMail() {
    return new ProviderConfig(
        "vkmail",
        "VK Mail",
        new String[] {"vk.com", "vk.ru"},
        "imap.mail.ru",
        993,
        "smtp.mail.ru",
        465,
        true,
        "Uses Mail.ru IMAP/SMTP endpoints; requires app password.",
        false);
  }

  static ProviderConfig yandex() {
    return new ProviderConfig(
        "yandex",
        "Yandex",
        new String[] {"yandex.ru", "yandex.com", "ya.ru"},
        "imap.yandex.com",
        993,
        "smtp.yandex.com",
        465,
        true,
        "IMAP must be enabled; use app password or provider-approved auth path.",
        false);
  }

  ProviderConfig withDebugOverride(String imapHost, int imapPort, String smtpHost, int smtpPort) {
    return new ProviderConfig(
        id,
        displayName + " (debug override)",
        domains,
        imapHost,
        imapPort,
        smtpHost,
        smtpPort,
        requiresAppPassword,
        note,
        true);
  }
}
