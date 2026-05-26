package com.example.imapdiag;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;

public class MainActivity extends Activity {
  private static final int REQ_CREATE_REPORT = 1001;

  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private Spinner providerSpinner;
  private Spinner deliverySpinner;
  private Spinner networkModeSpinner;
  private EditText senderEmail;
  private EditText senderPassword;
  private EditText receiverEmail;
  private EditText receiverPassword;
  private EditText operator;
  private EditText region;
  private EditText notes;
  private EditText overrideImapHost;
  private EditText overrideImapPort;
  private EditText overrideSmtpHost;
  private EditText overrideSmtpPort;
  private CheckBox selfSendSmoke;
  private CheckBox preflightPassed;
  private CheckBox debugOverride;
  private LinearLayout receiverGroup;
  private LinearLayout debugOverrideGroup;
  private TextView statusView;
  private TextView resultView;
  private Button runButton;
  private Button exportButton;
  private Button copyButton;
  private DiagnosticReport lastReport;
  private String pendingJsonExport;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    buildUi();
  }

  private void buildUi() {
    ScrollView scroll = new ScrollView(this);
    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    int pad = dp(12);
    root.setPadding(pad, pad, pad, pad);
    scroll.addView(root);

    TextView title = text("IMAP/SMTP Android Diagnostics MVP-0a");
    title.setTextSize(20);
    root.addView(title);
    root.addView(text("Foreground-only transport diagnostics. Not a messenger."));

    providerSpinner = spinner(new String[] {"Mail.ru", "VK Mail", "Yandex"});
    root.addView(label("Provider"));
    root.addView(providerSpinner);

    deliverySpinner =
        spinner(
            new String[] {
              "Single-account connectivity smoke test", "Two-account canonical delivery test"
            });
    root.addView(label("Delivery test mode"));
    root.addView(deliverySpinner);

    senderEmail = input("Sender email", false);
    senderPassword = input("Sender app password", true);
    root.addView(senderEmail);
    root.addView(senderPassword);

    receiverGroup = new LinearLayout(this);
    receiverGroup.setOrientation(LinearLayout.VERTICAL);
    receiverEmail = input("Receiver email", false);
    receiverPassword = input("Receiver app password", true);
    receiverGroup.addView(receiverEmail);
    receiverGroup.addView(receiverPassword);
    root.addView(receiverGroup);

    selfSendSmoke = new CheckBox(this);
    selfSendSmoke.setText("Run optional self-send in smoke mode");
    root.addView(selfSendSmoke);

    networkModeSpinner =
        spinner(new String[] {"wifi_control", "normal_mobile", "whitelist_restricted"});
    root.addView(label("Manual network mode"));
    root.addView(networkModeSpinner);

    operator = input("Operator manual input, e.g. mts", false);
    region = input("Region/city manual input", false);
    notes = input("Optional notes, no personal data", false);
    root.addView(operator);
    root.addView(region);
    root.addView(notes);

    preflightPassed = new CheckBox(this);
    preflightPassed.setText("Wi-Fi provider preflight passed for this account/config");
    root.addView(preflightPassed);

    if (BuildConfig.DEBUG) {
      debugOverride = new CheckBox(this);
      debugOverride.setText("Debug endpoint override");
      root.addView(debugOverride);
      debugOverrideGroup = new LinearLayout(this);
      debugOverrideGroup.setOrientation(LinearLayout.VERTICAL);
      overrideImapHost = input("Override IMAP host", false);
      overrideImapPort = input("Override IMAP port", false);
      overrideSmtpHost = input("Override SMTP host", false);
      overrideSmtpPort = input("Override SMTP port", false);
      overrideImapPort.setInputType(InputType.TYPE_CLASS_NUMBER);
      overrideSmtpPort.setInputType(InputType.TYPE_CLASS_NUMBER);
      debugOverrideGroup.addView(overrideImapHost);
      debugOverrideGroup.addView(overrideImapPort);
      debugOverrideGroup.addView(overrideSmtpHost);
      debugOverrideGroup.addView(overrideSmtpPort);
      debugOverrideGroup.setVisibility(View.GONE);
      root.addView(debugOverrideGroup);
      debugOverride.setOnCheckedChangeListener((buttonView, isChecked) -> updateVisibility());
    }

    runButton = new Button(this);
    runButton.setText("Run foreground diagnostics");
    runButton.setOnClickListener(v -> runDiagnostics());
    root.addView(runButton);

    exportButton = new Button(this);
    exportButton.setText("Export JSON");
    exportButton.setEnabled(false);
    exportButton.setOnClickListener(v -> exportJson());
    root.addView(exportButton);

    copyButton = new Button(this);
    copyButton.setText("Copy sanitized summary");
    copyButton.setEnabled(false);
    copyButton.setOnClickListener(v -> copySummary());
    root.addView(copyButton);

    resultView = text("No result yet.");
    statusView = text("");
    root.addView(label("Result"));
    root.addView(resultView);
    root.addView(label("Checklist"));
    root.addView(statusView);

    deliverySpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            updateVisibility();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });

    setContentView(scroll);
    updateVisibility();
  }

  private void updateVisibility() {
    boolean twoAccount = deliverySpinner.getSelectedItemPosition() == 1;
    receiverGroup.setVisibility(twoAccount ? View.VISIBLE : View.GONE);
    selfSendSmoke.setVisibility(twoAccount ? View.GONE : View.VISIBLE);
    if (debugOverrideGroup != null) {
      debugOverrideGroup.setVisibility(debugOverride.isChecked() ? View.VISIBLE : View.GONE);
    }
  }

  private void runDiagnostics() {
    DiagnosticConfig config = collectConfig();
    if (config == null) return;
    statusView.setText("");
    resultView.setText("Running...");
    exportButton.setEnabled(false);
    copyButton.setEnabled(false);
    runButton.setEnabled(false);
    lastReport = null;

    executor.execute(
        () ->
            new DiagnosticRunner(
                    this,
                    config,
                    new DiagnosticRunner.Callback() {
                      @Override
                      public void onCheckUpdated(CheckResult check) {
                        runOnUiThread(() -> appendStatus(check));
                      }

                      @Override
                      public void onFinished(DiagnosticReport report) {
                        runOnUiThread(
                            () -> {
                              lastReport = report;
                              resultView.setText(
                                  "Result: "
                                      + report.result
                                      + "\nError: "
                                      + (report.errorSummary == null ? "<none>" : report.errorSummary)
                                      + "\nExport does not include app password or raw protocol transcript.");
                              exportButton.setEnabled(true);
                              copyButton.setEnabled(true);
                              runButton.setEnabled(true);
                              clearPasswords();
                            });
                      }
                    })
                .run());
  }

  private DiagnosticConfig collectConfig() {
    DiagnosticConfig c = new DiagnosticConfig();
    c.provider = selectedProvider();
    c.deliveryMode =
        deliverySpinner.getSelectedItemPosition() == 1
            ? DiagnosticConfig.MODE_TWO_ACCOUNT
            : DiagnosticConfig.MODE_SINGLE_SMOKE;
    c.senderEmail = senderEmail.getText().toString().trim();
    c.senderPassword = senderPassword.getText().toString();
    c.receiverEmail = receiverEmail.getText().toString().trim();
    c.receiverPassword = receiverPassword.getText().toString();
    c.runSelfSendSmoke = selfSendSmoke.isChecked();
    c.manualNetworkMode = networkModeSpinner.getSelectedItem().toString();
    c.operatorManual = operator.getText().toString().trim();
    c.region = region.getText().toString().trim();
    c.notes = notes.getText().toString().trim();
    c.providerPreflightPassed = preflightPassed.isChecked();

    if (!validEmail(c.senderEmail)) {
      toast("Enter valid sender email.");
      return null;
    }
    if (c.senderPassword.isEmpty()) {
      toast("Enter sender app password.");
      return null;
    }
    if (DiagnosticConfig.MODE_TWO_ACCOUNT.equals(c.deliveryMode)) {
      if (!validEmail(c.receiverEmail)) {
        toast("Enter valid receiver email.");
        return null;
      }
      if (c.receiverPassword.isEmpty()) {
        toast("Enter receiver app password.");
        return null;
      }
    }

    if (BuildConfig.DEBUG && debugOverride != null && debugOverride.isChecked()) {
      try {
        c.provider =
            c.provider.withDebugOverride(
                overrideImapHost.getText().toString().trim(),
                Integer.parseInt(overrideImapPort.getText().toString().trim()),
                overrideSmtpHost.getText().toString().trim(),
                Integer.parseInt(overrideSmtpPort.getText().toString().trim()));
      } catch (Exception e) {
        toast("Debug override requires valid host/port values.");
        return null;
      }
    }
    return c;
  }

  private ProviderConfig selectedProvider() {
    switch (providerSpinner.getSelectedItemPosition()) {
      case 1:
        return ProviderConfig.vkMail();
      case 2:
        return ProviderConfig.yandex();
      case 0:
      default:
        return ProviderConfig.mailRu();
    }
  }

  private void appendStatus(CheckResult check) {
    String current = statusView.getText().toString();
    statusView.setText(
        current
            + check.name
            + ": "
            + check.status
            + (check.errorCategory == null ? "" : " (" + check.errorCategory + ")")
            + "\n");
  }

  private void exportJson() {
    if (lastReport == null) return;
    try {
      JSONObject json = lastReport.toJson(true);
      pendingJsonExport = json.toString(2);
      Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("application/json");
      intent.putExtra(Intent.EXTRA_TITLE, lastReport.exportFileName());
      startActivityForResult(intent, REQ_CREATE_REPORT);
    } catch (Exception e) {
      toast("Export failed: " + Redactor.safeError(e));
    }
  }

  private void copySummary() {
    if (lastReport == null) return;
    String summary =
        "runId="
            + lastReport.runId
            + "\nresult="
            + lastReport.result
            + "\nprovider="
            + lastReport.provider.id
            + "\nmode="
            + lastReport.deliveryMode
            + "\nmaskedSender="
            + lastReport.maskedSenderEmail
            + "\nmaskedReceiver="
            + lastReport.maskedReceiverEmail;
    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    if (clipboard != null) {
      clipboard.setPrimaryClip(ClipData.newPlainText("imap diagnostics summary", summary));
      toast("Sanitized summary copied.");
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQ_CREATE_REPORT && resultCode == RESULT_OK && data != null) {
      Uri uri = data.getData();
      if (uri == null || pendingJsonExport == null) return;
      try (OutputStream out = getContentResolver().openOutputStream(uri)) {
        if (out != null) {
          out.write(pendingJsonExport.getBytes(StandardCharsets.UTF_8));
          toast("Report exported.");
        }
      } catch (Exception e) {
        toast("Write failed: " + Redactor.safeError(e));
      } finally {
        pendingJsonExport = null;
      }
    }
  }

  private boolean validEmail(String value) {
    return value != null && Patterns.EMAIL_ADDRESS.matcher(value).matches();
  }

  private EditText input(String hint, boolean password) {
    EditText edit = new EditText(this);
    edit.setHint(hint);
    edit.setSingleLine(false);
    if (password) {
      edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    } else {
      edit.setInputType(InputType.TYPE_CLASS_TEXT);
    }
    return edit;
  }

  private Spinner spinner(String[] values) {
    Spinner spinner = new Spinner(this);
    ArrayAdapter<String> adapter =
        new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, values);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    return spinner;
  }

  private TextView label(String value) {
    TextView v = text(value);
    v.setTextSize(14);
    v.setPadding(0, dp(12), 0, 0);
    return v;
  }

  private TextView text(String value) {
    TextView v = new TextView(this);
    v.setText(value);
    v.setPadding(0, dp(4), 0, dp(4));
    return v;
  }

  private void clearPasswords() {
    senderPassword.setText("");
    receiverPassword.setText("");
  }

  @Override
  protected void onDestroy() {
    clearPasswords();
    executor.shutdownNow();
    super.onDestroy();
  }

  private void toast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
  }

  private int dp(int value) {
    return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
  }
}
