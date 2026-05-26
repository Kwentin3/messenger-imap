package com.example.imapdiag;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

final class DiagnosticReport {
  final String runId = UUID.randomUUID().toString();
  final String timestampUtc = nowUtc();
  final TimeoutPolicy timeoutPolicy = new TimeoutPolicy();
  final ArrayList<CheckResult> checks = new ArrayList<>();
  final ArrayList<String> invalidationReasons = new ArrayList<>();
  NetworkMetadata network;
  ProviderConfig provider;
  String deliveryMode;
  String senderEmailDomain;
  String receiverEmailDomain;
  String maskedSenderEmail;
  String maskedReceiverEmail;
  String result = "inconclusive";
  String errorSummary;
  String notes;
  JSONObject messageCorrelation;
  boolean providerPreflightPassed;
  boolean networkChanged;

  CheckResult addCheck(String name, String protocol, String host, int port) {
    CheckResult check = new CheckResult(name, protocol, host, port);
    checks.add(check);
    return check;
  }

  JSONObject toJson(boolean forExport) throws JSONException {
    JSONObject root = new JSONObject();
    root.put("schemaVersion", 1);
    root.put("timestampUtc", timestampUtc);
    root.put("runId", runId);
    root.put("mvpStage", "mvp_0a");
    root.put("scenario", "foreground");
    root.put("deliveryTestMode", deliveryMode);
    root.put("app", appJson());
    root.put("device", deviceJson());
    root.put("network", network.toJson());
    root.put("provider", providerJson());
    root.put("timeoutPolicy", timeoutPolicy.toJson());
    JSONArray arr = new JSONArray();
    for (CheckResult check : checks) arr.put(check.toJson());
    root.put("checks", arr);
    root.put("messageCorrelation", messageCorrelation == null ? JSONObject.NULL : messageCorrelation);
    root.put("result", result);
    root.put("errorSummary", errorSummary == null ? JSONObject.NULL : errorSummary);
    root.put("fieldValidity", fieldValidityJson(forExport));
    root.put("notes", notes == null ? "" : notes);
    return root;
  }

  String exportFileName() {
    String date = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
    String operator = network == null ? "unknown_operator" : network.operatorManual;
    String mode = network == null ? "unknown_mode" : network.manualMode;
    return "imapdiag_"
        + date
        + "_"
        + provider.id
        + "_"
        + Redactor.slug(operator, "unknown_operator")
        + "_"
        + Redactor.slug(mode, "unknown_mode")
        + "_foreground_"
        + Redactor.slug(result, "inconclusive")
        + ".json";
  }

  private JSONObject appJson() throws JSONException {
    JSONObject o = new JSONObject();
    o.put("name", "android-imap-diagnostics");
    o.put("version", BuildConfig.VERSION_NAME);
    o.put("buildType", BuildConfig.DEBUG ? "debug" : "release");
    o.put("buildNumber", Integer.toString(BuildConfig.VERSION_CODE));
    return o;
  }

  private JSONObject deviceJson() throws JSONException {
    JSONObject o = new JSONObject();
    o.put("manufacturer", android.os.Build.MANUFACTURER);
    o.put("model", android.os.Build.MODEL);
    o.put("androidVersion", android.os.Build.VERSION.RELEASE);
    o.put("sdk", android.os.Build.VERSION.SDK_INT);
    return o;
  }

  private JSONObject providerJson() throws JSONException {
    JSONObject o = new JSONObject();
    o.put("name", provider.id);
    o.put("senderEmailDomain", senderEmailDomain);
    o.put("receiverEmailDomain", receiverEmailDomain);
    o.put("maskedSenderEmail", maskedSenderEmail);
    o.put("maskedReceiverEmail", maskedReceiverEmail);
    JSONObject imap = new JSONObject();
    imap.put("host", provider.imapHost);
    imap.put("port", provider.imapPort);
    imap.put("security", "tls");
    o.put("imap", imap);
    JSONObject smtp = new JSONObject();
    smtp.put("host", provider.smtpHost);
    smtp.put("port", provider.smtpPort);
    smtp.put("security", "tls");
    o.put("smtp", smtp);
    o.put("debugOverrideUsed", provider.debugOverrideUsed);
    return o;
  }

  private JSONObject fieldValidityJson(boolean forExport) throws JSONException {
    List<String> reasons = new ArrayList<>(invalidationReasons);
    if (!"whitelist_restricted".equals(network.manualMode)) reasons.add("manual_mode_not_whitelist");
    if (network.vpnActiveBestEffort) reasons.add("vpn_active");
    if (!providerPreflightPassed) reasons.add("provider_preflight_not_confirmed");
    if (networkChanged) reasons.add("network_changed");
    if (!forExport) reasons.add("not_exported");
    boolean valid = reasons.isEmpty();
    JSONObject o = new JSONObject();
    o.put("validForWhitelistConclusion", valid);
    JSONArray arr = new JSONArray();
    for (String reason : reasons) arr.put(reason);
    o.put("invalidationReasons", arr);
    return o;
  }

  private static String nowUtc() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format.format(new Date());
  }
}
