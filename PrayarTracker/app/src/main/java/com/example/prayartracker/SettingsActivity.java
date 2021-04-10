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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
         final RadioGroup ShafiiGroup = findViewById(R.id.ShafiiButtonGroup);
         final RadioGroup AltitudeGroup = findViewById(R.id.AltitudeButtonGroup);
         final RadioGroup CalcMethod = findViewById(R.id.CalcMethodBtnGroup);
         SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isNotificationAllowed = settings.getBoolean("isNotificationAllowed",true);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        final LinearLayout linearLayout = (LinearLayout) findViewById((R.id.linearLayout));
        final EditText minutesInput = (EditText) findViewById((R.id.editTextNumber2));
        Button saveBtn = (Button) findViewById((R.id.SaveBtn));
        if(settings.getBoolean("SilentMode",false)){
            silentModeSwitch.setChecked(true);
            linearLayout.setVisibility(View.VISIBLE);

        }
        else{
            silentModeSwitch.setChecked(false);
            linearLayout.setVisibility(View.INVISIBLE);

        }
        if(!settings.getBoolean("is24Hour",true)){
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
        RadioButton Jafari = (RadioButton) findViewById(R.id.Jafari);
        RadioButton MWL = (RadioButton) findViewById(R.id.MWL);
        RadioButton Karachi = (RadioButton) findViewById(R.id.Karachi);
        RadioButton ISNA = (RadioButton) findViewById(R.id.ISNA);
        RadioButton Tehran = (RadioButton) findViewById(R.id.Tehran);
        RadioButton Makkah = (RadioButton) findViewById(R.id.Makkah);
        RadioButton Egypt = (RadioButton) findViewById(R.id.Egypt);
        RadioButton Custom = (RadioButton) findViewById(R.id.Custom);

        switch(settings.getString("CalcMethod","Makkah")){
        case "Makkah":
            Makkah.setChecked(true);
            break;
            case "Tehran":
                Tehran.setChecked(true);
                break;

            case "ISNA":
                ISNA.setChecked(true);
                break;

            case "Egypt":
                Egypt.setChecked(true);
                break;

            case "Custom":
                Custom.setChecked(true);
                break;

            case "Karachi":
                Karachi.setChecked(true);
                break;

            case "MWL":
                MWL.setChecked(true);
                break;

            case "Jafari":
                Jafari.setChecked(true);
                break;

        }
        RadioButton Hanafi = (RadioButton) findViewById(R.id.HanafiBtn);
        RadioButton Shafii = (RadioButton) findViewById(R.id.ShafiiBtn);

        switch(settings.getString("JuristicMethod","Shafii")){
            case "Shafii":
                Shafii.setChecked(true);
                break;

            case "Hanafi":
                Hanafi.setChecked(true);
                break;

        }
        RadioButton None = (RadioButton) findViewById(R.id.None);
        RadioButton Midnight = (RadioButton) findViewById(R.id.MidNight);
        RadioButton OneSeventh = (RadioButton) findViewById(R.id.OneSeventh);
        RadioButton AngleBased = (RadioButton) findViewById(R.id.AngleBased);
        switch(settings.getString("HighAltCalc","AngleBased")){
            case "AngleBased":
                AngleBased.setChecked(true);
                break;
            case "None":
                None.setChecked(true);
                break;

            case "Midnight":
                Midnight.setChecked(true);
                break;

            case "OneSeventh":
                OneSeventh.setChecked(true);
                break;

        }
        AltitudeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
//
//                switch(checkedId){
//                    case R.id.None:
//                        editor.putString("HighAltCalc","None");
//                        break;
//                    case R.id.MidNight:
//                        editor.putString("HighAltCalc","Midnight");
//                        break;
//                    case R.id.OneSeventh:
//                        editor.putString("HighAltCalc","OneSeventh");
//                        break;
//                    case R.id.AngleBased:
//                        editor.putString("HighAltCalc","AngleBased");
//                        break;
//                }
            }
        });
        ShafiiGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
