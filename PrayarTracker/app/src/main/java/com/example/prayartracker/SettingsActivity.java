package com.example.prayartracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.prayartracker.HomeScreenActivity.prayTime;
import static com.example.prayartracker.HomeScreenActivity.prayerTimes24;
import static com.example.prayartracker.HomeScreenActivity.silentModeTimer;

public class SettingsActivity extends AppCompatActivity {

    public static boolean isSilentMode = false;
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if(preferences.getBoolean("SilentMode",false)){
            silentModeSwitch.setChecked(true);
            linearLayout.setVisibility(View.VISIBLE);

        }
        else{
            silentModeSwitch.setChecked(false);
            linearLayout.setVisibility(View.INVISIBLE);

        }
        if(!preferences.getBoolean("is24Hour",true)){
            Time24HFormat.setChecked(false);
        }
        else{
            Time24HFormat.setChecked(true);
        }
       if(isNotificationAllowed){ //notifications are allowed
         allowNotification.setChecked(true);
       }
    else{
       allowNotification.setChecked(false);
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
                boolean flag = true;
                String minutes = minutesInput.getText().toString();
                if(silentModeSwitch.isChecked()){
                    if(minutes.equals("")){
                        Toast.makeText(SettingsActivity.this, "لطفًا قم بإدخال مدة إبفاء الجهاز على وضعية الصامت", Toast.LENGTH_SHORT).show();
                        Log.d("silent","silent");
                         flag = false;
                    }
                    else {
                        int numMinutes = Integer.parseInt(minutesInput.getText().toString());
                        SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                        pref.putBoolean("SilentMode",true);
                        pref.putInt("Interval",numMinutes);
                        pref.commit();
                        Toast.makeText(SettingsActivity.this, "سيتم وضع الجهاز على وضعية الصامت بعد كل آذان ولمدة "+ numMinutes + " دقيقة", Toast.LENGTH_SHORT).show();

                    }
                }
                else {
                    SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                    pref.putBoolean("SilentMode",false);
                    pref.putInt("Interval",0);
                    pref.commit();
                    if(silentModeTimer!= null)
                    HomeScreenActivity.silentModeTimer.cancel();
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    Toast.makeText(SettingsActivity.this, "تم إلغاء وضع الصامت", Toast.LENGTH_SHORT).show();

                }
                SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                if(Time24HFormat.isChecked()){
                    preferences.putBoolean("is24Hour",true);
                    preferences.commit();
                    Toast.makeText(SettingsActivity.this, "تم وضع التطبيق على نظام ٢٤ ساعة", Toast.LENGTH_SHORT).show();

                }
                else{
                    preferences.putBoolean("is24Hour",false);
                    preferences.commit();
                    Toast.makeText(SettingsActivity.this, "تم وضع التطبيق على نظام ١٢ ساعة", Toast.LENGTH_SHORT).show();

                }
                if(flag) {
                    Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}