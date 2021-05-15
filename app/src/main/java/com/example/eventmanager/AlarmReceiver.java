package com.example.eventmanager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.eventmanager.App.EVENT_CHANNEL_ID;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    private final String ACTION = "EVENT_REMINDER";

    @Override
    public void onReceive(Context context, Intent intent) {

        //String action = intent.getAction();
        //if(ACTION.equals(action)) {
            createNotification(context, intent.getStringExtra("eventName"),"Are you ready? Event starts in 1 hour");
            Log.d(TAG, "NOTIFICATION SHOULD DISPLAY NOW");
        //}
    }

    public void createNotification(Context context, String title, String message){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, EVENT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nitte.edu.in"));

        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);
        mBuilder.setContentIntent(pending);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,mBuilder.build());
    }
}
