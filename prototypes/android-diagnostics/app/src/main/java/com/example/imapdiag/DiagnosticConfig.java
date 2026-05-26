package com.example.imapdiag;

final class DiagnosticConfig {
  static final String MODE_SINGLE_SMOKE = "single_account_smoke";
  static final String MODE_TWO_ACCOUNT = "two_account_canonical";

  ProviderConfig provider;
  String senderEmail;
  String senderPassword;
  String receiverEmail;
  String receiverPassword;
  String deliveryMode;
  boolean runSelfSendSmoke;
  String manualNetworkMode;
  String operatorManual;
  String region;
  String notes;
  boolean providerPreflightPassed;
}
