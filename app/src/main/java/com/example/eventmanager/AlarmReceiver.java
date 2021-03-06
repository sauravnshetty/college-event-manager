package com.example.eventmanager;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.eventmanager.App.EVENT_CHANNEL_ID;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";
    //private final String ACTION = "EVENT_REMINDER";

    private String eventId, eventClubId;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {

        //String action = intent.getAction();
        //if(ACTION.equals(action)) {
        String eventName = intent.getStringExtra("eventName");
        eventId = intent.getStringExtra("eventId");
        eventClubId = intent.getStringExtra("eventClubId");
        createNotification(context, eventName,"Are you ready? Just 1 hour left for " + eventName);
        Log.d(TAG, "NOTIFICATION SHOULD DISPLAY NOW");
        //}
    }

    public void createNotification(Context context, String title, String message){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, EVENT_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round).setColor(context.getResources().getColor(R.color.purple_200))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_app_icon_round))
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent intent = new Intent(context, EventViewActivity.class);
        intent.putExtra("selectedEventId", eventId);
        intent.putExtra("eventClubId", eventClubId);

        PendingIntent pending = PendingIntent.getActivity(context, (int)System.currentTimeMillis(), intent, 0);
        mBuilder.setContentIntent(pending);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,mBuilder.build());
    }
}
