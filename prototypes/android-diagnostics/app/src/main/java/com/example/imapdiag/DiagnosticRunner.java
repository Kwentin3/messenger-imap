package com.example.imapdiag;

import android.content.Context;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.json.JSONObject;

final class DiagnosticRunner {
  interface Callback {
    void onCheckUpdated(CheckResult check);

    void onFinished(DiagnosticReport report);
  }

  private final Context context;
  private final DiagnosticConfig config;
  private final Callback callback;
  private final DiagnosticReport report = new DiagnosticReport();

  DiagnosticRunner(Context context, DiagnosticConfig config, Callback callback) {
    this.context = context.getApplicationContext();
    this.config = config;
    this.callback = callback;
  }

  void run() {
    report.provider = config.provider;
    report.deliveryMode = config.deliveryMode;
    report.senderEmailDomain = Redactor.domain(config.senderEmail);
    report.receiverEmailDomain =
        DiagnosticConfig.MODE_TWO_ACCOUNT.equals(config.deliveryMode)
            ? Redactor.domain(config.receiverEmail)
            : Redactor.domain(config.senderEmail);
    report.maskedSenderEmail = Redactor.maskEmail(config.senderEmail);
    report.maskedReceiverEmail =
        DiagnosticConfig.MODE_TWO_ACCOUNT.equals(config.deliveryMode)
            ? Redactor.maskEmail(config.receiverEmail)
            : Redactor.maskEmail(config.senderEmail);
    report.notes = config.notes;
    report.providerPreflightPassed = config.providerPreflightPassed;
    report.network = NetworkMetadata.collect(context, config);

    if (report.network.vpnActiveBestEffort) {
      report.result = "diagnostic_only";
      report.invalidationReasons.add("vpn_active");
    }

    String recipient =
        DiagnosticConfig.MODE_TWO_ACCOUNT.equals(config.deliveryMode)
            ? config.receiverEmail
            : config.senderEmail;
    String receiverPassword =
        DiagnosticConfig.MODE_TWO_ACCOUNT.equals(config.deliveryMode)
            ? config.receiverPassword
            : config.senderPassword;
    String messageId = "<imapdiag-" + UUID.randomUUID() + "@diagnostics.invalid>";
    String subject = "IMAP diagnostics test " + messageId;
    String body =
        "Synthetic diagnostics message.\n"
            + "No personal data or secrets should be placed in this body.\n"
            + messageId;

    ImapSession imap = null;
    boolean canSearch = false;
    boolean sent = false;
    long sendAcceptedAt = 0;
    try {
      dns("imap_dns", "imap", config.provider.imapHost, config.provider.imapPort);
      tcp("imap_tcp", "imap", config.provider.imapHost, config.provider.imapPort);
      tls("imap_tls_handshake", "imap", config.provider.imapHost, config.provider.imapPort);

      imap = new ImapSession(config.provider.imapHost, config.provider.imapPort, report.timeoutPolicy.tlsHandshakeMs);
      imapGreeting(imap);
      imapLogin(imap, recipient, receiverPassword);
      imapSelectInbox(imap);
      canSearch = true;
      imapIdle(imap);

      dns("smtp_dns", "smtp", config.provider.smtpHost, config.provider.smtpPort);
      tcp("smtp_tcp", "smtp", config.provider.smtpHost, config.provider.smtpPort);
      tls("smtp_tls_handshake", "smtp", config.provider.smtpHost, config.provider.smtpPort);
      boolean shouldSend =
          DiagnosticConfig.MODE_TWO_ACCOUNT.equals(config.deliveryMode) || config.runSelfSendSmoke;
      sent = smtpAuthAndOptionalSend(messageId, subject, body, recipient, shouldSend);
      if (sent) sendAcceptedAt = System.currentTimeMillis();

      if (DiagnosticConfig.MODE_SINGLE_SMOKE.equals(config.deliveryMode) && !config.runSelfSendSmoke) {
        report.result = report.network.vpnActiveBestEffort ? "diagnostic_only" : "diagnostic_only";
        report.messageCorrelation =
            messageJson(messageId, false, 0, false, -1, "not_requested", false, 0, "smoke_no_send");
      } else if (canSearch && sent) {
        correlate(imap, messageId, sendAcceptedAt);
      }
    } catch (Exception e) {
      if (report.errorSummary == null) report.errorSummary = Redactor.safeError(e);
      if ("inconclusive".equals(report.result)) report.result = classifyTopLevelFailure();
    } finally {
      NetworkMetadata endNetwork = NetworkMetadata.collect(context, config);
      report.networkChanged = !report.network.type.equals(endNetwork.type);
      if (imap != null) {
        imap.logout();
        imap.close();
      }
      callback.onFinished(report);
    }
  }

