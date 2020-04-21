package ie.ul.traintracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static ie.ul.traintracker.CheckTrains.CHANNEL_ID;
import static ie.ul.traintracker.CheckTrains.RQS_1;
import static ie.ul.traintracker.CheckTrains.TITLE_ID;
import static ie.ul.traintracker.CheckTrains.CONTENT_ID;

@SuppressWarnings("deprecation")
public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, builder.build());
    }
}