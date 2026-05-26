package com.example.imapdiag;

import org.json.JSONException;
import org.json.JSONObject;

final class CheckResult {
  final String name;
  final String protocol;
  final String host;
  final int port;
  String status = "pending";
  long latencyMs = -1;
  String errorCategory;
  String errorMessageRedacted;
  JSONObject tlsInfo;
  JSONObject dnsInfo;

  CheckResult(String name, String protocol, String host, int port) {
    this.name = name;
    this.protocol = protocol;
    this.host = host;
    this.port = port;
  }

  JSONObject toJson() throws JSONException {
    JSONObject o = new JSONObject();
    o.put("name", name);
    o.put("protocol", protocol);
    if (host != null) {
      JSONObject endpoint = new JSONObject();
      endpoint.put("host", host);
      endpoint.put("port", port);
      o.put("endpoint", endpoint);
    }
    o.put("status", status);
    if (latencyMs >= 0) o.put("latencyMs", latencyMs);
    o.put("errorCategory", errorCategory == null ? JSONObject.NULL : errorCategory);
    o.put("errorMessageRedacted", errorMessageRedacted == null ? JSONObject.NULL : errorMessageRedacted);
    o.put("tlsInfo", tlsInfo == null ? JSONObject.NULL : tlsInfo);
    o.put("dnsInfo", dnsInfo == null ? JSONObject.NULL : dnsInfo);
    return o;
  }
}
