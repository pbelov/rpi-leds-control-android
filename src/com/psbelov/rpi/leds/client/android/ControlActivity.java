package com.psbelov.rpi.leds.client.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ControlActivity extends Activity {
    private static String TAG = "ControlActivity";

    private Button btnOn, btnOff, btnRandom;
    private SeekBar sbRed, sbGreen, sbBlue, sbIndex;
    private Switch swShow, swChase, swCycled, swListen, swListen2, swRandom, swDynamic, swRainbow, swRainbowDynamic;
    private CheckBox cbFill, cbReverted;
    private TextView tvR, tvG, tvB, tvIndex;

    private int mLedsCount;

    private int r, g, b;
    private boolean isShow = false;
    private String rgbColor = "000000";

    private Timer mListenTimer;
    private SoundListener mSoundListener;

    private VoiceController mVoiceController;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_control);

        CommunicationHelper.setActivity(this);
        mVoiceController = new VoiceController(this);

        mSoundListener = new SoundListener();
        initUI();
    }

    private void initUI() {
        btnOn = (Button)findViewById(R.id.btnOn);
        btnOff = (Button)findViewById(R.id.btnOff);
        btnRandom = (Button)findViewById(R.id.btnAllRandom);

        sbRed = (SeekBar)findViewById(R.id.sbRed);
        sbGreen = (SeekBar)findViewById(R.id.sbGreen);
        sbBlue = (SeekBar)findViewById(R.id.sbBlue);
        sbIndex = (SeekBar)findViewById(R.id.sbIndex);
        swShow = (Switch)findViewById(R.id.swShow);

        cbFill = (CheckBox)findViewById(R.id.cbFill);

        cbReverted = (CheckBox)findViewById(R.id.cbReverted);

        tvR = (TextView)findViewById(R.id.tvRColor);
        tvG = (TextView)findViewById(R.id.tvGColor);
        tvB = (TextView)findViewById(R.id.tvBColor);
        tvIndex = (TextView)findViewById(R.id.tvIndex);

        swListen = (Switch)findViewById(R.id.swListen);
        swListen2 = (Switch)findViewById(R.id.swListen2);

        swRainbow = (Switch)findViewById(R.id.swRainbow);
        swRainbowDynamic = (Switch)findViewById(R.id.swRainbowDynamic);

        swRandom = (Switch)findViewById(R.id.swRandom);
        swDynamic = (Switch)findViewById(R.id.swDynamic);

        if (getIntent() != null) {
            mLedsCount = getIntent().getIntExtra(Communication.EXTRA_LENGTH, 0);
            if (mLedsCount <= 0) {
                Log.e(TAG, mLedsCount + " leds. IMPOSSIBRU!!!");
                finish();
            } else {
                updateUI();
            }
        }

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swListen.setChecked(false);
                swListen2.setChecked(false);
                swChase.setChecked(false);
                swRandom.setChecked(false);
                swRainbow.setChecked(false);
                swRainbowDynamic.setChecked(false);
                CommunicationHelper.sendTurnAllOn();
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swListen.setChecked(false);
                swListen2.setChecked(false);
                swChase.setChecked(false);
                swRandom.setChecked(false);
                swRainbow.setChecked(false);
                swRainbowDynamic.setChecked(false);
                CommunicationHelper.sendTurnAllOff();
            }
        });

        btnRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swListen.setChecked(false);
                swListen.setChecked(false);
                swListen2.setChecked(false);
                swChase.setChecked(false);
                swRandom.setChecked(false);
                swRainbow.setChecked(false);
                swRainbowDynamic.setChecked(false);

                Random random = new Random(System.currentTimeMillis());
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);

                rgbColor = Integer.toString(r + g * 0xFF + b * 0xFFFF, 16);
                CommunicationHelper.sendTurnAll(rgbColor);
            }
        });

        sbRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bb) {
                r = i;
                tvR.setText("" + i);
                updateRGBColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bb) {
                g = i;
                tvG.setText("" + i);
                updateRGBColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bb) {
                b = i;
                tvB.setText("" + i);
                updateRGBColor();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        swShow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isShow = isChecked;
                if (isChecked) {
                    updateRGBColor();
                } else {
                    CommunicationHelper.sendTurnAllOff();
                }
            }
        });

        sbIndex.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                CommunicationHelper.sendIndex(progress - 1, rgbColor, cbFill.isChecked(), cbReverted.isChecked());
                tvIndex.setText("" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        swListen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sbIndex.setEnabled(!isChecked);
                CommunicationHelper.sendCancel();

                if (isChecked) {
                    startListen();
                } else {
                    maxAmp = 0;
                    mListenTimer.cancel();
                    mSoundListener.stop();
                    CommunicationHelper.sendTurnAllOff();
                }
            }
        });

        swListen2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    CommunicationHelper.sendCancel();
                    mVoiceController.startVoiceRecognition();
                }
            }
        });

        swChase = (Switch)findViewById(R.id.swChase);
        swChase.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swShow.setChecked(false);
                CommunicationHelper.sendCancel();

                if (isChecked) {
                    swRandom.setChecked(false);
                    updateRGBColor();
                    if (rgbColor == null && rgbColor.equals("000000")) {
                        rgbColor = "FFFFFF"; // if color is not set, making it as white
                    }
                    CommunicationHelper.sendRunChase(rgbColor, swCycled.isChecked());
                }
            }
        });

        swCycled = (Switch)findViewById(R.id.swCycled);
        swCycled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CommunicationHelper.sendCancel();

                if (swChase.isChecked()) {
                    CommunicationHelper.sendRunChase(rgbColor, isChecked);
                }
            }
        });

        swRandom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                swShow.setChecked(false);
                CommunicationHelper.sendCancel();

                if (isChecked) {
                    updateRGBColor();
                    CommunicationHelper.sendRunRandom(false);
                }
            }
        });

        swDynamic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CommunicationHelper.sendCancel();

                if (swRandom.isChecked()) {
                    CommunicationHelper.sendRunRandom(true);
                }
            }
        });

        swRainbow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CommunicationHelper.sendCancel();

                if (isChecked) {
                    CommunicationHelper.sendRainbow();
                }
            }
        });

        swRainbowDynamic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                CommunicationHelper.sendCancel();

                if (isChecked) {
                    CommunicationHelper.sendRainbowDynamic();
                }
            }
        });
    }

    private void updateUI() {
        Toast.makeText(this, "LEDs count = " + mLedsCount, Toast.LENGTH_SHORT).show();
        sbIndex.setMax(mLedsCount);

        sbRed.setProgress(r > 0 ? r : 0);
        sbGreen.setProgress(g > 0 ? g : 0);
        sbBlue.setProgress(b > 0 ? b : 0);

        updateRGBColor();
    }

    private void updateRGBColor() {
        rgbColor = String.format("%02x%02x%02x", r, g, b);
        if (isShow) {
            CommunicationHelper.sendTurnAll(rgbColor);
        }
    }

    private void startListen() {
        mSoundListener.start();

        mListenTimer = new Timer();
        mListenTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendSoundVolume();
            }
        }, 1, 1);
    }

    private double maxAmp = 0;
    private void sendSoundVolume() {
        double amp = mSoundListener.getAmplitude();
        int soundVolume;
        if (amp > 0) {
            if (amp > maxAmp) {
                maxAmp = amp;
                Log.i(TAG, "getAmplitude = " + amp);
            }
            soundVolume = (int)(mLedsCount * amp / maxAmp);
            if (soundVolume > 0)
            Log.i(TAG, "soundVolume = " + amp / maxAmp);
            CommunicationHelper.sendIndex(soundVolume - 1, rgbColor, cbFill.isChecked(), cbReverted.isChecked());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemDisconnect:
                disconnect();
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        mVoiceController.destroy();
    }

    private void disconnect() {
        swListen.setChecked(false);
        CommunicationHelper.sendDisconnect("User disconnects");
    }


    public void onPause() {
        super.onPause();

        CommunicationHelper.setActivityIsPaused(true);
    }

    public void onResume() {
        super.onResume();

        CommunicationHelper.setActivityIsPaused(false);
    }
}
