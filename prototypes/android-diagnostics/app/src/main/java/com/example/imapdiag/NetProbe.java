package com.example.imapdiag;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

final class NetProbe {
  private NetProbe() {}

  static JSONObject resolve(String host, int timeoutMs, boolean includeAddresses) throws Exception {
    ExecutorService executor = Executors.newSingleThreadExecutor();
    try {
      Future<InetAddress[]> future =
          executor.submit(
              new Callable<InetAddress[]>() {
                @Override
                public InetAddress[] call() throws Exception {
                  return InetAddress.getAllByName(host);
                }
              });
      InetAddress[] addresses = future.get(timeoutMs, TimeUnit.MILLISECONDS);
      Set<String> families = new HashSet<>();
      JSONArray raw = new JSONArray();
      for (InetAddress address : addresses) {
        String text = address.getHostAddress();
        if (text != null && text.contains(":")) families.add("IPv6");
        else families.add("IPv4");
        if (includeAddresses) raw.put(text);
      }
      JSONObject o = new JSONObject();
      o.put("resolvedAddressCount", addresses.length);
      JSONArray fam = new JSONArray();
      for (String family : families) fam.put(family);
      o.put("addressFamilies", fam);
      o.put("resolvedAddresses", includeAddresses ? raw : JSONObject.NULL);
      return o;
    } finally {
      executor.shutdownNow();
    }
  }

  static void tcpConnect(String host, int port, int timeoutMs) throws IOException {
    Socket socket = new Socket();
    try {
      socket.connect(new InetSocketAddress(host, port), timeoutMs);
    } finally {
      try {
        socket.close();
      } catch (IOException ignored) {
      }
    }
  }

  static JSONObject tlsHandshake(String host, int port, int timeoutMs) throws Exception {
    SSLSocket socket = openTlsSocket(host, port, timeoutMs);
    try {
      return tlsInfo(socket.getSession());
    } finally {
      try {
        socket.close();
      } catch (IOException ignored) {
      }
    }
  }

  static SSLSocket openTlsSocket(String host, int port, int timeoutMs) throws IOException {
    Socket plain = new Socket();
    plain.connect(new InetSocketAddress(host, port), timeoutMs);
    plain.setSoTimeout(timeoutMs);
    SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    SSLSocket socket =
        (SSLSocket) factory.createSocket(plain, host, port, true);
    socket.setSoTimeout(timeoutMs);
    socket.startHandshake();
    return socket;
  }

  static JSONObject tlsInfo(SSLSession session) throws JSONException {
    JSONObject o = new JSONObject();
    o.put("protocol", session.getProtocol());
    o.put("cipherSuite", session.getCipherSuite());
    try {
      Certificate[] certificates = session.getPeerCertificates();
      if (certificates.length > 0 && certificates[0] instanceof X509Certificate) {
        X509Certificate cert = (X509Certificate) certificates[0];
        o.put("subject", cert.getSubjectX500Principal().getName());
        o.put("issuer", cert.getIssuerX500Principal().getName());
        o.put("validFrom", cert.getNotBefore().toString());
        o.put("validTo", cert.getNotAfter().toString());
      }
    } catch (Exception e) {
      o.put("certificateErrorRedacted", Redactor.safeError(e));
    }
    return o;
  }
}
