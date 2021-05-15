package com.example.eventmanager;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static final String EVENT_CHANNEL_ID = "EventReminder";
    public static final String CHANNEL_2_ID = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    EVENT_CHANNEL_ID,
                    "Event Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");
//            NotificationChannel channel2 = new NotificationChannel(
//                    CHANNEL_2_ID,
//                    "Channel 2",
//                    NotificationManager.IMPORTANCE_LOW
//            );
//            channel2.setDescription("This is Channel 2");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            //manager.createNotificationChannel(channel2);
        }
    }
}