  private void dns(String name, String protocol, String host, int port) throws Exception {
    CheckResult check = start(name, protocol, host, port);
    long started = System.currentTimeMillis();
    try {
      check.dnsInfo = NetProbe.resolve(host, report.timeoutPolicy.dnsResolveMs, BuildConfig.DEBUG);
      ok(check, started);
    } catch (Exception e) {
      fail(check, started, "dns_fail", e);
      throw e;
    }
  }

  private void tcp(String name, String protocol, String host, int port) throws Exception {
    CheckResult check = start(name, protocol, host, port);
    long started = System.currentTimeMillis();
    try {
      NetProbe.tcpConnect(host, port, report.timeoutPolicy.tcpConnectMs);
      ok(check, started);
    } catch (Exception e) {
      fail(check, started, tcpCategory(e), e);
      throw e;
    }
  }

  private void tls(String name, String protocol, String host, int port) throws Exception {
    CheckResult check = start(name, protocol, host, port);
    long started = System.currentTimeMillis();
    try {
      check.tlsInfo = NetProbe.tlsHandshake(host, port, report.timeoutPolicy.tlsHandshakeMs);
      ok(check, started);
    } catch (Exception e) {
      fail(check, started, "tls_fail", e);
      throw e;
    }
  }

  private void imapGreeting(ImapSession imap) throws Exception {
    CheckResult check = start("imap_greeting", "imap", config.provider.imapHost, config.provider.imapPort);
    long started = System.currentTimeMillis();
    try {
      imap.readGreeting(report.timeoutPolicy.imapGreetingMs);
      ok(check, started);
    } catch (Exception e) {
      fail(check, started, "imap_greeting_fail", e);
      throw e;
    }
  }

  private void imapLogin(ImapSession imap, String email, String password) throws Exception {
    CheckResult check = start("imap_login", "imap", config.provider.imapHost, config.provider.imapPort);
    long started = System.currentTimeMillis();
    try {
      imap.login(email, password, report.timeoutPolicy.imapLoginMs);
      ok(check, started);
    } catch (Exception e) {
      fail(check, started, "auth_fail", e);
      throw e;
    }
  }

  private void imapSelectInbox(ImapSession imap) throws Exception {
    CheckResult check = start("imap_select_inbox", "imap", config.provider.imapHost, config.provider.imapPort);
    long started = System.currentTimeMillis();
    try {
      imap.select("INBOX", report.timeoutPolicy.imapSelectMs);
      ok(check, started);
    } catch (Exception e) {
      fail(check, started, "imap_select_fail", e);
      throw e;
    }
  }

  private void imapIdle(ImapSession imap) {
    CheckResult check = start("imap_idle_enter_exit", "imap", config.provider.imapHost, config.provider.imapPort);
    long started = System.currentTimeMillis();
    try {
      boolean ok = imap.idleEnterExit(report.timeoutPolicy.imapIdleEnterExitMs);
      if (ok) ok(check, started);
      else fail(check, started, "idle_unavailable", new Exception("IDLE unavailable"));
    } catch (Exception e) {
      fail(check, started, "idle_unavailable", e);
    }
  }

