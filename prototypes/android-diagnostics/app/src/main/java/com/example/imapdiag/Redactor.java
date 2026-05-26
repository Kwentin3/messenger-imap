package com.example.imapdiag;

final class Redactor {
  private Redactor() {}

  static String maskEmail(String email) {
    if (email == null) return "";
    int at = email.indexOf('@');
    if (at <= 0 || at == email.length() - 1) return "***";
    return "***@" + email.substring(at + 1).toLowerCase();
  }

  static String domain(String email) {
    if (email == null) return "";
    int at = email.indexOf('@');
    if (at < 0 || at == email.length() - 1) return "";
    return email.substring(at + 1).toLowerCase();
  }

  static String safeError(Throwable t) {
    if (t == null) return null;
    String msg = t.getClass().getSimpleName();
    if (t.getMessage() != null && !t.getMessage().trim().isEmpty()) {
      msg += ": " + t.getMessage();
    }
    return msg.replaceAll("(?i)(password|pass|auth|login)\\s*[:=]\\s*\\S+", "$1=<redacted>");
  }

  static String slug(String value, String fallback) {
    if (value == null || value.trim().isEmpty()) return fallback;
    String out = value.trim().toLowerCase().replaceAll("[^a-z0-9]+", "_");
    out = out.replaceAll("^_+", "").replaceAll("_+$", "");
    return out.isEmpty() ? fallback : out;
  }

  static String quoteHeader(String value) {
    if (value == null) return "";
    return value.replace("\r", " ").replace("\n", " ");
  }
}
