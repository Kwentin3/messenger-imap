package com.example.imapdiag;

import android.util.Base64;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.net.ssl.SSLSocket;

final class SmtpSession implements Closeable {
  private final SSLSocket socket;
  private final BufferedReader reader;
  private final BufferedWriter writer;

  SmtpSession(String host, int port, int timeoutMs) throws IOException {
    socket = NetProbe.openTlsSocket(host, port, timeoutMs);
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
  }

  void greeting(int timeoutMs) throws IOException {
    Response r = readResponse(timeoutMs);
    if (r.code != 220) throw new IOException("SMTP greeting failed");
  }

  void ehlo(String domain, int timeoutMs) throws IOException {
    sendLine("EHLO " + sanitizeDomain(domain));
    Response r = readResponse(timeoutMs);
    if (r.code != 250) throw new IOException("SMTP EHLO failed");
  }

  void authPlain(String email, String password, int timeoutMs) throws IOException {
    String auth = "\u0000" + email + "\u0000" + password;
    String encoded = Base64.encodeToString(auth.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
    sendLine("AUTH PLAIN " + encoded);
    Response r = readResponse(timeoutMs);
    if (r.code != 235) throw new IOException("SMTP AUTH failed");
  }

  void sendMessage(
      String from,
      String to,
      String messageId,
      String subject,
      String body,
      int timeoutMs)
      throws IOException {
    command("MAIL FROM:<" + from + ">", 250, timeoutMs, "MAIL FROM rejected");
    command("RCPT TO:<" + to + ">", 250, timeoutMs, "RCPT TO rejected");
    sendLine("DATA");
    Response data = readResponse(timeoutMs);
    if (data.code != 354) throw new IOException("SMTP DATA rejected");
    writer.write("From: <" + Redactor.quoteHeader(from) + ">\r\n");
    writer.write("To: <" + Redactor.quoteHeader(to) + ">\r\n");
    writer.write("Subject: " + Redactor.quoteHeader(subject) + "\r\n");
    writer.write("Message-ID: " + Redactor.quoteHeader(messageId) + "\r\n");
    writer.write("Date: " + rfc2822Date() + "\r\n");
    writer.write("MIME-Version: 1.0\r\n");
    writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
    writer.write("\r\n");
    writer.write(body.replace("\r", "").replace("\n.", "\n.."));
    writer.write("\r\n.\r\n");
    writer.flush();
    Response accepted = readResponse(timeoutMs);
    if (accepted.code < 200 || accepted.code >= 300) {
      throw new IOException("SMTP message rejected");
    }
  }

  void quit() {
    try {
      sendLine("QUIT");
      readResponse(3000);
    } catch (Exception ignored) {
    }
  }

  private void command(String command, int expected, int timeoutMs, String error) throws IOException {
    sendLine(command);
    Response r = readResponse(timeoutMs);
    if (r.code != expected) throw new IOException(error);
  }

  private void sendLine(String line) throws IOException {
    writer.write(line + "\r\n");
    writer.flush();
  }

  private Response readResponse(int timeoutMs) throws IOException {
    socket.setSoTimeout(timeoutMs);
    String line = reader.readLine();
    if (line == null || line.length() < 3) throw new IOException("SMTP connection closed");
    int code = Integer.parseInt(line.substring(0, 3));
    while (line.length() > 3 && line.charAt(3) == '-') {
      line = reader.readLine();
      if (line == null) break;
    }
    return new Response(code);
  }

  private static String sanitizeDomain(String domain) {
    if (domain == null || domain.trim().isEmpty()) return "android-imap-diagnostics.local";
    return domain.replaceAll("[^A-Za-z0-9.-]", "");
  }

  private static String rfc2822Date() {
    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    return format.format(new Date());
  }

  @Override
  public void close() {
    try {
      socket.close();
    } catch (IOException ignored) {
    }
  }

  private static final class Response {
    final int code;

    Response(int code) {
      this.code = code;
    }
  }
}
