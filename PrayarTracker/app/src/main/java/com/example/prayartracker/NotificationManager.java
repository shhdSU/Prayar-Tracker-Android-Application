package com.example.prayartracker;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationManager extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = context.getSharedPreferences("PREFS",0);
        boolean isNotificationAllowed = settings.getBoolean("isNotificationAllowed",true);
        if(!isNotificationAllowed)
            return;
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        Intent prayNowIntent = new Intent(context, HomeScreenActivity.class);//on tap go to home
        String prayer = "=================================================";
        prayNowIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,prayNowIntent,0);

            NotificationCompat.Builder notifyBuilder =
                    new NotificationCompat.Builder(context, "high_important_channel")
                            .setContentTitle("عماد")
                            .setContentText("حان الآن موعد صلاة "+prayer)
                            //.setSmallIcon(R.drawable.================)
                            //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.======))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

            manager.notify(1, notifyBuilder.build());
           }



}
