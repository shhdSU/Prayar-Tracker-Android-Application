package com.example.prayartracker;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;//
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import android.os.CountDownTimer;
import android.os.Handler;

public class HomeScreenActivity extends AppCompatActivity {
    static TextView TimerTextView;
    // initializing
    // FusedLocationProviderClient
    // object
    FusedLocationProviderClient mFusedLocationClient;
    public static ArrayList<String> prayerTimes12;
    public static ArrayList<String> prayerTimes24;
    static PrayTime prayTime;
    static Handler handler;
    static Runnable runnable;
    static String upcomingPrayer;
    static boolean onFinish = true;
    static long minValue;
    static long [] PrayerDiff;
    static boolean first = true;
    SharedPreferences sp;
    // Initializing other items
    // from layout file
    TextView fajerTextView,dhuhrTextView,asrTextView,maghribTextView,IshaTextView;
    int PERMISSION_ID = 44;
    static Timer silentModeTimer;
    static CountDownTimer countDownTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        handler = new Handler();
        fajerTextView = findViewById(R.id.fajerTextView);
        dhuhrTextView = findViewById(R.id.dhuhrTextView);
        asrTextView = findViewById(R.id.asrTextView);
        maghribTextView = findViewById(R.id.maghribTextView);
        IshaTextView = findViewById(R.id.IshaTextView);
        TimerTextView = findViewById(R.id.CountDownTextView);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();

    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            prayerTimes24 = null;
                            calculatePrayerTimes(location);
                            schedulePrayersDaily(location);
                            ArrayList<String> prayerTimes = null;
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            if (!preferences.getBoolean("is24Hour", true)) {
                                prayerTimes = prayerTimes12;
                            } else {
                                prayerTimes = prayerTimes24;
                            }
                            ArrayList<String> prayerTimesForCount24H = prayerTimes24;
                            for (String prayer : prayerTimes) {
                                Log.d(" ", "prayer" + prayer);
                            }
                            fajerTextView.setText(prayerTimes.get(0));
                            dhuhrTextView.setText(prayerTimes.get(2));
                            asrTextView.setText(prayerTimes.get(3));
                            maghribTextView.setText(prayerTimes.get(5));
                            IshaTextView.setText(prayerTimes.get(6));


