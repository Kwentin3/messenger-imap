package com.example.imapdiag;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.net.ssl.SSLSocket;

final class ImapSession implements Closeable {
  private final SSLSocket socket;
  private final BufferedReader reader;
  private final BufferedWriter writer;
  private int tagCounter = 1;

  ImapSession(String host, int port, int timeoutMs) throws IOException {
    socket = NetProbe.openTlsSocket(host, port, timeoutMs);
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
  }

  String readGreeting(int timeoutMs) throws IOException {
    socket.setSoTimeout(timeoutMs);
    String line = reader.readLine();
    if (line == null || !line.startsWith("* OK")) {
      throw new IOException("Unexpected IMAP greeting");
    }
    return line;
  }

  void login(String email, String password, int timeoutMs) throws IOException {
    Response response = tagged("LOGIN " + quote(email) + " " + quote(password), timeoutMs);
    if (!response.ok) throw new IOException("IMAP login failed");
  }

  void select(String mailbox, int timeoutMs) throws IOException {
    Response response = tagged("SELECT " + quoteMailbox(mailbox), timeoutMs);
    if (!response.ok) throw new IOException("IMAP SELECT failed");
  }

  boolean idleEnterExit(int timeoutMs) throws IOException {
    socket.setSoTimeout(timeoutMs);
    String tag = nextTag();
    writer.write(tag + " IDLE\r\n");
    writer.flush();
    String line = reader.readLine();
    if (line == null || !line.startsWith("+")) {
      return false;
    }
    writer.write("DONE\r\n");
    writer.flush();
    while ((line = reader.readLine()) != null) {
      if (line.startsWith(tag + " ")) {
        return line.toUpperCase(Locale.US).contains("OK");
      }
    }
    return false;
  }

  boolean searchMessageId(String messageId, int timeoutMs) throws IOException {
    Response response = tagged("SEARCH HEADER Message-ID " + quote(messageId), timeoutMs);
    if (!response.ok) return false;
    for (String line : response.lines) {
      if (line.startsWith("* SEARCH")) {
        String rest = line.substring("* SEARCH".length()).trim();
        return !rest.isEmpty();
      }
    }
    return false;
  }

  List<String> listMailboxes(int timeoutMs) throws IOException {
    Response response = tagged("LIST \"\" \"*\"", timeoutMs);
    ArrayList<String> boxes = new ArrayList<>();
    if (!response.ok) return boxes;
    for (String line : response.lines) {
      if (!line.startsWith("* LIST")) continue;
      String name = parseMailboxName(line);
      if (name != null && !name.trim().isEmpty()) boxes.add(name);
    }
    return boxes;
  }

  void logout() {
    try {
      tagged("LOGOUT", 3000);
    } catch (Exception ignored) {
    }
  }

  private Response tagged(String command, int timeoutMs) throws IOException {
    socket.setSoTimeout(timeoutMs);
    String tag = nextTag();
    writer.write(tag + " " + command + "\r\n");
    writer.flush();
    ArrayList<String> lines = new ArrayList<>();
    String line;
    while ((line = reader.readLine()) != null) {
      lines.add(line);
      if (line.startsWith(tag + " ")) {
        boolean ok = line.toUpperCase(Locale.US).contains(" OK");
        return new Response(ok, lines);
      }
    }
    throw new IOException("IMAP connection closed");
  }

  private String nextTag() {
    return "A" + String.format(Locale.US, "%04d", tagCounter++);
  }

  private static String quote(String value) {
    String cleaned = value == null ? "" : value.replace("\r", "").replace("\n", "");
    return "\"" + cleaned.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }

  private static String quoteMailbox(String value) {
    if ("INBOX".equalsIgnoreCase(value)) return "INBOX";
    return quote(value);
  }

  private static String parseMailboxName(String line) {
    int quotedEnd = line.lastIndexOf('"');
    if (quotedEnd > 0) {
      int quotedStart = line.lastIndexOf('"', quotedEnd - 1);
      if (quotedStart >= 0 && quotedStart < quotedEnd) {
        return line.substring(quotedStart + 1, quotedEnd);
      }
    }
    int lastSpace = line.lastIndexOf(' ');
    if (lastSpace >= 0 && lastSpace < line.length() - 1) {
      return line.substring(lastSpace + 1).trim();
    }
    return null;
  }

  @Override
  public void close() {
    try {
      socket.close();
    } catch (IOException ignored) {
    }
  }

  private static final class Response {
    final boolean ok;
    final List<String> lines;

    Response(boolean ok, List<String> lines) {
      this.ok = ok;
      this.lines = lines;
    }
  }
}
