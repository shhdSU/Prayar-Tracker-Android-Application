package com.example.prayartracker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationManager extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isNotificationAllowed = pref.getBoolean("isNotificationAllowed",true);
        if(!isNotificationAllowed)
            return;
        System.out.println("RECEIVED NOTIFICATION");
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        Intent prayNowIntent = new Intent(context, HomeScreenActivity.class);//on tap go to home
        String prayer =  pref.getString("upcomingPrayer","ERROR CHECK UPCOMING PRAYER");
        prayNowIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,prayNowIntent.getIntExtra("id",0),prayNowIntent,0);

        NotificationCompat.Builder notifyBuilder =
                new NotificationCompat.Builder(context, "high_important_channel")
                        .setContentTitle("عِمَـــــاد")
                        .setContentText("حان الآن موعد صلاة "+prayer)
                        .setSmallIcon(R.drawable.ic_logo)
                        //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.======))
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify(1, notifyBuilder.build());
           }



}
