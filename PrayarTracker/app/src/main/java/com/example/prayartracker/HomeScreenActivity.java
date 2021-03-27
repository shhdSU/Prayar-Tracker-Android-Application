package com.example.prayartracker;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
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
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;//
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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


public class HomeScreenActivity extends AppCompatActivity {
    TextView TimerTextView;
    // initializing
    // FusedLocationProviderClient
    // object
    FusedLocationProviderClient mFusedLocationClient;
    public static ArrayList<String> prayerTimes12;
    public static ArrayList<String> prayerTimes24;
    public static boolean isSilentMode = false;
    public static int interval = 0;
    static PrayTime prayTime;

    // Initializing other items
    // from layout file
    TextView latitudeTextView, longitTextView,fajerTextView,dhuhrTextView,asrTextView,maghribTextView,IshaTextView;
    int PERMISSION_ID = 44;
    static Timer silentModeTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        //latitudeTextView = findViewById(R.id.latTextView);
        //longitTextView = findViewById(R.id.lonTextView);
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
                            calculatePrayerTimes(location);
                            schedulePrayersDaily(location);
                            ArrayList<String> prayerTimes = prayerTimes12;
                            if (SettingsActivity.is24Hour) {
                                prayerTimes = prayerTimes24;
                            }
                                for (String prayer : prayerTimes) {
                                    Log.d(" ", "prayer" + prayer);
                                    fajerTextView.setText(prayerTimes.get(0));
                                    dhuhrTextView.setText(prayerTimes.get(2));
                                    asrTextView.setText(prayerTimes.get(3));
                                    maghribTextView.setText(prayerTimes.get(5));
                                    IshaTextView.setText(prayerTimes.get(6));

                                }

                            // Create an array for the difference between prayer time and the current time in ms.
                            long [] PrayerDiff = new long[7];

                                // loop to fill the PrayerDiff array
                            for (int i = 0; i < 7; i++) {
                                DateFormat df = new SimpleDateFormat("HH:mm:ss"); // current time format
                                Calendar calobj = Calendar.getInstance(); // to get current time
                                String time = prayerTimes.get(i)+ ":00"; //  convert the prayer time format to HH:mm:ss format // if the time format 12 hours we use .replaceAll(" am","")
                                LocalTime localTime = LocalTime.parse(time); // convert from String to LocalTime
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
                            // loop to ensure from the result (only print)
                            for (int j = 0; j < 7; j++) {
                                String time = prayerTimes.get(j)+ ":00"; // if the time format 12 hours we use .replaceAll(" am","")
                                LocalTime localTime = LocalTime.parse(time);
                                Log.d("Before sub", String.valueOf(localTime.toSecondOfDay()*1000));
                            }
                            for (int j = 0; j < 7; j++) {
                                Log.d("Aftar sub",Long.toString(PrayerDiff[j]));
                            }


                            // loop to find the minimum positive value ( the nearest prayer time == the smallest difference )
                            long minValue = Integer.MAX_VALUE;
                            for(int i=0;i<7;i++) {
                                if(PrayerDiff[i] > 0 && minValue > PrayerDiff[i])
                                {
                                    minValue = PrayerDiff[i];
                                }
                            }
                                Log.d("Minemum",Long.toString(minValue));

                            //method to Count Down the time 
                            new CountDownTimer(minValue, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    long secondsInMilli = 1000;
                                    long minutesInMilli = secondsInMilli * 60;
                                    long hoursInMilli = minutesInMilli * 60;

                                    long elapsedHours = millisUntilFinished / hoursInMilli;
                                    millisUntilFinished = millisUntilFinished % hoursInMilli;

                                    long elapsedMinutes = millisUntilFinished / minutesInMilli;
                                    millisUntilFinished = millisUntilFinished % minutesInMilli;

                                    long elapsedSeconds = millisUntilFinished / secondsInMilli;

                                    String yy = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes,elapsedSeconds);
                                    TimerTextView.setText(yy);
                                }

                                public void onFinish() {

                                    TimerTextView.setText("00:00:00"); // here i prefer to set the notification "حان موعد صلاة ال... حسب توقيت مكة المكرمة"
                                }
                            }.start();
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
    private void schedulePrayersDaily(Location location){
        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_MONTH);
        SharedPreferences settings = getSharedPreferences("PREFS",0);
        int yesterday = settings.getInt("day",0);
        if(yesterday!=today){
            Log.d("","first time today");
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("day",today);
            editor.commit();
            calculatePrayerTimes(location);
            if(SettingsActivity.isSilentMode){
                int numMinutes = SettingsActivity.interval;
                for (String prayer: prayerTimes24) {
                    String[] time = prayer.split ( ":" );
                    int hrs = Integer.parseInt ( time[0].trim() );
                    int min = Integer.parseInt ( time[1].trim() );
                    silentModeTimer = new Timer();

                    silentModeTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        }

                    }, new Date(cal.getTime().getYear(),cal.getTime().getMonth(),cal.getTime().getDay(),hrs,min,0));
                    silentModeTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        }

                    }, new Date(cal.getTime().getYear(),cal.getTime().getMonth(),cal.getTime().getDay(),hrs,min+numMinutes,0));
                }
            }
            setPrayersNotifications(location);//ONLY if the notification permission is granted
        }
    }
private void calculatePrayerTimes(Location location){
        prayTime = new PrayTime();
    TimeZone timeZone = TimeZone.getDefault();
    String zone = TimeZone.getTimeZone(timeZone.getID()).getDisplayName(false,
            TimeZone.SHORT);
    zone = zone.substring(4);
    zone = zone.replaceAll(":",".");
    Double tz = Double.parseDouble(zone);
    prayTime.setCalcMethod(prayTime.Makkah);
    prayTime.setAdjustHighLats(prayTime.AngleBased);
    int[] offsets = {0, 0, 0, 0, 0, 0, 0};
    prayTime.tune(offsets);
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);
    prayerTimes24 = prayTime.getPrayerTimes(cal,
            location.getLatitude(), location.getLongitude(), 3);
    prayTime.setTimeFormat(prayTime.Time24);
    prayerTimes12 = prayTime.getPrayerTimes(cal,
            location.getLatitude(), location.getLongitude(), 3);
    for (String prayer: prayerTimes12
    ) {
        Log.d(" ","prayer" + prayer);
    }
}
    public void setPrayersNotifications(Location location){
        Calendar cal = Calendar.getInstance();
        int alarmsCounter = 0;
        for (String prayer: prayerTimes24){
            if(!prayer.equalsIgnoreCase(prayerTimes24.get(1))){
                alarmsCounter++;
                cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(prayer.substring(0,2)));
                cal.set(Calendar.MINUTE,Integer.parseInt(prayer.substring(3,5)));
                cal.set(Calendar.SECOND,0);
                cal.set(Calendar.MILLISECOND,0);
                scheduleAlarms(cal,alarmsCounter);
            }
        }
    }
    public void scheduleAlarms(Calendar calendar, int alarmsCounter){
        //Prepare the intents to be used to set the alarm
        Intent reminderIntent = new Intent(this, NotificationManager.class);
        PendingIntent intent = PendingIntent.getBroadcast(this,alarmsCounter, reminderIntent.putExtra("id",alarmsCounter), PendingIntent.FLAG_UPDATE_CURRENT);
        //Create the alarm manager to schedule the prayer notification
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,  calendar.getTimeInMillis(), intent);
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

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    public void goToSetting(View view) {
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(intent);
    }
}

