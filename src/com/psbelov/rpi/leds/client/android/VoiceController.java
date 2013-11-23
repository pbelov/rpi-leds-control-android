package com.psbelov.rpi.leds.client.android;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;

public class VoiceController {
    private final String CMD_RED = "red";
    private final String[] CMDS_RED = {CMD_RED, "красный"};
    private final String CMD_GREEN = "green";
    private final String[] CMDS_GREEN = {CMD_GREEN, "зелёный", "зеленый"};
    private final String CMD_BLUE = "blue";
    private final String[] CMDS_BLUE = {CMD_BLUE, "синий"};

    private final String CMD_YELLOW = "yellow";
    private final String[] CMDS_YELLOW = {CMD_YELLOW, "жёлтый", "желтый"};

    private final String CMD_WHITE = "white";
    private final String[] CMDS_WHITE = {CMD_WHITE, "белый"};
    private final String CMD_OFF = "off";
    private final String[] CMDS_OFF = {CMD_OFF, "выключить"};

    private final String CMD_RAINBOW = "rainbow";
    private final String[] CMDS_RAINBOW = {CMD_RAINBOW, "радуга"};

    private final String CMD_STARS = "stars";
    private final String[] CMDS_STARS = {CMD_STARS, "звёзды", "звезды"};


    private static final String TAG = "GoogleVoiceController";
    
    private SpeechRecognizer mSpeechRecognizer = null;
    private RecognitionListener mRecognitionListener;

    private Intent intent;
    private Activity mActivity;

    private boolean isStarted = false;

    private ArrayList<String> data;
    

    protected VoiceController(Activity context) {
        mActivity = context;
        Log.i(TAG, "onCreate()");
        
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        initializeRecognitionListener();
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
        
        init();
    }
    
    private void init() {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU");
        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, Long.valueOf(3000L));
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
    }
    
    public void startVoiceRecognition() {
        if (isStarted == false) {
            isStarted = true;
            initializeRecognitionListener();
        
            mSpeechRecognizer.startListening(intent);
        } else {
            stopVoiceRecognition();
        }
    }
    
    public void stopVoiceRecognition() {
        mSpeechRecognizer.stopListening();
    }

    private void initializeRecognitionListener() {
        mRecognitionListener = new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle params) {
                Log.e("onReadyForSpeech()", "onReadyForSpeech!");
            }
            
            @Override
            public void onBeginningOfSpeech() {
                Log.e("onBeginningOfSpeech()", "onBeginningOfSpeech!");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }
            @Override
            public void onResults(Bundle results) {
                Log.e(TAG, "onResults");
                isStarted = false;

                data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.i(TAG, "results = " + data);
                String command = parseCommand(data);
                if (handleResults(command)) {
                    Toast.makeText(mActivity, "ok", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "nok", Toast.LENGTH_SHORT).show();
                }

                ((Switch)mActivity.findViewById(R.id.swListen2)).setChecked(false);

            }
            
            @Override
            public void onPartialResults(Bundle partialResults) {
                Log.e("onPartialResults", "onPartialResults " + partialResults);
            }
            
            @Override
            public void onError(int error) {
                Log.e(TAG, "errorCode #" + error);
                Toast.makeText(mActivity, "speech_error #" + error, Toast.LENGTH_SHORT).show();
                ((Switch)mActivity.findViewById(R.id.swListen2)).setChecked(false);

                isStarted = false;
            }
            
            @Override
            public void onEndOfSpeech() {
                Log.e("onEndOfSpeech()", "onEndOfSpeech!");
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
            }
            
            @Override
            public void onEvent(int eventType, Bundle params) {
            }

        };
    }

    private String parseCommand(ArrayList<String> data) {
        String command = null;

        String commands[][] = {CMDS_RED, CMDS_GREEN, CMDS_BLUE, CMDS_YELLOW, CMDS_WHITE, CMDS_OFF, CMDS_RAINBOW, CMDS_STARS};
        for (String string : data) {
            // cycle for all possible commands arrays
            for (int i = 0; i < commands.length; i++) {
                // cycle for all possible commands in each array
                for (int j = 0; j < commands[i].length; j++) {
                    String cmd = string.toLowerCase().replaceAll(":", "").trim();
                    if (cmd.endsWith(commands[i][j])) {
                        Log.i(TAG, "returned command: " + commands[i][0] + " (" + cmd + ")");
                        // returning the first command of internal array (w/o number)
                        return commands[i][0];
                    }
                }
            }
        }

        return command;
    }

    private boolean handleResults(String command) {
        if (command == null) {
            return false;
        } else {
            switch (command) {
                case CMD_RED:
                    CommunicationHelper.sendCancel();
                    CommunicationHelper.sendTurnAll("FF0000");
                    break;

                case CMD_GREEN:
                    CommunicationHelper.sendCancel();
                    CommunicationHelper.sendTurnAll("00FF00");
                    break;

                case CMD_BLUE:
                    CommunicationHelper.sendCancel();
                    CommunicationHelper.sendTurnAll("0000FF");
                    break;

                case CMD_YELLOW:
                    CommunicationHelper.sendCancel();
                    CommunicationHelper.sendTurnAll("FFFF00");
                    break;

                case CMD_WHITE:
                    CommunicationHelper.sendCancel();
                    CommunicationHelper.sendTurnAll("FFFFFF");
                    break;

                case CMD_OFF:
                    CommunicationHelper.sendTurnAll("000000");
                    break;

                case CMD_RAINBOW:
                    CommunicationHelper.sendCancel();
                    CommunicationHelper.sendRainbow();
                    break;

                case CMD_STARS:
                    CommunicationHelper.sendCancel();
                    CommunicationHelper.sendRunStars2("0000A2");
                    break;
            }

            return true;
        }
    }

    public void destroy() {
        mSpeechRecognizer.stopListening();
        mSpeechRecognizer.cancel();
        mSpeechRecognizer.destroy();
    }
}
