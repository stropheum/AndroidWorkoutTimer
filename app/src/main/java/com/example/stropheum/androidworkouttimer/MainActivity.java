package com.example.stropheum.androidworkouttimer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.stropheum.androidworkouttimer.R;

import com.example.stropheum.androidworkouttimer.view.MyTimePickerDialog;
import com.example.stropheum.androidworkouttimer.view.TimePicker;

import java.io.IOException;

public class MainActivity extends Activity {
    private TextView time;
    ImageButton setTimeButton;
    Button startButton;
    Button stopButton;
    CountDownTimer timer;
    long millisRemaining;
    long minutesRemaining, secondsRemaining;
    Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_main);
        updateViews();

        setTimeButton = (ImageButton) findViewById(R.id.time_set);
        startButton = (Button) findViewById(R.id.startButton);
        stopButton  = (Button) findViewById(R.id.stopButton);
        millisRemaining = 0;
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
            }
        });
    }

    private void updateViews(){
        time = (TextView) findViewById(R.id.time);
    }

    public void showPicker(View v){
        MyTimePickerDialog mTimePicker = new MyTimePickerDialog(this, new MyTimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int minute, int seconds) {
                final TextView timerDisplay = (TextView)findViewById(R.id.timerDisplay);
                timerDisplay.setText(String.format(String.format("%02d", minute) + ":" + String.format("%02d", seconds)));
                Button goButton = (Button) findViewById(R.id.startButton);

                // Initialize timer value when new time is selected
                millisRemaining = minute * 60 * 1000;
                millisRemaining += seconds * 1000;

                goButton.setEnabled(true);
            }
        }, true);
        mTimePicker.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Method activated by "go" button. Starts/resume timer
     */
    private void startTimer() {
        startButton.setEnabled(false);
        setTimeButton.setEnabled(false);

        timer = new CountDownTimer(millisRemaining, 100) {
            public void onTick(long millisUntilFinished) {
                millisRemaining = millisUntilFinished;
                minutesRemaining = millisRemaining / 60000;
                secondsRemaining = (millisRemaining % 60000) / 1000;

                final TextView timerDisplay = (TextView) findViewById(R.id.timerDisplay);
                timerDisplay.setText(String.format(String.format("%02d", minutesRemaining) +
                        ":" + String.format("%02d", secondsRemaining)));
            }

            public void onFinish() {
                final TextView timerDisplay = (TextView) findViewById(R.id.timerDisplay);
                timerDisplay.setText("00:00");
                stopButton.setEnabled(false);
                setTimeButton.setEnabled(true);

                // Play beep sound
                playSound();
                vibrateStop();
            }
        };

        // Prep timer to give a 3 second countdown before resuming
        new CountDownTimer(3000, 100) {
            public void onTick(long millisUntilFinished) {
                long seconds;
                if (millisUntilFinished > 2000) {
                    seconds = 3;
                } else if (millisUntilFinished > 1000) {
                    seconds = 2;
                } else {
                    seconds = 1;
                }

                final TextView timerDisplay = (TextView)findViewById(R.id.timerDisplay);
                timerDisplay.setText(String.format("   ") + String.format("%d", seconds) + String.format("    "));
            }
            public void onFinish() {
                timer.start();

                // Play sound when countdown finishes
                playSound();
                vibrateStart();
                stopButton.setEnabled(true);
            }
        }.start();

    }

    /**
     * Method activated by "stop" button. Pauses current timer
     */
    private void stopTimer() {
        setTimeButton.setEnabled(true);
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        timer.cancel();
    }

    /**
     * Method called to audibly notify user when workout is completed
     */
    private void playSound() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method called to vibrate when workout begins
     */
    private void vibrateStart() {
        v.vibrate(new long[]{0, 1000}, -1);
    }

    /**
     * Method called to vibrate when workout ends
     */
    private void vibrateStop() {
        v.vibrate(new long[]{0, 200, 200, 200, 200, 200}, -1);
    }
}
