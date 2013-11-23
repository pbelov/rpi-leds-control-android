package com.psbelov.rpi.leds.client.android;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import android.widget.Toast;
import com.psbelov.rpi.leds.data.Command;
import com.psbelov.rpi.leds.data.CommandsEnum;

class Communication implements Runnable {
    private static String TAG = "Communication";

    public final static String EXTRA_LENGTH = "EXTRA_LENGTH";
    public final static String EXTRA_RECONNECT = "EXTRA_RECONNECT";

    private ObjectInputStream is;
    private ObjectOutputStream os;

    private int mPort;
    private String mName;
    private String mPass;

    private boolean IO;
    private Activity mActivity;
    private static boolean mIsPaused;

    private int mLEDsCount = 0;
    private String mServerVersion = "";
    
    public Communication(Activity activity, String name, int port, String pass) {
        mName = name;
        mPort = port;
        mPass = pass;
        mActivity = activity;
        IO = false;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
        if (activity instanceof LoginActivity) {
            mIsPaused = false;
        }
    }

    public void seIsActivityPaused(boolean isPaused) {
        mIsPaused = isPaused;
    }

    @Override
    public void run() {
        try {
            Socket s = new Socket(InetAddress.getByName(mName), mPort);
            is = new ObjectInputStream(s.getInputStream());
            os = new ObjectOutputStream(s.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            disconnect(e.getMessage());
        }
        if (!IO) {
            sendCommand(new Command(CommandsEnum.CONNECT, mPass));
        }

        Command c;
        while (!IO) {
            c = null;
            try {
                c = (Command) is.readObject();
            } catch (EOFException e) {
                e.printStackTrace();
                disconnect("Server down");
            } catch (Exception e) {
                e.printStackTrace();
                disconnect(e.getMessage());
            }
            if (null != c) {
                processCommand(c);
            }
        }
        
    }
    
    public synchronized boolean sendCommand(Command c) {
        try {
            os.writeObject(c);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect(e.getMessage());
            return false;
        }
        return true;
    }
    
    private void showLogicForm() {
        Intent i = new Intent(mActivity, LoginActivity.class);
        i.putExtra(EXTRA_RECONNECT, false);

        mActivity.startActivity(i);
        mActivity.finish();
    }

    private void disconnect(String cause) {
        if (mIsPaused == true) {
            mActivity.finish();
        } else {
            Log.e(TAG, "Disconnected: " + cause);
            if (cause == null) {
                showLogicForm();
            } else if (cause.contains("Server down")) {
                CommunicationHelper.setCommunication(null);
                showInfoDialog(mActivity.getString(R.string.SERVER_DOWN), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mIsPaused == false) {
                            showLogicForm();
                        }
                    }
                });
            } else if (cause.contains("password")) {
                showInfoDialog(mActivity.getString(R.string.INCORRECT_PASSWORD));
            } else if (cause.contains(mActivity.getString(R.string.DISCONNECTED))) {
                showLogicForm();
            } else if (cause.contains("ECONNREFUSED")) {
                showInfoDialog(mActivity.getString(R.string.INCORRECT_SERVER));
            } else if (cause.startsWith("Unable to resolve")) {
                showInfoDialog(mActivity.getString(R.string.UNABLE_TO_CONNECT));
            } else {
                showInfoDialog(mActivity.getString(R.string.DISCONNECTED_FROM_HOST));
            }
        }
        IO = true;
    }

    private void showInfoDialog(final String msg) {
        showInfoDialog(msg, null);
    }

    private void showInfoDialog(final String msg, final DialogInterface.OnClickListener listener) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(mActivity);
                ab.setPositiveButton(android.R.string.ok, listener);
                ab.setMessage(msg);
                ab.create().show();
            }
        });
    }

    private void showNullDataError(Command c) {
        Toast.makeText(mActivity, "NULL data in " + c + " command", Toast.LENGTH_SHORT).show();
    }

    private void processCommand(final Command c) {
        Log.i(TAG, "Command " + c.getCommand().toString());
        switch (c.getCommand()) {
            case VERSION:
                if (c.getData() != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, mActivity.getString(R.string.SERVER_VERSION) + ": " + c.getData(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    mServerVersion = c.getData().toString();
                    CommunicationHelper.sendGetLength();
                } else {
                    showNullDataError(c);
                }
                break;
            case DISCONNECT:
                disconnect(mActivity.getString(R.string.DISCONNECTED) + ": " + c.getStringData());

                break;
            case LENGTH:
                if (c.getData() != null) {
                    mLEDsCount = c.getIntData();
                    startControlActivity();
                } else {
                    showNullDataError(c);
                }
                break;

        }
    }

    private void startControlActivity() {
        if (mLEDsCount > 0 && mServerVersion != null && mServerVersion.length() > 0) {
            Intent i = new Intent(mActivity, ControlActivity.class);
            i.putExtra(EXTRA_LENGTH, mLEDsCount);
            mActivity.startActivity(i);
            mActivity.finish();
        } else {
            System.out.println("Incorrect data: ledCount = " + mLEDsCount + ", Server Version = " + mServerVersion);
        }
    }
}