package com.example.imapdiag;

import org.json.JSONException;
import org.json.JSONObject;

final class TimeoutPolicy {
  final int dnsResolveMs = 5000;
  final int tcpConnectMs = 10000;
  final int tlsHandshakeMs = 15000;
  final int imapGreetingMs = 10000;
  final int imapLoginMs = 20000;
  final int imapSelectMs = 15000;
  final int imapIdleEnterExitMs = 20000;
  final int smtpGreetingMs = 10000;
  final int smtpAuthMs = 20000;
  final int smtpSendAcceptedMs = 30000;
  final int receiveCorrelationMs = 120000;
  final int receivePollingIntervalMs = 5000;
  final int idleObserveWindowMs = 60000;
  final boolean modifiedFromDefault = false;

  JSONObject toJson() throws JSONException {
    JSONObject o = new JSONObject();
    o.put("dnsResolveMs", dnsResolveMs);
    o.put("tcpConnectMs", tcpConnectMs);
    o.put("tlsHandshakeMs", tlsHandshakeMs);
    o.put("imapGreetingMs", imapGreetingMs);
    o.put("imapLoginMs", imapLoginMs);
    o.put("imapSelectMs", imapSelectMs);
    o.put("imapIdleEnterExitMs", imapIdleEnterExitMs);
    o.put("smtpGreetingMs", smtpGreetingMs);
    o.put("smtpAuthMs", smtpAuthMs);
    o.put("smtpSendAcceptedMs", smtpSendAcceptedMs);
    o.put("receiveCorrelationMs", receiveCorrelationMs);
    o.put("receivePollingIntervalMs", receivePollingIntervalMs);
    o.put("idleObserveWindowMs", idleObserveWindowMs);
    o.put("modifiedFromDefault", modifiedFromDefault);
    return o;
  }
}