  private boolean smtpAuthAndOptionalSend(
      String messageId, String subject, String body, String recipient, boolean shouldSend)
      throws Exception {
    SmtpSession smtp = null;
    try {
      smtp = new SmtpSession(config.provider.smtpHost, config.provider.smtpPort, report.timeoutPolicy.tlsHandshakeMs);
      smtpGreeting(smtp);
      smtpEhlo(smtp);
      smtpAuth(smtp);
      if (!shouldSend) return false;
      CheckResult check = start("smtp_test_send", "smtp", config.provider.smtpHost, config.provider.smtpPort);
      long started = System.currentTimeMillis();
      try {
        smtp.sendMessage(
            config.senderEmail,
            recipient,
            messageId,
            subject,
            body,
            report.timeoutPolicy.smtpSendAcceptedMs);
        ok(check, started);
        return true;
      } catch (Exception e) {
        fail(check, started, "smtp_rejected", e);
        throw e;
      }
    } finally {
      if (smtp != null) {
        smtp.quit();
        smtp.close();
      }
    }
  }

  private void smtpGreeting(SmtpSession smtp) throws Exception {
    CheckResult check = start("smtp_greeting", "smtp", config.provider.smtpHost, config.provider.smtpPort);
    long started = System.currentTimeMillis();
    try {
      smtp.greeting(report.timeoutPolicy.smtpGreetingMs);
      ok(check, started);
    } catch (Exception e) {
      fail(check, started, "smtp_greeting_fail", e);
      throw e;
    }
  }

  private void smtpEhlo(SmtpSession smtp) throws Exception {
    CheckResult check = start("smtp_ehlo", "smtp", config.provider.smtpHost, config.provider.smtpPort);
    long started = System.currentTimeMillis();
    try {
      smtp.ehlo(Redactor.domain(config.senderEmail), report.timeoutPolicy.smtpGreetingMs);
      ok(check, started);
    } catch (Exception e) {
      fail(check, started, "smtp_greeting_fail", e);
      throw e;
    }
  }

  private void smtpAuth(SmtpSession smtp) throws Exception {
    CheckResult check = start("smtp_auth", "smtp", config.provider.smtpHost, config.provider.smtpPort);
    long started = System.currentTimeMillis();
    try {
      smtp.authPlain(config.senderEmail, config.senderPassword, report.timeoutPolicy.smtpAuthMs);
      ok(check, started);
    } catch (Exception e) {
      fail(check, started, "smtp_auth_fail", e);
      throw e;
    }
  }

  private void correlate(ImapSession imap, String messageId, long sendAcceptedAt) throws Exception {
    CheckResult check = start("receive_by_message_id", "imap", config.provider.imapHost, config.provider.imapPort);
    long started = System.currentTimeMillis();
    int attempts = 0;
    boolean found = false;
    while (System.currentTimeMillis() - started < report.timeoutPolicy.receiveCorrelationMs) {
      attempts++;
      imap.select("INBOX", report.timeoutPolicy.imapSelectMs);
      if (imap.searchMessageId(messageId, report.timeoutPolicy.imapSelectMs)) {
        found = true;
        break;
      }
      Thread.sleep(report.timeoutPolicy.receivePollingIntervalMs);
    }
    if (found) {
      ok(check, started);
      long latency = System.currentTimeMillis() - sendAcceptedAt;
      report.messageCorrelation = messageJson(messageId, true, 0, true, latency, "INBOX", false, attempts, "received");
      report.result =
          DiagnosticConfig.MODE_TWO_ACCOUNT.equals(config.deliveryMode) && !report.network.vpnActiveBestEffort
              ? "transport_pass"
              : "diagnostic_only";
      return;
    }
    fail(check, started, "message_not_received", new Exception("Message not found in INBOX"));
    spamScan(imap, messageId, sendAcceptedAt, attempts);
  }

