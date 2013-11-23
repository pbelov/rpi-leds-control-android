package com.psbelov.rpi.leds.client.android;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class LoginActivity extends Activity {
    private final String TAG = "LoginActivity";

    private EditText etName, etPort, etPass;
    private CheckBox cbName, cbPort, cbPass;

    private Button btnConnect;
    private Communication c;

    String name = ""; // default for debug;
    int port = 0;
    String pass = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName = (EditText)findViewById(R.id.etAddr);
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                name = s.toString();
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etPort = (EditText)findViewById(R.id.etPort);
        etPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    try {
                        port = Integer.parseInt(s.toString());
                    } catch (NumberFormatException nfe) {
                        port = 65535;
                    }
                }

                check();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etPass = (EditText)findViewById(R.id.etPasswd);
        etPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pass = s.toString();
                check();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        cbName = (CheckBox)findViewById(R.id.cbSaveName);
        cbPort = (CheckBox)findViewById(R.id.cbSavePort);
        cbPass = (CheckBox)findViewById(R.id.cbSavePass);

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();

                c = new Communication(LoginActivity.this, name, port, pass);
                CommunicationHelper.setCommunication(c);
                new Thread(c).start();
            }
        });

        loadInfo();

        c = CommunicationHelper.getCommunication();
        if (c != null) {
            CommunicationHelper.setActivity(this);
            if (getIntent().getBooleanExtra(Communication.EXTRA_RECONNECT, true)) {
                new Thread(c).start();
            }
        } else {
            Log.e(TAG, "Communication is null");
        }
    }

    private void saveInfo() {
        Preferences.setName(this, cbName.isChecked() ? name : "");
        Preferences.setPort(this, cbPort.isChecked() ? port : -1);
        Preferences.setPass(this, cbPass.isChecked() ? pass : "");
    }

    private void loadInfo() {
        name = Preferences.getName(this);
        etName.setText(name);

        port = Preferences.getPort(this);
        if (port == -1) {
            etPort.setText("");
        } else {
            etPort.setText("" + port);
        }

        pass = Preferences.getPass(this);
        etPass.setText(pass);

        Log.d(TAG, "loaded info: name = " + name + ", port = " + port + ", pass = " + pass);

        if (name != null && name.length() > 0) {
            cbName.setChecked(true);
        }

        if (port > 0 && port < 65536) {
            cbPort.setChecked(true);
        }

        if (pass != null && pass.length() > 0) {
            cbPass.setChecked(true);
        }
    }

    private void check() {
        int dotIndex = name.lastIndexOf(".");
        boolean isNameCorrect = (name != null && name.length() > 0 && dotIndex != -1 && dotIndex < name.length() - 1);
        boolean isPortCorrect = (port >= 1024 && port < 65536);
        boolean isPassCorrect = (pass != null && pass.length() > 0);

        if (isNameCorrect && isPortCorrect && isPassCorrect) {
            btnConnect.setEnabled(true);
        } else {
            btnConnect.setEnabled(false);
        }

        etName.setTextColor(isNameCorrect ? Color.WHITE : Color.RED);
        etPort.setTextColor(isPortCorrect ? Color.WHITE : Color.RED);
        etPass.setTextColor(isPassCorrect ? Color.WHITE : Color.RED);
    }

}