                            getPrayerDiff(prayerTimesForCount24H);
                            // loop to ensure from the result (only print)
                            for (int j = 0; j < 7; j++) {
                                String time = prayerTimesForCount24H.get(j) + ":00"; // if the time format 12 hours we use .replaceAll(" am","")
                                LocalTime localTime = LocalTime.parse(time);
                                Log.d("Before sub", String.valueOf(localTime.toSecondOfDay() * 1000));
                            }
                            for (int j = 0; j < 7; j++) {
                                Log.d("Aftar sub", Long.toString(PrayerDiff[j]));
                            }
                            getMinValue();
                            SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                            for (int i = 0; i < 7; i++) {
                                if (PrayerDiff[i] == minValue) {
                                    switch (i) {
                                        case 0:
                                            upcomingPrayer = "الفجر";
                                            break;
                                        case 2:
                                            upcomingPrayer = "الظهر";

                                            break;

                                        case 3:
                                            upcomingPrayer = "العصر";
                                            break;

                                        case 5:
                                            upcomingPrayer = "المغرب";
                                            break;

                                        case 6:
                                            upcomingPrayer = "العشاء";
                                            break;

                                    }
                                }
                                pref.putString("upcomingPrayer", upcomingPrayer);
                                pref.commit();
                            }
                            Log.d("Minemum", Long.toString(minValue));
                            Calendar cal = Calendar.getInstance();
                            int numMinutes = preferences.getInt("Interval", 0);
                            if (silentModeTimer != null) {
                                silentModeTimer.cancel();
                            }
                            silentModeTimer = new Timer();
                            for (String prayer : prayerTimes24) {
                                String[] time = prayer.split(":");
                                int hrs = Integer.parseInt(time[0].trim());
                                int min = Integer.parseInt(time[1].trim());
                                LocalDateTime date1 = LocalDateTime.of(cal.getTime().getYear(), cal.getTime().getMonth(), cal.getTime().getDate(), cal.getTime().getHours(), cal.getTime().getMinutes(), cal.getTime().getSeconds());
                                LocalDateTime date2 = LocalDateTime.of(cal.getTime().getYear(), cal.getTime().getMonth(), cal.getTime().getDate(), hrs, min, 0);
                                Log.d("date1",date1+"");
                                Log.d("date2",date2+"");
                                    if (date1.isBefore(date2)){
                                        Log.d("hi","inside prayer");
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        android.app.NotificationManager notificationManager = getSystemService(android.app.NotificationManager.class);
                                        if (!notificationManager.isNotificationPolicyAccessGranted()) {
                                            Intent in = new Intent();
                                            in.setAction(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                            startActivityForResult(in, 1);
                                        }
                                        silentModeTimer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                                            }

                                        }, new Date(cal.getTime().getYear(), cal.getTime().getMonth(), cal.getTime().getDate(), hrs, min, 0));
                                        silentModeTimer.schedule(new TimerTask() {
                                            @Override
                                            public void run() {
                                                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                                            }

                                        }, (hrs * 60 * 60 * 1000) + (min * 60 * 1000) + (numMinutes * 60 * 1000));

                                    }
                            }
                            if(first) {
                                first = false;
                                countDownTimer = new CountDownTimer(minValue, 1000) {
                                    public void onTick(long millisUntilFinished) {
                                        long secondsInMilli = 1000;
                                        long minutesInMilli = secondsInMilli * 60;
                                        long hoursInMilli = minutesInMilli * 60;

                                        long elapsedHours = millisUntilFinished / hoursInMilli;
                                        millisUntilFinished = millisUntilFinished % hoursInMilli;

                                        long elapsedMinutes = millisUntilFinished / minutesInMilli;
                                        millisUntilFinished = millisUntilFinished % minutesInMilli;

                                        long elapsedSeconds = millisUntilFinished / secondsInMilli;

                                        String text = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
                                        TimerTextView.setText(text);
                                    }

                                    public void onFinish() {
                                        TimerTextView.setText("00:00:00"); // here i prefer to set the notification "حان موعد صلاة ال... حسب توقيت مكة المكرمة"
                                        this.cancel();
                                        Log.d("Minvalue", "" + minValue);
                                        getMinValue();
                                        Log.d("Minvalue", "" + minValue);
                                        this.start();
                                    }
                                };
                                countDownTimer.start();
                            }
                            handler.postDelayed(runnable = new Runnable() {
                                public void run() {
                                    handler.postDelayed(runnable, minValue);
                                    if (countDownTimer != null) {
                                        countDownTimer.cancel();
                                        Log.d("cancel", "cancel");
                                    }
                                    getMinValue();
                                    Log.d("cancel", "cancel");
                                    if (onFinish) {
                                        //method to Count Down the time
                                        countDownTimer = new CountDownTimer(minValue, 1000) {
                                            public void onTick(long millisUntilFinished) {
                                                long secondsInMilli = 1000;
                                                long minutesInMilli = secondsInMilli * 60;
                                                long hoursInMilli = minutesInMilli * 60;

                                                long elapsedHours = millisUntilFinished / hoursInMilli;
                                                millisUntilFinished = millisUntilFinished % hoursInMilli;

                                                long elapsedMinutes = millisUntilFinished / minutesInMilli;
                                                millisUntilFinished = millisUntilFinished % minutesInMilli;

                                                long elapsedSeconds = millisUntilFinished / secondsInMilli;

                                                String text = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
                                                TimerTextView.setText(text);
                                            }

                                            public void onFinish() {
                                                TimerTextView.setText("00:00:00"); // here i prefer to set the notification "حان موعد صلاة ال... حسب توقيت مكة المكرمة"
                                                this.cancel();
                                                sendNotification();
                                                Log.d("Minvalue", "" + minValue);
                                                getMinValue();
                                                Log.d("Minvalue", "" + minValue);
                                                this.start();
                                            }
                                        };
                                        countDownTimer.start();
                                    }
                                }

                            },minValue);

                        }
                            }

                });
            } else {
                Toast.makeText(this, "لطفًا قم بتفعيل خاصية الموقع لهذا التطبيق", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getPrayerDiff(ArrayList<String> prayerTimesForCount24H) {
        // Create an array for the difference between prayer time and the current time in ms.
        PrayerDiff = new long[7];
        // loop to fill the PrayerDiff array
        for (int i = 0; i < 7; i++) {
            DateFormat df = new SimpleDateFormat("HH:mm:ss"); // current time format
            Calendar calobj = Calendar.getInstance(); // to get current time
            String time;
            time = prayerTimesForCount24H.get(i)+ ":00"; //  convert the prayer time format to HH:mm:ss format // if the time format 12 hours we use .replaceAll(" am","")
            LocalTime localTime = null; // convert from String to LocalTime
                localTime = LocalTime.parse(time);
            LocalTime CurrentTime = LocalTime.parse(df.format(calobj.getTime()));// convert from String to LocalTime
            if(Math.abs(localTime.toSecondOfDay()*1000 - CurrentTime.toSecondOfDay()*1000) >= 53940000){ // if the difference is grater or equal 8(20:00:00) then
            long diff = (localTime.toSecondOfDay()*1000 - CurrentTime.toSecondOfDay()*1000)+ 86400000; // add 24 hours to get the correct time.
            long diffSeconds = TimeUnit.MILLISECONDS.toMillis(diff) ; // convert to ms
            PrayerDiff[i] = diffSeconds;} //assign
            else{
                long diff = (localTime.toSecondOfDay()*1000 - CurrentTime.toSecondOfDay()*1000); // without adding 24 hours
                long diffSeconds = TimeUnit.MILLISECONDS.toMillis(diff) ;// convert to ms
                PrayerDiff[i] = diffSeconds;//assign
            }
            Log.d("MY CUURENT TIME 2", String.valueOf(CurrentTime.toSecondOfDay()*1000));

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getMinValue() {
        getPrayerDiff(prayerTimes24);
        // loop to find the minimum positive value ( the nearest prayer time == the smallest difference )
         minValue = Integer.MAX_VALUE;
        for(int i=0;i<7;i++) {
            if(PrayerDiff[i] > 0 && minValue > PrayerDiff[i])
            {
                minValue = PrayerDiff[i];
            }
        }
    }

    private void schedulePrayersDaily(Location location){
        calculatePrayerTimes(location);
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_MONTH);
        SharedPreferences settings = getSharedPreferences("PREFS",0);
        int yesterday = settings.getInt("day",0);
          if(yesterday!=today){
            Log.d("","first time today");
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("day",today);
            editor.commit();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            }
       }
private void calculatePrayerTimes(Location location){
        prayTime = new PrayTime();
    TimeZone timeZone = TimeZone.getDefault();
    String zone = TimeZone.getTimeZone(timeZone.getID()).getDisplayName(false,
            TimeZone.SHORT);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    zone = zone.substring(4);
    zone = zone.replaceAll(":",".");
    Double tz = Double.parseDouble(zone);
    switch (preferences.getString("CalcMethod","Makkah")){
        case "Makkah":
            prayTime.setCalcMethod(prayTime.Makkah);
            break;
        case "Tehran":
            prayTime.setCalcMethod(prayTime.Tehran);
            break;

        case "ISNA":
            prayTime.setCalcMethod(prayTime.ISNA);
            break;

        case "Egypt":
            prayTime.setCalcMethod(prayTime.Egypt);
            break;

        case "Custom":
            prayTime.setCalcMethod(prayTime.Custom);
            break;

        case "Karachi":
            prayTime.setCalcMethod(prayTime.Karachi);
            break;

        case "MWL":
            prayTime.setCalcMethod(prayTime.MWL);
            break;

        case "Jafari":
            prayTime.setCalcMethod(prayTime.Jafari);
            break;
    }
    switch (preferences.getString("JuristicMethod","Shafii")){
        case "Shafii":
            prayTime.setAsrJuristic(prayTime.Shafii);
            break;
        case "Hanafi":
            prayTime.setAsrJuristic(prayTime.Hanafi);
            break;
    }

    switch(preferences.getString("HighAltCalc","AngleBased")){
        case "AngleBased":
            prayTime.setAdjustHighLats(prayTime.AngleBased);
            break;
        case "None":
            prayTime.setAdjustHighLats(prayTime.None);
            break;

        case "Midnight":
            prayTime.setAdjustHighLats(prayTime.MidNight);
            break;

        case "OneSeventh":
            prayTime.setAdjustHighLats(prayTime.OneSeventh);
            break;
    }
    int[] offsets = {0, 0, 0, 0, 0, 0, 0};
    prayTime.tune(offsets);
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);
    Log.d("S",""+location.getLatitude());
    Log.d("S",""+location.getLongitude());
    prayTime.setTimeFormat(prayTime.Time24);
    prayerTimes24 = prayTime.getPrayerTimes(cal,
            location.getLatitude(), location.getLongitude(), 3);
    prayTime.setTimeFormat(prayTime.Time12);
    prayerTimes12 = prayTime.getPrayerTimes(cal,
            location.getLatitude(), location.getLongitude(), 3);
    for (String prayer: prayerTimes12
    ) {
        Log.d(" ","prayer" + prayer);
    }
}

    public void sendNotification(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        boolean isNotificationAllowed = pref.getBoolean("isNotificationAllowed",true);
        if(!isNotificationAllowed)
            return;
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        Intent prayNowIntent = new Intent(getApplicationContext(), HomeScreenActivity.class);//on tap go to home
        String prayer =  pref.getString("upcomingPrayer","ERROR CHECK UPCOMING PRAYER");
        prayNowIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,prayNowIntent,0);
        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(getApplicationContext(), "high_important_channel")
                        .setContentTitle("عِمَـــــاد")
                        .setContentText("حان الآن موعد صلاة "+prayer)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(1, notifyBuilder.build());

    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            //latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
           // longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
            getLastLocation();

    }

    public void goToSetting(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }
    public void signOut(View view){
        sp.edit().putBoolean("logged",false).apply(); //Please don't remove it
        //Start your code HERE
    finish(); 

    }
}