  private void spamScan(ImapSession imap, String messageId, long sendAcceptedAt, int previousAttempts) {
    CheckResult check = start("spam_junk_detection", "imap", config.provider.imapHost, config.provider.imapPort);
    long started = System.currentTimeMillis();
    try {
      List<String> boxes = imap.listMailboxes(report.timeoutPolicy.imapSelectMs);
      for (String box : boxes) {
        if (!looksSpam(box)) continue;
        imap.select(box, report.timeoutPolicy.imapSelectMs);
        if (imap.searchMessageId(messageId, report.timeoutPolicy.imapSelectMs)) {
          fail(check, started, "spam_or_junk_placement", new Exception("Message found in Spam/Junk"));
          long latency = System.currentTimeMillis() - sendAcceptedAt;
          report.messageCorrelation =
              messageJson(messageId, true, 0, true, latency, box, true, previousAttempts, "spam_or_junk");
          report.result = "spam_junk_fail";
          return;
        }
      }
      ok(check, started);
      report.messageCorrelation =
          messageJson(messageId, true, 0, false, -1, "not_found", false, previousAttempts, "not_found");
      report.result = "fail";
      report.errorSummary = "message_not_received";
    } catch (Exception e) {
      fail(check, started, "unknown", e);
      report.result = "fail";
      report.errorSummary = "message_not_received";
    }
  }

  private static boolean looksSpam(String box) {
    String lower = box.toLowerCase(Locale.US);
    return lower.contains("spam")
        || lower.contains("junk")
        || lower.contains("bulk")
        || lower.contains("спам");
  }

  private JSONObject messageJson(
      String messageId,
      boolean sendAccepted,
      long sendLatencyMs,
      boolean received,
      long receiveLatencyMs,
      String folder,
      boolean spamOrJunk,
      int pollAttempts,
      String finalStatus)
      throws Exception {
    JSONObject o = new JSONObject();
    o.put("messageId", messageId);
    o.put("sendAccepted", sendAccepted);
    o.put("sendLatencyMs", sendLatencyMs);
    o.put("received", received);
    if (receiveLatencyMs >= 0) o.put("receiveLatencyMs", receiveLatencyMs);
    o.put("folder", folder);
    o.put("spamOrJunk", spamOrJunk);
    o.put("pollAttempts", pollAttempts);
    o.put("finalStatus", finalStatus);
    return o;
  }

  private CheckResult start(String name, String protocol, String host, int port) {
    CheckResult check = report.addCheck(name, protocol, host, port);
    check.status = "running";
    callback.onCheckUpdated(check);
    return check;
  }

  private void ok(CheckResult check, long started) {
    check.status = "ok";
    check.latencyMs = System.currentTimeMillis() - started;
    callback.onCheckUpdated(check);
  }

  private void fail(CheckResult check, long started, String category, Throwable e) {
    check.status = "fail";
    check.latencyMs = System.currentTimeMillis() - started;
    check.errorCategory = category;
    check.errorMessageRedacted = Redactor.safeError(e);
    if (report.errorSummary == null) report.errorSummary = category;
    callback.onCheckUpdated(check);
  }

  private static String tcpCategory(Exception e) {
    if (e instanceof SocketTimeoutException) return "tcp_timeout";
    if (e instanceof ConnectException) return "tcp_refused";
    return "tcp_timeout";
  }

  private String classifyTopLevelFailure() {
    String category = report.errorSummary == null ? "" : report.errorSummary;
    if ("auth_fail".equals(category) || "smtp_auth_fail".equals(category)) return "auth_fail";
    if (("dns_fail".equals(category)
            || "tcp_timeout".equals(category)
            || "tcp_refused".equals(category)
            || "tls_fail".equals(category))
        && "whitelist_restricted".equals(report.network.manualMode)) {
      return "network_whitelist_fail";
    }
    if ("spam_or_junk_placement".equals(category)) return "spam_junk_fail";
    return "fail";
  }
}