//
//                switch(checkedId) {
//                    case R.id.ShafiiBtn:
//                        editor.putString("JuristicMethod", "Shafii");
//                        break;
//                    case R.id.HanafiBtn:
//                        editor.putString("JuristicMethod", "Hanafi");
//                        break;
//                }
            }
        });
        CalcMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                Log.d("hi!", ""+checkedId);
//                switch(checkedId){
//                    case R.id.Jafari:
//                        editor.putString("CalcMethod","Jafari");
//                        break;
//                    case R.id.Karachi:
//                        editor.putString("CalcMethod","Karachi");
//                        break;
//                    case R.id.ISNA:
//                        editor.putString("CalcMethod","ISNA");
//                        break;
//                    case R.id.MWL:
//                        editor.putString("CalcMethod","MWL");
//                        break;
//                    case R.id.Makkah:
//                        editor.putString("CalcMethod","Makkah");
//                        break;
//                    case R.id.Egypt:
//                        editor.putString("CalcMethod","Egypt");
//                        break;
//                    case R.id.Tehran:
//                        editor.putString("CalcMethod","Tehran");
//                        break;
//                    case R.id.Custom:
//                        editor.putString("CalcMethod","Custom");
//                        break;
//                }

            }
        });
        allowNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                if(isChecked) {
                    // The toggle is enabled
                    editor.putBoolean("isNotificationAllowed",true);
                    editor.commit();
                } else {
                    editor.putBoolean("isNotificationAllowed",false);
                    editor.commit();

                }
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
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                boolean flag = true;
                String minutes = minutesInput.getText().toString();
                if(silentModeSwitch.isChecked()){
                    if(minutes.equals("")){
                        Toast.makeText(SettingsActivity.this, "لطفًا قم بإدخال مدة إبفاء الجهاز على وضعية الصامت", Toast.LENGTH_SHORT).show();
                        Log.d("silent","silent");
                         flag = false;
                         return;
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
                if(allowNotification.isChecked()){
                    editor.putBoolean("isNotificationAllowed",true);
                }
                else{
                    editor.putBoolean("isNotificationAllowed",false);
                }

                switch(AltitudeGroup.getCheckedRadioButtonId()){
                    case R.id.None:
                        editor.putString("HighAltCalc","None");
                        break;
                    case R.id.MidNight:
                        editor.putString("HighAltCalc","Midnight");
                        break;
                    case R.id.OneSeventh:
                        editor.putString("HighAltCalc","OneSeventh");
                        break;
                    case R.id.AngleBased:
                        editor.putString("HighAltCalc","AngleBased");
                        break;
                }

                switch(ShafiiGroup.getCheckedRadioButtonId()) {
                    case R.id.ShafiiBtn:
                        editor.putString("JuristicMethod", "Shafii");
                        break;
                    case R.id.HanafiBtn:
                        editor.putString("JuristicMethod", "Hanafi");
                        break;
                }
                switch(CalcMethod.getCheckedRadioButtonId()){
                    case R.id.Jafari:
                        editor.putString("CalcMethod","Jafari");
                        break;
                    case R.id.Karachi:
                        editor.putString("CalcMethod","Karachi");
                        break;
                    case R.id.ISNA:
                        editor.putString("CalcMethod","ISNA");
                        break;
                    case R.id.MWL:
                        editor.putString("CalcMethod","MWL");
                        break;
                    case R.id.Makkah:
                        editor.putString("CalcMethod","Makkah");
                        break;
                    case R.id.Egypt:
                        editor.putString("CalcMethod","Egypt");
                        break;
                    case R.id.Tehran:
                        editor.putString("CalcMethod","Tehran");
                        break;
                    case R.id.Custom:
                        editor.putString("CalcMethod","Custom");
                        break;
                }
                editor.commit();
                if(flag) {
                    Intent intent = new Intent(getApplicationContext(), HomeScreenActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    public void GoBack(View view) {
     finish();
    }
}
