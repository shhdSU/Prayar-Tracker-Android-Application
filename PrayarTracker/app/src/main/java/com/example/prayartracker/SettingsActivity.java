package com.example.prayartracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.ToggleButton;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.prayartracker.HomeScreenActivity.prayTime;
import static com.example.prayartracker.HomeScreenActivity.prayerTimes24;

public class SettingsActivity extends AppCompatActivity {

    public static boolean isSilentMode = false;
    public static int interval = 0;
    public static boolean is24Hour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
         final Switch allowNotification = findViewById(R.id.allowNotif);
         final Switch silentModeSwitch = findViewById(R.id.silentModeSwitch);
         final Switch Time24HFormat = findViewById(R.id.Time24HFormat);
         SharedPreferences settings = getSharedPreferences("PREFS",0);
        boolean isNotificationAllowed = settings.getBoolean("isNotificationAllowed",true);
        final SharedPreferences.Editor editor = settings.edit();
        final LinearLayout linearLayout = (LinearLayout) findViewById((R.id.linearLayout));
        final EditText minutesInput = (EditText) findViewById((R.id.editTextNumber2));
        Button saveBtn = (Button) findViewById((R.id.SaveBtn));
        if(prayTime.getTimeFormat() == prayTime.Time12){
            Time24HFormat.setChecked(false);
        }
        else{
            Time24HFormat.setChecked(true);
        }
       if(isNotificationAllowed){ //notifications are allowed
         allowNotification.setChecked(true);
       }
    else{
       allowNotification.setEnabled(false);
    }
        allowNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    editor.putBoolean("isNotificationAllowed",true);
                } else {
                    editor.putBoolean("isNotificationAllowed",false);
                }
                editor.commit();

            }
        });
        silentModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    linearLayout.setVisibility(View.VISIBLE);
                } else {
                    linearLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(silentModeSwitch.isChecked()){
                    int numMinutes = Integer.parseInt(minutesInput.getText().toString());
                    interval = numMinutes;
                    isSilentMode = true;
                    for (String prayer: prayerTimes24) {

                        String[] time = prayer.split ( ":" );
                        int hrs = Integer.parseInt ( time[0].trim() );
                        int min = Integer.parseInt ( time[1].trim() );
                        Date currentDate = Calendar.getInstance().getTime();
                        HomeScreenActivity.silentModeTimer = new Timer();

                        HomeScreenActivity.silentModeTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                            }

                        }, new java.util.Date(currentDate.getYear(),currentDate.getMonth(),currentDate.getDay(),hrs,min,0));
                        HomeScreenActivity.silentModeTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                            }

                        }, new Date(currentDate.getYear(),currentDate.getMonth(),currentDate.getDay(),hrs,min+numMinutes,0));
                    }
                }
                else {
                    isSilentMode = false;
                    HomeScreenActivity.silentModeTimer.cancel();
                }
//                if(allowNotifications.isChecked()){
//                }
//                else{
//                }
                if(Time24HFormat.isChecked()){
                    prayTime.setTimeFormat(prayTime.Time24);
                    is24Hour = true;
                }
                else{
                    prayTime.setTimeFormat(prayTime.Time12);
                    is24Hour = false;

                }
                Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                startActivity(intent);
            }
        });

    }
}