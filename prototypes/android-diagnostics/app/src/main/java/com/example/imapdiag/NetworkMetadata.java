package com.example.imapdiag;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.PowerManager;
import org.json.JSONException;
import org.json.JSONObject;

final class NetworkMetadata {
  String type = "unknown";
  String manualMode;
  String operatorManual;
  String carrierNameBestEffort;
  String mccMncBestEffort;
  String region;
  boolean batteryOptimizationIgnored;
  String foregroundState = "foreground";
  boolean vpnActiveBestEffort;
  boolean vpnValidityWarningShown;

  static NetworkMetadata collect(Context context, DiagnosticConfig config) {
    NetworkMetadata m = new NetworkMetadata();
    m.manualMode = config.manualNetworkMode;
    m.operatorManual = Redactor.slug(config.operatorManual, "unknown_operator");
    m.region = config.region == null ? "" : config.region.trim();
    ConnectivityManager cm =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (cm != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      Network network = cm.getActiveNetwork();
      NetworkCapabilities caps = network == null ? null : cm.getNetworkCapabilities(network);
      if (caps != null) {
        if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
          m.vpnActiveBestEffort = true;
          m.type = "vpn";
        } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
          m.type = "wifi";
        } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
          m.type = "mobile";
        } else if (caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
          m.type = "ethernet";
        }
      }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      try {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        m.batteryOptimizationIgnored =
            pm != null && pm.isIgnoringBatteryOptimizations(context.getPackageName());
      } catch (Exception ignored) {
      }
    }
    m.vpnValidityWarningShown = m.vpnActiveBestEffort;
    return m;
  }

  JSONObject toJson() throws JSONException {
    JSONObject o = new JSONObject();
    o.put("type", type);
    o.put("manualMode", manualMode);
    o.put("operatorManual", operatorManual);
    o.put("carrierNameBestEffort", carrierNameBestEffort == null ? JSONObject.NULL : carrierNameBestEffort);
    o.put("mccMncBestEffort", mccMncBestEffort == null ? JSONObject.NULL : mccMncBestEffort);
    o.put("region", region);
    o.put("batteryOptimizationIgnored", batteryOptimizationIgnored);
    o.put("foregroundState", foregroundState);
    o.put("vpnActiveBestEffort", vpnActiveBestEffort);
    o.put("vpnValidityWarningShown", vpnValidityWarningShown);
    return o;
  }
}
