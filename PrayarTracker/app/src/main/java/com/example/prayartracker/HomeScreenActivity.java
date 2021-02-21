package com.example.prayartracker;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
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

public class HomeScreenActivity extends AppCompatActivity {

    // initializing
    // FusedLocationProviderClient
    // object
    FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    // from layout file
    TextView latitudeTextView, longitTextView,fajerTextView,dhuhrTextView,asrTextView,maghribTextView,IshaTextView;
    int PERMISSION_ID = 44;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        latitudeTextView = findViewById(R.id.latTextView);
        longitTextView = findViewById(R.id.lonTextView);
        fajerTextView = findViewById(R.id.fajerTextView);
        dhuhrTextView = findViewById(R.id.dhuhrTextView);
        asrTextView = findViewById(R.id.asrTextView);
        maghribTextView = findViewById(R.id.maghribTextView);
        IshaTextView = findViewById(R.id.IshaTextView);

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

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            latitudeTextView.setText(location.getLatitude() + "");
                            longitTextView.setText(location.getLongitude() + "");
                            Log.d("check location",  location.getLatitude()+ "/" + location.getLongitude());
                            PrayTime prayTime = new PrayTime();
                            TimeZone timeZone = TimeZone.getDefault();
                            String zone = TimeZone.getTimeZone(timeZone.getID()).getDisplayName(false,
                                    TimeZone.SHORT);
                            zone = zone.substring(4);
                            zone = zone.replaceAll(":",".");
                            Double tz = Double.parseDouble(zone);
                            prayTime.setTimeFormat(prayTime.Time12);
                            prayTime.setCalcMethod(prayTime.Makkah);
                            prayTime.setAsrJuristic(prayTime.Shafii);
                            prayTime.setAdjustHighLats(prayTime.AngleBased);
                            int[] offsets = {0, 0, 0, 0, 0, 0, 0};
                            prayTime.tune(offsets);
                            Date now = new Date();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(now);
                            ArrayList<String> prayerNames = prayTime.getTimeNames();
                            ArrayList<String> prayerTimes = prayTime.getPrayerTimes(cal,
                                    location.getLatitude(), location.getLongitude(), 3);

                            for (String prayer: prayerTimes
                                 ) {
                                    Log.d(" ","prayer" + prayer);
                                fajerTextView.setText(prayerTimes.get(0));
                                dhuhrTextView.setText(prayerTimes.get(2));
                                asrTextView.setText(prayerTimes.get(3));
                                maghribTextView.setText(prayerTimes.get(5));
                                IshaTextView.setText(prayerTimes.get(6));

                            }
                            setPrayersNotifications(location);//ONLY if the notification permission is granted
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    public void setPrayersNotifications(Location location){
        PrayTime prayTime24Format = new PrayTime();
        //prayTime24Format.setTimeFormat(prayTime24Format.Time24);
        prayTime24Format.setCalcMethod(prayTime24Format.Makkah);
        prayTime24Format.setAsrJuristic(prayTime24Format.Shafii);
        prayTime24Format.setAdjustHighLats(prayTime24Format.AngleBased);
        int[] offsets = {0, 0, 0, 0, 0, 0, 0};
        prayTime24Format.tune(offsets);
        Calendar cal = Calendar.getInstance();
        ArrayList<String> prayerTimes = prayTime24Format.getPrayerTimes(cal,location.getLatitude(), location.getLongitude(), 3);
        int alarmsCounter = 0;
        for (String prayer: prayerTimes){
            if(!prayer.equalsIgnoreCase(prayerTimes.get(1))){
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
            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
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
}

